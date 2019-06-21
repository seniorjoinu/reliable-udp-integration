import net.joinu.rudp.RUDPSocket
import net.joinu.rudp.send
import java.lang.RuntimeException
import java.net.InetSocketAddress

fun sender() {
    val sender = RUDPSocket()
    sender.bind(InetSocketAddress(1337))

    var sent = false

    println("Transmission started...")

    sender.send(ByteArray(10) { it.toByte() }, InetSocketAddress("receiver", 1337)) { sent = true }

    while (!sent) {
        sender.runOnce()
    }

    println("Transmission success!")
    sender.close()
}

fun receiver() {
    val receiver = RUDPSocket()
    receiver.bind(InetSocketAddress(1337))

    println("Receive started...")

    while (true) {
        receiver.runOnce()
        val data = receiver.receive()
        if (data != null) break
    }

    println("Receive success!")
    receiver.close()
}

fun main(args: Array<String>) {
    if (args.isEmpty())
        throw RuntimeException("You should provide argument (-s or -r)")

    System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE")

    when {
        args.first() == "-s" -> sender()
        args.first() == "-r" -> receiver()
        else -> throw RuntimeException("Invalid argument")
    }
}
