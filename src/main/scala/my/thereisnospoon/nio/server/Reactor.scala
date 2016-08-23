package my.thereisnospoon.nio.server

import java.nio.channels.{SelectionKey, Selector}
import java.util.concurrent.Executors

import my.thereisnospoon.nio.server.handlers.Handler

import scala.concurrent.ExecutionContext

class Reactor(private val port: Int) extends Runnable {

  private val selector = Selector.open()
  private val acceptor: Acceptor = new Acceptor(port, selector)
  private val handlersExecutionContext = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(2))

  override def run() = {

    while (!Thread.interrupted()) {

      selector.select(1000)
      val selectedKeys = selector.selectedKeys()
      val iterator: java.util.Iterator[SelectionKey] = selectedKeys.iterator()
      while (iterator.hasNext) {

        val selectionKey = iterator.next()
        if (selectionKey.isAcceptable) {
          acceptor.acceptConnection()
        } else {
          handlersExecutionContext.execute(selectionKey.attachment().asInstanceOf[Handler])
        }

        iterator.remove()
      }
    }
  }
}
