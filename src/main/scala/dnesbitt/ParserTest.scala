package dnesbitt

import java.io.{BufferedReader, FileReader}
import java.nio.file.Paths
import java.util.stream.Collectors

import io.circe.parser.parse

/**
  * @author Daniel Nesbitt
  */
object ParserTest extends App {

  override def main(args: Array[String]) {
    val path = Paths.get("C:\\Users\\Daniel\\IdeaProjects\\Foo.json")
    val time = System.currentTimeMillis()
    val str = new BufferedReader(new FileReader(path.toFile)).lines().collect(Collectors.joining())

    testParse(str, "circe")(parse)

//    val mapper = new ObjectMapper
//    testParse(str, "jackson")(mapper.readTree)
  }

  def testParse(str: String, parser: String)(f: String => Any): Unit = {
    val startTime = System.currentTimeMillis()
    1 to 100 foreach {
      _ => f(str)
    }
    println("Took " + (System.currentTimeMillis() - startTime) + "ms to parse json via " + parser)
  }

}
