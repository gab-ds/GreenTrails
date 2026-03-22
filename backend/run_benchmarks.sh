#!/bin/bash
set -e

echo "=== Compilazione del progetto e dei test ==="
mvn clean test-compile

echo "=== Generazione del classpath ==="
mvn dependency:build-classpath -Dmdep.outputFile=cp.txt -DincludeScope=test

echo "=== Avvio dei Benchmark JMH ==="
java -cp target/test-classes:target/classes:$(cat cp.txt) it.greentrails.backend.benchmarks.BenchmarkRunner


