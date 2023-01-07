import scala.io.Source
import java.io.File
import java.io.BufferedWriter
import java.io.FileWriter

// generate set of k-shingles for given text
def generateShingles(text: String, k: Int): Set[String] = {
  text.sliding(k).toSet
}

// calculate jaccard similarity between two sets
def jaccardSimilarity(set1: Set[String], set2: Set[String]): Double = {
  val intersection = set1.intersect(set2).size
  val union = set1.union(set2).size
  intersection.toDouble / union.toDouble
}



@main def exactJaccard: Unit = 
  val pathToBooks = "/home/jakubp/Code/Studies/semester_2/big_data_algorithms/list1/tfidf/books/"
  val pathToStopWords = pathToBooks + "stopwords.txt"
  
  val fileList = new File(pathToBooks + "similar_topics").listFiles
  .filter(_.isFile)
  .toList
  .map(_.getPath)
  .take(3) ++ new File(pathToBooks + "different_topics").listFiles
  .filter(_.isFile)
  .toList
  .map(_.getPath)
  .take(3)
  
  val fileNames = fileList.map(w => w.split("/").last)
  println("The files are: ")
  fileNames.foreach(println)
  
  val stopwords = Source.fromFile(pathToStopWords).getLines.toSet
  val texts = fileList.map(f => 
    Source.fromFile(f)
    .getLines
    .mkString(" ")
    .replaceAll("[^\\s\\w]", "")
    .split("\\s+")
    .map(_.toLowerCase)
    .filterNot(stopwords.contains)
    .toList
    .mkString(" ")
    )

  val shingleSizes = List(3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14)
  val jaccardSimilarities = shingleSizes.map(k => {
    val shingles = texts.map(generateShingles(_, k))
    val jaccardMatrix = shingles.map(s1 => shingles.map(s2 => jaccardSimilarity(s1, s2)))
    jaccardMatrix
  })
  
  jaccardSimilarities.zip(shingleSizes).foreach(j => {
    println("Jaccard similarities for shingle size: " + j._2)
    j._1.foreach(row => {
      row.foreach(col => print(f"$col%1.6f "))
      println()
    })
    println()
  })

  // save to file
  val file = new File("jaccard_similarities.txt")
  val bw = new BufferedWriter(new FileWriter(file))
  // write file list to file
  bw.write("The files are: ")
  bw.newLine()
  fileNames.foreach(f => {
    bw.write(f)
    bw.newLine()
  })
  bw.newLine()
  jaccardSimilarities.zip(shingleSizes).foreach(j => {
    bw.write("Jaccard similarities for shingle size: " + j._2)
    bw.newLine()
    j._1.foreach(row => {
      row.foreach(col => bw.write(f"$col%1.6f "))
      bw.newLine()
    })
    bw.newLine()
  })
  bw.close()



