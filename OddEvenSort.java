import java.util.Arrays;
import java.util.Random;

/**
 * Implementation of Odd-Even Sort (Parallel Bubble Sort)
 */
public class OddEvenSort {
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
        
        System.out.println("Odd-Even Sort");
        System.out.println("Array size: " + arraySize);
        
        // Generate random array
        int[] array = generateRandomArray(arraySize);
        
        // Make a copy for verification
        int[] sortedArrayCopy = Arrays.copyOf(array, array.length);
        Arrays.sort(sortedArrayCopy);
        
        // Sort and measure time
        long startTime = System.nanoTime();
        oddEvenSort(array);
        long endTime = System.nanoTime();
        
        double timeTaken = (endTime - startTime) / 1_000_000_000.0;
        
        // Verify sort was correct
        boolean isSorted = Arrays.equals(array, sortedArrayCopy);
        
        System.out.println("Time taken: " + timeTaken + " seconds");
        System.out.println("Sort correct: " + isSorted);
    }
    
    /**
     * Implements the odd-even sort algorithm
     */
    public static void oddEvenSort(int[] arr) {
        int n = arr.length;
        boolean sorted = false;
        
        while (!sorted) {
            sorted = true;
            
            // Odd phase
            for (int i = 1; i < n - 1; i += 2) {
                if (arr[i] > arr[i + 1]) {
                    swap(arr, i, i + 1);
                    sorted = false;
                }
            }
            
            // Even phase
            for (int i = 0; i < n - 1; i += 2) {
                if (arr[i] > arr[i + 1]) {
                    swap(arr, i, i + 1);
                    sorted = false;
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