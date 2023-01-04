import org.apache.spark.sql.SparkSession

object SparkWordCloud {

  case class cloud_element(word: String, count: Int)

  def main(args: Array[String]): Unit = {

    /** Creating a Spark session */
    val spark = SparkSession
      .builder()
      .master("local[1]")
      .appName("Spark Application")
      .getOrCreate()

    import spark.implicits._

    val filePath = "src/main/resources/dune.txt"
    val stopWordsPath = "src/main/resources/stopwords.txt"

    /** Reading the text file */
    val textFile = spark.read.text(filePath)
    val stopWords = spark.read.textFile(stopWordsPath)


    textFile.show()

    textFile.flatMap(_.getString(0).split(" "))
      .filter(_.length > 3)
      .filter(!stopWords.collect().contains(_))
      .map(word => (word, 1))
      .toDF("word", "count")
      .groupBy("word")
      .sum("count")
      .orderBy($"sum(count)".desc)
      .show()

    /** Splitting the text file into words */
    // val words = textFile
    //   .flatMap(_.split(" "))
    //   .map(_.replaceAll("[^a-zA-Z]", "").toLowerCase)
    //   .filter(_.length > 0)
    //   .filter(!stopWords.collect().contains(_))

    // words.foreach(println)

    // words.show()
    /** Counting the words */
    // val wordCounts = words
    //   .groupBy("value")
    //   .count()
    //   .withColumnRenamed("value", "word")
    //   .as[cloud_element]


// /** Extract data into Spark application */
// val filePath = "/home/user/desktop/students.csv"
// val fileDF = spark.read.csv(filePath)

// /** Iterate through records and map them accordingly into the case class */
// val processed_data = fileDF
//     .map(
//        row => students_case_class(
//     row(0).toString,
//        if (row(1).toString == "P") "Pass" else "Fail",
//        row(2) match {
//            case "NJ" => "New Jersey"
//            case "NY" => "New York"
//            case "CA" => "California"
//            case "CO" => "Colorado"
//            case "NE" => "Nebraska"
//            case "TX" => "Texas"
//        },
//       if (row(3).toString == "M") "Male" else "Female"
//    )
// )

// processed_data.show()

// /** Load processed data to a csv file*/
// processed_data.write.mode("overwrite").csv("/home/user/desktop/output")

    spark.stop()

  }

}
