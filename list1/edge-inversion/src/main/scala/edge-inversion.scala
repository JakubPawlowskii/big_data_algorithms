// import scala.collection.parallel.CollectionConverters._

def map(graphPart: List[(Int, List[Int])]): List[(Int, Int)] = {

  graphPart.flatMap((v, edges) => edges.map((_, v)))
}

def reduce(edges: List[(Int, Int)]): List[(Int, List[Int])] = {
  edges.groupBy(_._1).view.mapValues(_.map(_._2)).toList
}

@main def main: Unit = {

  val graph = List(
    (1, List(2, 3)),
    (3, List(1, 5, 2)),
    (2, List(5, 3)),
    (5, List(2))
  )
  println(graph)


  val chunks = graph.grouped(2).toList
  // println(chunks)
  // println(chunks.map(map))
  // println(chunks.map(map).map(reduce))
  // println(chunks.map(map).map(reduce).flatten)

  val reversed_graph = chunks
  .map(map)
  .map(reduce)
  .flatten
  .groupBy(_._1)
  .view
  .mapValues(v => v.flatMap(_._2).toList)
  .toList

  println(reversed_graph)
}
