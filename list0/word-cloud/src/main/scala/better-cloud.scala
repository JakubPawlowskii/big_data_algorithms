import scala.io.Source
import java.io.File
import java.io.BufferedWriter
import java.io.FileWriter

class wordCloud(var outputPath: String = "") {

  var cloud: Seq[(String, Int)] = Seq()
  var stopWords: Set[String] = Set()

  def readStopWords(path: String): Unit = {
    val stream = Source.fromFile(path)
    stopWords = stream.getLines().toSet
    stream.close()
  }

  def addWordsFromString(text: String): Unit = {
    val words = text
      .replaceAll("[^\\s\\w\\d]", "")
      .split("\\s+")
      .map(_.toLowerCase)
      .toSeq

    val cloudTmp = words
      .filterNot(stopWords.contains(_))
      .groupBy(identity)
      .view
      .mapValues(_.size)
      .toSeq
      .sortBy(-_._2)

    cloud = cloud ++ cloudTmp
    cloud = cloud.groupBy(_._1).view.mapValues(_.map(_._2).sum).toSeq.sortBy(-_._2)
  }

  def addWordsFromFile(path: String): Unit = {
    val text = Source.fromFile(path).getLines().mkString(" ")
    addWordsFromString(text)
  }

  def writeCloudToFile(): Unit = {
    val file = new File(outputPath)
    val bw = new BufferedWriter(new FileWriter(file))
    cloud.foreach { case (word, count) =>
      bw.write(s"$count,$word")
      bw.newLine()
    }
    bw.close()
  }

  def printCloud(n: Int): Unit = {
    cloud.take(n).foreach { case (word, count) =>
      println(s"$count $word")
    }
  }
  def printCloud(): Unit = {
    cloud.foreach { case (word, count) =>
      println(s"$count,$word")
    }
  }
  def getCloud(): Seq[(String, Int)] = cloud

}

@main
def betterCloud: Unit = {

  var exit = false
  var hello_flag = true
  var cloud = new wordCloud()

  while (!exit) {
    if (hello_flag) {
      println("Hello! This is a simple word cloud generator.")
      println("You can add word by entering a path to a file or a string.")
      println("You can also add a path to a file with stop words.")
      println("It can save the result to a file or print it to the console.")
      println("Have fun!")
      println()
      hello_flag = false
    }
    var flag = true
    while (flag) {
      println(
        "Enter path to file with stop words or 'd' to use default (English) ones:"
      )
      val stopWordsPath = scala.io.StdIn.readLine()
      try {
        if (stopWordsPath == "d") {
          cloud.readStopWords("src/main/resources/stopwords.txt")
          flag = false
        } else {
          cloud.readStopWords(stopWordsPath)
          flag = false
        }
      } catch {
        case e: Exception => println("Error: " + e)
      }
    }

    flag = true
    while (flag) {
      println(
        "Enter 'f' to add words from file or 's' to add words from string:"
      )
      val inputType = scala.io.StdIn.readLine()
      try {
        if (inputType == "f") {
          println("Enter path to file:")
          val path = scala.io.StdIn.readLine()
          cloud.addWordsFromFile(path)
          flag = false
        } else if (inputType == "s") {
          println("Enter string:")
          val text = scala.io.StdIn.readLine()
          cloud.addWordsFromString(text)
          flag = false
        } else {
          throw new Exception("Wrong input mode")
        }
      } catch {
        case e: Exception => println("Error: " + e)
      }
    }

    println("Would you like to keep adding words or go to output? (a/o)")
    val answer = scala.io.StdIn.readLine()
    if (answer == "o") {
      flag = true
      while (flag) {
        println("Enter 'f' to save to file or 'p' to print to console:")
        val outputType = scala.io.StdIn.readLine()
        try {
          if (outputType == "f") {
            println("Enter path to file:")
            val path = scala.io.StdIn.readLine()
            cloud.outputPath = path
            cloud.writeCloudToFile()
            flag = false
          } else if (outputType == "p") {
            println(
              "Enter number of most frequent words to print (or 'a' to print all):"
            )
            val n = scala.io.StdIn.readLine()
            if (n == "a") {
              cloud.printCloud()
            } else {
              cloud.printCloud(n.toInt)
            }
            flag = false
          } else {
            throw new Exception("Wrong output mode")
          }
        } catch {
          case e: Exception => println("Error: " + e)
        }
      }
      exit = true
    }
  }
  println("Goodbye!")
}
