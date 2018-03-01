#!/usr/bin/env bash
# Run a Spark job on YARN
sbt assembly
spark-submit --class hdpcdspark.getting_started.Ex1 --master yarn \
    --deploy-mode cluster \
    --driver-memory 4g \
    --executor-memory 2g \
    --executor-cores 4 \
    --queue myQueue \
    --conf spark.executor.instances=5
    target/scala-2.11/hdpcd_spark.jar