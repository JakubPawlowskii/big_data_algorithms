package page_rank

import scala.collection.parallel.CollectionConverters._
import scala.annotation.tailrec
import scala.io.Source
import scala.collection.parallel.ParSeq

// adjacency matrix of the graph, only the positions of 1's are stored

class PageRank(taxation: Float = 0.0, epsilon: Float = 0.0001, maxIter: Int = 1000) {
    
    type LinkMatrix = ParSeq[(Int,Int)]
    
    def readEdgeList(pathToEdges: String): Vector[(String, String,Int,Int)] = 
        {
            def invertEdge(line: String) = {
                val edge = line.split(",")
                (edge(1), edge(0))
            }
            val edges = Source.fromFile(pathToEdges).getLines().map(invertEdge(_)).toVector
            val nodes = (edges.map(_._1).distinct ++ edges.map(_._2).distinct).distinct
            // val nodes = edges.map(_._1).toSet.union(edges.map(_._2).toSet).toSeq
            val nodeMap = nodes.zipWithIndex.toMap
            edges.map(x => (x._1, x._2, nodeMap(x._1), nodeMap(x._2)))
        }


    // matrix multiplication
    private def sumElements(rankVec: ParSeq[Float], P: LinkMatrix, outPage: Int): Option[Float] = 
        {
            val outLinks = P.filter(_._2 == outPage)
            if (outLinks.nonEmpty) then
                {
                    val res = rankVec(outPage)/outLinks.length
                    Some(res)
                }
            else None
        }
    
    // all pages connected to inPage
    private def findConnected(inPage: Int, P: LinkMatrix): ParSeq[Int] = 
        {
            P.filter(_._1 == inPage).map(_._2)
        }

    // check for convergence
    private  def isConverged(rankVec: ParSeq[Float], rankVecOld: ParSeq[Float], epsilon: Float): Boolean = 
        {
            val diff = rankVec.zip(rankVecOld).map(x => Math.pow(x._1 - x._2,2)).sum
            Math.sqrt(diff/rankVec.size) <= epsilon
        }
    
    @tailrec final def calculatePageRank(rankVec: ParSeq[Float], P: LinkMatrix, iter: Int = 0): ParSeq[Float] = {
        println("Iteration : " + iter.toString())
        val rankIdx: Seq[Int] = 0 until rankVec.size
        val rightRank: ParSeq[Float] = rankIdx.map(i => {
            val connected = findConnected(i,P)
            connected.map(j => sumElements(rankVec, P, j) match
                case Some(x) => x
                case None => 0.0f).sum
        }).par
        val newRank: ParSeq[Float] = rightRank.map(taxation*_ + (1.0f-taxation)/rankVec.size)
        if (isConverged(newRank, rankVec, epsilon) || iter >= maxIter) 
        then{
            if(iter >= maxIter) println("Max iterations reached")
            newRank
        } 
        else calculatePageRank(newRank, P, iter + 1)
    }

    
}
