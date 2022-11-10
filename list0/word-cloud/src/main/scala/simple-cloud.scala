import scala.io.Source
import java.io.File
import java.io.BufferedWriter
import java.io.FileWriter
@main
def simpleCloud: Unit =
  println("Welcome to word cloud generator!")
  println("Please enter the path to the file you want to process:")
  // val path = scala.io.StdIn.readLine()
  val path: String = "src/main/resources/dune.txt"
  println(path)

  val source = Source.fromFile(path)
  val text = source.getLines().mkString(" ")
  source.close()
  val stopWordsSource = Source.fromFile("src/main/resources/stopwords.txt")
  val stopWords = stopWordsSource.getLines().toSet
  stopWordsSource.close()

  val words = text
    .replaceAll("[^\\s\\w\\d]", "")
    .split("\\s+")
    .map(_.toLowerCase)

  val wordsCountedSorted = words
    .filterNot(stopWords.contains(_))
    .groupBy(identity)
    .view
    .mapValues(_.size)
    .toSeq
    .sortBy(-_._2)

  println(wordsCountedSorted.take(10))

  val file = new File("src/main/resources/dune_wordcloud.csv")
  val bw = new BufferedWriter(new FileWriter(file))
  wordsCountedSorted.foreach { case (word, count) =>
    bw.write(s"$count,$word")
    bw.newLine()
  }
  bw.close()
