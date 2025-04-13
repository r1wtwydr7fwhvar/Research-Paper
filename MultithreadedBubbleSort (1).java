import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of Multithreaded Bubble Sort using ExecutorService
 */
public class MultithreadedBubbleSort {
    private static final Random random = new Random(42); // Fixed seed for reproducibility
    
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
        
        System.out.println("Multithreaded Bubble Sort");
        System.out.println("Array size: " + arraySize);
        System.out.println("Number of threads: " + numThreads);
        
        // Generate random array
        int[] array = generateRandomArray(arraySize);
        
        // Make a copy for verification
        int[] sortedArrayCopy = Arrays.copyOf(array, array.length);
        Arrays.sort(sortedArrayCopy);
        
        // Sort and measure time
        long startTime = System.nanoTime();
        try {
            multithreadedBubbleSort(array, numThreads);
        } catch (InterruptedException e) {
            System.err.println("Sorting interrupted: " + e.getMessage());
            return;
        }
        long endTime = System.nanoTime();
        
        double timeTaken = (endTime - startTime) / 1_000_000_000.0;
        
        // Verify sort was correct
        boolean isSorted = Arrays.equals(array, sortedArrayCopy);
        
        System.out.println("Time taken: " + timeTaken + " seconds");
        System.out.println("Sort correct: " + isSorted);
    }
    
    /**
     * Sorts an array using multiple threads with ExecutorService
     */
    public static void multithreadedBubbleSort(int[] arr, int numThreads) throws InterruptedException {
        int n = arr.length;
        if (n <= 1) return;
        if (n < numThreads * 10) {
            sequentialBubbleSort(arr);
            return;
        }
    
        int sectionSize = n / numThreads;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
    
        // Sort each section in parallel
        for (int i = 0; i < numThreads; i++) {
            final int start = i * sectionSize;
            final int end = (i < numThreads - 1) ? (i + 1) * sectionSize : n;
            executor.submit(() -> bubbleSortSection(arr, start, end));
        }
    
        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    
        // Merge the sorted sections
        int currentSize = sectionSize;
        while (currentSize < n) {
            ExecutorService mergeExecutor = Executors.newFixedThreadPool(numThreads);
    
            for (int i = 0; i < n; i += currentSize * 2) {
                final int start = i;
                final int middle = Math.min(i + currentSize, n);
                final int end = Math.min(i + 2 * currentSize, n);
                if (middle < end) {
                    mergeExecutor.submit(() -> oddEvenMerge(arr, start, middle, end));
                }
            }
    
            mergeExecutor.shutdown();
            mergeExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            currentSize *= 2;
        }
    }
    
    /**
     * Sequential bubble sort for small arrays or fallback
     */
    private static void sequentialBubbleSort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    swap(arr, j, j + 1);
                    swapped = true;
                }
            }
            if (!swapped) break;
        }
    }
    
    /**
     * Bubble sort on a section of the array
     */
    private static void bubbleSortSection(int[] arr, int start, int end) {
        for (int i = start; i < end; i++) {
            for (int j = start; j < end - 1 - (i - start); j++) {
                if (arr[j] > arr[j + 1]) {
                    swap(arr, j, j + 1);
                }
            }
        }
    }
    
    /**
     * Merges two adjacent sorted sections
     */
    private static void oddEvenMerge(int[] arr, int start, int middle, int end) {
        // Simple implementation: just bubble sort the combined section
        for (int i = start; i < end; i++) {
            for (int j = start; j < end - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    swap(arr, j, j + 1);
                }
            }
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