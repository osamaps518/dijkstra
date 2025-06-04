package university.dijkstra.data_structures;

import java.util.Arrays;

/**
 * A generic min-heap implementation that maintains the heap property
 * where parent nodes are always smaller than their children.
 * 
 * @param <T> the type of elements in this heap, must be Comparable
 */
public class MinHeap<T extends Comparable<T>> {
  private static final int DEFAULT_CAPACITY = 16;
  private T[] heap;
  private int size;
  private int capacity;

  /**
   * Creates a new MinHeap with default capacity of 16.
   */
  public MinHeap() {
    this(DEFAULT_CAPACITY);
  }

  /**
   * Creates a new MinHeap with the specified initial capacity.
   * 
   * @param capacity the initial capacity of the heap
   * @throws IllegalArgumentException if capacity is less than 1
   */
  @SuppressWarnings("unchecked")
  public MinHeap(int capacity) {
    if (capacity < 1) {
      throw new IllegalArgumentException("Capacity must be at least 1");
    }
    this.capacity = capacity;
    this.size = 0;
    this.heap = (T[]) new Comparable[capacity];
  }

  /**
   * Creates a new MinHeap from an existing array using heapify.
   * 
   * @param array the array to build the heap from
   * @throws IllegalArgumentException if array is null or empty
   */
  @SuppressWarnings("unchecked")
  public MinHeap(T[] array) {
    if (array == null || array.length == 0) {
      throw new IllegalArgumentException("Array cannot be null or empty");
    }
    this.capacity = array.length * 2;
    this.size = array.length;
    this.heap = (T[]) new Comparable[capacity];

    // Copy array elements
    System.arraycopy(array, 0, heap, 0, array.length);

    // Build heap using heapify
    buildHeap();
  }

  /**
   * Inserts a new value into the heap.
   * 
   * @param value the value to insert
   * @throws IllegalArgumentException if value is null
   */
  public void insert(T value) {
    if (value == null) {
      throw new IllegalArgumentException("Cannot insert null value");
    }

    // Resize if necessary
    if (size == capacity) {
      resize();
    }

    heap[size] = value;
    size++;
    heapifyUp(size - 1);
  }

  /**
   * Removes and returns the minimum element from the heap.
   * 
   * @return the minimum element
   * @throws IllegalStateException if the heap is empty
   */
  public T extractMin() {
    if (size == 0) {
      throw new IllegalStateException("Heap is empty");
    }

    T minValue = heap[0];
    heap[0] = heap[size - 1];
    heap[size - 1] = null; // Help GC
    size--;

    if (size > 0) {
      heapifyDown(0);
    }

    return minValue;
  }

  /**
   * Returns the minimum element without removing it.
   * 
   * @return the minimum element
   * @throws IllegalStateException if the heap is empty
   */
  public T peek() {
    if (size == 0) {
      throw new IllegalStateException("Heap is empty");
    }
    return heap[0];
  }

  /**
   * Returns the number of elements in the heap.
   * 
   * @return the size of the heap
   */
  public int getSize() {
    return size;
  }

  /**
   * Checks if the heap is empty.
   * 
   * @return true if the heap is empty, false otherwise
   */
  public boolean isEmpty() {
    return size == 0;
  }

  /**
   * Removes all elements from the heap.
   */
  public void clear() {
    // Help GC by nulling out references
    for (int i = 0; i < size; i++) {
      heap[i] = null;
    }
    size = 0;
  }

  /**
   * Returns an array containing all elements in the heap.
   * The array is not guaranteed to be in any particular order.
   * 
   * @return an array of all elements in the heap
   */
  @SuppressWarnings("unchecked")
  public T[] toArray() {
    T[] array = (T[]) new Comparable[size];
    System.arraycopy(heap, 0, array, 0, size);
    return array;
  }

  /**
   * Returns a sorted array of all elements in the heap.
   * This operation does not modify the heap itself.
   * 
   * @return a sorted array of all elements
   */
  @SuppressWarnings("unchecked")
  public T[] toSortedArray() {
    T[] sorted = (T[]) new Comparable[size];
    T[] tempHeap = Arrays.copyOf(heap, size);
    int tempSize = size;

    // Extract all elements to get them in sorted order
    for (int i = 0; i < sorted.length; i++) {
      sorted[i] = heap[0];
      heap[0] = heap[size - 1];
      size--;
      if (size > 0) {
        heapifyDown(0);
      }
    }

    // Restore original heap
    System.arraycopy(tempHeap, 0, heap, 0, tempSize);
    size = tempSize;

    return sorted;
  }

  /**
   * Maintains heap property by moving an element up.
   */
  private void heapifyUp(int index) {
    while (index > 0) {
      int parentIndex = (index - 1) / 2;
      if (heap[index].compareTo(heap[parentIndex]) >= 0) {
        break;
      }
      swap(index, parentIndex);
      index = parentIndex;
    }
  }

  /**
   * Maintains heap property by moving an element down.
   */
  private void heapifyDown(int index) {
    while (true) {
      int leftChildIndex = 2 * index + 1;

      // No children
      if (leftChildIndex >= size) {
        break;
      }

      int rightChildIndex = 2 * index + 2;
      int smallestChildIndex = leftChildIndex;

      // Find the smaller child
      if (rightChildIndex < size &&
          heap[rightChildIndex].compareTo(heap[leftChildIndex]) < 0) {
        smallestChildIndex = rightChildIndex;
      }

      // If parent is smaller than both children, we're done
      if (heap[index].compareTo(heap[smallestChildIndex]) <= 0) {
        break;
      }

      swap(index, smallestChildIndex);
      index = smallestChildIndex;
    }
  }

  /**
   * Builds a heap from an unordered array using bottom-up approach.
   */
  private void buildHeap() {
    // Start from the last non-leaf node and heapify down
    for (int i = (size / 2) - 1; i >= 0; i--) {
      heapifyDown(i);
    }
  }

  /**
   * Doubles the capacity of the heap when it's full.
   */
  @SuppressWarnings("unchecked")
  private void resize() {
    capacity = capacity * 2;
    T[] newHeap = (T[]) new Comparable[capacity];
    System.arraycopy(heap, 0, newHeap, 0, size);
    heap = newHeap;
  }

  /**
   * Swaps two elements in the heap.
   */
  private void swap(int index1, int index2) {
    T temp = heap[index1];
    heap[index1] = heap[index2];
    heap[index2] = temp;
  }

  /**
   * Returns a string representation of the heap for debugging.
   * 
   * @return string representation of the heap
   */
  @Override
  public String toString() {
    if (size == 0) {
      return "MinHeap[]";
    }

    StringBuilder sb = new StringBuilder("MinHeap[");
    for (int i = 0; i < size; i++) {
      sb.append(heap[i]);
      if (i < size - 1) {
        sb.append(", ");
      }
    }
    sb.append("]");
    return sb.toString();
  }
}