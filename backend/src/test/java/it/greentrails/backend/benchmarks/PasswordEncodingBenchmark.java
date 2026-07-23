package it.greentrails.backend.benchmarks;

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
import org.openjdk.jmh.annotations.DynamicHalt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class PasswordEncodingBenchmark {

    private PasswordEncoder passwordEncoder;
    private String rawPassword;

    @Setup
    public void setup() {
        // Default strength is 10
        passwordEncoder = new BCryptPasswordEncoder();
        rawPassword = "passwordSuperSicura123!";
    }

    @Benchmark
    public void benchmarkPasswordEncoding(Blackhole bh) {
        String encoded = passwordEncoder.encode(rawPassword);
        bh.consume(encoded);
    }

    @Benchmark
    public void benchmarkPasswordMatching(Blackhole bh) {
        // Pre-encoded password
        String encoded = "$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRkgVduVfzCore3fakZ.OD.O/O6";
        boolean match = passwordEncoder.matches(rawPassword, encoded);
        bh.consume(match);
    }
}

