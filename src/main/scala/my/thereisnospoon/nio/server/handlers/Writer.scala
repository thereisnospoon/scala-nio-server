package my.thereisnospoon.nio.server.handlers

import java.nio.ByteBuffer
import java.nio.channels.{Selector, SocketChannel, SelectionKey}

import scala.collection.mutable

class Writer(private val key: SelectionKey, private val data: List[Byte]) extends Runnable {

  @volatile
  private var responseData: List[Byte] = "Hello ".getBytes.toList ::: data

  private def doWrite(): Unit = {

    val channel = key.channel().asInstanceOf[SocketChannel]

    if (responseData.isEmpty) {
      key.attach(null)
      return
    }

    val buffer = ByteBuffer.wrap(responseData.toArray)
    val bytesWritten = channel.write(buffer)

    responseData = responseData.drop(bytesWritten)
  }

  override def run() = {
    synchronized {
      doWrite()
    }
  }
}
