package my.thereisnospoon.nio.server.handlers

import java.nio.ByteBuffer
import java.nio.channels.{Selector, SelectionKey, SocketChannel}
import java.nio.charset.StandardCharsets
import java.util.regex.Pattern

import scala.collection.mutable.ListBuffer

class Reader(private val key: SelectionKey) extends Runnable {

  val buffer = ByteBuffer.allocate(1024)

  @volatile
  var messageData: List[Byte] = Nil

  private def doRun(): Unit = {

    buffer.clear()

    if (!key.isValid || !key.isReadable) {
      return
    }

    val bytesRead = key.channel().asInstanceOf[SocketChannel].read(buffer)

    if (bytesRead == 0) {
      return
    }

    appendMessageData(buffer)

    if (isEndOfMessageReached) {
      attachWriterToChannel()
    }
  }

  override def run() = {

    synchronized {
      doRun()
    }
  }

  private def appendMessageData(buffer: ByteBuffer): List[Byte] = {

    buffer.flip()
    val array = new Array[Byte](buffer.limit())
    buffer.get(array)
    val appendedData = array.toList
    messageData = messageData ::: appendedData
    appendedData
  }

  private def isEndOfMessageReached: Boolean = {

    messageData.reverse.take(4) match {
      case '\n' :: '\r' :: '\n' :: '\r' :: Nil => true
      case _ => false
    }
  }

  private def attachWriterToChannel() = {

    key.attach(new Writer(key, messageData))
    key.interestOps(SelectionKey.OP_WRITE)
  }
}
