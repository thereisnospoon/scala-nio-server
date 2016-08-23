package my.thereisnospoon.nio.server

import java.net.InetSocketAddress
import java.nio.channels.{SelectionKey, Selector, ServerSocketChannel}

import my.thereisnospoon.nio.server.handlers.Reader

class Acceptor(private val port: Int, private val selector: Selector) {

  private val serverSocketChannel = ServerSocketChannel.open()
  serverSocketChannel.bind(new InetSocketAddress(port))
  serverSocketChannel.configureBlocking(false)
  serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT)

  def acceptConnection() = {

    val socketChannel = serverSocketChannel.accept()
    socketChannel.configureBlocking(false)
    val selectionKey = socketChannel.register(selector, SelectionKey.OP_READ)
    selectionKey.attach(new Reader(selectionKey))

    println("Connection accepted")
  }
}
