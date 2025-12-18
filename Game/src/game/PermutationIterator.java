package game;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterator that generates permutations of values for empty cells.
 * Implements the Iterator pattern to generate combinations on-the-fly
 * instead of storing all 9^5 (~60,000) permutations in memory.
 * 
 * For 5 empty cells, each can have values 1-9, giving 9^5 = 59,049 combinations.
 */
public class PermutationIterator implements Iterator<int[]> {
    
    private final int numPositions;
    private final int[] current;
    private boolean hasNext;
    
    /**
     * Creates an iterator for permutations.
     * @param numPositions Number of empty cells to fill (should be 5 for this lab)
     */
    public PermutationIterator(int numPositions) {
        if (numPositions < 1 || numPositions > 9) {
            throw new IllegalArgumentException("numPositions must be between 1 and 9");
        }
        
        this.numPositions = numPositions;
        this.current = new int[numPositions];
        
        // Initialize to first permutation: all 1s
        for (int i = 0; i < numPositions; i++) {
            current[i] = 1;
        }
        
        this.hasNext = true;
    }
    
    @Override
    public boolean hasNext() {
        return hasNext;
    }
    
    @Override
    public int[] next() {
        if (!hasNext) {
            throw new NoSuchElementException("No more permutations");
        }
        
        // Make a copy of current state to return
        int[] result = current.clone();
        
        // Generate next permutation (like counting in base-9 but with digits 1-9)
        advance();
        
        return result;
    }
    
    /**
     * Advances to the next permutation.
     * Works like incrementing a number, but in base-9 with digits 1-9.
     * Example progression: [1,1,1,1,1] → [1,1,1,1,2] → ... → [9,9,9,9,9]
     */
    private void advance() {
        int position = numPositions - 1;
        
        // Start from rightmost position and increment
        while (position >= 0) {
            if (current[position] < 9) {
                // Can increment this position
                current[position]++;
                return;
            } else {
                // This position is at 9, reset to 1 and carry over
                current[position] = 1;
                position--;
            }
        }
        
        // If we've carried over past the first position, we're done
        hasNext = false;
    }
    
    /**
     * Gets the total number of permutations this iterator will generate.
     */
    public long getTotalPermutations() {
        return (long) Math.pow(9, numPositions);
    }
}