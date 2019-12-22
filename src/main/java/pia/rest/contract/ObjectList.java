package pia.rest.contract;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
public class ObjectList<T> {
    List<T> items = new ArrayList<>();

    public void add(T obj) {
        items.add(obj);
    }

    public List<T> getItems() {
        return items;
    }
}
