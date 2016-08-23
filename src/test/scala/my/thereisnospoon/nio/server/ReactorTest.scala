package my.thereisnospoon.nio.server

import java.io.{BufferedReader, InputStreamReader}
import java.net.Socket

import org.scalatest.FlatSpec

class ReactorTest extends FlatSpec {

  "Reactor" should "accept connections" in {

    new Server
    val clientSocket = new Socket("localhost", 9898)
    clientSocket.getOutputStream.write(Array[Byte]('D', 'i', 'm', 'a'))
    val inp = new BufferedReader(new InputStreamReader(clientSocket.getInputStream))
    println(inp.readLine())
  }
}
