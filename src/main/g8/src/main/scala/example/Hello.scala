package example

import cats.effect._
import cats.implicits._
import monix.eval._

object Hello extends TaskApp {
  /** App's main entry point. */
  def run(args: List[String]): Task[ExitCode] =
    args.headOption match {
      case Some(name) =>
        Task(println(s"Hello, \$name!")).as(ExitCode.Success)
      case None =>
        Task(System.err.println("Usage: Hello name")).as(ExitCode(2))
    }
}

