package my.thereisnospoon.nio.server.handlers

import java.nio.ByteBuffer
import java.nio.channels.{SelectionKey, SocketChannel}

import scala.collection.mutable.ListBuffer

class Reader(private val selectionKey: SelectionKey) extends Handler {

  private val messageData = new ListBuffer[Byte]
  private val buffer = ByteBuffer.allocate(1024)

  override def run(): Unit = {

    val channel = selectionKey.channel().asInstanceOf[SocketChannel]
    val bytesRead = channel.read(buffer)
    if (bytesRead <= 0) {
      selectionKey.interestOps(SelectionKey.OP_WRITE)
      selectionKey.attach(new Writer(selectionKey, messageData))
      selectionKey.selector().wakeup()

    } else {
      appendMessageData()
    }
  }

  private def appendMessageData(): Unit = {

    buffer.flip()
    val array = new Array[Byte](buffer.limit())
    buffer.get(array)
    messageData ++= array


    println(s"Message: ${messageData.mkString(", ")}")
  }
}
