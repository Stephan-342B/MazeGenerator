package org.mahefa.data_structure;

import org.mahefa.matrix.maze.obj.Cell;

public class DynamicArray {
    private Cell[] array;
    private int count;
    private int size;

    public DynamicArray() {
        this.array = new Cell[1];
        this.count = 0;
        this.size = 1;
    }

    public Cell[] getArray() {
        return array;
    }

    public void setArray(Cell[] array) {
        this.array = array;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void add(Cell cell) {
        // If the array is full; double its size
        if(this.count == this.size) {
            this.growSize();
        }

        // Append the element at the end of the array
        // Increase the count value
        this.array[this.count] = cell;
        this.count++;
    }

    public void removeAt(int index) {
        if(this.count > 0) {
            for(int i = index; i < count - 1; i++) {
                this.array[i] = this.array[i + 1];
            }

            this.removeLastElement();
        }
    }

    public void removeLastElement() {
        if(this.count > 0) {
            this.array[this.count - 1] = null;
            this.count--;
        }
    }

    public Cell get(int index) throws Exception {
        if(this.count > 0) {
            try {
                return this.array[index];
            } catch (IndexOutOfBoundsException e) {
                throw new Exception("Index out of bounds");
            }
        }

        throw new Exception("Array is empty");
    }

    public void growSize() {
        final int newSize = this.size * 2;
        Cell[] temp = null;

        if(this.count == this.size) {
            temp = new Cell[newSize];

            // Copy all data
            for(int i = 0; i < this.size; i++) {
                temp[i] = this.array[i];
            }
        }

        this.array = temp;
        this.size = newSize;
    }

    public void shrinkSize() {
        Cell[] temp = null;

        if(this.count > 0) {
            temp = new Cell[count];

            for(int i = 0; i < count; i++) {
                temp[i] = this.array[i];
            }

            this.size = count;
            this.array = temp;
        }
    }

    public void swap(int index1, int index2) {
        if(this.count > 1) {
            Cell temp = this.array[index1];
            this.array[index1] = this.array[index2];
            this.array[index2] = temp;
        }
    }
}
