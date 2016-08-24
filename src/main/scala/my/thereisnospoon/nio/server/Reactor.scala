package my.thereisnospoon.nio.server

import java.nio.channels.{SocketChannel, SelectionKey, Selector}
import java.util.concurrent.Executors

import my.thereisnospoon.nio.server.handlers.{Writer, Reader}

import scala.concurrent.ExecutionContext

class Reactor(private val port: Int) extends Runnable {

  private val selector = Selector.open()
  private val acceptor: Acceptor = new Acceptor(port, selector)
  private val executionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(2))

  override def run() = {

    while (!Thread.interrupted()) {

      selector.select(1000)
      val selectedKeys = selector.selectedKeys()
      val iterator: java.util.Iterator[SelectionKey] = selectedKeys.iterator()
      while (iterator.hasNext) {

        val selectionKey = iterator.next()
        if (selectionKey.isValid) {
          processKeyEvent(selectionKey)
        }

        iterator.remove()
      }
    }
  }

  private def processKeyEvent(selectionKey: SelectionKey) = {

    if (selectionKey.isAcceptable) {
      acceptor.acceptConnection()
    } else {
      val handler = selectionKey.attachment()
      if (handler == null) {
        selectionKey.channel().close()
      } else {
        executionContext.execute(handler.asInstanceOf[Runnable])
      }
    }
  }
}
