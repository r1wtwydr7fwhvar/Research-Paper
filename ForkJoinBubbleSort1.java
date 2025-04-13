import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * Implementation of Fork/Join Bubble Sort (Implementation 1)
 */
public class ForkJoinBubbleSort1 {
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
        
        System.out.println("Fork/Join Bubble Sort (Implementation 1)");
        System.out.println("Array size: " + arraySize);
        System.out.println("Number of threads: " + numThreads);
        System.out.println("Task threshold: " + THRESHOLD);
        
        // Generate random array
        int[] array = generateRandomArray(arraySize);
        
        // Make a copy for verification
        int[] sortedArrayCopy = Arrays.copyOf(array, array.length);
        Arrays.sort(sortedArrayCopy);
        
        // Create ForkJoinPool with specified number of threads
        ForkJoinPool pool = new ForkJoinPool(numThreads);
        
        // Sort and measure time
        long startTime = System.nanoTime();
        parallelBubbleSort(array, pool);
        long endTime = System.nanoTime();
        
        double timeTaken = (endTime - startTime) / 1_000_000_000.0;
        
        // Verify sort was correct
        boolean isSorted = Arrays.equals(array, sortedArrayCopy);
        
        System.out.println("Time taken: " + timeTaken + " seconds");
        System.out.println("Sort correct: " + isSorted);
        
        // Shutdown the pool
        pool.shutdown();
    }
    
    /**
     * RecursiveAction for Fork/Join bubble sort implementation
     */
    static class ParallelBubbleSort extends RecursiveAction {
        private int[] arr;
        private int start, end;
        private boolean isPhaseEven;
        
        public ParallelBubbleSort(int[] arr, int start, int end, boolean isPhaseEven) {
            this.arr = arr;
            this.start = start;
            this.end = end;
            this.isPhaseEven = isPhaseEven;
        }
        
        @Override
        protected void compute() {
            if (end - start + 1 <= THRESHOLD) {
                bubbleSortSegment();
            } else {
                int mid = (start + end) / 2;
                ParallelBubbleSort leftTask = new ParallelBubbleSort(arr, start, mid, isPhaseEven);
                ParallelBubbleSort rightTask = new ParallelBubbleSort(arr, mid + 1, end, isPhaseEven);
                invokeAll(leftTask, rightTask);
                
                if (mid > start && mid < end) {
                    handleBoundary(mid);
                }
            }
        }
        
        private void bubbleSortSegment() {
            int startIndex = isPhaseEven ? start : start + 1;
            
            for (int i = startIndex; i < end; i += 2) {
                if (i + 1 <= end && arr[i] > arr[i + 1]) {
                    swap(arr, i, i + 1);
                }
            }
        }
        
        private void handleBoundary(int mid) {
            if (arr[mid] > arr[mid + 1]) {
                swap(arr, mid, mid + 1);
            }
        }
    }
    
    /**
     * Performs parallel bubble sort using the Fork/Join framework
     */
    public static void parallelBubbleSort(int[] arr, ForkJoinPool pool) {
        int n = arr.length;
        
        for (int phase = 0; phase < n; phase++) {
            boolean isPhaseEven = (phase % 2 == 0);
            pool.invoke(new ParallelBubbleSort(arr, 0, arr.length - 1, isPhaseEven));
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