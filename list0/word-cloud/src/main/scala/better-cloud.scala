import scala.io.Source
import java.io.File
import java.io.BufferedWriter
import java.io.FileWriter

// Class for creation of a world cloud. Argument str can be a path to a file or a string.
class WordCloud(val stopWordsPath: String, val outputPath: String) {

  var cloud: Seq[(String, Int)] = Seq()

  // close the stream somehow
  val stopWords = Source.fromFile(stopWordsPath).getLines().toSet


  def countWordsFromString(text: String): Seq[(String, Int)] = {
    val words = text
      .replaceAll("[^\\s\\w\\d]", "")
      .split("\\s+")
      .map(_.toLowerCase)
    cloud = words
      .filterNot(stopWords.contains(_))
      .groupBy(identity)
      .view
      .mapValues(_.size)
      .toSeq
      .sortBy(-_._2)
    cloud
  }

  def countWordsFromFile(path: String): Seq[(String, Int)] = {
    val text = 
    countWordsFromString(text)
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
}

@main
def betterCloud: Unit =
  println("Welcome to word cloud generator!")
  println("Please enter the path to the file you want to process:")
  // val path = scala.io.StdIn.readLine()
  val path: String = "src/main/resources/dune.txt"
  println(path)

  val stopWordsPath = "src/main/resources/stopwords.txt"
  val outputPath = "src/main/resources/dune_wordcloud_v2.csv"
  val wordCloud = new WordCloud(stopWordsPath, outputPath)
  wordCloud.countWordsFromFile(path)
  wordCloud.writeCloudToFile()
