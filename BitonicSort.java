import java.util.Arrays;
import java.util.Random;

/**
 * Implementation of Bitonic Sort algorithm
 */
public class BitonicSort {
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
        
        System.out.println("Bitonic Sort");
        System.out.println("Array size: " + arraySize);
        System.out.println("Note: Array will be padded to next power of 2");
        
        // Generate random array
        int[] array = generateRandomArray(arraySize);
        
        // Make a copy for verification
        int[] originalArrayCopy = Arrays.copyOf(array, array.length);
        Arrays.sort(originalArrayCopy);
        
        // For bitonic sort, pad to power of 2
        int bitonicSize = nextPowerOfTwo(arraySize);
        System.out.println("Padded size: " + bitonicSize);
        
        int[] paddedArray = Arrays.copyOf(array, bitonicSize);
        for (int i = arraySize; i < bitonicSize; i++) {
            paddedArray[i] = Integer.MAX_VALUE; // Pad with max values
        }
        
        // Sort and measure time
        long startTime = System.nanoTime();
        bitonicSort(paddedArray, 0, paddedArray.length, true);
        long endTime = System.nanoTime();
        
        double timeTaken = (endTime - startTime) / 1_000_000_000.0;
        
        // Get back the original sized array and verify
        int[] sortedArray = Arrays.copyOf(paddedArray, arraySize);
        boolean isSorted = Arrays.equals(sortedArray, originalArrayCopy);
        
        System.out.println("Time taken: " + timeTaken + " seconds");
        System.out.println("Sort correct: " + isSorted);
    }
    
    /**
     * Sorts the array using bitonic sort algorithm
     */
    public static void bitonicSort(int[] arr, int low, int count, boolean dir) {
        if (count > 1) {
            int k = count / 2;
            
            // Sort in ascending order since dir = true
            bitonicSort(arr, low, k, true);
            
            // Sort in descending order since dir = false
            bitonicSort(arr, low + k, k, false);
            
            // Merge the entire sequence in ascending/descending order
            bitonicMerge(arr, low, count, dir);
        }
    }
    
    /**
     * Merges two bitonic sequences
     */
    private static void bitonicMerge(int[] arr, int low, int count, boolean dir) {
        if (count > 1) {
            int k = count / 2;
            for (int i = low; i < low + k; i++) {
                if (dir == (arr[i] > arr[i + k])) {
                    swap(arr, i, i + k);
                }
            }
            bitonicMerge(arr, low, k, dir);
            bitonicMerge(arr, low + k, k, dir);
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
    
    /**
     * Finds the next power of two that is greater than or equal to n
     */
    private static int nextPowerOfTwo(int n) {
        int power = 1;
        while (power < n) {
            power *= 2;
        }
        return power;
    }
}