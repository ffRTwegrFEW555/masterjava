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

        final int[][] matrixC = new int[aLengthV][0];
        final CountDownLatch cdl = new CountDownLatch(aLengthV);

        for (int i = 0; i < aLengthV; i++) {
            final int finalI = i;
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    final int[] tempArray = new int[bLengthH];
                    for (int j = 0; j < bLengthV; j++) {
                        final int n = matrixA[finalI][j];
                        for (int l = 0; l < bLengthH; l++) {
                            tempArray[l] += n * matrixB[j][l];
                        }
                    }
                    matrixC[finalI] = tempArray;
                    cdl.countDown();
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

        final int[][] matrixC = new int[aLengthV][0];

        for (int i = 0; i < aLengthV; i++) {
            final int[] tempArray = new int[bLengthH];
            for (int j = 0; j < bLengthV; j++) {
                final int n = matrixA[i][j];
                for (int l = 0; l < bLengthH; l++) {
                    tempArray[l] += n * matrixB[j][l];
                }
            }
            matrixC[i] = tempArray;
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
