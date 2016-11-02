package com.badgeville.twitter

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.headers.{Authorization, BasicHttpCredentials, Connection}
import akka.http.scaladsl.settings.ClientConnectionSettings
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import com.typesafe.config.ConfigFactory
import org.json4s._
import org.json4s.jackson.JsonMethods._;

/**
  * Connects to Gnip's powertrack stream and sends relevant messages to MessageProcessor.
  */
class GnipStreamConnector extends Actor with ActorLogging {

  implicit val conf = ConfigFactory.load()
  implicit val system = ActorSystem()
  implicit val ec = system.dispatcher
  implicit val mat = ActorMaterializer(ActorMaterializerSettings(system))
  val processor = system.actorOf(Props[MessageProcessor], "processor")

  val user = conf.getString("gnip.username")
  val pass = conf.getString("gnip.password")
  val account = conf.getString("gnip.account")
  val stream = conf.getString("gnip.stream")
  val url = s"https://gnip-stream.twitter.com/stream/powertrack/accounts/$account/publishers/twitter/$stream.json"
  val client = Http(system).outgoingConnectionHttps(host = "gnip-stream.twitter.com", settings = ClientConnectionSettings(system))
  val creds = Authorization(BasicHttpCredentials(user, pass))

  implicit val formats = DefaultFormats

  override def preStart() = {
    val cred = Authorization
    val req: HttpRequest = HttpRequest(GET, url).withHeaders(Connection("Keep-Alive"), creds)

    Source.single(req).via(client).runWith(Sink.foreach(r =>
    {
      r.entity.dataBytes.runForeach( {
        case s if ( s.utf8String.trim.length > 0 ) =>
          log.debug( s"Raw message from gnip: ${ s.utf8String }" )
          val parsed = parse(s.utf8String)
          log.debug(s"Parsed gnip message: $parsed")
          val msg = parsed.extract[GnipMessage]
          if (msg.gnip.map(_._2.tag == "internal").foldLeft(false)((a, b) => (a || b)))
            processor ! msg
          else log.debug("skipping message")
        case s =>
          // Gnip sends a line break every 5 seconds (as of 2016-10 ).
          // If we are not getting those periodic new lines, then something is wrong.
          log.debug(s"Line break received from gnip.")
      })
    }))
  }

  def receive = {
    case x => log.warning("Was not expecting any messages.")
  }

}

case class GnipRule(id: Long, tag: String)
case class GnipMessage(body: String, link: String, gnip: Map[String, GnipRule])

