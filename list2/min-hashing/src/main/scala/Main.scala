import scala.io.Source
import java.io.File
import scala.util.Random
import java.io.BufferedWriter
import java.io.FileWriter


def generateShingles(text: String, k: Int): List[String] = {
  text.sliding(k).toList.distinct
}

// given a list of sets of shingles, generate a list of lists of minhashes
def minHashSig(
    shingles: List[List[String]],
    nHashes: Int
): List[List[Int]] = {
  val allShingles = shingles.flatten.toList.distinct
  val seeds = List.range(0, nHashes).map(_ => Random.nextInt(1000000))
  shingles.map(shingleSet => seeds.map(s => {
      Random.setSeed(s)
      val hash = Random.shuffle(List.range(0, allShingles.size))
      hash.takeWhile(h => !shingleSet.contains(allShingles(h))).size
    }).toList
  )

}

// given two minhash signatures, compute the Jaccard similarity as the probability of two hashes being the same
def jaccardSimilarity(
    minHashSig1: List[Int],
    minHashSig2: List[Int]
): Double = {
  val nHashes = minHashSig1.size
  val nSame = minHashSig1.zip(minHashSig2).count(x => x._1 == x._2)
  nSame.toDouble / nHashes
}

@main def minHashing: Unit =
  val pathToBooks = "/home/jakubp/Code/Studies/semester_2/big_data_algorithms/list1/tfidf/books/"
  val pathToStopWords = pathToBooks + "stopwords.txt"
  
  val fileList = new File(pathToBooks + "similar_topics").listFiles
  .filter(_.isFile)
  .toList
  .map(_.getPath)
  .take(5) ++ new File(pathToBooks + "different_topics").listFiles
  .filter(_.isFile)
  .toList
  .map(_.getPath)
  .take(5)
  
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
  val nHashes = List(10, 100, 250, 500)

  println("Generating shingles...")
  val shinglesSets =
    shingleSizes.map(k => (k, texts.map(generateShingles(_, k))))
  
  val nkPairs = shingleSizes.flatMap(k => nHashes.map(n => (k, n)))
  
  println("Generating minhash signatures...")
  val minHashSigs = nkPairs.map((k, n) =>
    println("k = " + k + ", n = " + n)
    val shingles = shinglesSets.filter(_._1 == k).head._2
    (k, n, minHashSig(shingles, n))
  )


  println("Computing Jaccard similarities...")

  val jaccardSims = nkPairs.map((k, n) => {
    val minHashSig = minHashSigs.filter(x => x._1 == k && x._2 == n).head._3
    val jSims = minHashSig.map(sig1 =>
      minHashSig.map(sig2 => jaccardSimilarity(sig1, sig2))
    )
    (k,n,jSims)
  })
  
  jaccardSims.foreach((k, n, jSims) => {
    println("k = " + k + ", n = " + n)
    jSims.foreach(row => {
      row.foreach(x => print(f"$x%1.6f" + " "))
      println()
    })
    println()
  })
  
  // val file = new File("jaccard_sims_"+ shingleSizes(0).toString() +".txt")
  val file = new File("jaccard_sims_5.txt")
  val bw = new BufferedWriter(new FileWriter(file))
  bw.write("The files are: ")
  bw.newLine()
  fileNames.foreach(f => {
    bw.write(f)
    bw.newLine()
  })
  bw.newLine()
  jaccardSims.foreach((k, n, jSims) => {
    bw.write("k = " + k + ", n = " + n)
    bw.newLine()
    jSims.foreach(row => {
      row.foreach(x => {
        bw.write(f"$x%1.6f" + " ")
      })
      bw.newLine()
    })
    bw.newLine()
  })
  bw.close()

