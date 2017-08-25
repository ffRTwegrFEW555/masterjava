package ru.javaops.masterjava.matrix;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Vadim Gamaliev <a href="mailto:gamaliev-vadim@yandex.com">gamaliev-vadim@yandex.com</a>
 */
@Warmup(iterations = 5)
@Measurement(iterations = 5)
@BenchmarkMode({Mode.SingleShotTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Threads(1)
@Fork(1)
@Timeout(time = 5, timeUnit = TimeUnit.MINUTES)
public class MatrixBenchmark {

    private static final int MATRIX_SIZE = 1500;
    private static final int[][] MATRIX_A = MatrixUtilOriginal.create(MATRIX_SIZE);
    private static final int[][] MATRIX_B = MatrixUtilOriginal.create(MATRIX_SIZE);
    private static final int[][] MATRIX_C = MatrixUtilOriginal.singleThreadMultiply(MATRIX_A, MATRIX_B);

    private static final int THREAD_NUMBER = 10;
    private final static ExecutorService EXECUTOR = Executors.newFixedThreadPool(THREAD_NUMBER, r -> {
        Thread t = Executors.defaultThreadFactory().newThread(r);
        t.setDaemon(true);
        return t;
    });


    /*
        Runs
     */

    public static void main(final String[] args) throws RunnerException {
        final Options options = new OptionsBuilder()
                .include(MatrixBenchmark.class.getSimpleName())
                .build();
        new Runner(options).run();
    }


    /*
        Benchmarks
     */

    /*@Benchmark
    public int[][] testOriginalSingle() throws ExecutionException, InterruptedException {
        final int[][] matrixC = MatrixUtilOriginal.singleThreadMultiply(MATRIX_A, MATRIX_B);
        if (!MatrixUtilCustom.compare(MATRIX_C, matrixC)) throw new RuntimeException("Not equals.");
        return matrixC;
    }*/

    /*@Benchmark
    public int[][] testCustomSingle() throws ExecutionException, InterruptedException {
        final int[][] matrixC = MatrixUtilCustom.singleThreadMultiply(MATRIX_A, MATRIX_B);
        if (!MatrixUtilCustom.compare(MATRIX_C, matrixC)) throw new RuntimeException("Not equals.");
        return matrixC;
    }*/

    @Benchmark
    public int[][] testCustomConcurrent() throws ExecutionException, InterruptedException {
        final int[][] matrixC = MatrixUtilCustom.concurrentMultiply(MATRIX_A, MATRIX_B, EXECUTOR);
        if (!MatrixUtilCustom.compare(MATRIX_C, matrixC)) throw new RuntimeException("Not equals.");
        return matrixC;
    }

/*    @Benchmark
    public int[][] testConcurrentMultiplyStreams() throws ExecutionException, InterruptedException {
        final int[][] matrixC = MatrixUtilColleagues.concurrentMultiplyStreams(MATRIX_A, MATRIX_B, EXECUTOR);
        if (!MatrixUtilCustom.compare(MATRIX_C, matrixC)) throw new RuntimeException("Not equals.");
        return matrixC;
    }

    @Benchmark
    public int[][] testConcurrentMultiply2() throws ExecutionException, InterruptedException {
        final int[][] matrixC = MatrixUtilColleagues.concurrentMultiply2(MATRIX_A, MATRIX_B, EXECUTOR);
        if (!MatrixUtilCustom.compare(MATRIX_C, matrixC)) throw new RuntimeException("Not equals.");
        return matrixC;
    }*/

    @Benchmark
    public int[][] testConcurrentMultiply3() throws ExecutionException, InterruptedException {
        final int[][] matrixC = MatrixUtilColleagues.concurrentMultiply3(MATRIX_A, MATRIX_B, EXECUTOR);
        if (!MatrixUtilCustom.compare(MATRIX_C, matrixC)) throw new RuntimeException("Not equals.");
        return matrixC;
    }
}
