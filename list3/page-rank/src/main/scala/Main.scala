import scala.io._
import crawler._

@main def pageRank(): Unit = 
  print("Enter the wikipedia article url to start scrapping: ")
  val pageURL = scala.io.StdIn.readLine()
  val crawler = new Crawler("https://en.wikipedia.org/wiki/")
  val res = crawler.crawlHorizontal(List(pageURL), List(), List(), 100)

  val out = new java.io.PrintWriter("edges.txt")
  res.foreach(edge => out.println(edge._1 + " -> " + edge._2))
  out.close()
