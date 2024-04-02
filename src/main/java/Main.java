import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

public class Main {
    public static String letters = "abc";
    public static ArrayBlockingQueue<String> queue1 = new ArrayBlockingQueue<>(100);
    public static ArrayBlockingQueue<String> queue2 = new ArrayBlockingQueue<>(100);
    public static ArrayBlockingQueue<String> queue3 = new ArrayBlockingQueue<>(100);
    public static Thread generateThread;
    public static void main(String[] args) throws InterruptedException {
        List<ArrayBlockingQueue<String>> queueList = new ArrayList<>();
        queueList.add(queue1);
        queueList.add(queue2);
        queueList.add(queue3);

        generateThread = new Thread(() -> {
            String text = generateText(letters, 100_000);
            for (int i = 0; i < 10_000; i++) {
                try {
                    for(ArrayBlockingQueue<String> queue: queueList) {
                        queue.put(text);
                    }
                } catch (InterruptedException e) {
                    return;
                }
            }
        });
        generateThread.start();

        List<Thread> threadList = new ArrayList<>();

        for (int i = 0; i < letters.length(); i++) {
            threadList.add(getThread(queueList.get(i), letters.charAt(i)));
        }

        for (Thread thread: threadList) {
            thread.start();
        }

        for (Thread thread: threadList) {
            thread.join();
        }
    }
    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static Thread getThread(ArrayBlockingQueue<String> queue, char letter) {
        return new Thread(() -> {
            int max = findMax(queue, letter);
            System.out.printf("Максимальное количество буквы %s в тексте: %d\n", letter, max);
        });
    }

    public static int findMax(ArrayBlockingQueue<String> queue, char letter) {
        int count = 0;
        int max = 0;
        try {
            while (generateThread.isAlive()) {
                String text = queue.take();
                for (int i = 0; i < text.length(); i++) {
                    if (text.charAt(i) == letter) count++;
                }
                max = Math.max(count, max);
                count = 0;
            }
        } catch (InterruptedException e) {
            return -1;
        }
        return max;
    }
}
