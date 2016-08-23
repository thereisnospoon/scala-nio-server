package my.thereisnospoon.nio.server

class Server {

  val reactor = new Reactor(9898)
  new Thread(reactor).start()
}
