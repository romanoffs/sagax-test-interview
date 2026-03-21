package com.sagax.shop.exception;

/**
 * This exception is thrown by exercise methods that have not been implemented yet.
 * <p>
 * TODO: remove all usages by implementing the corresponding methods.
 */
public class ExerciseNotCompletedException extends RuntimeException {

    public ExerciseNotCompletedException() {
        super("Exercise is not completed yet. Please implement this method.");
    }
}
