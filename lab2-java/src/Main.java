import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        int n = 100_000_000;

        int threadNum;
        Scanner in = new Scanner(System.in);
        threadNum = Integer.parseInt(in.nextLine());

        int[] array = createArray(n);

        MinimalFinder minimalFinder = new SimpleMinimalFinder();
        MinimalFinder parallelMinimalFinder = new ParallelMinimalFinder(threadNum);

        long startSimpleTime = System.nanoTime();
        int[] min = minimalFinder.findMinimumAndItsIndex(array, 0, array.length);
        long endSimpleTime = System.nanoTime();
        double elapsedSimpleTime = (endSimpleTime - startSimpleTime) / 1_000_000.0;
        System.out.printf("Minimum = %d; with index = %d;\n", min[0], min[1]);
        System.out.printf("Done in: %f\n", elapsedSimpleTime);

        long startParallelTime = System.nanoTime();
        int[] parallelMin = parallelMinimalFinder.findMinimumAndItsIndex(array, 0, array.length);
        long endParallelTime = System.nanoTime();
        double elapsedParallelTime = (endParallelTime - startParallelTime) / 1_000_000.0;
        System.out.printf("Parallel Minimum = %d; with index = %d;\n", parallelMin[0], parallelMin[1]);
        System.out.printf("Done in: %f\n", elapsedParallelTime);
    }

    private static int[] createArray(int n) {
        int[] array = new int[n];

        Random random = new Random();
        for (int i = 0; i < n; i++) {
            array[i] = random.nextInt(n);
        }

        int index = random.nextInt(n);
        array[index] = -1;

        return array;
    }
}