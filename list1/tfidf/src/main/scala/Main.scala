import scala.io.Source
import java.io.File
import java.io.BufferedWriter
import java.io.FileWriter

val log2 = (x: Double) => scala.math.log(x) / scala.math.log(2)

class wordCloud(var fileList: List[String]) {

  var cloudByFile = Map[String, Map[String, Int]]()
  var cloudTotal: Map[String, Int] = Map()
  var stopWords: Set[String] = Set()
  var tfIDF = Map[String, Map[String, Double]]()

  def readStopWords(path: String): Unit = {
    val stream = Source.fromFile(path)
    stopWords = stream.getLines().toSet
    stream.close()
  }

  def addFile(path: String): Unit = {
    fileList = fileList :+ path
    updateCloud()
  }
  def removeFile(path: String): Unit = {
    fileList = fileList.filter(_ != path)

    cloudByFile.get(path) match {
      case Some(fileCloud) =>
        fileCloud.foreach { case (word, count) =>
          cloudTotal.get(word) match {
            case Some(totalCount) =>
              cloudTotal = cloudTotal.updated(word, totalCount - count)
            case None =>
          }
        }
      case None =>
    }
    cloudByFile = cloudByFile - path
  }

  def updateCloud(): Unit = {
    val newFiles = fileList.filterNot(cloudByFile.contains(_))
    for (file <- newFiles) {
      val stream = Source.fromFile(file)
      val words = stream
        .getLines()
        .mkString(" ")
        .replaceAll("[^\\s\\w]", "")
        .split("\\s+")
        .map(_.toLowerCase)
        .toList

      stream.close()

      val wordMap = words.foldLeft(Map[String, Int]())((map, word) => {
        if (!stopWords.contains(word) && !word.matches("[0-9]+")) {
          map + (word -> (map.getOrElse(word, 0) + 1))
        } else {
          map
        }
      })
      cloudByFile = cloudByFile + (file -> wordMap)
      cloudTotal = cloudTotal ++ wordMap.map { case (k, v) =>
        k -> (v + cloudTotal.getOrElse(k, 0))
      }
    }
  }

  def updateTFIDF(): Unit = {
    val totalFiles = fileList.length
    for (file <- fileList) {
      val max_count = cloudByFile(file).values.max
      // val total_count = cloudByFile(file).values.sum
      val fileTFIDF = cloudByFile(file).map { case (word, count) =>
        val wordTF = count.toDouble / max_count
        // val wordTF = count.toDouble / total_count

        val wordDF = fileList.count(cloudByFile(_).contains(word))
        val wordIDF = log2(totalFiles.toDouble / wordDF)
        word -> (wordTF * wordIDF)
      }
      tfIDF = tfIDF + (file -> fileTFIDF)
    }
  }

  def getTFIDF(): Map[String, Map[String, Double]] = {
    tfIDF
  }

  def writeCloudToFile(path: String): Unit = {
    val file = new File(path)
  }

  def printCloudForFile(file: String, n: Int = 0): Unit = {

    println("Cloud for file: " + file)
    if (n == 0) {
      println(cloudByFile(file))
    } else {
      println(cloudByFile(file).toList.sortBy(_._2).takeRight(n).reverse)
    }
  }

  def printCloudSubset(fileList: List[String], n: Int = 0): Unit = {
    var subset = Map[String, Int]()
    for (file <- fileList) {
      subset = subset ++ cloudByFile(file)
    }
    println("Cloud for subset of files: " + fileList)
    if (n == 0) {
      println(subset)
    } else {
      println(subset.toList.sortBy(_._2).takeRight(n).reverse)
    }
  }

  def printCloudTotal(n: Int = 0): Unit = {
    println("Cloud for all files: ")
    if (n == 0) {
      println(cloudTotal)
    } else {
      println(cloudTotal.toList.sortBy(_._2).takeRight(n).reverse)
    }
  }

  def printCloudTotalToFile(file: String, n: Int = 0): Unit = {
    val writer = new BufferedWriter(new FileWriter(file))
    if (n == 0) {
      writer.write(cloudTotal.toString)
    } else {
      writer.write(cloudTotal.toList.sortBy(_._2).takeRight(n).toString)
    }
    writer.close()
  }

  def printCloudForFileToFile(file: String, n: Int = 0): Unit = {
    val writer = new BufferedWriter(new FileWriter(file))
    if (n == 0) {
      writer.write(cloudByFile(file).toString)
    } else {
      writer.write(cloudByFile(file).toList.sortBy(_._2).takeRight(n).toString)
    }
    writer.close()
  }

  def printCloudSubsetToFile(
      fileList: List[String],
      file: String,
      n: Int = 0
  ): Unit = {
    val writer = new BufferedWriter(new FileWriter(file))
    var subset = Map[String, Int]()
    for (file <- fileList) {
      subset = subset ++ cloudByFile(file)
    }
    if (n == 0) {
      writer.write(subset.toString)
    } else {
      writer.write(subset.toList.sortBy(_._2).takeRight(n).toString)
    }
    writer.close()
  }

  def getCloudTotal = cloudTotal
  def getCloudByFile = cloudByFile
  def getCloudForFile(file: String) = cloudByFile(file)
  def getCloudSubset(fileList: List[String]) = {
    var subset = Map[String, Int]()
    for (file <- fileList) {
      subset = subset ++ cloudByFile(file)
    }
    subset
  }

}

@main def cloud: Unit =
  val pathToStopWords = "books/stopwords.txt"

  // ================= Part (b) =================

  val folderPath = "books/novels/"
  val fileList = new File(folderPath).listFiles
    .filter(_.isFile)
    .toList
    .map(_.getPath)

  println("Generating cloud for all files in folder: " + folderPath)
  println("The files are: ")
  fileList.map(w=>w.split("/").last).foreach(println)

  val cloud = new wordCloud(fileList)
  cloud.readStopWords(pathToStopWords)
  cloud.updateCloud()
  cloud.printCloudTotal(10)
  // cloud.printCloudSubset(List(fileList(0), fileList(3)), 10)

  // cloud.removeFile(folderPath + "The One-Dimensional Hubbard Model.txt")
  // cloud.printCloudTotal(10)
  // cloud.addFile(folderPath + "The One-Dimensional Hubbard Model.txt")
  // cloud.printCloudTotal(10)

  // ================= Part (c) =================

  cloud.updateTFIDF()
  println("Max TFIDF for all files:")
  val tfidf = cloud.getTFIDF()
  // print out the max tfidf and word for each file
  for (file <- fileList) {
    val max = tfidf(file).maxBy(_._2)
    println(file.split("/").last + " " + max)
  }