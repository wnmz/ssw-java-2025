package org.parkov;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        int threadCount = 5;
        int calculationTime = 2;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        StringBuilder[] progressBars = new StringBuilder[threadCount];
        for (int i = 0; i < threadCount; i++) {
            progressBars[i] = new StringBuilder("[                    ]");
        }

        for (int i = 1; i <= threadCount; i++) {
            int threadNumber = i;
            int finalI = i;
            executor.submit(() -> simulateCalculation(
                    threadNumber,
                    calculationTime * finalI,
                    progressBars)
            );
        }

        new Thread(() -> {
            while (!executor.isTerminated()) {
                synchronized (System.out) {
                    System.out.print("\033[H\033[2J");
                    System.out.flush();
                    for (int i = 0; i < threadCount; i++) {
                        System.out.printf("Поток %d: %s%n", i + 1, progressBars[i]);
                    }
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
    }

    private static void simulateCalculation(int threadNumber, int calculationTime, StringBuilder[] progressBars) {
        String threadId = Thread.currentThread().getName();
        System.out.printf("Поток %d (ID: %s) начал выполнение.%n", threadNumber, threadId);

        int progressBarLength = progressBars[threadNumber - 1].length() - 2;

        for (int i = 1; i <= progressBarLength; i++) {
            try {
                TimeUnit.MILLISECONDS.sleep((calculationTime * 1000L) / progressBarLength);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.printf("Поток %d был прерван.%n", threadNumber);
                return;
            }

            synchronized (progressBars[threadNumber - 1]) {
                progressBars[threadNumber - 1].setCharAt(i, '=');
            }
        }

        System.out.printf("%nПоток %d завершил выполнение. Затраченное время: %d секунд.%n",
                threadNumber, calculationTime);
    }
}
