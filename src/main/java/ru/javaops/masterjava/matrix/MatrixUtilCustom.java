package ru.javaops.masterjava.matrix;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

/**
 * gkislin
 * 03.07.2016
 */
@SuppressWarnings("Duplicates")
public class MatrixUtilCustom {

    // TODO implement parallel multiplication matrixA*matrixB
    public static int[][] concurrentMultiply(
            final int[][] matrixA,
            final int[][] matrixB,
            final ExecutorService executor) throws InterruptedException, ExecutionException {

        if (matrixA == null || matrixB == null) return new int[0][0];

        final int aLengthV = matrixA.length;
        final int bLengthV = matrixB.length;
        if (aLengthV == 0 || bLengthV == 0) return new int[0][0];

        final int bLengthH = matrixB[0].length;
        if (bLengthH == 0 || matrixA[0].length != bLengthV) return new int[0][0];

        final int[][] matrixC = new int[aLengthV][bLengthH];
        final int[][] matrixBReverse = new int[bLengthH][bLengthV];
        final CountDownLatch cdl = new CountDownLatch(aLengthV);

        for (int i = 0; i < bLengthH; i++) {
            for (int j = 0; j < bLengthV; j++) {
                matrixBReverse[i][j] = matrixB[j][i];
            }
        }

        for (int i = 0; i < aLengthV; i++) {
            final int finalI = i;
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < bLengthH; j++) {
                        int sum = 0;
                        for (int k = 0; k < bLengthV; k++) {
                            sum += matrixA[finalI][k] * matrixBReverse[j][k];
                        }
                        matrixC[finalI][j] = sum;
                        if (j == bLengthH - 1) cdl.countDown();
                    }
                }
            });
        }

        cdl.await();

        return matrixC;
    }

    // TODO optimize by https://habrahabr.ru/post/114797/
    public static int[][] singleThreadMultiply(final int[][] matrixA, final int[][] matrixB) {
        if (matrixA == null || matrixB == null) return new int[0][0];

        final int aLengthV = matrixA.length;
        final int bLengthV = matrixB.length;
        if (aLengthV == 0 || bLengthV == 0) return new int[0][0];

        final int bLengthH = matrixB[0].length;
        if (bLengthH == 0 || matrixA[0].length != bLengthV) return new int[0][0];

        final int[][] matrixC = new int[aLengthV][bLengthH];
        final int[][] matrixBReverse = new int[bLengthH][bLengthV];

        for (int i = 0; i < bLengthH; i++) {
            for (int j = 0; j < bLengthV; j++) {
                matrixBReverse[i][j] = matrixB[j][i];
            }
        }

        for (int i = 0; i < aLengthV; i++) {
            for (int j = 0; j < bLengthH; j++) {
                int sum = 0;
                for (int k = 0; k < bLengthV; k++) {
                    sum += matrixA[i][k] * matrixBReverse[j][k];
                }
                matrixC[i][j] = sum;
            }
        }

        return matrixC;
    }

    public static int[][] create(int size) {
        int[][] matrix = new int[size][size];
        Random rn = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rn.nextInt(10);
            }
        }
        return matrix;
    }

    public static boolean compare(final int[][] matrixA, final int[][] matrixB) {
        return Arrays.deepEquals(matrixA, matrixB);
    }
}
