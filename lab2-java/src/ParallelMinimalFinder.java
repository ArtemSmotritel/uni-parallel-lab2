import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ParallelMinimalFinder implements MinimalFinder {
     private final int threadCount;
     private final SimpleMinimalFinder simpleMinimalFinder = new SimpleMinimalFinder();
     private int[] minAndIndex = new int[] { Integer.MAX_VALUE, -1 };

     private synchronized void setMinimalAndIndex(int[] localMinAndIndex) {
         if (minAndIndex[0] > localMinAndIndex[0]) {
             minAndIndex[0] = localMinAndIndex[0];
             minAndIndex[1] = localMinAndIndex[1];
         }
     }

    public ParallelMinimalFinder(int threadCount) {
        this.threadCount = threadCount;
    }

    @Override
    public int[] findMinimumAndItsIndex(int[] array, int fromIndex, int toIndex) {
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        int batchSize = array.length / threadCount;

        for (int i = 0; i < threadCount; i++) {
            int from = i * batchSize;
            int to = (i == threadCount - 1) ? array.length : (i + 1) * batchSize;
            new Thread(() -> {
                int[] minimalAndIndex = simpleMinimalFinder.findMinimumAndItsIndex(array, from, to);
                setMinimalAndIndex(minimalAndIndex);
                countDownLatch.countDown();
            }).start();
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        
        return minAndIndex;
    }
}
