package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

public class Main {

    private static final int COUNTOFSTRINGS = 10_000;
    private static final int COUNTOFCHARS = 100_000;
    private static final char CHAR1 = 'a';
    private static final char CHAR2 = 'b';
    private static final char CHAR3 = 'c';
    private static final int LENGTHOFQUEUE = 100;
    private static ArrayBlockingQueue<String> queue1, queue2, queue3;
    private static List<Result> results; //Для хранения результатов анализа
    private static List<Thread> threads; //Для хранения потоков


    public static void main(String[] args) throws InterruptedException {

        queue1 = new ArrayBlockingQueue<>(LENGTHOFQUEUE);
        queue2 = new ArrayBlockingQueue<>(LENGTHOFQUEUE);
        queue3 = new ArrayBlockingQueue<>(LENGTHOFQUEUE);
        threads = new ArrayList<>();
        results = new ArrayList<>();


        //Заполнение очереди сгенерированными строками
        Runnable textGen = () -> {
            for (int i = 0; i < COUNTOFSTRINGS; i++) {
                String text = generateText("abc", COUNTOFCHARS);
                try {
                    queue1.put(text);
                    queue2.put(text);
                    queue3.put(text);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        Runnable analyzer1 = () -> {
            try {
                results.add(analyzer(queue1, threads.get(0), CHAR1));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };

        Runnable analyzer2 = () -> {
            try {
                results.add(analyzer(queue2, threads.get(0), CHAR2));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };

        Runnable analyzer3 = () -> {
            try {
                results.add(analyzer(queue3, threads.get(0), CHAR3));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };

        threads.add(new Thread(textGen));
        threads.add(new Thread(analyzer1));
        threads.add(new Thread(analyzer2));
        threads.add(new Thread(analyzer3));

        for (Thread thread : threads) { //Запускаем потоки
            thread.start();
        }

        for (Thread thread : threads) { // зависаем, ждём когда поток объект которого лежит в thread завершится
            thread.join();
        }


        //Вывод на консоль результатов
        for (Result result : results) {
            System.out.println("Максимальное количество символов " + result.getChr() + ": " + result.getAmountOfLetter());
            System.out.println(result.getString());
        }
    }


    //Анализатор строк
    public static Result analyzer(ArrayBlockingQueue<String> queue, Thread threadOfQueue, char chr) throws InterruptedException {
        int maxLetters = 0;
        String string = null;
        while (true) {
            String tmpTxt = queue.take();
            int tmp = countLetter(tmpTxt, chr);
            if (tmp >= maxLetters) {
                maxLetters = countLetter(tmpTxt, chr);
                string = tmpTxt;
            }

            if (!threadOfQueue.isAlive() & queue.isEmpty()) {
                return new Result(chr, string, maxLetters);
            }
        }
    }


    //Подсчет количества символов
    public static int countLetter(String string, char letter) {
        int countOfLetter = 0;
        for (char ch : string.toCharArray()) {
            if (ch == letter) countOfLetter++;
        }
        return countOfLetter;
    }


    //Генератор текста
    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }


}


