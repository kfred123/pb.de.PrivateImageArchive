package pia.rest.contract;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
public class ObjectList<T> {
    long totalCount = 0;

    List<T> items = new ArrayList<>();

    public void add(T obj) {
        items.add(obj);
        totalCount++;
    }

    public List<T> getItems() {
        return items;
    }

    public long getTotalCount() {
        return totalCount;
    }
}
