import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ParallelMinimalFinder implements MinimalFinder {
     private final int threadCount;
     private final List<int[]> minimalsAndIndexes;
     private final SimpleMinimalFinder simpleMinimalFinder = new SimpleMinimalFinder();

     private synchronized void addMinimalAndIndex(int[] minimalAndIndex) {
         minimalsAndIndexes.add(minimalAndIndex);
     }

    public ParallelMinimalFinder(int threadCount) {
        this.threadCount = threadCount;
        this.minimalsAndIndexes = new ArrayList<>(threadCount);
    }

    @Override
    public int[] findMinimumAndItsIndex(int[] array, int fromIndex, int toIndex) {
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        minimalsAndIndexes.clear();
        int batchSize = array.length / threadCount;

        for (int i = 0; i < threadCount; i++) {
            int from = i * batchSize;
            int to = (i == threadCount - 1) ? array.length : (i + 1) * batchSize;
            new Thread(() -> {
                int[] minimalAndIndex = simpleMinimalFinder.findMinimumAndItsIndex(array, from, to);
                addMinimalAndIndex(minimalAndIndex);
                countDownLatch.countDown();
            }).start();
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        int[] minAndIndex = new int[]{ minimalsAndIndexes.getFirst()[0], minimalsAndIndexes.getFirst()[1] };
        for (int i = 1; i < threadCount; i++) {
            if (minAndIndex[0] > minimalsAndIndexes.get(i)[0]) {
                minAndIndex[0] = minimalsAndIndexes.get(i)[0];
                minAndIndex[1] = minimalsAndIndexes.get(i)[1];
            }
        }
        
        return minAndIndex;
    }
}
