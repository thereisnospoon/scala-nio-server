package my.thereisnospoon.nio.server.handlers

import java.nio.ByteBuffer
import java.nio.channels.{Selector, SelectionKey, SocketChannel}

import scala.collection.mutable.ListBuffer

class Reader(private val key: SelectionKey) extends Runnable {

  @volatile
  var messageData: List[Byte] = Nil

  private def doRun(): Unit = {

    val buffer = ByteBuffer.allocate(1024)
    buffer.clear()

    val bytesRead = key.channel().asInstanceOf[SocketChannel].read(buffer)

    if (bytesRead == 0) {
      return
    }

    val appendedData = appendMessageData(buffer)

    if (isEndOfMessageReached(appendedData)) {
      println(s"Received ${messageData.mkString(", ")}")
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

  private def isEndOfMessageReached(readData: List[Byte]): Boolean = readData.toSet(Constants.End_of_message)

  private def attachWriterToChannel() = {

    key.attach(new Writer(key, messageData))
    key.interestOps(SelectionKey.OP_WRITE)
  }
}
