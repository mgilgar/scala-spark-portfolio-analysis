package org.miguel

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

object WordCount {
	def main(args: Array[String]) = {
        val inputFile = args(0)
      	val outputFile = args(1)	
		val conf = new SparkConf().setAppName("Miguel Test")
		val sc = new SparkContext(conf)
		val input = sc.textFile(inputFile)
		val words = input.flatMap(line => line.split(" "))
		val counts = words.map(word => (word, 1)).reduceByKey{ case (x, y) => x + y}
		counts.saveAsTextFile(outputFile)
	}
}
