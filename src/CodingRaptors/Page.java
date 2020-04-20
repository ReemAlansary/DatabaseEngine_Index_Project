package CodingRaptors;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

public class Page implements Serializable {
    private Vector<Hashtable<String, Object>> tuples;
    private int n;
    private static final long serialVersionUID = 1L;

    public Page() {
        tuples = new Vector();
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream("config/DBApp.properties"));
            n = Integer.parseInt(prop.getProperty("MaximumRowsCountinPage"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Vector<Hashtable<String, Object>> getTuples() {
        return tuples;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public void setTuples(Vector<Hashtable<String, Object>> tuples) {
        this.tuples = tuples;
    }
}
