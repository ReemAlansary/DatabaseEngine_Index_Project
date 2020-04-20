package Indices;
import java.util.Hashtable;

public class TupleExtraPointer extends TuplePointer {
    Hashtable<String, Comparable> allValues;

    public TupleExtraPointer(int idx, String pagePath, Comparable key) {
        super(idx, pagePath, key);
        allValues = new Hashtable<>();
    }

    public Hashtable<String, Comparable> getAllValues() {
        return allValues;
    }

    public void addToHash(String columnName, Comparable value) {

        allValues.put(columnName, value);
    }

    public void setAllValues(Hashtable<String, Comparable> allValues) {
        this.allValues = allValues;
    }

    public void addToHash(Hashtable<String, Comparable> hash) {
        allValues.putAll(hash);
    }

}
