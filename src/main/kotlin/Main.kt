import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.joinu.rudp.RUDPSocket
import net.joinu.rudp.runSuspending
import net.joinu.rudp.send
import java.net.InetSocketAddress


suspend fun sender(socket: RUDPSocket) {
    println("Transmission started...")

    socket.send(ByteArray(10000) { it.toByte() }, InetSocketAddress("receiver", 1337))

    println("Transmission success!")
}

suspend fun receiver(socket: RUDPSocket) {
    println("Receive started...")

    socket.receive()

    println("Receive success!")
}

fun main(args: Array<String>) {
    if (args.isEmpty())
        throw RuntimeException("You should provide argument (-s or -r)")

    System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE")

    val socket = RUDPSocket()
    socket.bind(InetSocketAddress(1337))

    runBlocking {
        launch { socket.runSuspending() }

        when {
            args.first() == "-s" -> sender(socket)
            args.first() == "-r" -> receiver(socket)
            else -> throw RuntimeException("Invalid argument")
        }

        coroutineContext.cancelChildren()
    }

    socket.close()
}
