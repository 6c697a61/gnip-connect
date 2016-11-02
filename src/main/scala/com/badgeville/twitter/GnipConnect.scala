package com.badgeville.twitter

import akka.actor.{ActorSystem, Props}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}

object GnipConnect extends App {

  implicit val system = ActorSystem()
  implicit val dispatcher = system.dispatcher
  implicit val materializer = ActorMaterializer(ActorMaterializerSettings(system))
  val connector = system.actorOf(Props[GnipStreamConnector], "connector")
}
