import scala.io._
import page_rank._
import crawler._

import scala.util.hashing.MurmurHash3
import scala.collection.parallel.ParSeq
import scala.collection.parallel.CollectionConverters._

@main def pageRank(): Unit = {
  val wikipediaLink = "https://en.wikipedia.org/wiki/"
  
  println("Welcome to the page rank calculator!")
  println("To crawl a wikipedia article and calculate the page rank, enter 1")
  println("To calculate the page rank from collected data, enter 2")
  println("To perform link analysis, enter 3")
  val choice = scala.io.StdIn.readLine()

  if choice == "1" then
    {
   
      print("Enter the wikipedia article url to start scrapping: ")
      
      val pageURL = scala.io.StdIn.readLine()
      val crawler = new Crawler(wikipediaLink)
      val res = crawler.crawlHorizontal(List(pageURL), List(), List(), 5)
      val folder = crawler.createFolder("data/" + pageURL.replace(wikipediaLink, "").replace("/", "___"))
      
      val out = new java.io.PrintWriter(folder + "/links_hashed.txt")
      res.foreach(edge => 
        out.println(MurmurHash3.stringHash(edge._1).toString() + "," + MurmurHash3.stringHash(edge._2).toString()))
        out.close()
        
      val out2 = new java.io.PrintWriter(folder + "/links.txt")
      res.foreach(edge => 
      out2.println(edge._1 + "," + edge._2))
      out2.close()

      println("Total number of links: " + res.length.toString())
      println("Results saved in folder: " + folder)

  }
  else if choice=="2" then 
    {
      val folder = "data"
      val files = new java.io.File(folder).listFiles.filter(_.isDirectory).map(_.getName)
      println("Choose the folder to calculate page rank from:")
      files.zipWithIndex.foreach(x => println(x._2.toString() + " - " + x._1))
      val choice = scala.io.StdIn.readLine()
      val folderName = files(choice.toInt)
      
      // val pathToHashedEdges = folder + "/" + folderName + "/links_hashed.txt"
      val pathToEdges = folder + "/" + folderName + "/links.txt"
      
      val pageRank = new PageRank(0.85f, 1e-8, 100)
      
      val edgesFull = pageRank.readEdgeList(pathToEdges)
      val edgesString = edgesFull.map(x => (x._1, x._2))
      val edges = edgesFull.map(x => (x._3, x._4)).toSeq.par
      
      val parEdges = edgesFull.par
      val nodesFull: ParSeq[(String, Int)] = (parEdges.map(x=>(x._1, x._3)).distinct ++ parEdges.map(x=>(x._2, x._4)).distinct).distinct.toSeq
      // val nodesFull: ParSeq[(String, Int)] = parEdges.map(x => (x._1, x._3)).toSet.union(parEdges.map(x => (x._2, x._4)).toSet).toSeq
      val nodes = nodesFull.map(x => x._2)

      
      val initialPageRank: ParSeq[Float] = ParSeq.fill(nodes.size)(1.0f/nodes.size)
      
      // val edgesNoDeadEnds = pageRank.removeDeadEnds(edges, initialPageRank.toList.toSeq)
      // val res = pageRank.calculatePageRank(edgesNoDeadEnds._2, edgesNoDeadEnds._1)

      // val res = pageRank.restoreDeadEndsWithPageRank(edgesNoDeadEnds._1, finalPageRank)
      val res = pageRank.calculatePageRank(initialPageRank, edges)
      val norm = res.sum
      val normalizedPR = res.map(x => x/norm)
      val out3 = new java.io.PrintWriter(folder + "/" + folderName + "/page_rank.txt")

      // nodes.zip(res._2).foreach(x => out3.println(x._1.toString() + "," + x._2.toString()))
      nodesFull.map(x=>x._1).zip(normalizedPR).foreach(x => out3.println(x._1.toString() + "," + x._2.toString()))
      out3.close()        
      
    }
    else if choice=="3" then{
      val folder = "data"
      val files = new java.io.File(folder).listFiles.filter(_.isDirectory).map(_.getName)
      println("Choose the folder to do link analysis:")
      files.zipWithIndex.foreach(x => println(x._2.toString() + " - " + x._1))
      val choice = scala.io.StdIn.readLine()
      val folderName = files(choice.toInt)

      val pathToEdges = folder + "/" + folderName + "/links.txt"
      val pr = new PageRank()
      val edgesFull = pr.readEdgeList(pathToEdges)
      val edges = edgesFull.map(x => (x._2, x._1)).toSeq

      // averaga number of links per page
      val avgLinks = edges.groupBy(x => x._1).map(x => x._2.size).sum.toFloat/edges.groupBy(x => x._1).size.toFloat
      println("Average number of links per page: " + avgLinks.toString())

      // let the user choose a page to analyze
      var nodes = edgesFull.map(x => x._1).toList.distinct
      println("Choose a page to analyze:")
      
      var selection = "-1"
      
      while (selection != "q") {
        val selectionOfNodes = nodes.toList.take(20)
        nodes = nodes.drop(20)
        selectionOfNodes.zipWithIndex.foreach(x => println(x._2.toString() + " - " + x._1))
        println("or enter m to see more pages.")
        selection = scala.io.StdIn.readLine()
        if selection != "m" then {
          val node = selectionOfNodes(selection.toInt)
          println("You chose: " + node)
          val outLinks = edges.filter(x => x._1 == node).map(x => x._2)
          val inLinks = edges.filter(x => x._2 == node).map(x => x._1)
          println("Outgoing links: " + outLinks.size.toString())
          println("Incoming links: " + inLinks.size.toString())
          println("Outgoing links: " + outLinks.mkString(", "))
          println("Incoming links: " + inLinks.mkString(", "))
          println("Enter a new page to analyze or enter q to quit.")
          selection = scala.io.StdIn.readLine()
          if selection == "q" then {
            println("Bye!")
            
          }
        }

      }

    }
    if choice == "4" then{
      val folder = "data"
      val files = new java.io.File(folder).listFiles.filter(_.isDirectory).map(_.getName)
      println("Choose the folder to search:")
      files.zipWithIndex.foreach(x => println(x._2.toString() + " - " + x._1))
      val choice = scala.io.StdIn.readLine()
      val folderName = files(choice.toInt)

    }

} 
  
