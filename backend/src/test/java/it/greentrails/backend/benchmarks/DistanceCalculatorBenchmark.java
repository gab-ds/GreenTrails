package it.greentrails.backend.benchmarks;

import it.greentrails.backend.utils.DistanceCalculator;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.springframework.data.geo.Point;

@State(Scope.Thread)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class DistanceCalculatorBenchmark {

    private Point punto1;
    private Point punto2;

    @Setup
    public void setup() {
        // Roma
        punto1 = new Point(41.9028, 12.4964);
        // Milano
        punto2 = new Point(45.4642, 9.1900);
    }

    @Benchmark
    public void testDistanceCalculation(Blackhole blackhole) {
        double result = DistanceCalculator.distance(punto1, punto2);
        blackhole.consume(result);
    }

}

