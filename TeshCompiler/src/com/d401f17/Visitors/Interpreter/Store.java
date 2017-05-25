package com.d401f17.Visitors.Interpreter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Store {
    private List<Object> elements = new ArrayList<>();
    private Queue<Integer> freeList = new LinkedList<>();
    private int next = 0;

    public int getNext() {
        if (freeList.isEmpty()) {
            elements.add(null);
            return next++;
        }
        return freeList.remove();
    }

    public void setElement(int index, Object o) {
        elements.set(index, o);
    }

    public Object getElement(int index) {
        return elements.get(index);
    }

    public void removeElement(int index) {
        elements.set(index, null);
        freeList.add(index);
    }

    public int setNext(Object o) {
        int index = getNext();
        setElement(index, o);
        return index;
    }
}
