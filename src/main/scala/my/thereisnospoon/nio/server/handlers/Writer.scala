package my.thereisnospoon.nio.server.handlers

import java.nio.ByteBuffer
import java.nio.channels.{Selector, SocketChannel, SelectionKey}
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime

import scala.StringBuilder
import scala.collection.mutable

class Writer(private val key: SelectionKey, private val data: List[Byte]) extends Runnable {

  @volatile
  private var responseData: List[Byte] = formResponseData
  val buffer = ByteBuffer.wrap(responseData.toArray)

  private def formResponseData: List[Byte] = {

    val responseBuilder = new StringBuilder
    responseBuilder ++= "HTTP/1.1 200 OK\n"
    responseBuilder ++= s"Date: ${LocalDateTime.now()}\n"
    responseBuilder ++= "Content-Encoding: UTF-8\n"
    responseBuilder ++= s"Content-Length: ${data.length}\n\n"

    responseBuilder.toString.getBytes(StandardCharsets.UTF_8).toList ::: data
  }

  private def doWrite(): Unit = {

    val channel = key.channel().asInstanceOf[SocketChannel]

    if (responseData.isEmpty) {
      key.attach(ReadyHandler)
      return
    }

    if (!key.isValid || !key.isWritable) {
      return 
    }

    val bytesWritten = channel.write(buffer)

    responseData = responseData.drop(bytesWritten)
  }

  override def run() = {
    synchronized {
      doWrite()
    }
  }
}
