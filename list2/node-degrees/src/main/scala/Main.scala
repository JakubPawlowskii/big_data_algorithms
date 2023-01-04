import scala.io.Source

// map function to calculate the number of in and out edges given edge list
def mapFun(edge: List[Int]): List[(Int, Int, Int)] = {
  val out = edge(0)
  val in = edge(1)
  List((in, 1, 0), (out, 0, 1))
}

// this can be used as .reduce(reduceFun) but it is very slow
// def reduceFun(
//     a: List[(Int, Int, Int)],
//     b: List[(Int, Int, Int)]
// ): List[(Int, Int, Int)] = {
//   val mapped = b.map(node => {
//     val nodeID = node._1
//     val in = node._2
//     val out = node._3
//     val idx = a.indexWhere(_._1 == nodeID)
//     if (idx == -1) {
//       (nodeID, in, out)      
//     } else {
//       val oldIn = a(idx)._2
//       val oldOut = a(idx)._3
//       (nodeID, in + oldIn, out + oldOut)
//     }
//   })
//   mapped ++ a.filterNot(node => mapped.map(_._1).contains(node._1))
// }


def reduceFun(v: (Int, List[(Int,Int,Int)])): (Int,Int,Int) = {
  v._2.reduce((a,b)=>(a._1,a._2+b._2,a._3+b._3))
}

@main def main: Unit =

  // val filename = "src/main/scala/test-graph.txt"
  val filename = "src/main/scala/web-Stanford.txt"
  println("Loading graph from file: " + filename)
  val file = Source.fromFile(filename)
  val lines = file.getLines.toList
  val edges = lines.map(_.split("\\s+").map(_.toInt).toList)
  file.close()

  println("There are " + edges.length + " edges in the graph.")

  println("Running map...")
  val mapped = edges.flatMap(mapFun).groupBy(_._1)
  println("Running reduce...")
  val reduced = mapped.map(reduceFun)
  
  println("Top 10 nodes with highest in degree:")
  reduced.toList.sortBy(_._2).reverse.take(10).foreach(println)

  println("Top 10 nodes with highest out degree:")
  reduced.toList.sortBy(_._3).reverse.take(10).foreach(println)


  // val reduced = mapped.reduce(reduceFun)
  // println("Reduced:")
  // reduced.take(10).foreach(println)


