package com.jrsoft.learning.microservices.threading.basics;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BasicsThreadingTest {

    @Test
    public void references() {

        Runnable run = this::display;

        new Thread(run).start();

    }

    void display() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    @Test
    public void threads() throws InterruptedException {
        List<Thread> pool = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            Runnable run = this::display;
            Thread t = new Thread(run);
            pool.add(t);
            t.start();
            Thread.sleep(10);
        }
    }

    public String messag() {
        return "hey duke" + System.currentTimeMillis();
    }

    @Test
    public void callabel() throws ExecutionException, InterruptedException {
        Callable<String> messagProvider = this::messag;
        ExecutorService threadPool = Executors.newFixedThreadPool(10);
        List<Future<String>> futures = new ArrayList<>();

        for (int i = 0; i<10 ; i++) {
            Future<String> futureResult = threadPool.submit(messagProvider);
            futures.add(futureResult);
        }

        for (Future<String> future : futures) {
            String result = future.get();
            System.out.println("result = " + result);
        }

    }

    @Test
    public void backpressure() {
        BlockingQueue<Runnable> queue = new LinkedBlockingDeque<>(2);

        ThreadPoolExecutor thredPool = new ThreadPoolExecutor(
                1,
                1,
                60,
                TimeUnit.SECONDS,
                queue, this::onOverload
        );
        long start = System.currentTimeMillis();
        thredPool.submit(this::display);
        duration(start);

        thredPool.submit(this::display);
        duration(start);

        thredPool.submit(this::display);
        duration(start);

        thredPool.submit(this::display);
        duration(start);
    }

    public void duration(long start) {
        System.out.println("-- took: " + (System.currentTimeMillis() - start));
    }

    public void onOverload(Runnable r, ThreadPoolExecutor executor) {
        System.out.println("-- runnable " + r + " executor " + executor.getActiveCount());
    }

    @Test
    public void threadPool() throws InterruptedException {
        final ExecutorService tp = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 10000; i++) {
            Runnable run = this::display;
            Thread t = new Thread(run);
            tp.submit(t);
            t.start();
            Thread.sleep(10);
        }
    }
}
