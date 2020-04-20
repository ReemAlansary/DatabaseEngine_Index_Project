package Indices;

import java.io.Serializable;

public class Pointer implements Comparable<TuplePointer>, Serializable {
    String pagePath;
    Comparable key;

    private static final long serialVersionUID = 7L;
    public Pointer(String pagePath, Comparable key) {
        this.key = key;
        this.pagePath = pagePath;
    }
    public Comparable getKey() {
        return key;
    }
    public void setKey(Comparable key) {
        this.key = key;
    }
    public String getPagePath() {
        return this.pagePath;
    }
    public void setPagePath(String pagePath) { this.pagePath = pagePath; }

    public int compareTo(TuplePointer tp) {
        return this.key.compareTo(tp.key);
    }
}
