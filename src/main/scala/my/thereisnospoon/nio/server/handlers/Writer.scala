package my.thereisnospoon.nio.server.handlers

import java.nio.ByteBuffer
import java.nio.channels.{SocketChannel, SelectionKey}

import scala.collection.mutable

class Writer(private val selectionKey: SelectionKey, private val data: mutable.Buffer[Byte]) extends Handler {

  private val responseData: Seq[Byte] = "Hello ".getBytes.toBuffer ++= data
  private val buffer = ByteBuffer.wrap(responseData.toArray)
  buffer.flip()

  override def run(): Unit = {

    val channel = selectionKey.channel().asInstanceOf[SocketChannel]
    val bytesWritten = channel.write(buffer)

    if (bytesWritten < 0) {
      channel.close()
    }
  }
}
