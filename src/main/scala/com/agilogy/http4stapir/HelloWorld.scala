package com.agilogy.http4stapir

import cats.Applicative
import cats.implicits._

trait HelloWorld[F[_]]{
  def hello(n: String): F[HelloWorld.Greeting]
}

object HelloWorld {
  implicit def apply[F[_]](implicit ev: HelloWorld[F]): HelloWorld[F] = ev

  /**
    * More generally you will want to decouple your edge representations from
    * your internal data structures, however this shows how you can
    * create encoders for your data.
    **/
  final case class Greeting(greeting: String)
//  object Greeting {
//    implicit val greetingEncoder: Encoder[Greeting] = new Encoder[Greeting] {
//      final def apply(a: Greeting): Json = Json.obj(
//        ("message", Json.fromString(a.greeting)),
//      )
//    }
//    implicit def greetingEntityEncoder[F[_]: Applicative]: EntityEncoder[F, Greeting] =
//      jsonEncoderOf[F, Greeting]
//  }

  def impl[F[_]: Applicative]: HelloWorld[F] = new HelloWorld[F]{
    def hello(n: String): F[HelloWorld.Greeting] =
        Greeting("Hello, " + n).pure[F]
  }
}