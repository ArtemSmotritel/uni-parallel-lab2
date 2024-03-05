public class SimpleMinimalFinder implements MinimalFinder {
    @Override
    public int[] findMinimumAndItsIndex(int[] array, int fromIndex, int toIndex) {
        int[] minAndIndex = new int[]{ Integer.MAX_VALUE, -1 };

        for (int i = fromIndex; i < toIndex; i++) {
            if (minAndIndex[0] > array[i]) {
                minAndIndex[0] = array[i];
                minAndIndex[1] = i;
            }
        }

        return minAndIndex;
    }
}
