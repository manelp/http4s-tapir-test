package com.agilogy.http4stapir

import cats.effect.{Concurrent, ContextShift, Sync, Timer}
import cats.implicits._
import com.agilogy.http4stapir.HelloWorld.Greeting
import io.circe.generic.auto._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import sttp.tapir._
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import sttp.tapir.openapi.OpenAPI
import sttp.tapir.server.http4s.Http4sServerInterpreter


object Http4sRoutes {

  def jokeRoutes[F[_]: Sync](J: Jokes[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "joke" =>
        for {
          joke <- J.get
          resp <- Ok(joke)
        } yield resp
    }
  }

  def helloWorld[F[_]: Sync]: Endpoint[String, Unit, Greeting, Any] =
    endpoint.get.in("hello").in(query[String]("name")).out(anyJsonBody[Greeting])

  def helloWorldRoutes[F[_]: Concurrent: ContextShift](helloWorldAlg: HelloWorld[F])(implicit timer: Timer[F]): HttpRoutes[F] =
    Http4sServerInterpreter.toRoutes(helloWorld)(name => helloWorldAlg.hello(name).map(_.asRight[Unit]))

  def docs[F[_]: Sync]: OpenAPI = OpenAPIDocsInterpreter.toOpenAPI(List(helloWorld), "HelloWorld", "1.0")


}