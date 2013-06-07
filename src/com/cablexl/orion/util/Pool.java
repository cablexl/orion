package com.cablexl.orion.util;

import android.util.Log;

/**
 * A simple, generic pool class that is designed to mimic stack allocations
 * and eliminate the need for garbage collection.
 *
 * @author Andy
 *
 * @param <T> The type the pool holds
 */
public class Pool<T> {

    public interface Allocator<T> {
        public T allocate();
    }

    /**
     * The values in the pool
     */
    private final T[] pool;

    /**
     * Index that marks the area between allocated and free components
     */
    private int partition;

    /**
     * Constructor
     *
     * @param maxsize Number of elements to create
     * @param allocator Allocator to allocate a single object of this type
     */
    public Pool(int maxsize, Allocator<T> allocator) {
        pool = initialize(maxsize, allocator);
    }

    /**
     * Constructor.  Simplified for cases where a default constructor is good enough
     *
     * @param maxsize Number of elements to create
     * @param clazz Class of the elements being created
     */
    public Pool(int maxsize, final Class<T> clazz) {
        pool = initialize(maxsize, new Allocator<T>() {
            @Override
            public T allocate() {
                try {
                    return clazz.newInstance();
                } catch (Exception e) {
                    Log.e("Pool", "Failed to instantiate object of type [" + clazz.getCanonicalName() + "]", e);
                }
                return null;
            }
        });
    }

    /**
     * Helper function to allocate the initial pool
     */
    private T[] initialize(int maxsize, Allocator<T> allocator) {
        partition = maxsize;
        T[] temp = (T[]) new Object[maxsize];

        // Populate the initial pool
        for(int i = 0; i < maxsize; i++) {
            T val = allocator.allocate();
            if(val == null) {
                throw new IllegalArgumentException("Allocator can not return null from allocate() method.");
            }
            temp[i] = val;
        }
        return temp;
    }

    /**
     * @return The next available object on the stack
     */
    public T take() {
        return pool[--partition];
    }

    /**
     *
     * @param object
     */
    public void release(T object) {
        pool[partition++] = object;
    }
}