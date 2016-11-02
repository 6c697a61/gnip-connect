package com.badgeville.twitter

import akka.actor.{Actor, ActorLogging, ActorSystem}

/** Per-message processing for gnip stream. */
class MessageProcessor extends Actor with ActorLogging {

  implicit val system = ActorSystem()
  implicit val ec = system.dispatcher

  override def receive: Receive = {
    case m: GnipMessage =>
      // what to do with the message? just logging for now
      log.info(s"hello ${m.body} ${m.link}")
  }
}
