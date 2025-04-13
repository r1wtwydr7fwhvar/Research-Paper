import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * Implementation of Fork/Join Bubble Sort (Implementation 2)
 */
public class ForkJoinBubbleSort2 {
    private static final Random random = new Random(42); // Fixed seed for reproducibility
    private static final int THRESHOLD = 1000; // Threshold for Fork/Join tasks
    
    public static void main(String[] args) {
        int arraySize = 10000; // Default size
        int numThreads = Runtime.getRuntime().availableProcessors(); // Default to available processors
        
        if (args.length > 0) {
            try {
                arraySize = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid array size. Using default: " + arraySize);
            }
        }
        
        if (args.length > 1) {
            try {
                numThreads = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid number of threads. Using default: " + numThreads);
            }
        }
        
        System.out.println("Fork/Join Bubble Sort (Implementation 2)");
        System.out.println("Array size: " + arraySize);
        System.out.println("Number of threads: " + numThreads);
        System.out.println("Task threshold: " + THRESHOLD);
        
        // Generate random array
        int[] array = generateRandomArray(arraySize);
        
        // Make a copy for verification
        int[] sortedArrayCopy = Arrays.copyOf(array, array.length);
        Arrays.sort(sortedArrayCopy);
        
        try (// Create ForkJoinPool with specified number of threads
        ForkJoinPool pool = new ForkJoinPool(numThreads)) {
            // Sort and measure time
            long startTime = System.nanoTime();
            BubbleSortTask sortTask = new BubbleSortTask(array, 0, array.length);
            pool.invoke(sortTask);
            finalPassBubbleSort(array); // Ensure array is fully sorted
            long endTime = System.nanoTime();
            
            double timeTaken = (endTime - startTime) / 1_000_000_000.0;
            
            // Verify sort was correct
            boolean isSorted = Arrays.equals(array, sortedArrayCopy);
            
            System.out.println("Time taken: " + timeTaken + " seconds");
            System.out.println("Sort correct: " + isSorted);
            
            // Shutdown the pool
            pool.shutdown();
        }
    }
    
    /**
     * Fork/Join task for bubble sort (Implementation 2)
     */
    private static class BubbleSortTask extends RecursiveAction {
        private final int[] array;
        private final int start;
        private final int end;
        
        public BubbleSortTask(int[] array, int start, int end) {
            this.array = array;
            this.start = start;
            this.end = end;
        }
        
        @Override
        protected void compute() {
            int length = end - start;
            
            if (length <= THRESHOLD) {
                // Sequential bubble sort for small chunks
                bubbleSort(array, start, end);
            } else {
                // Split the array and sort in parallel
                int mid = start + length / 2;
                
                invokeAll(
                    new BubbleSortTask(array, start, mid),
                    new BubbleSortTask(array, mid, end)
                );
                
                // Merge the two sorted halves
                merge(array, start, mid, end);
            }
        }
        
        private void bubbleSort(int[] arr, int start, int end) {
            for (int i = start; i < end - 1; i++) {
                for (int j = start; j < end - (i - start) - 1; j++) {
                    if (arr[j] > arr[j + 1]) {
                        swap(arr, j, j + 1);
                    }
                }
            }
        }
        
        private void merge(int[] arr, int start, int mid, int end) {
            int[] merged = new int[end - start];
            int i = start, j = mid, k = 0;
            
            while (i < mid && j < end) {
                if (arr[i] <= arr[j]) {
                    merged[k++] = arr[i++];
                } else {
                    merged[k++] = arr[j++];
                }
            }
            
            while (i < mid) merged[k++] = arr[i++];
            while (j < end) merged[k++] = arr[j++];
            
            System.arraycopy(merged, 0, arr, start, merged.length);
        }
    }
    
    /**
     * Final passes to ensure array is fully sorted
     */
    private static void finalPassBubbleSort(int[] arr) {
        boolean swapped;
        for (int i = 0; i < arr.length - 1; i++) {
            swapped = false;
            for (int j = 0; j < arr.length - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    swap(arr, j, j + 1);
                    swapped = true;
                }
            }
            if (!swapped) break;
        }
    }
    
    /**
     * Utility method to swap two elements in an array
     */
    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
    
    /**
     * Generates an array of random integers
     */
    private static int[] generateRandomArray(int size) {
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) {
            arr[i] = random.nextInt(1000000);
        }
        return arr;
    }
}