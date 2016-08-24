package my.thereisnospoon.nio.server

import java.nio.channels.{SocketChannel, SelectionKey, Selector}
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

import my.thereisnospoon.nio.server.handlers.{ReadyHandler, Writer, Reader}

import scala.concurrent.ExecutionContext

class Reactor(private val port: Int) extends Runnable {

  private val selector = Selector.open()
  private val acceptor: Acceptor = new Acceptor(port, selector)
  private val executionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(2))

  private var connectionsCounter = 0

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

    if (selectionKey.isAcceptable && connectionsCounter <= 10) {
      connectionsCounter += 1
      acceptor.acceptConnection()
    } else {
      val handler = selectionKey.attachment()
      if (handler == ReadyHandler) {
//        selectionKey.channel().close()
        connectionsCounter -= 1
      } else if (handler != null) {
        executionContext.execute(handler.asInstanceOf[Runnable])
      }
    }
  }
}
