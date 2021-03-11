package com.agilogy.http4stapir

import cats.effect.Sync
import cats.implicits._

import io.circe.generic.auto._
import org.http4s.Method._
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.implicits._

trait Jokes[F[_]]{
  def get: F[Jokes.Joke]
}

object Jokes {
  def apply[F[_]](implicit ev: Jokes[F]): Jokes[F] = ev

  final case class Joke(joke: String)

  final case class JokeError(e: Throwable) extends RuntimeException

  def impl[F[_]: Sync](C: Client[F]): Jokes[F] = new Jokes[F]{
    val dsl = new Http4sClientDsl[F]{}
    import dsl._
    def get: F[Jokes.Joke] = {
      C.expect[Joke](GET(uri"https://icanhazdadjoke.com/")).adaptError{ case t => JokeError(t)} // Prevent Client Json Decoding Failure Leaking
    }
  }
}
