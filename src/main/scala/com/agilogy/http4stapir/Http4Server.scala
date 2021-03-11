package com.agilogy.http4stapir

import cats.effect.{ConcurrentEffect, ContextShift, IO, Timer}
import cats.implicits._
import fs2.Stream
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.swagger.http4s.SwaggerHttp4s

import scala.concurrent.ExecutionContext.global

object Http4Server {

  def stream[F[_]: ConcurrentEffect: ContextShift](implicit T: Timer[F]): Stream[F, Nothing] = {
    for {
      client <- BlazeClientBuilder[F](global).stream
      helloWorldAlg = HelloWorld.impl[F]
      jokeAlg = Jokes.impl[F](client)

      // Combine Service Routes into an HttpApp.
      // Can also be done via a Router if you
      // want to extract a segments not checked
      // in the underlying routes.
      httpApp = (
        Http4sRoutes.helloWorldRoutes(helloWorldAlg) <+>
          new SwaggerHttp4s(Http4sRoutes.docs[IO].toYaml).routes <+>
          Http4sRoutes.jokeRoutes[F](jokeAlg)
      ).orNotFound

      // With Middlewares in place
      finalHttpApp = Logger.httpApp(true, true)(httpApp)

      exitCode <- BlazeServerBuilder[F](global)
        .bindHttp(8081, "0.0.0.0")
        .withHttpApp(finalHttpApp)
        .serve
    } yield exitCode
  }.drain
}
