import scala.io.Source
import java.io.File
import java.io.BufferedWriter
import java.io.FileWriter

import wordCloud._

@main def cloudTest: Unit = {

  println("======================  Word Cloud ======================")
  val pathToStopWords = "books/stopwords.txt"

  val folderPath = "books/novels/"
  val fileList = new File(folderPath).listFiles
    .filter(_.isFile)
    .toList
    .map(_.getPath)

  println("Generating cloud for all files in folder: " + folderPath)
  println("The files are: ")
  fileList.map(w => w.split("/").last).foreach(println)

  val cloud = new wordCloud(fileList)
  cloud.readStopWords(pathToStopWords)
  cloud.updateCloud()
  println("10 most common words in all texts together: ")
  cloud.printCloudTotal(10)

  println("10 most common words in the first text: ")
  cloud.printCloudForFile(fileList.head, 10)

  println("======================  TFIDF ======================")
  cloud.updateTFIDF()
  println("Max TFIDF for each files:")
  val tfidf = cloud.getTFIDF()
  fileList.foreach(file => {
    val max = tfidf(file).maxBy(_._2)
    println(file.split("/").last + ": " + max)
  })

  println("Max TFIDF in total:")
  val max = tfidf.flatMap(_._2).maxBy(_._2)
  println(max)
}

@main def experiment: Unit = {
  val pathToStopWords = "books/stopwords.txt"

  // all similar topics or all different

  // val folderPath = "books/different_topics"
  // val folderPath = "books/similar_topics"
  // val fileList = new File(folderPath).listFiles
  //   .filter(_.isFile)
  //   .toList
  //   .map(_.getPath)

  val fileList = new File("books/similar_topics").listFiles
    .filter(_.isFile)
    .toList
    .map(_.getPath)
    .take(5) ++ new File("books/different_topics").listFiles
    .filter(_.isFile)
    .toList
    .map(_.getPath)
    .take(5)
  
  fileList.foreach(println)
  
  val cloud = new wordCloud(fileList)
  cloud.readStopWords(pathToStopWords)
  cloud.updateCloud()
  cloud.updateTFIDF()
  
  println("================================ Frequencies ====================================================")
  // print top 10 words according to frequency
  cloud.printCloudTotal(20)
  // print top word in each file according to frequency
  fileList.foreach(file => {
    cloud.printCloudForFile(file, 1)
  })

  // print top 10 words according to tfidf
  println("================================ TFIDF ==========================================================")
  val tfidf = cloud.getTFIDF()
  fileList.foreach(file => {
    val max = tfidf(file).maxBy(_._2)
    println(file.split("/").last + ": " + max)
  })


}
