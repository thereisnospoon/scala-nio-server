package my.thereisnospoon.nio.server

import java.net.Socket
import java.nio.charset.StandardCharsets

import org.scalatest.FlatSpec

class ReactorTest extends FlatSpec {

  "Reactor" should "accept connections" in {

    new Server
    val clientSocket = new Socket("localhost", 9898)
    clientSocket.getOutputStream.write(Array[Byte]('D', 'i', 'm', 'a', '$'))

    val receivedData = new Array[Byte](11)
    clientSocket.getInputStream.read(receivedData)
    println(new String(receivedData, StandardCharsets.UTF_8))
  }
}
