package crawler

import org.jsoup._
import org.jsoup.select.Elements
import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer

def excludedTags = Set(
    ":", "Main_Page", "ISSN", "Doi", "ISBN", "SUDOC", 
  "Help", "Special", "Talk", "User", "User_talk", "Wikipedia", "Wikipedia_talk", 
  "File", "File_talk", "MediaWiki", "MediaWiki_talk", "Template", "Template_talk", "Help", 
  "Help_talk", "Category", "Category_talk", "Portal", "Wikipedia", "Wikipedia_talk", "Book", 
  "Book_talk", "Draft", "Draft_talk", "Education_Program", "Education_Program_talk", "TimedText", 
  "TimedText_talk", "Module", "Module_talk", "Gadget", "Gadget_talk", "Gadget_definition", 
  "Gadget_definition_talk", "Topic"
)

class Crawler(pageRoot: String){

    def containsExcludedTag(href: String): Boolean =
        {
            excludedTags.exists(href.contains(_))
        }

    def createFolder(path: String): String = 
        {
            val dir = new java.io.File(path)
            if (!dir.exists()) dir.mkdirs()
            path
        }
    
    def scrapLinks(pagePath: String): List[String] = 
        {
            var relativePage = pagePath
            if (pagePath.startsWith(pageRoot)) relativePage = pagePath.replace(pageRoot, "")
            
            val doc = Jsoup.connect(pageRoot + relativePage).get()
            val links = doc.select("a[href]")

            val outLinks = links
            .toArray()
            .map(link => link.asInstanceOf[org.jsoup.nodes.Element].attr("abs:href"))
            .filter(link => link.startsWith(pageRoot))
            .map(link => link.replace(pageRoot, ""))
            .filterNot(containsExcludedTag(_))
            .map(link => link.split("#")(0))
            
            outLinks.toList.distinct
        }

    // def scrapLinksWithWords(pagePath: String): (List[String], List[String]) = {
    //     var relativePage = pagePath
    //     if (pagePath.startsWith(pageRoot)) relativePage = pagePath.replace(pageRoot, "")
        
    //     val doc = Jsoup.connect(pageRoot + relativePage).get()
    //     val links = doc.select("a[href]")
    //     val words = doc.body().text().split("\\W+").map(_.toLowerCase()).toList

    //     val outLinks = links
    //     .toArray()
    //     .map(link => link.asInstanceOf[org.jsoup.nodes.Element].attr("abs:href"))
    //     .filter(link => link.startsWith(pageRoot))
    //     .map(link => link.replace(pageRoot, ""))
    //     .filterNot(containsExcludedTag(_))
    //     .map(link => link.split("#")(0))
        
    //     (outLinks.toList.distinct, words)
    // }

    // tail recursive function to crawl the web, given maximal number of pages to crawl
    // goes in horizontal direction, that is it first crawls all the pages in the same level
    @tailrec
    final def crawlHorizontal(startingPages: List[String], visitedPages: List[String], edges: List[(String,String)], maxPages: Int): List[(String, String)] = 
    {
        println(maxPages.toString() + " pages left to crawl...")
        if (maxPages > 0 && startingPages.nonEmpty) then
            {
                val links = scrapLinks(startingPages.head)
                val newPages = links.filterNot(visitedPages.contains(_)).filterNot(startingPages.contains(_))
                newPages.foreach(link => link.replace(pageRoot,""))
                val newEdges = newPages.map(page => (startingPages.head.replace(pageRoot,""), page)).filterNot(edges.contains(_))
                crawlHorizontal((startingPages.tail ++ newPages).take(maxPages),
                                visitedPages ++ startingPages,
                                edges ++ newEdges,
                                maxPages - 1)   
            }
        else edges
    }
    

    // @tailrec
    // final def crawlHorizontalWithWords(startingPages: List[String], visitedPages: List[String], edges: List[(String,String)], words: List[(String, String)], maxPages: Int): (List[(String, String)], List[(String, String)]) = 
    // {
    //     println(maxPages.toString() + " pages left to crawl...")
    //     if (maxPages > 0 && startingPages.nonEmpty) then
    //         {
    //             val links = scrapLinks(startingPages.head)
    //             val newPages = links.filterNot(visitedPages.contains(_)).filterNot(startingPages.contains(_))
    //             newPages.foreach(link => link.replace(pageRoot,""))
    //             val newEdges = newPages.map(page => (startingPages.head.replace(pageRoot,""), page)).filterNot(edges.contains(_))
    //             crawlHorizontalWithWords((startingPages.tail ++ newPages).take(maxPages),
    //                             visitedPages ++ startingPages,
    //                             edges ++ newEdges,
    //                             maxPages - 1)   
    //         }
    //     else edges
    // }
    

    
}

