package ru.hh.school.parallelism;

import com.google.common.util.concurrent.Uninterruptibles;
import java.util.concurrent.Callable;
import org.apache.commons.lang3.time.StopWatch;
import org.openjdk.jmh.infra.Blackhole;
import ru.hh.school.parallelism.executor.ExecutorComputation;
import ru.hh.school.parallelism.fjp.ForkJoinPoolComputation;
import ru.hh.school.parallelism.sequential.SequentialComputation;
import ru.hh.school.parallelism.stream.ParallelStreamComputation;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class Runner {

  private static int CYCLES = 1_000;
  private static int CPU_CYCLES = 10_000_000;

  public static void main(String[] args) throws Exception {
    System.out.println("Started");

    // run sequential
    measure("Sequential", () -> new SequentialComputation().compute(CYCLES));
    System.gc();

    // run executor
    measure("Executor", () -> new ExecutorComputation().compute(CYCLES));
    System.gc();

    // run fjp
    measure("ForkJoinPool", () -> new ForkJoinPoolComputation().compute(CYCLES));
    System.gc();

    // run parallel stream
    measure("Parallel stream", () -> new ParallelStreamComputation().compute(CYCLES));
    System.gc();
  }

  private static void measure(String name, Callable<Long> computation) throws Exception {
    StopWatch stopWatch = StopWatch.createStarted();

    long result = computation.call();

    stopWatch.stop();

    System.out.println(format("%s: (%d) in %s", name, result, stopWatch.toString()));
  }

  public static long performCPUJob() {
    Blackhole.consumeCPU(CPU_CYCLES);
    return 100;
  }

  public static long performIOJob() {
    Uninterruptibles.sleepUninterruptibly(5, MILLISECONDS);
    return 200;
  }
}
