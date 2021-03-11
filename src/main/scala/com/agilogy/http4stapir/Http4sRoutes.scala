package com.agilogy.http4stapir

import cats.effect.{Concurrent, ContextShift, Sync, Timer}
import cats.implicits._
import com.agilogy.http4stapir.HelloWorld.Greeting
import com.agilogy.http4stapir.Jokes.Joke
import io.circe.generic.auto._
import org.http4s.HttpRoutes
import sttp.tapir._
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import sttp.tapir.openapi.OpenAPI
import sttp.tapir.server.http4s.Http4sServerInterpreter


object Http4sRoutes {

  def jokes: Endpoint[Unit, Unit, Joke, Any] = endpoint.get.in("joke").out(anyJsonBody[Joke])

  def jokeRoutes[F[_]: Concurrent: ContextShift](J: Jokes[F])(implicit timer: Timer[F]): HttpRoutes[F] = {
    Http4sServerInterpreter.toRoutes(jokes)(_ => J.get.map(_.asRight[Unit]))
  }

  def helloWorld[F[_]: Sync]: Endpoint[String, Unit, Greeting, Any] =
    endpoint.get.in("hello" / path[String]("name")).out(anyJsonBody[Greeting]).description("Greeting endpoint")

  def helloWorldRoutes[F[_]: Concurrent: ContextShift](helloWorldAlg: HelloWorld[F])(implicit timer: Timer[F]): HttpRoutes[F] =
    Http4sServerInterpreter.toRoutes(helloWorld)(name => helloWorldAlg.hello(name).map(_.asRight[Unit]))

  def docs[F[_]: Sync]: OpenAPI = OpenAPIDocsInterpreter.toOpenAPI(List(helloWorld, jokes), "HelloWorld", "1.0")


}