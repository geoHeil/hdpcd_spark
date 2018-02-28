// Copyright (C) 2018
package hdpcdspark.getting_started

import org.apache.spark.sql.SQLContext
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

  val spark = new SparkContext(conf)
  val sql   = new SQLContext(spark)

  // Create an RDD

  // Create an RDD from a file or directory in HDFS

  spark.stop

}
