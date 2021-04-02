package org.mahefa.data_structure;

import org.mahefa.matrix.maze.obj.Cell;

public class Heap {
    private DynamicArray dynamicArray;
    private boolean isMaxHeap = true;

    public Heap(boolean isMaxHeap) {
        this.isMaxHeap = isMaxHeap;
    }

    public boolean isMaxHeap() {
        return isMaxHeap;
    }

    public void setMaxHeap(boolean maxHeap) {
        isMaxHeap = maxHeap;
    }

    public void insert(Cell cell) {
        if(this.dynamicArray == null) {
            this.dynamicArray = new DynamicArray();
        }

        // Insert
        this.dynamicArray.add(cell);

        int size = this.dynamicArray.getCount();

        // If there is two or more nodes apply heapify
        if(size > 1) {
            for(int i = (size / 2) - 1; i >= 0; i--) {
                this.heapify(i);
            }
        }
    }

    public Cell get() throws Exception {
        return this.dynamicArray.get(0);
    }

    public void remove(Cell cell) throws Exception {
        int size = this.dynamicArray.getCount();
        int i;

        for(i = 0; i < size; i++) {
            if(cell.equals(this.dynamicArray.get(i))) {
                break;
            }
        }

        // Swap it with the last element
        this.dynamicArray.swap(size - 1, i);

        // Remove the last element
        this.dynamicArray.removeLastElement();

        // Update size
        size = this.dynamicArray.getCount();

        // Heapify
        if(size > 1) {
            for(int j = (size / 2) - 1; j >= 0; j--) {
                this.heapify(j);
            }
        }
    }

    public boolean include(Cell cell) {
        try {
            if(this.dynamicArray.getCount() > 0) {
                if(cell.equals(this.dynamicArray.get(0))) {
                    return true;
                }

                for(int i = this.dynamicArray.getCount() - 1; i >= 1 ; i--) {
                    if(cell.equals(this.dynamicArray.get(i))) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return false;
    }

    public boolean isEmpty() {
        return this.dynamicArray.getCount() == 0;
    }

    private void heapify(int i) {
        final int n = this.dynamicArray.getCount();
        final int leftChildIndex = (2 * i) + 1;
        final int rightChildIndex = leftChildIndex + 1;
        final int value = this.getIndex(i, leftChildIndex, rightChildIndex, n, this.isMaxHeap);

        if(value != i) {
            this.dynamicArray.swap(i, value);
            heapify(value);
        }
    }

    private int getIndex(int parentIndex, int leftChildIndex, int rightChildIndex, int n, boolean isMaxHeap) {
        int i = parentIndex;

        try {
            final Cell parent = dynamicArray.get(parentIndex);

            if (leftChildIndex < n) {
                final Cell leftChild = dynamicArray.get(leftChildIndex);

                if(leftChild.getF() < parent.getF() || (isMaxHeap && leftChild.getF() > parent.getF())) {
                    i = leftChildIndex;
                }
            }

            if(rightChildIndex < n) {
                final Cell rightChild = dynamicArray.get(rightChildIndex);

                if(rightChild.getF() < parent.getF() || (isMaxHeap && rightChild.getF() > parent.getF())) {
                    i = rightChildIndex;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return i;
    }
}
