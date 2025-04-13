import java.util.Arrays;
import java.util.Random;

/**
 * Sequential (standard) implementation of bubble sort
 */
public class SequentialBubbleSort {
    private static final Random random = new Random(42); // Fixed seed for reproducibility
    
    public static void main(String[] args) {
        int arraySize = 10000; // Default size
        if (args.length > 0) {
            try {
                arraySize = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid array size. Using default: " + arraySize);
            }
        }
        
        System.out.println("Sequential Bubble Sort");
        System.out.println("Array size: " + arraySize);
        
        // Generate random array
        int[] array = generateRandomArray(arraySize);
        
        // Make a copy for verification
        int[] sortedArrayCopy = Arrays.copyOf(array, array.length);
        Arrays.sort(sortedArrayCopy);
        
        // Sort and measure time
        long startTime = System.nanoTime();
        sequentialBubbleSort(array);
        long endTime = System.nanoTime();
        
        double timeTaken = (endTime - startTime) / 1_000_000_000.0;
        
        // Verify sort was correct
        boolean isSorted = Arrays.equals(array, sortedArrayCopy);
        
        System.out.println("Time taken: " + timeTaken + " seconds");
        System.out.println("Sort correct: " + isSorted);
    }
    
    /**
     * Standard sequential bubble sort implementation
     */
    public static void sequentialBubbleSort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    swap(arr, j, j + 1);
                    swapped = true;
                }
            }
            if (!swapped) break; // Array is sorted
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