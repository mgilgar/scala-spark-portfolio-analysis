spark-submit --class org.miguel.WordCount ./target/scala-2.10/spark-example1_2.10-0.0.1.jar ./README.md ./wordCount



sbt clean package
ls -l target/scala-2.11/spark-example1_2.11-0.0.1.jar
spark-submit --class org.miguel.PortfolioAnalysis ./target/scala-2.11/spark-example1_2.11-0.0.1.jar
