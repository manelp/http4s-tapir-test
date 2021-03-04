package com.agilogy.http4stapir

import cats.effect.{ExitCode, IO, IOApp}

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    Http4Server.stream[IO].compile.drain.as(ExitCode.Success)
}
