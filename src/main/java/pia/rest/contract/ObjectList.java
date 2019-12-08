package pia.rest.contract;

import java.util.ArrayList;
import java.util.List;

public class ObjectList<T> {
    List<T> items = new ArrayList<>();

    public void add(T obj) {
        items.add(obj);
    }
}
