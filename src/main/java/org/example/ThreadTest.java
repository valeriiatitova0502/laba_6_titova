package org.example;

import java.util.Arrays;


public class ThreadTest {
    static final int size = 14;  // Размер массива
    static final int half = size / 2;    // Половина размера массива

    public void methodOne() {
        float[] array = new float[size];
        Arrays.fill(array, 1.0f);  // Заполняем массив значениями 1.0

        long time = System.currentTimeMillis();  // Засекаем текущее время

        // Выполняем вычисления для каждого элемента массива
        for (int i = 0; i < array.length; i++) {
            array[i] = (float) (array[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
        }

        System.out.println(array[0]);  // Выводим первый элемент массива
        System.out.println(array[array.length - 1]);  // Выводим последний элемент массива

        System.out.println("Время выполнения первого метода: " + (System.currentTimeMillis() - time));  // Выводим затраченное время
    }

    public void methodTwo() {
        float[] array = new float[size];
        float[] firstHalf = new float[half];
        float[] secondHalf = new float[half];
        Arrays.fill(array, 1.0f);  // Заполняем массив значениями 1.0

        long time = System.currentTimeMillis();  // Засекаем текущее время

        // Разделяем массив на две половины
        System.arraycopy(array, 0, firstHalf, 0, half);
        System.arraycopy(array, half, secondHalf, 0, half);

        // Создаем два потока для вычислений
        Thread threadOne = new Thread(() -> {
            for (int i = 0; i < firstHalf.length; i++) {
                firstHalf[i] = (float) (firstHalf[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
            }
            System.arraycopy(firstHalf, 0, array, 0, firstHalf.length);
        });

        Thread threadTwo = new Thread(() -> {
            for (int i = 0; i < secondHalf.length; i++) {
                secondHalf[i] = (float) (secondHalf[i] * Math.sin(0.2f + (half + i) / 5) * Math.cos(0.2f + (half + i) / 5) * Math.cos(0.4f + (half + i) / 2));
            }
            System.arraycopy(secondHalf, 0, array, half, secondHalf.length);
        });

        threadOne.start();
        threadTwo.start();

        try {
            threadOne.join();
            threadTwo.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(array[0]);  // Выводим первый элемент массива
        System.out.println(array[array.length - 1]);  // Выводим последний элемент массива

        System.out.println("Время выполнения второго метода: " + (System.currentTimeMillis() - time));  // Выводим затраченное время
    }

    public void methodThree(int n) {
        float[] array = new float[size];
        Arrays.fill(array, 1.0f);

        long time = System.currentTimeMillis();

        Thread[] threads = new Thread[n];
        float[][] results = new float[n][];
        int partSize = size / n;

        float[] matrixArray = new float[size];
        String[] matrixArrayInBrackets = new String[n]; // Изменено здесь

        for (int i = 0; i < n; i++) {
            final int startIndex = i * partSize;
            final int endIndex = (i == n - 1) ? size : startIndex + partSize;
            final int index = i;
            final int threadIndex = i;

            threads[i] = new Thread(() -> {
                float[] subArray = Arrays.copyOfRange(array, startIndex, endIndex);
                for (int j = 0; j < subArray.length; j++) {
                    subArray[j] = (float) (subArray[j] * Math.sin(0.2f + (startIndex + j) / 5) * Math.cos(0.2f + (startIndex + j) / 5) * Math.cos(0.4f + (startIndex + j) / 2));
                }

                StringBuilder sb = new StringBuilder();
                sb.append('[');
                for (int j = 0; j < subArray.length; j++) {
                    if (j > 0) sb.append(", ");
                    sb.append(subArray[j]);
                }
                sb.append(']');

                matrixArrayInBrackets[threadIndex] = sb.toString();
                results[index] = subArray;
                System.arraycopy(subArray, 0, matrixArray, startIndex, subArray.length);

            });

            threads[i].start();
        }

        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Матрица перед объединением: " + Arrays.toString(matrixArrayInBrackets));

        for (int i = 0; i < n; i++) {
            System.arraycopy(results[i], 0, array, i * partSize, results[i].length);
        }

        System.out.println(array[0]);
        System.out.println(array[array.length - 1]);

        System.out.println("Время третьего метода: " + (System.currentTimeMillis() - time));
    }
}