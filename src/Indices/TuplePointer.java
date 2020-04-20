package Indices;

public class TuplePointer extends Pointer {
    int idx;
    private static final long serialVersionUID = 8L;

    public TuplePointer(int idx, String pagePath, Comparable key) {
        super(pagePath, key);
        this.idx = idx;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Key: " + key +"\n");
        sb.append("Page Path: " + pagePath +"\n");
        sb.append("Index: " + idx +"\n");
        return sb.toString();
    }
    public boolean equals(Object tpo) {
        TuplePointer tp = (TuplePointer)tpo;
        return tp.getIdx() == idx && tp.getPagePath().equals(pagePath);
    }


}