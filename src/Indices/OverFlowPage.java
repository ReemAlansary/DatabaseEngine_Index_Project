package Indices;

import java.io.Serializable;
import java.util.ArrayList;

public class OverFlowPage implements Serializable {
    ArrayList<TuplePointer> pointers;
    private static final long serialVersionUID = 9L;

    public OverFlowPage() {
        pointers = new ArrayList<>();
    }

    public ArrayList<TuplePointer> getPointers() {
        return pointers;
    }

    public void setPointers(ArrayList<TuplePointer> pointers) {
        this.pointers = pointers;
    }
}

