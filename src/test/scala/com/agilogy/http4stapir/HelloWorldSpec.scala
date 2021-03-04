package com.agilogy.http4stapir

import cats.effect.IO
import munit.CatsEffectSuite
import org.http4s._
import org.http4s.implicits._

class HelloWorldSpec extends CatsEffectSuite {

  private[this] val retHelloWorld: IO[Response[IO]] = {
    val getHW = Request[IO](Method.GET, uri"/hello?name=world")
    val helloWorld = HelloWorld.impl[IO]
    Http4sRoutes.helloWorldRoutes[IO](helloWorld).orNotFound(getHW)
  }


  test("HelloWorld returns status code 200") {
    assertIO(retHelloWorld.map(_.status) ,Status.Ok)
  }

  test("HelloWorld returns hello world message") {
    assertIO(retHelloWorld.flatMap(_.as[String]), "{\"greeting\":\"Hello, world\"}")
  }


}