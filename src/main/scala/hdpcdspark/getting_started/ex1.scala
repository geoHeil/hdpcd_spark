// Copyright (C) 2018
package hdpcdspark.getting_started

import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.storage.StorageLevel
import org.apache.spark.{ SparkConf, SparkContext }

/**
 * Write a Spark Core application in Python or Scala
 * Initialize a Spark application
 */
object Ex1 extends App {
  println("simple spark application")

  val conf = new SparkConf()
    .setAppName("myApp")
    .setIfMissing("spark.master", "local[4]")
    // Configure Spark properties
    .setIfMissing("spark.driver.memory", "10G")

  val spark = new SparkContext(conf)

  // Write a Spark SQL application
  val sql = new SQLContext(spark)

  // Create an RDD
  final case class Foo(bar: Int)
  val foo    = Seq(Foo(1), Foo(2))
  val fooRDD = spark.parallelize(foo)
  sql.createDataFrame(fooRDD).show

  // Create an RDD from a file or directory in HDFS
  val file = "./src/test/resources/mobydick"
  val r    = spark.textFile(file)
  r.take(1)
  r.filter(line => line.contains("a")).count

  // Persist an RDD in memory or on disk
  val rp = r.persist(StorageLevel.MEMORY_ONLY)
  rp.filter(line => line.contains("b")).count
  rp.filter(line => line.contains("c")).count

  // Perform Spark transformations on an RDD
  rp.flatMap(line => line.split(" "))
    .map(word => (word, 1))
    .reduceByKey(_ + _)
    // Perform Spark actions on an RDD
    .take(3)

  // Create and use broadcast variables and accumulators
  val myBroadCastStuff = 1
  val br               = spark.broadcast(myBroadCastStuff)
  println(br.value)

  val acc = spark.accumulator(0, "foo")
  spark
    .parallelize(Array(1, 2, 3, 4))
    .map(value => value + br.value)
    .foreach(x => acc += x)
  println(acc)

  spark.parallelize(Array(1, 2, 3, 4)).sum.toInt

  // Create Spark DataFrames from an existing RDD
  import sql.implicits._
  sql.createDataFrame(fooRDD)
  r.toDF.show

  import org.apache.spark.sql.functions._
  val d = sql.read.json("./src/test/resources/gb_books.json.gz")
  d.printSchema
  d.show

  // Perform operations on a DataFrame
  d.count
  d.groupBy('author).agg(avg('downloads)).show
  d.sort().head
  d.select($"formats.text/html").show

//  Use Hive with ORC from Spark SQL
  val hive = new HiveContext(spark)
  d.write.saveAsTable("my_hive_table")
//  Write a Spark SQL application that reads and writes data from Hive tables
  hive.sql("SELECT * FROM my_hive_table").show
  spark.stop

}
