package CodingRaptors;

import Indices.*;

import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class DBApp {
// ---------------------------------------------------------------------------------------------------------------------

    /**
     * Method 1.
     */
    public void init() {
    }
// ---------------------------------------------------------------------------------------------------------------------

    /**
     * Method 2.
     *
     * @param strTableName           Table Name
     * @param strClusteringKeyColumn Clustering Key Column Name
     * @param htblColNameType        Hashtable of mappings of column names and column types. Only five types are allowed:\n
     *                               1. java.lang.Integer
     *                               2. java.lang.Double
     *                               3. java.lang.String
     *                               4. java.lang.Boolean
     *                               5. java.util.Date
     *                               6. java.awt.Polygon
     * @throws DBAppException Case 1: Table already exists.
     *                        Case 2: Wrong data type: should be java.lang.String or java.lang.Integer or java.lang.Double or java.lang.Boolean or java.util.Date or java.awt.Polygon.
     */
    public void createTable(String strTableName,
                            String strClusteringKeyColumn,
                            Hashtable<String, String> htblColNameType) throws DBAppException {

        saveTableMetadata(strTableName, strClusteringKeyColumn, htblColNameType);
        saveTableInformation(strTableName, strClusteringKeyColumn);

    }
// ---------------------------------------------------------------------------------------------------------------------

    /**
     * Method 3.
     *
     * @param strTableName           Table Name
     * @param strClusteringKeyColumn Clustering Key Column Name
     * @param htblColNameType        Hashtable of mappings of column names and column types. Only five types are allowed:\n
     *                               1. java.lang.Integer
     *                               2. java.lang.Double
     *                               3. java.lang.String
     *                               4. java.lang.Boolean
     *                               5. java.util.Date
     *                               6. java.awt.Polygon
     * @throws DBAppException Case 1: Table already exists.
     *                        Case 2: Wrong data type: should be java.lang.String or java.lang.Integer or java.lang.Double or java.lang.Boolean or java.util.Date or java.awt.Polygon.
     */
    public void saveTableMetadata(String strTableName,
                                  String strClusteringKeyColumn,
                                  Hashtable<String, String> htblColNameType) throws DBAppException {

        // Checking that the table does not already exist.
        try (BufferedReader br = new BufferedReader(new FileReader("data/metadata.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] row = line.split(",");
                if (row[0].equals(strTableName)) throw new DBAppException("Table already exists!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Writing the new table information.
        try (FileWriter csvWriter = new FileWriter("data/metadata.csv", true)) {
            ArrayList<String> fullRow = new ArrayList<>(); // String to collect values for one row including the commas.
            Set<Map.Entry<String, String>> entries = htblColNameType.entrySet(); // Set of all columnName-columnType pairs.

            for (Map.Entry<String, String> entry : entries) { // To iterate over each single columnName-columnType pair.
                if (!(entry.getValue().equals("java.lang.Integer")
                        || entry.getValue().equals("java.lang.String")
                        || entry.getValue().equals("java.lang.Double")
                        || entry.getValue().equals("java.lang.Boolean")
                        || entry.getValue().equals("java.util.Date")
                        || entry.getValue().equals("java.awt.Polygon")))
                    throw new DBAppException("Wrong data type: should be java.lang.String or java.lang.Integer or java.lang.Double or java.util.Date or java.awt.Polygon");

                fullRow.add(strTableName); // first column value
                fullRow.add(entry.getKey()); // second column value
                fullRow.add(entry.getValue()); // third column value

                // fourth column value
                if (entry.getKey().equals(strClusteringKeyColumn)) fullRow.add("True");
                else fullRow.add("False");

                fullRow.add("False"); // fifth column value

                // Whole row is delimited with commas, then written as one entry row in the csv file.
                csvWriter.write(String.join(",", fullRow));
                csvWriter.write("\n");
                fullRow.clear();
            }
            fullRow.add(strTableName);
            fullRow.add("Touch Date");
            fullRow.add("java.util.Date");
            fullRow.add("False");
            fullRow.add("False");

            csvWriter.write(String.join(",", fullRow));
            csvWriter.write("\n");
            fullRow.clear();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
// ---------------------------------------------------------------------------------------------------------------------

    /**
     * Method 4.
     *
     * @param tableName Table Name
     * @param Key       Clustering Key Column Name
     */
    public void saveTableInformation(String tableName,
                                     String Key) {
        List<String[]> allTables;
        try (ObjectInputStream tableData = new ObjectInputStream(new FileInputStream("data/tables.class"))) {
            allTables = (ArrayList<String[]>) tableData.readObject();
        } catch (Exception e) { //First table created, thus no file created yet.
            allTables = new ArrayList<>();
        }
        String[] myTable = {tableName, Key, "0"};
        allTables.add(myTable);

        try (ObjectOutputStream upTableData = new ObjectOutputStream(new FileOutputStream("data/tables.class"))) {
            upTableData.writeObject(allTables);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
// ---------------------------------------------------------------------------------------------------------------------

    /**
     * Method 5.
     *
     * @param filename File name.
     * @return The object that is serialized in the file.
     */
    public static Object readFile(String filename) {
        try (ObjectInputStream o = new ObjectInputStream(new FileInputStream(filename))) {
            return o.readObject();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
// ---------------------------------------------------------------------------------------------------------------------

    /**
     * Method 6.
     *
     * @param filename File name.
     * @param o        The object to be serialized.
     */
    public void writeFile(String filename, Object o) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(o);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
// ---------------------------------------------------------------------------------------------------------------------

    /**
     * Method 7. [For use in Indices package].
     *
     * @param filename File name.
     * @param o        The object to be serialized.
     */
    public static void writeObject(Object o, String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(o);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
// ---------------------------------------------------------------------------------------------------------------------

    /**
     * Method 8.
     *
     * @param tname Table name.
     * @return Array of strings containing table name, clustering key column name and total number of pages.
     */
    public String[] getTable(String tname) {
        ArrayList<String[]> alltables = (ArrayList<String[]>) readFile("data/tables.class");
        for (int i = 0; i < alltables.size(); i++) {
            if (alltables.get(i)[0].equals(tname)) {
                return alltables.get(i);
            }
        }
        return null;
    }
// ---------------------------------------------------------------------------------------------------------------------

    /**
     * Method 9.
     *
     * @param tname Table name.
     * @return ArrayList of rows from the csv file that correspond to metadata of that table.
     */
    public ArrayList<String[]> getTableColumns(String tname) {
        ArrayList<String[]> csvData = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("data/metadata.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] x = line.split(",");
                if (x[0].equals(tname)) csvData.add(x);
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
        return csvData;
    }
// ---------------------------------------------------------------------------------------------------------------------

    /**
     * Method 10.
     *
     * @param tableName  Table Name.
     * @param columnName Column name.
     * @return Returns true if that column has index. If the table name is invalid, or column name is invalid,
     * or there is no index on that column, false is returned.
     */
    public boolean hasIndex(String tableName, String columnName) {
        try (BufferedReader br = new BufferedReader(new FileReader("data/metadata.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] x = line.split(",");
                if ((x[0].equals(tableName)) &&
                        (x[1].equals(columnName)) &&
                        (x[4].equals("True")))
                    return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
// ---------------------------------------------------------------------------------------------------------------------

    /**
     * Method 11.
     *
     * @param strTableName Table name.
     * @return An list of all columns that are indexed using a B+ tree (not of type Polygon). If the table name is invalid,
     * or the table has no indexed columns, an empty list is returned.
     */
    public static ArrayList<String> allIndexes(String strTableName) {
        ArrayList<String> index = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("data/metadata.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] x = line.split(",");
                if ((x[0].equals(strTableName)) &&
                        (x[4].equals("True")) &&
                        !(x[2].equals("java.awt.Polygon")))
                    index.add(x[1]);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return index;
    }

// ---------------------------------------------------------------------------------------------------------------------

    /**
     * Method 12.
     *
     * @param strTableName Table name
     * @return A list of columns that are indexed using a R tree (are of type Polygon). If the table name is invalid,
     * or the table has no indexed columns, an empty list is returned.
     */
    public static ArrayList<String> allPolIndexes(String strTableName) {
        ArrayList<String> index = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("data/metadata.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] x = line.split(",");
                if ((x[0].equals(strTableName)) &&
                        (x[4].equals("True")) &&
                        (x[2].equals("java.awt.Polygon")))
                    index.add(x[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return index;
    }

// ---------------------------------------------------------------------------------------------------------------------

    /**
     * Method 13.
     *
     * @param tableName  Table name.
     * @param columnName Column name.
     * @return Returns the type of the column.
     */
    public String columnType(String tableName, String columnName) {
        try (BufferedReader br = new BufferedReader(new FileReader("data/metadata.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] x = line.split(",");
                if ((x[0].equals(tableName)) &&
                        (x[1].equals(columnName)))
                    return x[2];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

// ---------------------------------------------------------------------------------------------------------------------

    /**
     * Method 14.
     *
     * @param tableName Table name
     * @return Return clustering key name.
     */
    public String findCkey(String tableName) {
        ArrayList<String[]> cols = getTableColumns(tableName);
        String ckey = null;
        for (String[] s : cols)
            if (s[3].equals("True"))
                ckey = s[1];
        return ckey;
    }

// ---------------------------------------------------------------------------------------------------------------------

    /**
     * Method 15
     *
     * @param operator Comparison operator [=, !=, >, <, <=, >=]
     * @return ID integer
     * @throws DBAppException
     */
    public int checkOperatorIndexedCol(String operator) throws DBAppException {
        switch (operator) {
            case "=":
                return 1;
            case "!=":
                return 2;
            case ">":
                return 3;
            case ">=":
                return 4;
            case "<":
                return 5;
            case "<=":
                return 6;
            default:
                throw new DBAppException("Invalid operator.");
        }
    }
// ---------------------------------------------------------------------------------------------------------------------

    /**
     * Method 16.
     *
     * @param p First polygon.
     * @param o Second polygon.
     * @return True, if both polygons are equal (same coordinates even if they are not in the same order), and False otherwise.
     */
    static boolean samePol(Polygon p, Polygon o) {
        int[] x1 = p.xpoints;
        int[] y1 = p.ypoints;
        int[] x2 = o.xpoints;
        int[] y2 = o.ypoints;

        if (x1.length == x2.length) {
            loop:
            for (int i = 0; i < x1.length; i++) {
                for (int k = 0; k < x2.length; k++) {

                    // Same x and same y.
                    if (x2[k] == x1[i] && y2[k] == y1[i]) continue loop;

                    // Reached end of second array and same coordinates was not found.
                    if (k == x2.length - 1)
                        return false;
                }
            }
            return true;
        } else return false;
    }
// ---------------------------------------------------------------------------------------------------------------------


    /**
     * Method 17.
     *
     * @param strTableName     Table Name
     * @param strKey           Clustering Key Value
     * @param htblColNameValue Hashtable of mapping between column names and column values.
     * @throws DBAppException Case 1: Table is not found.
     *                        Case 2: Incompatible data types for a column in hashtable.
     *                        Case 3: Incompatible data type for the clustering key value.
     */
    public void updateTable(String strTableName,
                            String strKey,
                            Hashtable<String, Object> htblColNameValue) throws DBAppException {

        // 1. Adding Touch Date to the hashtable.
        htblColNameValue.put("Touch Date", Calendar.getInstance().getTime());

        // 2. Finding [total number of pages] and [clustering key column name] for this table, and check the validity of the table name.
        String[] tinfo = getTable(strTableName);
        if (tinfo == null) throw new DBAppException("Table does not exist!");
        String pnumber = tinfo[2];
        String clusteringkey = tinfo[1];

        // 3. Verify the data types in the SET clause hashtable, and get data type for clustering key.
        String datatype = "";
        ArrayList<String[]> csvData = getTableColumns(strTableName);
        try {
            for (String[] row : csvData) {
                if (htblColNameValue.containsKey(row[1]) &&
                        !(Class.forName(row[2]).equals(htblColNameValue.get(row[1]).getClass())))
                    throw new DBAppException("Incompatible data type for column name: " + row[1] + ".");
                if (row[1].equals(clusteringkey)) datatype = row[2];
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // 4. Parsing the strKey according to the data type we found from the metadata file, and getting a set of the hashtable mappings.
        Object strKeyParsed = parsedValue(strTableName, clusteringkey, strKey);
        Set<Map.Entry<String, Object>> entries = htblColNameValue.entrySet();

        // 5. Checking if clusteringKey is indexed.
        if (hasIndex(strTableName, clusteringkey)) {
            Pointer p;
            String generalPath = "data/" + strTableName + "indices" + clusteringkey;

            // Arriving to the the Pointer.
            if (datatype.equals("java.awt.Polygon")) {
                String RTreePath = generalPath + "Rtree.class";
                RTree tree = (RTree) readFile(RTreePath);
                p = tree.find((Comparable) strKeyParsed);
            } else {
                String BTreePath = generalPath + "Btree.class";
                BPTree tree = (BPTree) readFile(BTreePath);
                p = tree.find((Comparable) strKeyParsed);
            }

            // Pointer instance of TuplePointer.
            if (p instanceof TuplePointer) {
                boolean valid = true;
                // We check that the pointer key has not only the same area, but same coordinates as well.
                if (datatype.equals("java.awt.Polygon"))
                    if (!(CPolygon.sameCPol((CPolygon) strKeyParsed, (CPolygon) p.getKey()))) valid = false;

                if (valid) {

                    // 1. Fetching the tuple from the pages using the pointer.
                    Page pa = (Page) readFile(p.getPagePath());
                    Vector<Hashtable<String, Object>> v = pa.getTuples();
                    int idx = ((TuplePointer) p).getIdx();
                    Hashtable<String, Object> old = v.get(idx);

                    // 2. Looping on the SET clause.
                    for (Map.Entry<String, Object> entry : entries) {
                        // Column in the SET clause is indexed and corresponding tree has to be modified.
                        if (hasIndex(strTableName, entry.getKey())) {
                            String generalP = "data/" + strTableName + "indices" + entry.getKey();
                            // Column in the SET clause is of type polygon.
                            if (entry.getValue() instanceof Polygon) {
                                String RTreeP = generalP + "Rtree.class";
                                RTree CurrentTree = (RTree) readFile(RTreeP);
                                CPolygon oldPolygon = new CPolygon((Polygon) old.get(entry.getKey()));
                                CPolygon newPolygon = new CPolygon((Polygon) entry.getValue());
                                if (oldPolygon.compareTo(newPolygon) != 0)
                                    CurrentTree.updateKey((TuplePointer) p, oldPolygon, newPolygon);
                            }
                            // Column in the SET clause is not of type polygon.
                            else {
                                String BTreeP = generalP + "Btree.class";
                                BPTree CurrentTree = (BPTree) readFile(BTreeP);
                                Comparable oldValue = (Comparable) old.get(entry.getKey());
                                Object newValue = entry.getValue();
                                if (oldValue.compareTo(newValue) != 0)
                                    CurrentTree.updateKey((TuplePointer) p, oldValue, (Comparable) newValue);
                            }
                        }
                        // Updating the new value from the current part in the SET clause in the tuple.
                        old.replace(entry.getKey(), entry.getValue());
                    }

                    // 3. Assigning the updated hashtable back to the vector, to the page and then writing the page.
                    v.set(idx, old);
                    pa.setTuples(v);
                    writeObject(pa, p.getPagePath());
                }
            }

            // Pointer not instance of TuplePointer.
            else {
                OverFlowPage ovp = (OverFlowPage) readFile(p.getPagePath());

                for (int j = 0; j < ovp.getPointers().size(); j++) {
                    // 1. Fetching the current tuple from the current tuplepointer in the pointer set.
                    TuplePointer tp = ovp.getPointers().get(j);
                    boolean valid = true;
                    // We check that the pointer key has not only the same area, but same coordinates as well.
                    if (datatype.equals("java.awt.Polygon"))
                        if (!(CPolygon.sameCPol((CPolygon) strKeyParsed, (CPolygon) tp.getKey()))) valid = false;

                    if (valid) {
                        Page pa = (Page) readFile(tp.getPagePath());
                        Vector<Hashtable<String, Object>> tuples = pa.getTuples();
                        int idx = tp.getIdx();
                        Hashtable<String, Object> old = pa.getTuples().get(idx);

                        // 2. Looping on the SET clause.
                        for (Map.Entry<String, Object> entry : entries) {
                            // Column in the SET clause is indexed and corresponding tree has to be modified.
                            if (hasIndex(strTableName, entry.getKey())) {
                                String generalP = "data/" + strTableName + "indices" + entry.getKey();
                                // Column in the SET clause is of type polygon.
                                if (entry.getValue() instanceof Polygon) {
                                    String RTreeP = generalP + "Rtree.class";
                                    RTree CurrentTree = (RTree) readFile(RTreeP);
                                    CPolygon oldPolygon = new CPolygon((Polygon) old.get(entry.getKey()));
                                    CPolygon newPolygon = new CPolygon((Polygon) entry.getValue());
                                    if (oldPolygon.compareTo(newPolygon) != 0)
                                        CurrentTree.updateKey(tp, oldPolygon, newPolygon);
                                }
                                // Column in the SET clause is not of type polygon.
                                else {
                                    String BTreeP = generalP + "Btree.class";
                                    BPTree CurrentTree = (BPTree) readFile(BTreeP);
                                    Comparable oldValue = (Comparable) old.get(entry.getKey());
                                    Object newValue = entry.getValue();
                                    if (oldValue.compareTo(newValue) != 0)
                                        CurrentTree.updateKey(tp, oldValue, (Comparable) newValue);
                                }
                            }

                            // Updating the new value from the current part in the SET clause in the tuple.
                            old.replace(entry.getKey(), entry.getValue());
                        }

                        // 3. Assigning the updated hashtable back to the vector, to the page and then writing the page.
                        tuples.set(idx, old);
                        pa.setTuples(tuples);
                        writeObject(pa, tp.getPagePath());
                    }
                }
            }
        }


        // Non-index search.
        else {
            String basePagePath = "data/" + strTableName + "page";
            pageloop:
            for (int k = 1; k <= Integer.parseInt(pnumber); k++) {
                String pagePath = basePagePath + k + ".class";
                Page p = (Page) readFile(pagePath);
                Vector<Hashtable<String, Object>> vector = p.getTuples();

                // Binary search on the vector.
                int min = 0;
                int max = vector.size() - 1;
                int shift = -1;
                while (min <= max) {
                    int mid = min + (max - min + 1) / 2;
                    if (!datatype.equals("java.awt.Polygon")) {
                        if (((Comparable) vector.get(mid).get(clusteringkey)).compareTo(strKeyParsed) < 0) {
                            min = mid + 1;
                        }
                        if (((Comparable) vector.get(mid).get(clusteringkey)).compareTo(strKeyParsed) > 0) {
                            max = mid - 1;
                        }
                        if (((Comparable) vector.get(mid).get(clusteringkey)).compareTo(strKeyParsed) == 0) {
                            shift = mid;
                            max = mid - 1;
                        }
                    } else {
                        if (new CPolygon((Polygon) vector.get(mid).get(clusteringkey)).compareTo(strKeyParsed) < 0) {
                            min = mid + 1;
                        }
                        if (new CPolygon((Polygon) vector.get(mid).get(clusteringkey)).compareTo(strKeyParsed) > 0) {
                            max = mid - 1;
                        }
                        if (new CPolygon((Polygon) vector.get(mid).get(clusteringkey)).compareTo(strKeyParsed) == 0) {
                            shift = mid;
                            max = mid - 1;
                        }
                    }
                }
                if (shift != -1) {
                    updateloop:
                    for (int v = shift; v < vector.size(); v++) {
                        // Data type of clustering key is not polygon.
                        if (!datatype.equals("java.awt.Polygon")) {
                            if (((Comparable) vector.get(v).get(clusteringkey)).compareTo(strKeyParsed) == 0)

                                // 2. Looping on the SET clause.
                                for (Map.Entry<String, Object> pair : entries) {
                                    // Column in the SET clause is indexed and corresponding tree has to be modified.
                                    if (hasIndex(strTableName, pair.getKey())) {
                                        String generalP = "data/" + strTableName + "indices" + pair.getKey();
                                        // Column in the SET clause is of type polygon.
                                        if (pair.getValue() instanceof Polygon) {
                                            String RTreeP = generalP + "Rtree.class";
                                            RTree CurrentTree = (RTree) readFile(RTreeP);
                                            CPolygon oldPolygon = new CPolygon((Polygon) vector.get(v).get(pair.getKey()));
                                            CPolygon newPolygon = new CPolygon((Polygon) pair.getValue());
                                            TuplePointer tp = new TuplePointer(v, pagePath, oldPolygon);
                                            if (oldPolygon.compareTo(newPolygon) != 0)
                                                CurrentTree.updateKey(tp, oldPolygon, newPolygon);
                                        }
                                        // Column in the SET clause is of not of type polygon.
                                        else {
                                            String BTreeP = generalP + "Btree.class";
                                            BPTree CurrentTree = (BPTree) readFile(BTreeP);
                                            Comparable oldValue = (Comparable) vector.get(v).get(pair.getKey());
                                            Object newValue = pair.getValue();
                                            TuplePointer tp = new TuplePointer(v, pagePath, oldValue);
                                            if (oldValue.compareTo(newValue) != 0)
                                                CurrentTree.updateKey(tp, oldValue, (Comparable) newValue);
                                        }
                                    }
                                    // Updating the new value from the current part in the SET clause in the tuple.
                                    vector.get(v).replace(pair.getKey(), pair.getValue());
                                }
                            else break updateloop;
                        }
                        // Data type of clustering key is of type polygon.
                        else {
                            if (CPolygon.sameCPol(new CPolygon((Polygon) vector.get(v).get(clusteringkey)), (CPolygon) strKeyParsed))
                                // 2. Looping on the SET clause.
                                for (Map.Entry<String, Object> pair : entries) {
                                    String generalP = "data/" + strTableName + "indices" + pair.getKey();
                                    // Column in the SET clause is indexed and corresponding tree has to be modified.
                                    if (hasIndex(strTableName, pair.getKey())) {
                                        // Column in the SET clause is of type polygon.
                                        if (pair.getValue() instanceof Polygon) {
                                            String RTreeP = generalP + "Rtree.class";
                                            RTree CurrentTree = (RTree) readFile(RTreeP);
                                            CPolygon oldPolygon = new CPolygon((Polygon) vector.get(v).get(pair.getKey()));
                                            CPolygon newPolygon = new CPolygon((Polygon) pair.getValue());
                                            TuplePointer tp = new TuplePointer(v, pagePath, oldPolygon);
                                            if (oldPolygon.compareTo(newPolygon) != 0)
                                                CurrentTree.updateKey(tp, oldPolygon, newPolygon);
                                        }
                                        // Column in the SET clause is not of type polygon.
                                        else {
                                            String BTreeP = generalP + "Btree.class";
                                            BPTree CurrentTree = (BPTree) readFile(BTreeP);
                                            Comparable oldValue = (Comparable) vector.get(v).get(pair.getKey());
                                            Object newValue = pair.getValue();
                                            TuplePointer tp = new TuplePointer(v, pagePath, oldValue);
                                            if (oldValue.compareTo(newValue) != 0)
                                                CurrentTree.updateKey(tp, oldValue, (Comparable) newValue);
                                        }
                                    }

                                    vector.get(v).replace(pair.getKey(), pair.getValue());
                                }
                            else break updateloop;
                        }
                    }
                }
                // 3. Assigning the updated hashtable back to the vector, to the page and then writing the page.
                p.setTuples(vector);
                writeFile(pagePath, p);
            }
        }
    }

// ---------------------------------------------------------------------------------------------------------------------

    /**
     * Method 18.
     *
     * @param strTableName
     * @param firstPage
     * @param pnumber
     */
    public void shiftIndicesPagePaths(String strTableName, int firstPage, int pnumber) {

        String str = "data/" + strTableName + "page";
        for (int i = firstPage; i <= pnumber; i++) {
            String pagePath = str + i + ".class";
            String oldPagePath = str + (i + 1) + ".class";
            Page currentPage = (Page) readFile(pagePath);
            Vector<Hashtable<String, Object>> vector = currentPage.getTuples();

            for (int j = 0; j < vector.size(); j++) {
                for (int k = 0; k < allIndexes(strTableName).size(); k++) {
                    BPTree tree = (BPTree) readFile("data/" + strTableName + "indices" + allIndexes(strTableName).get(k) + "Btree.class");
                    TuplePointer oldTPointer = new TuplePointer(j,
                            oldPagePath,
                            (Comparable) vector.get(j).get(allIndexes(strTableName).get(k)));

                    TuplePointer newTPointer = new TuplePointer(j,
                            pagePath,
                            (Comparable) vector.get(j).get(allIndexes(strTableName).get(k)));

                    tree.updateTuplePointer(oldTPointer, newTPointer);
                }

                for (int k = 0; k < allPolIndexes(strTableName).size(); k++) {
                    RTree tree = (RTree) readFile("data/" + strTableName + "indices" + allPolIndexes(strTableName).get(k) + "Rtree.class");
                    TuplePointer oldTPointer = new TuplePointer(j,
                            oldPagePath,
                            new CPolygon((Polygon) vector.get(j).get(allPolIndexes(strTableName).get(k))));

                    TuplePointer newTPointer = new TuplePointer(j,
                            pagePath,
                            new CPolygon((Polygon) vector.get(j).get(allPolIndexes(strTableName).get(k))));

                    tree.updateTuplePointer(oldTPointer, newTPointer);
                }
            }
        }
    }
// ---------------------------------------------------------------------------------------------------------------------

    /**
     * Method 19.
     *
     * @param strTableName           Table Name
     * @param indexedColumns         ArrayList of indexed columns names in the WHERE clause. [Not of type polygon]
     * @param indexedHashtable       Hashtable of the mappings in the WHERE clause.
     * @param NonindexedColumns      ArrayList of non-indexed columns names in the WHERE clause. [Not of type polygon]
     * @param NonIndexedHashtable    Hashtable of the mappings in the WHERE clause.
     * @param indexedPolColumns      ArrayList of indexed columns names in the WHERE clause. [Of type polygon]
     * @param indexedPolHashtable    Hashtable of the mappings in the WHERE clause.
     * @param NonindexedPolColumns   ArrayList of non-indexed columns names in the WHERE clause. [Not of type polygon]
     * @param NonindexedPolHashtable Hashtable of the mappings in the WHERE clause.
     * @return ArrayList of pointers to be deleted.
     */
    public ArrayList<TupleExtraPointer> filter(String strTableName, ArrayList<String> indexedColumns,
                                               Hashtable<String, Object> indexedHashtable, ArrayList<String> NonindexedColumns,
                                               Hashtable<String, Object> NonIndexedHashtable, ArrayList<String> indexedPolColumns,
                                               Hashtable<String, Object> indexedPolHashtable, ArrayList<String> NonindexedPolColumns,
                                               Hashtable<String, Object> NonindexedPolHashtable) {
        ArrayList<TupleExtraPointer> indexedTuples = new ArrayList<>();
        ArrayList<String> allIndexes = allIndexes(strTableName);
        ArrayList<String> allPolIndexes = allPolIndexes(strTableName);
        for (int i = 0; i < indexedColumns.size(); i++) {
            BPTree tree = (BPTree) readFile("data/" + strTableName + "indices" + indexedColumns.get(i) + "Btree.class");
            Object obj = indexedHashtable.get(indexedColumns.get(i));
            Pointer p = tree.find((Comparable) obj);
            if (!(p instanceof TuplePointer)) {
                OverFlowPage ovp = (OverFlowPage) readFile(p.getPagePath());

                if (indexedTuples.size() > 0) {
                    ArrayList<TupleExtraPointer> sub = new ArrayList<>();

                    for (int j = 0; j < ovp.getPointers().size(); j++)
                        if (indexedTuples.contains(ovp.getPointers().get(j))) {
                            int index = indexedTuples.indexOf(ovp.getPointers().get(j));
                            TupleExtraPointer oldtxp = indexedTuples.get(index);
                            TupleExtraPointer newtxp = new TupleExtraPointer(oldtxp.getIdx(),
                                    oldtxp.getPagePath(), null);
                            newtxp.addToHash(oldtxp.getAllValues());
                            newtxp.addToHash(indexedColumns.get(i), ovp.getPointers().get(j).getKey());
                            sub.add(newtxp);
                        }

                    indexedTuples = sub;
                } else
                    for (int j = 0; j < ovp.getPointers().size(); j++) {
                        TupleExtraPointer txp = new TupleExtraPointer(ovp.getPointers().get(j).getIdx(),
                                ovp.getPointers().get(j).getPagePath(),
                                null);
                        txp.addToHash(indexedColumns.get(i), ovp.getPointers().get(j).getKey());
                        indexedTuples.add(txp);
                    }

            } else {
                if (indexedTuples.size() > 0) {
                    ArrayList<TupleExtraPointer> sub = new ArrayList<>();

                    if (indexedTuples.contains((TuplePointer) p)) {
                        int index = indexedTuples.indexOf((TuplePointer) p);
                        TupleExtraPointer oldtxp = indexedTuples.get(index);
                        TupleExtraPointer newtxp = new TupleExtraPointer(oldtxp.getIdx(),
                                oldtxp.getPagePath(), null);
                        newtxp.addToHash(oldtxp.getAllValues());
                        newtxp.addToHash(indexedColumns.get(i), p.getKey());
                        sub.add(newtxp);
                    }
                    indexedTuples = sub;
                } else {
                    TupleExtraPointer txp = new TupleExtraPointer(((TuplePointer) p).getIdx(),
                            p.getPagePath(),
                            null);
                    txp.addToHash(indexedColumns.get(i), p.getKey());
                    indexedTuples.add(txp);
                }
            }

            allIndexes.remove(indexedColumns.get(i));

        }
        for (int g = 0; g < indexedPolColumns.size(); g++) {
            RTree tree = (RTree) readFile("data/" + strTableName + "indices" + indexedPolColumns.get(g) + "Rtree.class");
            CPolygon obj = new CPolygon((Polygon) indexedPolHashtable.get(indexedPolColumns.get(g)));
            Pointer p = tree.find(obj);
            if (!(p instanceof TuplePointer)) {
                OverFlowPage ovp = (OverFlowPage) readFile(p.getPagePath());

                if (indexedTuples.size() > 0) {
                    ArrayList<TupleExtraPointer> sub = new ArrayList<>();

                    for (int j = 0; j < ovp.getPointers().size(); j++)
                        if (indexedTuples.contains(ovp.getPointers().get(j))) {
                            int index = indexedTuples.indexOf(ovp.getPointers().get(j));
                            TupleExtraPointer oldtxp = indexedTuples.get(index);
                            TupleExtraPointer newtxp = new TupleExtraPointer(oldtxp.getIdx(),
                                    oldtxp.getPagePath(), null);
                            newtxp.addToHash(oldtxp.getAllValues());
                            newtxp.addToHash(indexedPolColumns.get(g), ovp.getPointers().get(g).getKey());
                            sub.add(newtxp);
                        }

                    indexedTuples = sub;
                } else
                    for (int j = 0; j < ovp.getPointers().size(); j++) {
                        int Polidx = ovp.getPointers().get(j).getIdx();
                        Page Polpage = (Page) readFile(ovp.getPointers().get(j).getPagePath());
                        Hashtable<String, Object> tuple = Polpage.getTuples().get(Polidx);
                        Polygon Pol = (Polygon) tuple.get(indexedPolColumns.get(g));
                        Polygon toDelete = (Polygon) indexedPolHashtable.get(indexedPolColumns.get(g));
                        if (samePol(Pol, toDelete)) {

                            TupleExtraPointer txp = new TupleExtraPointer(ovp.getPointers().get(j).getIdx(),
                                    ovp.getPointers().get(j).getPagePath(),
                                    null);
                            txp.addToHash(indexedPolColumns.get(g), ovp.getPointers().get(j).getKey());
                            indexedTuples.add(txp);
                        }
                    }

            } else {
                if (indexedTuples.size() > 0) {
                    ArrayList<TupleExtraPointer> sub = new ArrayList<>();

                    if (indexedTuples.contains((TuplePointer) p)) {
                        int index = indexedTuples.indexOf((TuplePointer) p);
                        TupleExtraPointer oldtxp = indexedTuples.get(index);
                        TupleExtraPointer newtxp = new TupleExtraPointer(oldtxp.getIdx(),
                                oldtxp.getPagePath(), null);
                        newtxp.addToHash(oldtxp.getAllValues());
                        newtxp.addToHash(indexedPolColumns.get(g), p.getKey());
                        sub.add(newtxp);
                    }
                    indexedTuples = sub;
                } else {
                    int Polidx = ((TuplePointer) p).getIdx();
                    Page Polpage = (Page) readFile(p.getPagePath());
                    Hashtable<String, Object> tuple = Polpage.getTuples().get(Polidx);
                    Polygon Pol = (Polygon) tuple.get(indexedPolColumns.get(g));
                    Polygon toDelete = (Polygon) indexedPolHashtable.get(indexedPolColumns.get(g));
                    if (samePol(Pol, toDelete)) {

                        TupleExtraPointer txp = new TupleExtraPointer(((TuplePointer) p).getIdx(),
                                p.getPagePath(),
                                null);
                        txp.addToHash(indexedPolColumns.get(g), p.getKey());
                        indexedTuples.add(txp);
                    }
                }
            }

            allPolIndexes.remove(indexedPolColumns.get(g));

        }


        // Making sure nonIndexed columns in where condition are equal in the tuples we picked
        for (int k = 0; k < indexedTuples.size(); k++) {
            Page p = (Page) readFile(indexedTuples.get(k).getPagePath());
            int idx = indexedTuples.get(k).getIdx();
            Hashtable<String, Object> tuple = p.getTuples().get(idx);

            for (int j = 0; j < NonindexedColumns.size(); j++) {
                Object key = tuple.get(NonindexedColumns.get(j));
                if (!(key.equals(NonIndexedHashtable.get(NonindexedColumns.get(j))))) {
                    indexedTuples.remove(indexedTuples.get(k));
                    k--;
                    break;
                }
            }
        }
        // Making sure nonIndexed columns in where condition are equal in the tuples we picked for POLYGONS
        for (int k = 0; k < indexedTuples.size(); k++) {
            Page p = (Page) readFile(indexedTuples.get(k).getPagePath());
            int idx = indexedTuples.get(k).getIdx();
            Hashtable<String, Object> tuple = p.getTuples().get(idx);

            for (int j = 0; j < NonindexedPolColumns.size(); j++) {
                Polygon key = (Polygon) tuple.get(NonindexedPolColumns.get(j));
                if (!((new CPolygon(key)).equals(new CPolygon((Polygon) (NonindexedPolHashtable.get(NonindexedPolColumns.get(j))))) && samePol(key, (Polygon) (NonindexedPolHashtable.get(NonindexedPolColumns.get(j)))))) {
                    indexedTuples.remove(indexedTuples.get(k));
                    k--;
                    break;
                }
            }
        }


        for (int h = 0; h < indexedTuples.size(); h++) {
            TupleExtraPointer txp = indexedTuples.get(h);
            Page p = (Page) readFile(txp.getPagePath());
            Vector<Hashtable<String, Object>> vector = p.getTuples();
            int index = txp.getIdx();
            for (int k = 0; k < allIndexes.size(); k++) {
                txp.addToHash(allIndexes.get(k), (Comparable) vector.get(index).get(allIndexes.get(k)));
            }
            indexedTuples.set(h, txp);
        }
        for (int h = 0; h < indexedTuples.size(); h++) {
            TupleExtraPointer txp = indexedTuples.get(h);
            Page p = (Page) readFile(txp.getPagePath());
            Vector<Hashtable<String, Object>> vector = p.getTuples();
            int index = txp.getIdx();
            for (int k = 0; k < allPolIndexes.size(); k++) {
                txp.addToHash(allPolIndexes.get(k), new CPolygon((Polygon) vector.get(index).get(allPolIndexes.get(k))));
            }
            indexedTuples.set(h, txp);
        }

        return indexedTuples;

    }
// ---------------------------------------------------------------------------------------------------------------------

    /**
     * Method 20.
     *
     * @param strTableName     Table name.
     * @param htblColNameValue Hashtable of mappings in the WHERE clause.
     * @throws DBAppException Case 1: Table does not exist.
     *                        Case 2: Invalid data type for column.
     */
    public void deleteFromTable(String strTableName, Hashtable<String, Object> htblColNameValue) throws DBAppException {

        // 1. Finding [total number of pages] and [clustering key column name] for this table.
        String[] tinfo = getTable(strTableName);
        if (tinfo == null) throw new DBAppException("Table does not exist!");
        String pnumber = tinfo[2];
        String ckey = tinfo[1];

        // 2. Verifying the data types of the objects in the hashtable, and data type of clustering key.
        ArrayList<String[]> csvData = getTableColumns(strTableName);
        String datatype = "";
        try {
            for (String[] row : csvData) {
                if (htblColNameValue.containsKey(row[1]) && !(Class.forName(row[2]).equals(htblColNameValue.get(row[1]).getClass())))
                    throw new DBAppException("Incompatible data type for column name: " + row[1] + ".");
                if (row[1].equals(ckey)) datatype = row[2];
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // 3. Get indexes from the WHERE clause.
        boolean flag;
        Set<Map.Entry<String, Object>> entries = htblColNameValue.entrySet();

        ArrayList<String> indexedColumns = new ArrayList<>(), indexedPolColumns = new ArrayList<>();
        Hashtable<String, Object> indexedHashtable = new Hashtable<>(), indexedPolHashtable = new Hashtable<>();

        ArrayList<String> NonIndexedColumns = new ArrayList<>(), NonIndexedPolColumns = new ArrayList<>();
        Hashtable<String, Object> NonIndexedHashtable = new Hashtable<>(), NonIndexedPolHashtable = new Hashtable<>();

        for (Map.Entry<String, Object> entry : entries) {
            boolean indexed = hasIndex(strTableName, entry.getKey());

            // Collecting indexed column names and mappings in the WHERE clause.
            if (indexed && entry.getValue() instanceof Polygon) {
                indexedPolHashtable.put(entry.getKey(), entry.getValue());
                indexedPolColumns.add(entry.getKey());
            } else if (indexed) {
                indexedHashtable.put(entry.getKey(), entry.getValue());
                indexedColumns.add(entry.getKey());
            }

            // Collecting non-indexed column names and mappings in the WHERE clause.
            if (!indexed && entry.getValue() instanceof Polygon) {
                NonIndexedPolHashtable.put(entry.getKey(), entry.getValue());
                NonIndexedPolColumns.add(entry.getKey());
            } else if (!indexed) {
                NonIndexedHashtable.put(entry.getKey(), entry.getValue());
                NonIndexedColumns.add(entry.getKey());
            }
        }

        // Some of the columns in the WHERE clause are indexed and intersection of the pointers of these columns is used.
        if (indexedColumns.size() != 0 || indexedPolColumns.size() != 0) {
            ArrayList<TupleExtraPointer> toDelete = filter(strTableName, indexedColumns, indexedHashtable, NonIndexedColumns, NonIndexedHashtable, indexedPolColumns, indexedPolHashtable, NonIndexedPolColumns, NonIndexedPolHashtable);
            if (toDelete.size() != 0) {
                for (int k = 0; k < toDelete.size(); k++) {
                    String pagePath = toDelete.get(k).getPagePath();
                    int idx = toDelete.get(k).getIdx();

                    // Getting page number.
                    String str = "data/" + strTableName + "page";
                    String newStr = pagePath.replace(str, "");
                    int i = Integer.parseInt(newStr.charAt(0) + "");

                    // Removing the toDelete tuple.
                    Page p = (Page) readFile(pagePath);
                    Vector<Hashtable<String, Object>> vec = p.getTuples();
                    vec.remove(idx);
                    p.setTuples(vec);
                    writeFile(pagePath, p);


                    // For all indexed, non-polygon columns delete their pointers from the BPTrees, and adjust other tuple pointers in the same page.
                    for (int n = 0; n < allIndexes(strTableName).size(); n++) {
                        BPTree tree = (BPTree) readFile("data/" + strTableName + "indices" + allIndexes(strTableName).get(n) + "Btree.class");
                        Page pa = (Page) readFile(pagePath);


                        TuplePointer tp = new TuplePointer(toDelete.get(k).getIdx(),
                                toDelete.get(k).getPagePath(),
                                toDelete.get(k).getAllValues().get(allIndexes(strTableName).get(n)));
                        tree.deletePointer(tp);


                        for (int s = 0; s < pa.getTuples().size(); s++) {
                            if (s >= idx) {
                                Object key = pa.getTuples().get(s).get(allIndexes(strTableName).get(n));
                                TuplePointer newTPointer = new TuplePointer(s, pagePath, (Comparable) key);
                                TuplePointer oldTPointer = new TuplePointer(s + 1, pagePath, (Comparable) key);
                                tree.updateTuplePointer(oldTPointer, newTPointer);
                            }
                        }

                    }
                    // For all indexed, polygon columns delete their pointers from the BPTrees, and adjust other tuple pointers in the same page.
                    for (int n = 0; n < allPolIndexes(strTableName).size(); n++) {
                        RTree tree = (RTree) readFile("data/" + strTableName + "indices" + allPolIndexes(strTableName).get(n) + "Rtree.class");
                        Page pa = (Page) readFile(pagePath);

                        TuplePointer tp = new TuplePointer(toDelete.get(k).getIdx(),
                                toDelete.get(k).getPagePath(),
                                toDelete.get(k).getAllValues().get(allPolIndexes(strTableName).get(n)));
                        tree.deletePointer(tp);

                        for (int s = 0; s < pa.getTuples().size(); s++) {
                            if (s >= idx) {
                                Polygon key = (Polygon) pa.getTuples().get(s).get(allPolIndexes(strTableName).get(n));
                                TuplePointer newTPointer = new TuplePointer(s, pagePath, new CPolygon(key));
                                TuplePointer oldTPointer = new TuplePointer(s + 1, pagePath, new CPolygon(key));
                                tree.updateTuplePointer(oldTPointer, newTPointer);
                            }
                        }
                    }
                    // Adjusting the TupleExtraPointers in the toDelete.
                    for (int z = 0; z < toDelete.size(); z++) {

                        // Tuples in same page and index larger than the deleted tuple.
                        if ((toDelete.get(z).getPagePath().equals(toDelete.get(k).getPagePath())) &&
                                ((toDelete.get(z).getIdx()) > (toDelete.get(k).getIdx()))) {

                            Hashtable<String, Comparable> old = toDelete.get(z).getAllValues();
                            TupleExtraPointer oldT = toDelete.get(z);
                            TupleExtraPointer newT = new TupleExtraPointer(oldT.getIdx() - 1, oldT.getPagePath(), oldT.getKey());
                            newT.setAllValues(old);
                            toDelete.set(z, newT);
                        }
                    }

                    // If page now empty, delete it and move all page numbers upwards.
                    if (vec.size() == 0) {
                        File f = new File(pagePath);
                        if (i < Integer.parseInt(pnumber)) {
                            File f1 = new File(str + (i + 1) + ".class");
                            f.delete();
                            f1.renameTo(f);
                        } else {
                            f.delete();
                        }
                        for (int l = i + 1; l < Integer.parseInt(pnumber); l++) {
                            f = new File(str + l + ".class");
                            File f1 = new File(str + (l + 1) + ".class");
                            f1.renameTo(f);
                        }
                        ArrayList<String[]> alltables = (ArrayList<String[]>) readFile("data/tables.class");
                        for (int t = 0; t < alltables.size(); t++) {
                            if (alltables.get(t)[0].equals(strTableName)) {

                                String[] s = {alltables.get(t)[0], alltables.get(t)[1], (Integer.parseInt(alltables.get(t)[2]) - 1) + ""};
                                alltables.set(t, s);
                                writeFile("data/tables.class", alltables);
                            }
                        }

                        shiftIndicesPagePaths(strTableName, i, Integer.parseInt(pnumber) - 1);
                    }
                }
            }
        } else {
            pageloop:
            for (int i = 1; i <= Integer.parseInt(pnumber); i++) {
                String pagePath = "data/" + strTableName + "page" + i + ".class";
                Page currentpage = (Page) readFile(pagePath);
                Vector<Hashtable<String, Object>> vector = currentpage.getTuples();

                // Linear search on the vector.
                for (int j = 0; j < vector.size(); j++) {
                    flag = true;

                    // Loop on the WHERE clause.
                    maploop:
                    for (Map.Entry<String, Object> entry : entries) {

                        // Break out of page loop if tuple value of clustering key is larger than the one in the WHERE clause.
                        if (!(datatype.equals("java.awt.Polygon"))) {
                            if ((entry.getKey().equals(ckey)) &&
                                    (((Comparable) vector.get(j).get(ckey)).compareTo(entry.getValue()) > 0))
                                break pageloop;
                        } else if ((entry.getKey().equals(ckey)) &&
                                ((new CPolygon((Polygon) vector.get(j).get(ckey))).compareTo(entry.getValue()) > 0))
                            break pageloop;

                        // Decide if tuple value of a column is the same as the one in WHERE clause, and in the case of polygons: same coordinates.
                        String key = entry.getKey();
                        if ((vector.get(j).get(key) instanceof Polygon) &&
                                (!(samePol((Polygon) entry.getValue(), (Polygon) vector.get(j).get(key))))) {
                            flag = false;
                            break maploop;
                        } else if ((!(vector.get(j).get(key) instanceof Polygon)) &&
                                (!(entry.getValue().equals(vector.get(j).get(key))))) {
                            flag = false;
                            break maploop;
                        }
                    }

                    // If the tuple is valid for deletion.
                    if (flag) {

                        // Delete the tuple.
                        int idx = j;
                        Hashtable<String, Object> hashtable = vector.get(j);
                        vector.remove(j);
                        j--;
                        currentpage.setTuples(vector);
                        writeFile(pagePath, currentpage);

                        // For all indexed, non-polygon columns delete their pointers from the BPTrees, and adjust other tuple pointers in the same page.
                        for (int g = 0; g < allIndexes(strTableName).size(); g++) {
                            BPTree tree = (BPTree) readFile("data/" + strTableName + "indices" + allIndexes(strTableName).get(g) + "Btree.class");
                            Page pa = (Page) readFile(pagePath);

                            TuplePointer tp = new TuplePointer(idx,
                                    pagePath,
                                    (Comparable) hashtable.get(allIndexes(strTableName).get(g)));
                            tree.deletePointer(tp);

                            for (int s = 0; s < pa.getTuples().size(); s++) {
                                if (s >= idx) {
                                    Object key = pa.getTuples().get(s).get(allIndexes(strTableName).get(g));
                                    TuplePointer newTPointer = new TuplePointer(s, pagePath, (Comparable) key);
                                    TuplePointer oldTPointer = new TuplePointer(s + 1, pagePath, (Comparable) key);
                                    tree.updateTuplePointer(oldTPointer, newTPointer);
                                }
                            }
                        }

                        // For all indexed, polygon columns delete their pointers from the BPTrees, and adjust other tuple pointers in the same page.
                        for (int g = 0; g < allPolIndexes(strTableName).size(); g++) {
                            RTree tree = (RTree) readFile("data/" + strTableName + "indices" + allPolIndexes(strTableName).get(g) + "Rtree.class");
                            Page pa = (Page) readFile(pagePath);

                            TuplePointer tp = new TuplePointer(idx,
                                    pagePath,
                                    new CPolygon((Polygon) hashtable.get(allPolIndexes(strTableName).get(g))));
                            tree.deletePointer(tp);

                            for (int s = 0; s < pa.getTuples().size(); s++) {
                                if (s >= idx) {
                                    Polygon key = (Polygon) pa.getTuples().get(s).get(allPolIndexes(strTableName).get(g));
                                    TuplePointer newTPointer = new TuplePointer(s, pagePath, new CPolygon(key));
                                    TuplePointer oldTPointer = new TuplePointer(s + 1, pagePath, new CPolygon(key));
                                    tree.updateTuplePointer(oldTPointer, newTPointer);
                                }
                            }
                        }

                        // Delete the page if it has zero tuples.
                        if (vector.size() == 0) {
                            File f = new File("data/" + strTableName + "page" + i + ".class");
                            if (i < Integer.parseInt(pnumber)) {
                                File f1 = new File("data/" + strTableName + "page" + (i + 1) + ".class");
                                f.delete();
                                f1.renameTo(f);
                            } else {
                                f.delete();
                            }
                            for (int p = i + 1; p < Integer.parseInt(pnumber); p++) {
                                f = new File("data/" + strTableName + "page" + p + ".class");
                                File f1 = new File("data/" + strTableName + "page" + (p + 1) + ".class");
                                f1.renameTo(f);
                            }
                            ArrayList<String[]> alltables = (ArrayList<String[]>) readFile("data/tables.class");
                            for (int t = 0; t < alltables.size(); t++) {
                                if (alltables.get(t)[0].equals(strTableName)) {

                                    String[] s = {alltables.get(t)[0], alltables.get(t)[1], (Integer.parseInt(alltables.get(t)[2]) - 1) + ""};
                                    alltables.set(t, s);
                                    writeFile("data/tables.class", alltables);
                                }
                            }
                            shiftIndicesPagePaths(strTableName, i, Integer.parseInt(pnumber) - 1);
                            i--;
                        }
                    }
                }
            }
        }
    }
// ---------------------------------------------------------------------------------------------------------------------

    /**
     * Method 21.
     *
     * @param strTableName Table name.
     * @param strColName   Column name [not of type polygon].
     * @throws DBAppException
     */
    public void createBTreeIndex(String strTableName, String strColName) throws DBAppException {
        // 1. Check that table name is valid.
        String[] tinfo = getTable(strTableName);
        if (tinfo == null) throw new DBAppException("Table does not exist!");
        String pnumber = tinfo[2];

        /* 2. Check that column name is valid, and get a copy of the data.
           An exception is thrown if the column type is Polygon. */
        ArrayList<String[]> csvData = getTableColumns(strTableName);
        StringBuilder sb = new StringBuilder();
        boolean flag = false;

        for (String[] row : csvData) {
            if (row[1].equals(strColName)) {
                if (row[2].equals("java.awt.Polygon")) throw new DBAppException("Column type is Polygon!");
                row[4] = "True";
                flag = true;
            }
            for (int k = 0; k < 5; k++) {
                sb.append(row[k]);
                if (k < 4) sb.append(",");
            }
            sb.append("\n");
        }

        // 3. Throw an exception if the column name is invalid.
        if (!flag) throw new DBAppException("Column does not exist!");

        // 4. Write the updated csv data back.
        try (FileWriter writer = new FileWriter("data/metadata.csv")) {
            writer.write(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 5. Create a new B+ tree index. And then insert the entries in the index.
        BPTree tr = new BPTree(strTableName, strColName);
        for (int j = 1; j <= Integer.parseInt(pnumber); j++) {
            String pagePath = "data/" + strTableName + "page" + j + ".class";
            Page p = (Page) readFile(pagePath);
            Vector<Hashtable<String, Object>> v = p.getTuples();

            for (int k = 0; k < v.size(); k++)
                tr.insert((Comparable) v.get(k).get(strColName), pagePath, k);
        }
    }

// ---------------------------------------------------------------------------------------------------------------------

    /**
     * Method 22.
     *
     * @param strTableName Table name.
     * @param strColName   Column name [of type polygon].
     * @throws DBAppException
     */
    public void createRTreeIndex(String strTableName, String strColName) throws DBAppException {

        // 1. Check that table name is valid.
        String[] tinfo = getTable(strTableName);
        if (tinfo == null) throw new DBAppException("Table does not exist!");
        String pnumber = tinfo[2];

        /* 2. Check that column name is valid, and get a copy of the data.
           An exception is thrown if the column type is Polygon. */
        ArrayList<String[]> csvData = getTableColumns(strTableName);
        StringBuilder sb = new StringBuilder();
        boolean flag = false;

        for (String[] row : csvData) {
            if (row[1].equals(strColName)) {
                if (!row[2].equals("java.awt.Polygon")) throw new DBAppException("Column type is not polygon!");
                row[4] = "True";
                flag = true;
            }
            for (int k = 0; k < 5; k++) {
                sb.append(row[k]);
                if (k < 4) sb.append(",");
            }
            sb.append("\n");
        }

        // 3. Throw an exception if the column name is invalid.
        if (!flag) throw new DBAppException("Column does not exist!");

        // 4. Write the updated csv data back.
        try (FileWriter writer = new FileWriter("data/metadata.csv")) {
            writer.write(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        RTree tr = new RTree(strTableName, strColName);
        for (int j = 1; j <= Integer.parseInt(pnumber); j++) {
            String pagePath = "data/" + strTableName + "page" + j + ".class";
            Page p = (Page) readFile(pagePath);
            Vector<Hashtable<String, Object>> v = p.getTuples();

            for (int k = 0; k < v.size(); k++)
                tr.insert(new CPolygon((Polygon) (v.get(k).get(strColName))), pagePath, k);
        }

    }
// ---------------------------------------------------------------------------------------------------------------------

    /**
     * Method 23.
     *
     * @param strTableName Table name
     * @param columnName   Column name
     * @param strKey       The value of the column as a string
     * @return The parsed value according to the column type
     */
    public Object parsedValue(String strTableName, String columnName, String strKey) throws DBAppException {

        // 1. Check column type.
        String datatype = columnType(strTableName, columnName);

        // 2. Parsing the strKey according to the data type we found from the metadata file.
        Object strKeyParsed = null;
        try {
            switch (datatype) {
                case "java.lang.Integer":
                    strKeyParsed = Integer.parseInt(strKey);
                    break;
                case "java.lang.Double":
                    strKeyParsed = Double.parseDouble(strKey);
                    break;
                case "java.lang.String":
                    strKeyParsed = strKey;
                    break;
                case "java.lang.Boolean":
                    strKeyParsed = Boolean.parseBoolean(strKey);
                    break;
                case "java.util.Date":
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
                    Date date = sdf.parse(strKey);
                    strKeyParsed = date;
                    break;
                case "java.awt.Polygon":
                    String[] points = strKey.split("[(,)]");
                    for (int i = 0; i < points.length; i++)
                        points[i] = points[i].replace(" ", "");

                    int[] x = new int[points.length / 2];
                    int[] y = new int[points.length / 2];
                    int j = 0;

                    for (int i = 1; i < points.length; i = i + 4) {
                        x[j] = Integer.parseInt(points[i]);
                        y[j] = Integer.parseInt(points[i + 1]);
                        j++;
                    }
                    strKeyParsed = new CPolygon(new Polygon(x, y, j));
            }
        } catch (Exception e) {
            throw new DBAppException("Clustering key value does not match the data type for this column.");
        }
        return strKeyParsed;
    }
// ---------------------------------------------------------------------------------------------------------------------

    /**
     * Method 24.
     *
     * @param x              Single entry in a table.
     * @param parsedObjValue Parsed object against which the comparison is done.
     * @param operator       Comparison operator [>, <, >=, <=, =, !=].
     * @param columnName     Column name.
     * @return Returns true if the hashtable satisfies the condition and false if the hashtable does not satisfy it.
     */
    public boolean checkOperator(Hashtable<String, Object> x, Object parsedObjValue, String operator, String columnName) {
        if (parsedObjValue instanceof CPolygon) {
            CPolygon tablePolygon = new CPolygon((Polygon) x.get(columnName));

            switch (operator) {
                case ">":
                    if (tablePolygon.compareTo(parsedObjValue) > 0)
                        return true;
                    break;
                case "<":
                    if (tablePolygon.compareTo(parsedObjValue) < 0)
                        return true;
                    break;
                case ">=":
                    if (tablePolygon.compareTo(parsedObjValue) >= 0)
                        return true;
                    break;
                case "<=":
                    if (tablePolygon.compareTo(parsedObjValue) <= 0)
                        return true;
                    break;
                case "=":
                    if (tablePolygon.compareTo(parsedObjValue) == 0)
                        return true;
                    break;
                case "!=":
                    if (tablePolygon.compareTo(parsedObjValue) != 0)
                        return true;
            }
            return false;
        } else {
            Comparable tableObject = (Comparable) x.get(columnName);
            switch (operator) {
                case ">":
                    if (tableObject.compareTo(parsedObjValue) > 0)
                        return true;
                    break;
                case "<":
                    if (tableObject.compareTo(parsedObjValue) < 0)
                        return true;
                    break;
                case ">=":
                    if (tableObject.compareTo(parsedObjValue) > 0 || tableObject.compareTo(parsedObjValue) == 0)
                        return true;
                    break;
                case "<=":
                    if (tableObject.compareTo(parsedObjValue) < 0 || tableObject.compareTo(parsedObjValue) == 0)
                        return true;
                    break;
                case "=":
                    if (tableObject.compareTo(parsedObjValue) == 0)
                        return true;
                    break;
                case "!=":
                    if (tableObject.compareTo(parsedObjValue) != 0)
                        return true;
            }
            return false;

        }
    }
// ---------------------------------------------------------------------------------------------------------------------

    /**
     * Method 25.
     */
    public Vector<Hashtable<String, Object>> getKeysInLeaf(Leaf l, int operator, Comparable key) {

        Vector<Hashtable<String, Object>> includedKeys = new Vector<Hashtable<String, Object>>();
        switch (operator) {
            case 2:
                for (Pointer p : l.getPointers()) {
                    if (p.getKey().compareTo(key) != 0) {
                        if (p instanceof TuplePointer) {
                            TuplePointer tptr = (TuplePointer) p;
                            Page page = (Page) readFile(tptr.getPagePath());
                            includedKeys.add(page.getTuples().get(tptr.getIdx()));
                        } else {
                            OverFlowPage overflow = (OverFlowPage) readFile(p.getPagePath());
                            ArrayList<TuplePointer> tptrs = overflow.getPointers();
                            for (TuplePointer tptr : tptrs) {
                                Page page = (Page) readFile(tptr.getPagePath());
                                includedKeys.add(page.getTuples().get(tptr.getIdx()));
                            }
                        }
                    }
                }
                break;
            case 3:
                for (Pointer p : l.getPointers()) {
                    if (p.getKey().compareTo(key) > 0) {
                        if (p instanceof TuplePointer) {
                            TuplePointer tptr = (TuplePointer) p;
                            Page page = (Page) readFile(tptr.getPagePath());
                            includedKeys.add(page.getTuples().get(tptr.getIdx()));
                        } else {
                            OverFlowPage overflow = (OverFlowPage) readFile(p.getPagePath());
                            ArrayList<TuplePointer> tptrs = overflow.getPointers();
                            for (TuplePointer tptr : tptrs) {
                                Page page = (Page) readFile(tptr.getPagePath());
                                includedKeys.add(page.getTuples().get(tptr.getIdx()));
                            }
                        }
                    }
                }
                break;
            case 4:
                for (Pointer p : l.getPointers()) {
                    if (p.getKey().compareTo(key) >= 0) {
                        if (p instanceof TuplePointer) {
                            TuplePointer tptr = (TuplePointer) p;
                            Page page = (Page) readFile(tptr.getPagePath());
                            includedKeys.add(page.getTuples().get(tptr.getIdx()));
                        } else {
                            OverFlowPage overflow = (OverFlowPage) readFile(p.getPagePath());
                            ArrayList<TuplePointer> tptrs = overflow.getPointers();
                            for (TuplePointer tptr : tptrs) {
                                Page page = (Page) readFile(tptr.getPagePath());
                                includedKeys.add(page.getTuples().get(tptr.getIdx()));
                            }
                        }
                    }
                }
                break;
            case 5:
                for (Pointer p : l.getPointers()) {
                    if (p.getKey().compareTo(key) < 0) {
                        if (p instanceof TuplePointer) {
                            TuplePointer tptr = (TuplePointer) p;
                            Page page = (Page) readFile(tptr.getPagePath());
                            includedKeys.add(page.getTuples().get(tptr.getIdx()));
                        } else {
                            OverFlowPage overflow = (OverFlowPage) readFile(p.getPagePath());
                            ArrayList<TuplePointer> tptrs = overflow.getPointers();
                            for (TuplePointer tptr : tptrs) {
                                Page page = (Page) readFile(tptr.getPagePath());
                                includedKeys.add(page.getTuples().get(tptr.getIdx()));
                            }
                        }
                    }
                }
                break;
            case 6:
                for (Pointer p : l.getPointers()) {
                    if (p.getKey().compareTo(key) <= 0) {
                        if (p instanceof TuplePointer) {
                            TuplePointer tptr = (TuplePointer) p;
                            Page page = (Page) readFile(tptr.getPagePath());
                            includedKeys.add(page.getTuples().get(tptr.getIdx()));
                        } else {
                            OverFlowPage overflow = (OverFlowPage) readFile(p.getPagePath());
                            ArrayList<TuplePointer> tptrs = overflow.getPointers();
                            for (TuplePointer tptr : tptrs) {
                                Page page = (Page) readFile(tptr.getPagePath());
                                includedKeys.add(page.getTuples().get(tptr.getIdx()));
                            }
                        }
                    }
                }
                break;
        }
        return includedKeys;
    }

// ---------------------------------------------------------------------------------------------------------------------

    /**
     * Method 26.
     */
    public Vector<Hashtable<String, Object>> searchBTree(Object tree, Comparable key, String operator) throws DBAppException {
        Vector<Hashtable<String, Object>> partialResult = new Vector<>();
        Boolean useR = false;
        if (tree instanceof RTree) {
            useR = true;
        }
        if (checkOperatorIndexedCol(operator) == 1) {
            Pointer ptr;
            if (useR) ptr = ((RTree) tree).findCoordinates(key); //coordinates are used in comparison
            else ptr = ((BPTree) tree).find(key);
            if (ptr != null) {
                if (ptr instanceof TuplePointer) {
                    TuplePointer tptr = (TuplePointer) ptr;
                    Page p = (Page) readFile(tptr.getPagePath());
                    partialResult.add(p.getTuples().get(tptr.getIdx()));
                } else {
                    OverFlowPage overflow = (OverFlowPage) readFile(ptr.getPagePath());
                    ArrayList<TuplePointer> tptrs = overflow.getPointers();
                    for (TuplePointer tptr : tptrs) {
                        Page p = (Page) readFile(tptr.getPagePath());
                        partialResult.add(p.getTuples().get(tptr.getIdx()));
                    }
                }
            }
        } else if (checkOperatorIndexedCol(operator) == 2) {
            Leaf leftmost;
            if (useR) leftmost = ((RTree) tree).getLeftmostLeaf();
            else leftmost = ((BPTree) tree).getLeftmostLeaf();
            Vector<Hashtable<String, Object>> v = getKeysInLeaf(leftmost, 2, key);
            for (Hashtable<String, Object> h : v)
                partialResult.add(h);
            while (leftmost.getNextLeafPath() != null) {
                leftmost = (Leaf) readFile(leftmost.getNextLeafPath());
                System.out.println("path " + leftmost.getNextLeafPath());
                v = getKeysInLeaf(leftmost, 2, key);
                for (Hashtable<String, Object> h : v)
                    partialResult.add(h);
            }
        } else if (checkOperatorIndexedCol(operator) == 3 || checkOperatorIndexedCol(operator) == 4) {
            Leaf keyLeaf;
            if (useR) keyLeaf = ((RTree) tree).getLeaf(key);
            else keyLeaf = ((BPTree) tree).getLeaf(key);
            if (keyLeaf != null) {
                Vector<Hashtable<String, Object>> v = getKeysInLeaf(keyLeaf, checkOperatorIndexedCol(operator), key);
                for (Hashtable<String, Object> h : v)
                    partialResult.add(h);
                while (keyLeaf.getNextLeafPath() != null) {
                    keyLeaf = (Leaf) readFile(keyLeaf.getNextLeafPath());
                    v = getKeysInLeaf(keyLeaf, checkOperatorIndexedCol(operator), key);
                    for (Hashtable<String, Object> h : v)
                        partialResult.add(h);
                }
            }
        } else if (checkOperatorIndexedCol(operator) == 5 || checkOperatorIndexedCol(operator) == 6) {
            Leaf keyLeaf, current;
            if (useR) {
                keyLeaf = ((RTree) tree).getLeaf(key);
                current = ((RTree) tree).getLeftmostLeaf();
            } else {
                keyLeaf = ((BPTree) tree).getLeaf(key);
                current = ((BPTree) tree).getLeftmostLeaf();
            }
            if (keyLeaf != null) {
                boolean equal = false;
                do {
                    equal = true;
                    Vector<Hashtable<String, Object>> v1 = getKeysInLeaf(current, checkOperatorIndexedCol(operator), key);
                    Vector<Hashtable<String, Object>> v2 = getKeysInLeaf(keyLeaf, checkOperatorIndexedCol(operator), key);
                    if (v1.size() == v2.size()) {
                        for (int i = 0; i < v1.size(); i++) {
                            Set<Map.Entry<String, Object>> sh = v1.get(i).entrySet();
                            Hashtable<String, Object> h2 = v2.get(i);
                            for (Map.Entry<String, Object> m : sh) {
                                Comparable value, otherValue;
                                if (m.getValue() instanceof java.awt.Polygon) {
                                    value = new CPolygon((Polygon) m.getValue());
                                    otherValue = new CPolygon((Polygon) h2.get(m.getKey()));
                                } else {
                                    value = (Comparable) m.getValue();
                                    otherValue = (Comparable) h2.get(m.getKey());
                                }
                                if (value.compareTo(otherValue) != 0)
                                    equal = false;
                            }
                        }
                    } else equal = false;
                    for (Hashtable<String, Object> h : v1)
                        partialResult.add(h);
                    if (current.getNextLeafPath() != null)
                        current = (Leaf) readFile(current.getNextLeafPath());
                } while (!equal);
            }
        }
        return partialResult;
    }

// ---------------------------------------------------------------------------------------------------------------------

    /**
     * Method 27.
     */
    public Vector<Hashtable<String, Object>> loopPagesForward(String tablename, Comparable keyValue, Vector<Hashtable<String, Object>> vector, int idx, String operator, int currentPage, int maxPage) {
        Vector<Hashtable<String, Object>> partial1 = new Vector<Hashtable<String, Object>>();
        if (operator.equals("=")) {
            for (int i = idx; i < vector.size(); i++) {
                Comparable value;
                if (vector.get(i).get(findCkey(tablename)) instanceof java.awt.Polygon)
                    value = new CPolygon((Polygon) vector.get(i).get(findCkey(tablename)));
                else value = (Comparable) vector.get(i).get(findCkey(tablename));
                if (keyValue.compareTo(value) == 0)
                    partial1.add(vector.get(i));
                else return partial1;
            }
            for (int i = currentPage + 1; i <= maxPage; i++) {
                Page p = (Page) readFile("data/" + tablename + "page" + i + ".class");
                vector = p.getTuples();
                for (int j = 0; j < p.getTuples().size(); j++) {
                    Comparable value;
                    if (vector.get(j).get(findCkey(tablename)) instanceof java.awt.Polygon)
                        value = new CPolygon((Polygon) vector.get(j).get(findCkey(tablename)));
                    else value = (Comparable) vector.get(j).get(findCkey(tablename));
                    if (keyValue.compareTo(value) == 0)
                        partial1.add(vector.get(j));
                    else return partial1;
                }
            }
        } else if (operator.equals(">=")) {
            for (int i = idx; i < vector.size(); i++)
                partial1.add(vector.get(i));

            for (int i = currentPage + 1; i <= maxPage; i++) {
                Page p = (Page) readFile("data/" + tablename + "page" + i + ".class");
                vector = p.getTuples();
                for (int j = 0; j < vector.size(); j++)
                    partial1.add(vector.get(j));
            }
        } else if (operator.equals(">")) {
            for (int i = idx + 1; i < vector.size(); i++) {
                Comparable value;
                if (vector.get(i).get(findCkey(tablename)) instanceof java.awt.Polygon)
                    value = new CPolygon((Polygon) vector.get(i).get(findCkey(tablename)));
                else value = (Comparable) vector.get(i).get(findCkey(tablename));
                if (keyValue.compareTo(value) < 0)
                    partial1.add(vector.get(i));
            }
            for (int i = currentPage + 1; i <= maxPage; i++) {
                Page p = (Page) readFile("data/" + tablename + "page" + i + ".class");
                vector = p.getTuples();
                for (int j = 0; j < vector.size(); j++) {
                    Comparable value;
                    if (vector.get(j).get(findCkey(tablename)) instanceof java.awt.Polygon)
                        value = new CPolygon((Polygon) vector.get(j).get(findCkey(tablename)));
                    else value = (Comparable) vector.get(j).get(findCkey(tablename));
                    if (keyValue.compareTo(value) < 0)
                        partial1.add(vector.get(j));
                }
            }
        }
        return partial1;
    }
// ---------------------------------------------------------------------------------------------------------------------

    /**
     * Method 28.
     */
    public Vector<Hashtable<String, Object>> loopPagesBackward(String tablename, Comparable keyValue, int idx, String operator, int currentPage) {
        Vector<Hashtable<String, Object>> partial1 = new Vector<Hashtable<String, Object>>();
        Vector<Hashtable<String, Object>> vector;
        if (operator.equals("<")) {
            pages:
            for (int i = 1; i < currentPage + 1; i++) {
                Page p = (Page) readFile("data/" + tablename + "page" + i + ".class");
                vector = p.getTuples();
                for (int j = 0; j < vector.size(); j++) {
                    Comparable value;
                    if (vector.get(j).get(findCkey(tablename)) instanceof java.awt.Polygon)
                        value = new CPolygon((Polygon) vector.get(j).get(findCkey(tablename)));
                    else value = (Comparable) vector.get(j).get(findCkey(tablename));
                    if (keyValue.compareTo(value) > 0)
                        partial1.add(vector.get(j));
                    if (i == currentPage && j == idx) break pages;
                }
            }
        } else if (operator.equals("<=")) {
            pages:
            for (int i = 1; i < currentPage + 1; i++) {
                Page p = (Page) readFile("data/" + tablename + "page" + i + ".class");
                vector = p.getTuples();
                for (int j = 0; j < vector.size(); j++) {
                    partial1.add(vector.get(j));
                    if (i == currentPage && j == idx) break pages;
                }
            }
        }
        return partial1;
    }

// ---------------------------------------------------------------------------------------------------------------------

    /**
     * Method 29.
     *
     * @param partial1 Vector of first set of hashtables.
     * @param partial2 Vector of second set of hashtables.
     * @return Their intersection.
     */
    public Vector<Hashtable<String, Object>> intersect(Vector<Hashtable<String, Object>> partial1, Vector<Hashtable<String, Object>> partial2) {
        Vector<Hashtable<String, Object>> partialResult = new Vector<Hashtable<String, Object>>();
        for (int i = 0; i < partial1.size(); i++) {
            Set<Map.Entry<String, Object>> h1 = partial1.get(i).entrySet();
            boolean identical = false;
            int j = 0;
            for (j = 0; j < partial2.size(); j++) {
                Hashtable<String, Object> h2 = partial2.get(j);
                for (Map.Entry<String, Object> values : h1) {
                    Comparable value, otherValue;
                    if (values.getValue() instanceof java.awt.Polygon) {
                        value = new CPolygon((Polygon) values.getValue());
                        otherValue = new CPolygon((Polygon) h2.get(values.getKey()));
                    } else {
                        value = (Comparable) values.getValue();
                        otherValue = (Comparable) h2.get(values.getKey());
                    }
                    if (value.compareTo(otherValue) == 0) identical = true;
                    else {
                        identical = false;
                        break;
                    }
                }
                if (identical) break;
            }
            if (identical)
                partialResult.add(partial1.get(i));
        }
        return partialResult;
    }

// ---------------------------------------------------------------------------------------------------------------------

    /**
     * Method 30.
     *
     * @param partial1 Vector of first set of hashtables.
     * @param partial2 Vector of second set of hashtables.
     * @return Their Xclusive or.
     */
    public Vector<Hashtable<String, Object>> XclusiveOr(Vector<Hashtable<String, Object>> partial1, Vector<Hashtable<String, Object>> partial2) {
        Vector<Hashtable<String, Object>> partialResult = new Vector<Hashtable<String, Object>>();
        Vector<Hashtable<String, Object>> partial3 = intersect(partial1, partial2);

        for (int i = 0; i < partial3.size(); i++) {
            Set<Map.Entry<String, Object>> h3 = partial3.get(i).entrySet();
            for (int j = 0; j < partial1.size(); j++) {
                Hashtable<String, Object> h1 = partial1.get(j);
                boolean identical = true;
                for (Map.Entry<String, Object> m : h3) {
                    Comparable value, otherValue;
                    if (m.getValue() instanceof java.awt.Polygon) {
                        value = new CPolygon((Polygon) m.getValue());
                        otherValue = new CPolygon((Polygon) h1.get(m.getKey()));
                    } else {
                        value = (Comparable) m.getValue();
                        otherValue = (Comparable) h1.get(m.getKey());
                    }
                    if (value.compareTo(otherValue) != 0) {
                        identical = false;
                        break;
                    }
                }
                if (identical) {
                    partial1.remove(j);
                    --j;
                }
            }
        }

        for (int i = 0; i < partial3.size(); i++) {
            Set<Map.Entry<String, Object>> h3 = partial3.get(i).entrySet();
            for (int j = 0; j < partial2.size(); j++) {
                Hashtable<String, Object> h2 = partial2.get(j);
                boolean identical = true;
                for (Map.Entry<String, Object> m : h3) {
                    Comparable value, otherValue;
                    if (m.getValue() instanceof java.awt.Polygon) {
                        value = new CPolygon((Polygon) m.getValue());
                        otherValue = new CPolygon((Polygon) h2.get(m.getKey()));
                    } else {
                        value = (Comparable) m.getValue();
                        otherValue = (Comparable) h2.get(m.getKey());
                    }
                    if (value.compareTo(otherValue) != 0) {
                        identical = false;
                        break;
                    }
                }
                if (identical) {
                    partial2.remove(j);
                    --j;
                }
            }
        }

        for (int i = 0; i < partial1.size(); i++)
            partialResult.add(partial1.get(i));
        for (int i = 0; i < partial2.size(); i++)
            partialResult.add(partial2.get(i));
        return partialResult;
    }

// ---------------------------------------------------------------------------------------------------------------------

    /**
     * Method 31.
     *
     * @param partial1 Vector of first set of hashtables.
     * @param partial2 Vector of second set of hashtables.
     * @return Their union.
     */

    public Vector<Hashtable<String, Object>> union(Vector<Hashtable<String, Object>> partial1, Vector<Hashtable<String, Object>> partial2) {
        Vector<Hashtable<String, Object>> partialResult = new Vector<>();

        for (int i = 0; i < partial1.size(); i++) {
            Set<Map.Entry<String, Object>> h1 = partial1.get(i).entrySet();
            for (int j = 0; j < partial2.size(); j++) {
                Hashtable<String, Object> h2 = partial2.get(j);
                boolean identical = true;
                for (Map.Entry<String, Object> values : h1) {
                    Comparable value, otherValue;
                    if (values.getValue() instanceof java.awt.Polygon) {
                        value = new CPolygon((Polygon) values.getValue());
                        otherValue = new CPolygon((Polygon) h2.get(values.getKey()));
                    } else {
                        value = (Comparable) values.getValue();
                        otherValue = (Comparable) h2.get(values.getKey());
                    }
                    if (value.compareTo(otherValue) != 0) {
                        identical = false;
                        break;
                    }
                }
                if (identical) {
                    partial2.remove(j);
                    break;
                }
            }
        }
        for (Hashtable<String, Object> h1 : partial1) {
            partialResult.add(h1);
        }
        for (Hashtable<String, Object> h2 : partial2) {
            partialResult.add(h2);
        }
        return partialResult;
    }
// ---------------------------------------------------------------------------------------------------------------------

    /**
     * Method 32.
     *
     * @param arrSQLTerms     WHERE clause conditions.
     * @param strarrOperators Operators between the different conditions.
     * @return Iterator for the select tuples.
     * @throws DBAppException
     */
    public Iterator selectFromTable(SQLTerm[] arrSQLTerms, String[] strarrOperators) throws DBAppException {

        String tableName;
        String columnName;
        String operator;
        Object objValue;

        Vector<Hashtable<String, Object>> selectResult;
        Vector<Hashtable<String, Object>> partial1 = new Vector<Hashtable<String, Object>>();

        ArrayList<String[]> columns;
        boolean isPolygon;
        String indexPath;
        Object colTree;
        BPTree colBTree;
        RTree colRTree;

        tableName = arrSQLTerms[0]._strTableName;
        columnName = arrSQLTerms[0]._strColumnName;
        operator = arrSQLTerms[0]._strOperator;
        objValue = arrSQLTerms[0]._objValue;

        checkOperatorIndexedCol(operator);

        String[] tinfo = getTable(tableName);
        if (tinfo == null) throw new DBAppException("Table does not exist!");
        if (objValue instanceof Polygon) objValue = new CPolygon((Polygon) objValue);
        if (!hasIndex(tableName, columnName)) {
            String pnumber = tinfo[2];

            pageloop:
            for (int j = 1; j <= Integer.parseInt(pnumber); j++) {
                Page p = (Page) readFile("data/" + tableName + "page" + j + ".class");
                Vector<Hashtable<String, Object>> v = p.getTuples();

                if (columnName.equals(findCkey(tableName)) && !operator.equals("!=") && !(objValue instanceof CPolygon && operator.equals("="))) {
                    int min = 0;
                    int max = v.size() - 1;
                    int idx = -1;
                    if (operator.equals("=") || operator.equals(">=") || operator.equals(">")) {
                        while (min <= max) {
                            int mid = min + (max - min + 1) / 2;
                            Comparable currentValue;
                            if (v.get(mid).get(findCkey(tableName)) instanceof Polygon)
                                currentValue = new CPolygon((Polygon) v.get(mid).get(findCkey(tableName)));
                            else currentValue = (Comparable) v.get(mid).get(findCkey(tableName));
                            if (currentValue.compareTo(objValue) < 0) {
                                min = mid + 1;
                            }
                            if (currentValue.compareTo(objValue) > 0) {
                                max = mid - 1;
                            }
                            if (currentValue.compareTo(objValue) == 0) {
                                idx = mid;
                                max = mid - 1;
                            }
                        }
                        if (idx > -1)
                            partial1 = loopPagesForward(tableName, (Comparable) objValue, v, idx, operator, j, Integer.parseInt(pnumber));
                    } else if (operator.equals("<") || operator.equals("<=")) {
                        while (min <= max) {
                            int mid = min + (max - min + 1) / 2;
                            Comparable currentValue;
                            if (v.get(mid).get(findCkey(tableName)) instanceof Polygon)
                                currentValue = new CPolygon((Polygon) v.get(mid).get(findCkey(tableName)));
                            else currentValue = (Comparable) v.get(mid).get(findCkey(tableName));
                            if (currentValue.compareTo(objValue) < 0) {
                                min = mid + 1;
                            }
                            if (currentValue.compareTo(objValue) > 0) {
                                max = mid - 1;
                            }
                            if (currentValue.compareTo(objValue) == 0) {
                                idx = mid;
                                min = mid + 1;
                            }
                        }
                        if (idx > -1)
                            partial1 = loopPagesBackward(tableName, (Comparable) objValue, idx, operator, j);
                    }
                    if (partial1.size() > 0)
                        break pageloop;
                } else {
                    for (int k = 0; k < v.size(); k++) {
                        if (objValue instanceof CPolygon && operator.equals("=")) {
                            CPolygon cp = new CPolygon((Polygon) v.get(k).get(columnName));
                            if (((CPolygon) objValue).equal(cp))
                                partial1.add(v.get(k));
                        } else if (checkOperator(v.get(k), objValue, operator, columnName))
                            partial1.add(v.get(k));
                    }
                }
            }
        } else {
            columns = getTableColumns(tableName);
            isPolygon = false;
            for (String[] x : columns) {
                if (x[1].equals(columnName)) {
                    if (x[2].equals("java.awt.Polygon"))
                        isPolygon = true;
                    break;
                }
            }

            if (isPolygon)
                indexPath = "data/" + tableName + "indices" + columnName + "Rtree.class";
            else
                indexPath = "data/" + tableName + "indices" + columnName + "Btree.class";

            colTree = readFile(indexPath);

            if (colTree instanceof BPTree) {
                colBTree = (BPTree) colTree;
                partial1 = searchBTree(colBTree, (Comparable) objValue, operator);
            } else {
                colRTree = (RTree) colTree;
                partial1 = searchBTree(colRTree, (Comparable) objValue, operator);
            }
        }

        for (int j = 0; j < strarrOperators.length; j++) {
            tableName = arrSQLTerms[j + 1]._strTableName;
            columnName = arrSQLTerms[j + 1]._strColumnName;
            operator = arrSQLTerms[j + 1]._strOperator;
            objValue = arrSQLTerms[j + 1]._objValue;

            checkOperatorIndexedCol(operator);

            if (objValue instanceof Polygon) objValue = new CPolygon((Polygon) objValue);
            Vector<Hashtable<String, Object>> partial2 = new Vector<>();

            tinfo = getTable(tableName);
            if (tinfo == null) throw new DBAppException("Table does not exist!");

            if (!hasIndex(tableName, columnName)) {
                String pnumber = tinfo[2];
                pageloop2:
                for (int l = 1; l <= Integer.parseInt(pnumber); l++) {
                    Page p = (Page) readFile("data/" + tableName + "page" + l + ".class");
                    Vector<Hashtable<String, Object>> v = p.getTuples();

                    if (columnName.equals(findCkey(tableName)) && !operator.equals("!=") && !(objValue instanceof CPolygon && operator.equals("="))) {
                        int min = 0;
                        int max = v.size() - 1;
                        int idx = -1;
                        if (operator.equals("=") || operator.equals(">=") || operator.equals(">")) {
                            while (min <= max) {
                                int mid = min + (max - min + 1) / 2;
                                Comparable currentValue;

                                if (v.get(mid).get(findCkey(tableName)) instanceof Polygon)
                                    currentValue = new CPolygon((Polygon) v.get(mid).get(findCkey(tableName)));
                                else currentValue = (Comparable) v.get(mid).get(findCkey(tableName));

                                if (currentValue.compareTo((Comparable) objValue) < 0) {
                                    min = mid + 1;
                                }
                                if (currentValue.compareTo((Comparable) objValue) > 0) {
                                    max = mid - 1;
                                }
                                if (currentValue.compareTo((Comparable) objValue) == 0) {
                                    idx = mid;
                                    max = mid - 1;
                                }
                            }

                            if (idx > -1)
                                partial2 = loopPagesForward(tableName, (Comparable) objValue, v, idx, operator, l, Integer.parseInt(pnumber));

                        } else if (operator.equals("<") || operator.equals("<=")) {
                            while (min <= max) {
                                int mid = min + (max - min + 1) / 2;
                                Comparable currentValue;
                                if (v.get(mid).get(findCkey(tableName)) instanceof Polygon)
                                    currentValue = new CPolygon((Polygon) v.get(mid).get(findCkey(tableName)));
                                else currentValue = (Comparable) v.get(mid).get(findCkey(tableName));
                                if (currentValue.compareTo(objValue) < 0) {
                                    min = mid + 1;
                                }
                                if (currentValue.compareTo(objValue) > 0) {
                                    max = mid - 1;
                                }
                                if (currentValue.compareTo(objValue) == 0) {
                                    idx = mid;
                                    min = mid + 1;
                                }
                            }
                            if (idx > -1)
                                partial2 = loopPagesBackward(tableName, (Comparable) objValue, idx, operator, l);
                        }
                        if (partial2.size() > 0)
                            break pageloop2;
                    } else {
                        for (int k = 0; k < v.size(); k++)
                            if (objValue instanceof CPolygon && operator.equals("=")) {
                                CPolygon cp = new CPolygon((Polygon) v.get(k).get(columnName));
                                if (((CPolygon) objValue).equal(cp))
                                    partial2.add(v.get(k));
                            } else if (checkOperator(v.get(k), objValue, operator, columnName))
                                partial2.add(v.get(k));
                    }
                }

            } else {
                columns = getTableColumns(tableName);
                isPolygon = false;
                for (String[] x : columns) {
                    if (x[1].equals(columnName)) {
                        if (x[2].equals("java.awt.Polygon"))
                            isPolygon = true;
                        break;
                    }
                }
                if (isPolygon)
                    indexPath = "data/" + tableName + "indices" + columnName + "Rtree.class";
                else
                    indexPath = "data/" + tableName + "indices" + columnName + "Btree.class";

                colTree = readFile(indexPath);
                if (colTree instanceof BPTree) {
                    colBTree = (BPTree) colTree;
                    partial2 = searchBTree(colBTree, (Comparable) objValue, operator);
                } else {
                    colRTree = (RTree) colTree;
                    partial2 = searchBTree(colRTree, (Comparable) objValue, operator);
                }
            }
            switch (strarrOperators[j]) {
                case "AND":
                    partial1 = intersect(partial1, partial2);
                    break;
                case "OR":
                    partial1 = union(partial1, partial2);
                    break;
                case "XOR":
                    partial1 = XclusiveOr(partial1, partial2);
                    break;
                default:
                    throw new DBAppException("Unsupported operator detected.");
            }
        }
        selectResult = partial1;
        return selectResult.iterator();

    }
// ---------------------------------------------------------------------------------------------------------------------

    /** Method 33.
     * @param tIndex    Index where i want to shift down starting from.
     * @param table     Table name.
     * @param m         Current page number.
     * @param pagetotal Total number of pages of that table.
     */
    public void shiftPage(int tIndex, String table, int m, String pagetotal, ArrayList<String> columnsindex, ArrayList<String> columnstype) {
        Page p = (Page) readFile("data/" + table + "page" + m + ".class"); // Read the page.
        Hashtable<String, Object> last = p.getTuples().get(p.getTuples().size() - 1); // Last tuple in the page.
        //new code BPTREE
        for (int l = 0; l < columnsindex.size(); l++) {
            if (columnstype.get(l).equals("java.awt.Polygon")) {
                RTree tree = (RTree) readFile("data/" + table + "indices" + columnsindex.get(l) + "Rtree.class");
                Set<Map.Entry<String, Object>> entries = last.entrySet();
                for (Map.Entry<String, Object> pair : entries) {
                    if (pair.getKey().equals(columnsindex.get(l))) {
                        CPolygon a = (CPolygon) new CPolygon((Polygon) pair.getValue());
                        if (!(tree.find((Comparable) (a)) instanceof TuplePointer)) {
                            OverFlowPage ovp = (OverFlowPage) DBApp.readFile(tree.find((Comparable) (a)).getPagePath());
                            for (int j = 0; j < ovp.getPointers().size(); j++) {
                                if (ovp.getPointers().get(j).getPagePath().equals("data/" + table + "page" + m + ".class") && ovp.getPointers().get(j).getIdx() == (p.getTuples().size() - 1)) {
                                    tree.updateTuplePointer(new TuplePointer(p.getTuples().size() - 1, "data/" + table + "page" + m + ".class", (Comparable) a), new TuplePointer(-1, "data/" + table + "page" + m + ".class", (Comparable) a));
                                    //ovp.getPointers().get(j).setIdx(-1);
                                    // writeFile(tree.find((Comparable)(a)).getPagePath(),ovp);
                                    break;
                                }

                            }
                        } else
                            tree.updateTuplePointer(new TuplePointer(p.getTuples().size() - 1, "data/" + table + "page" + m + ".class", (Comparable) a), new TuplePointer(-1, "data/" + table + "page" + m + ".class", (Comparable) a));
                    }
                }
            } else {
                BPTree tree = (BPTree) readFile("data/" + table + "indices" + columnsindex.get(l) + "Btree.class");
                Set<Map.Entry<String, Object>> entries = last.entrySet();
                for (Map.Entry<String, Object> pair : entries) {
                    if (pair.getKey().equals(columnsindex.get(l))) {
                        if (!(tree.find((Comparable) (pair.getValue())) instanceof TuplePointer)) {
                            OverFlowPage ovp = (OverFlowPage) DBApp.readFile(tree.find((Comparable) (pair.getValue())).getPagePath());
                            for (int j = 0; j < ovp.getPointers().size(); j++) {
                                if (ovp.getPointers().get(j).getPagePath().equals("data/" + table + "page" + m + ".class") && ovp.getPointers().get(j).getIdx() == (p.getTuples().size() - 1)) {
                                    tree.updateTuplePointer(new TuplePointer(p.getTuples().size() - 1, "data/" + table + "page" + m + ".class", (Comparable) pair.getValue()), new TuplePointer(-1, "data/" + table + "page" + m + ".class", (Comparable) pair.getValue()));
                                    // ovp.getPointers().get(j).setIdx(-1);
                                    //writeFile(tree.find((Comparable)(pair.getValue())).getPagePath(),ovp);
                                    break;
                                }

                            }
                        } else
                            tree.updateTuplePointer(new TuplePointer(p.getTuples().size() - 1, "data/" + table + "page" + m + ".class", (Comparable) pair.getValue()), new TuplePointer(-1, "data/" + table + "page" + m + ".class", (Comparable) pair.getValue()));
                    }
                }
            }
        }
        //khalas


        // Just shift all tuples down.
        for (int i = p.getTuples().size() - 1; i > tIndex; i--) {
            p.getTuples().set(i, p.getTuples().get(i - 1));
            writeFile("data/" + table + "page" + m + ".class", p);
            //new code BPTREE
            for (int k = 0; k < columnsindex.size(); k++) {
                if (columnstype.get(k).equals("java.awt.Polygon")) {
                    RTree tree = (RTree) readFile("data/" + table + "indices" + columnsindex.get(k) + "Rtree.class");
                    Set<Map.Entry<String, Object>> entries = p.getTuples().get(i - 1).entrySet();
                    for (Map.Entry<String, Object> pair : entries) {
                        if (pair.getKey().equals(columnsindex.get(k))) {
                            CPolygon a = new CPolygon((Polygon) pair.getValue());
                            if (tree.find((Comparable) (a)) instanceof TuplePointer) {

                                tree.updateTuplePointer((TuplePointer) (tree.find((Comparable) (a))), new TuplePointer(i, (tree.find((Comparable) (a))).getPagePath(), (Comparable) (a)));
                                // ((TuplePointer) (tree.find((Comparable)(a)))).setIdx(i);

                            } else {
                                OverFlowPage ovp = (OverFlowPage) DBApp.readFile(tree.find((Comparable) (a)).getPagePath());
                                for (int j = 0; j < ovp.getPointers().size(); j++) {
                                    if (ovp.getPointers().get(j).getPagePath().equals("data/" + table + "page" + m + ".class") && ovp.getPointers().get(j).getIdx() == (i - 1)) {
                                        tree.updateTuplePointer(ovp.getPointers().get(j), new TuplePointer(i, ovp.getPointers().get(j).getPagePath(), (Comparable) (a)));
                                        // ovp.getPointers().get(j).setIdx(i);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                } else {

                    BPTree tree = (BPTree) readFile("data/" + table + "indices" + columnsindex.get(k) + "Btree.class");
                    Set<Map.Entry<String, Object>> entries = p.getTuples().get(i - 1).entrySet();
                    for (Map.Entry<String, Object> pair : entries) {
                        if (pair.getKey().equals(columnsindex.get(k))) {
                            if (tree.find((Comparable) (pair.getValue())) instanceof TuplePointer) {
                                //Bosy hena

                                tree.updateTuplePointer((TuplePointer) (tree.find((Comparable) (pair.getValue()))), new TuplePointer(i, "data/" + table + "page" + m + ".class", (Comparable) (pair.getValue())));

                                // ((TuplePointer) (tree.find((Comparable)(pair.getValue())))).setIdx(i);
                            } else {
                                OverFlowPage ovp = (OverFlowPage) DBApp.readFile(tree.find((Comparable) (pair.getValue())).getPagePath());
                                for (int j = 0; j < ovp.getPointers().size(); j++) {
                                    if (ovp.getPointers().get(j).getPagePath().equals("data/" + table + "page" + m + ".class") && ovp.getPointers().get(j).getIdx() == (i - 1)) {
                                        tree.updateTuplePointer(ovp.getPointers().get(j), new TuplePointer(i, "data/" + table + "page" + m + ".class", (Comparable) (pair.getValue())));
                                        //System.out.println(" el "+pair.getValue()+" wasalet "+ovp.getPointers().get(j).getPagePath()+" index : "+i);
                                        //ovp.getPointers().get(j).setIdx(i);

                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            //khalas
        }
        // Current page is full and cannot accommodate the last tuple.
        if (p.getTuples().size() == p.getN()) {
            // Not last page.
            if (m < Integer.parseInt(pagetotal)) {
                System.out.println("hwa eh ely gaybo hena?");
                shiftPage(0, table, (m + 1), pagetotal, columnsindex, columnstype);
                Page next = (Page) readFile("data/" + table + "page" + (m + 1) + ".class");
                next.getTuples().set(0, last);
                writeFile("data/" + table + "page" + (m + 1) + ".class", next);
            }// Last page.
            else {
                Page np = new Page();
                np.getTuples().add(last);
                writeFile("data/" + table + "page" + (m + 1) + ".class", np);
                ArrayList<String[]> alltables = (ArrayList<String[]>) readFile("data/tables.class");
                for (int i = 0; i < alltables.size(); i++) {
                    if (alltables.get(i)[0].equals(table)) {
                        String[] y = {alltables.get(i)[0], alltables.get(i)[1], (m + 1) + ""};
                        alltables.set(i, y);
                        writeFile("data/tables.class", alltables);
                    }
                }

            }
            ///entyyyyy HENAAAAAAAA!!!!!!!!
            //new code BPTREE
            for (int k = 0; k < columnsindex.size(); k++) {
                if (columnstype.get(k).equals("java.awt.Polygon")) {
                    RTree tree = (RTree) readFile("data/" + table + "indices" + columnsindex.get(k) + "Rtree.class");
                    Set<Map.Entry<String, Object>> entries = last.entrySet();
                    for (Map.Entry<String, Object> pair : entries) {
                        if (pair.getKey().equals(columnsindex.get(k))) {
                            CPolygon a = new CPolygon((Polygon) pair.getValue());
                            if (tree.find((Comparable) (a)) instanceof TuplePointer) {
                                // (tree.find((Comparable)(a))).setPagePath("data/" + table + "page" + (m + 1) + ".class");
                                tree.updateTuplePointer(((TuplePointer) (tree.find((Comparable) (a)))), new TuplePointer(0, "data/" + table + "page" + (m + 1) + ".class", (Comparable) a));
                                //((TuplePointer) (tree.find((Comparable)(a)))).setIdx(0);
                            } else {
                                OverFlowPage ovp = (OverFlowPage) DBApp.readFile(tree.find((Comparable) (a)).getPagePath());
                                for (int j = 0; j < ovp.getPointers().size(); j++) {
                                    //hakhaleeha m 3ashan msh 3arfa leeh m+1
                                    if (ovp.getPointers().get(j).getPagePath().equals("data/" + table + "page" + (m) + ".class") && ovp.getPointers().get(j).getIdx() == -1) {
                                        tree.updateTuplePointer(ovp.getPointers().get(j), new TuplePointer(0, "data/" + table + "page" + (m + 1) + ".class", (Comparable) a));
                                        // ovp.getPointers().get(j).setIdx(0);
                                        // ovp.getPointers().get(j).setPagePath("data/" + table + "page" + (m+1) + ".class");
                                        // writeFile(tree.find((Comparable)(a)).getPagePath(),ovp);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                } else {
                    BPTree tree = (BPTree) readFile("data/" + table + "indices" + columnsindex.get(k) + "Btree.class");
                    Set<Map.Entry<String, Object>> entries = last.entrySet();
                    for (Map.Entry<String, Object> pair : entries) {
                        if (pair.getKey().equals(columnsindex.get(k))) {
                            if (tree.find((Comparable) (pair.getValue())) instanceof TuplePointer) {

                                //(tree.find((Comparable)(pair.getValue()))).setPagePath("data/" + table + "page" + (m + 1) + ".class");

                                tree.updateTuplePointer(((TuplePointer) (tree.find((Comparable) (pair.getValue())))), new TuplePointer(0, "data/" + table + "page" + (m + 1) + ".class", (Comparable) (pair.getValue())));
                                //((TuplePointer) (tree.find((Comparable)(pair.getValue())))).setIdx(0);

                            } else {
                                OverFlowPage ovp = (OverFlowPage) DBApp.readFile(tree.find((Comparable) (pair.getValue())).getPagePath());
                                for (int j = 0; j < ovp.getPointers().size(); j++) {
                                    //hakhaleeha m 3ashan msh 3arfa leeh m+1
                                    System.out.println("el " + pair.getValue() + " wasal hena hanshoof dakhala wla laa?" + ovp.getPointers().get(j));
                                    if (ovp.getPointers().get(j).getPagePath().equals("data/" + table + "page" + (m) + ".class") && ovp.getPointers().get(j).getIdx() == -1) {
                                        tree.updateTuplePointer(ovp.getPointers().get(j), new TuplePointer(0, "data/" + table + "page" + (m + 1) + ".class", (Comparable) (pair.getValue())));
                                        // System.out.println(pair.getValue()+" Dakhal");
                                        //ovp.getPointers().get(j).setIdx(0);
                                        //ovp.getPointers().get(j).setPagePath("data/" + table + "page" + (m+1) + ".class");
                                        // writeFile(tree.find((Comparable)(pair.getValue())).getPagePath(),ovp);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            //khalas
        }
        // Current page is not full and can accommodate the last tuple.
        else {
            p.getTuples().add(last);
            writeFile("data/" + table + "page" + m + ".class", p);
            //new code BPTREE
            for (int k = 0; k < columnsindex.size(); k++) {
                if (columnstype.get(k).equals("java.awt.Polygon")) {
                    RTree tree = (RTree) readFile("data/" + table + "indices" + columnsindex.get(k) + "Rtree.class");
                    Set<Map.Entry<String, Object>> entries = last.entrySet();
                    for (Map.Entry<String, Object> pair : entries) {
                        if (pair.getKey().equals(columnsindex.get(k))) {
                            CPolygon a = new CPolygon((Polygon) pair.getValue());
                            if (tree.find((Comparable) (a)) instanceof TuplePointer) {
                                tree.updateTuplePointer(((TuplePointer) (tree.find((Comparable) (a)))), new TuplePointer(p.getTuples().size() - 1, "data/" + table + "page" + m + ".class", (Comparable) a));
                                //(tree.find((Comparable)(a))).setPagePath("data/" + table + "page" + m + ".class");
                                //((TuplePointer) (tree.find((Comparable)(a)))).setIdx(p.getTuples().size()-1);
                            } else {
                                OverFlowPage ovp = (OverFlowPage) DBApp.readFile(tree.find((Comparable) (a)).getPagePath());
                                for (int j = 0; j < ovp.getPointers().size(); j++) {
                                    if (ovp.getPointers().get(j).getPagePath().equals("data/" + table + "page" + m + ".class") && ovp.getPointers().get(j).getIdx() == -1) {
                                        tree.updateTuplePointer(ovp.getPointers().get(j), new TuplePointer(p.getTuples().size() - 1, "data/" + table + "page" + m + ".class", (Comparable) a));
                                        // ovp.getPointers().get(j).setIdx(p.getTuples().size()-1);
                                        //writeFile(tree.find((Comparable)(a)).getPagePath(),ovp);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                } else {
                    BPTree tree = (BPTree) readFile("data/" + table + "indices" + columnsindex.get(k) + "Btree.class");
                    Set<Map.Entry<String, Object>> entries = last.entrySet();
                    for (Map.Entry<String, Object> pair : entries) {
                        if (pair.getKey().equals(columnsindex.get(k))) {
                            if (tree.find((Comparable) (pair.getValue())) instanceof TuplePointer) {
                                //(tree.find((Comparable)(pair.getValue()))).setPagePath("data/" + table + "page" + m + ".class");
                                //ghayart haga hena

                                tree.updateTuplePointer((TuplePointer) (tree.find((Comparable) (pair.getValue()))), new TuplePointer(p.getTuples().size() - 1, "data/" + table + "page" + m + ".class", (Comparable) (pair.getValue())));
                                // ((TuplePointer) (tree.find((Comparable)(pair.getValue())))).setIdx(((TuplePointer) (tree.find((Comparable)(pair.getValue())))).getIdx()+1);
                            } else {
                                OverFlowPage ovp = (OverFlowPage) DBApp.readFile(tree.find((Comparable) (pair.getValue())).getPagePath());
                                for (int j = 0; j < ovp.getPointers().size(); j++) {
                                    if (ovp.getPointers().get(j).getPagePath().equals("data/" + table + "page" + m + ".class") && ovp.getPointers().get(j).getIdx() == -1) {
                                        tree.updateTuplePointer(new TuplePointer(-1, "data/" + table + "page" + m + ".class", (Comparable) (pair.getValue())), new TuplePointer(p.getTuples().size() - 1, "data/" + table + "page" + m + ".class", (Comparable) (pair.getValue())));
                                        // ovp.getPointers().get(j).setIdx(p.getTuples().size()-1);
                                        //writeFile(tree.find((Comparable)(pair.getValue())).getPagePath(),ovp);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            //khalas
        }

    }
// ---------------------------------------------------------------------------------------------------------------------

    /** Method 34.
     * @param h         Hashtable to be shifted upwards.
     * @param tIndex    Index where a tuples with a larger clustering key was found.
     * @param table     Table name
     * @param m         Current page number.
     * @param pagetotal Total number of pages for this table.
     */
    public void shiftpage2(Hashtable<String, Object> h, int tIndex, String table, int m, String pagetotal, ArrayList<String> columnsindex, ArrayList<String> columnstype) {
        //System.out.println("ya admiiiiiiiin");
        // Search for the first empty page before the current full page.
        int i = 0;
        for (i = m - 1; i > 0; i--) {
            Page prevPage = (Page) readFile("data/" + table + "page" + (i) + ".class");
            if (prevPage.getTuples().size() < prevPage.getN())
                break;
        }
        // An empty page was found.
        if (i > 0) {
            for (int j = i; j < m; j++) {
                Page p1 = (Page) readFile("data/" + table + "page" + (j) + ".class");
                Page nextPage = (Page) readFile("data/" + table + "page" + (j + 1) + ".class");
                if (j == (m - 1) && tIndex == 0) {
                    p1.getTuples().add(h);

                    this.writeFile("data/" + table + "page" + (j) + ".class", p1);
                    //new code BPTREE
                    for (int l = 0; l < columnsindex.size(); l++) {
                        if (columnstype.get(l).equals("java.awt.Polygon")) {
                            RTree tree = (RTree) readFile("data/" + table + "indices" + columnsindex.get(l) + "Rtree.class");
                            Set<Map.Entry<String, Object>> entries = h.entrySet();
                            for (Map.Entry<String, Object> pair : entries) {
                                if (pair.getKey().equals(columnsindex.get(l))) {
                                    CPolygon a = new CPolygon((Polygon) pair.getValue());
                                    tree.insert((Comparable) (a), "data/" + table + "page" + j + ".class", p1.getTuples().size() - 1);
                                }
                            }
                        } else {
                            BPTree tree = (BPTree) readFile("data/" + table + "indices" + columnsindex.get(l) + "Btree.class");
                            Set<Map.Entry<String, Object>> entries = h.entrySet();
                            for (Map.Entry<String, Object> pair : entries) {
                                if (pair.getKey().equals(columnsindex.get(l))) {
                                    tree.insert((Comparable) (pair.getValue()), "data/" + table + "page" + j + ".class", p1.getTuples().size() - 1);
                                }
                            }
                        }
                        //khalas
                    }
                } else {
                    //new code BPTREE
                    for (int l = 0; l < columnsindex.size(); l++) {
                        if (columnstype.get(l).equals("java.awt.Polygon")) {
                            RTree tree = (RTree) readFile("data/" + table + "indices" + columnsindex.get(l) + "Rtree.class");
                            Set<Map.Entry<String, Object>> entries = nextPage.getTuples().get(0).entrySet();
                            for (Map.Entry<String, Object> pair : entries) {
                                if (pair.getKey().equals(columnsindex.get(l))) {
                                    CPolygon a = new CPolygon((Polygon) pair.getValue());
                                    if (tree.find((Comparable) (a)) instanceof TuplePointer) {
                                        tree.updateTuplePointer(((TuplePointer) (tree.find((Comparable) (a)))), new TuplePointer(p1.getTuples().size() - 1, "data/" + table + "page" + (j) + ".class", (Comparable) a));
                                        // tree.find((Comparable)(a)).setPagePath("data/" + table + "page" + (j) + ".class");
                                        //((TuplePointer)( tree.find((Comparable)(a)))).setIdx(p1.getTuples().size()-1);
                                    } else {
                                        OverFlowPage ovp = (OverFlowPage) readFile(tree.find((Comparable) (a)).getPagePath());
                                        for (int s = 0; s < ovp.getPointers().size(); s++) {
                                            if (ovp.getPointers().get(s).getPagePath().equals("data/" + table + "page" + (j + 1) + ".class") && ovp.getPointers().get(s).getIdx() == 0) {
                                                tree.updateTuplePointer(ovp.getPointers().get(s), new TuplePointer(p1.getTuples().size() - 1, "data/" + table + "page" + j + ".class", (Comparable) a));
                                                //ovp.getPointers().get(s).setIdx(p1.getTuples().size()-1);
//                                		   ovp.getPointers().get(s).setPagePath("data/" + table + "page" + j + ".class");
                                                // writeFile(tree.find((Comparable)(a)).getPagePath(),ovp);
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            BPTree tree = (BPTree) readFile("data/" + table + "indices" + columnsindex.get(l) + "Btree.class");
                            Set<Map.Entry<String, Object>> entries = nextPage.getTuples().get(0).entrySet();
                            for (Map.Entry<String, Object> pair : entries) {
                                if (pair.getKey().equals(columnsindex.get(l))) {
                                    if (tree.find((Comparable) (pair.getValue())) instanceof TuplePointer) {
                                        //tree.find((Comparable)(pair.getValue())).setPagePath("data/" + table + "page" + (j) + ".class");
                                        //SAME BRDOOOOO!!
                                        tree.updateTuplePointer(((TuplePointer) (tree.find((Comparable) (pair.getValue())))), new TuplePointer(p1.getTuples().size(), "data/" + table + "page" + j + ".class", (Comparable) (pair.getValue())));
                                        //((TuplePointer)( tree.find((Comparable)(pair.getValue())))).setIdx(p1.getTuples().size());
                                    } else {
                                        OverFlowPage ovp = (OverFlowPage) readFile(tree.find((Comparable) (pair.getValue())).getPagePath());
                                        for (int s = 0; s < ovp.getPointers().size(); s++) {
                                            if (ovp.getPointers().get(s).getPagePath().equals("data/" + table + "page" + (j + 1) + ".class") && ovp.getPointers().get(s).getIdx() == 0) {
                                                //NOT SURE MAKE SUREEEEEEE!!!!! 3AMALT SIZE BADAL -1

                                                tree.updateTuplePointer(ovp.getPointers().get(s), new TuplePointer(p1.getTuples().size(), "data/" + table + "page" + j + ".class", (Comparable) (pair.getValue())));
                                                //ovp.getPointers().get(s).setIdx(p1.getTuples().size());
                                                //ovp.getPointers().get(s).setPagePath("data/" + table + "page" + j + ".class");
                                                // writeFile(tree.find((Comparable)(pair.getValue())).getPagePath(),ovp);
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Hashtable<String, Object> removed = nextPage.getTuples().remove(0);
                    //khalas
                    p1.getTuples().add(removed);
                    writeFile("data/" + table + "page" + (j) + ".class", p1);
                    writeFile("data/" + table + "page" + (j + 1) + ".class", nextPage);
                    //new code BPTREE
                    for (int l = 0; l < columnsindex.size(); l++) {
                        if (columnstype.get(l).equals("java.awt.Polygon")) {
                            RTree tree = (RTree) readFile("data/" + table + "indices" + columnsindex.get(l) + "Rtree.class");
                            for (int k = 0; k < nextPage.getTuples().size(); k++) {
                                Set<Map.Entry<String, Object>> entries = nextPage.getTuples().get(k).entrySet();
                                for (Map.Entry<String, Object> pair : entries) {
                                    if (pair.getKey().equals(columnsindex.get(l))) {
                                        CPolygon a = new CPolygon((Polygon) pair.getValue());
                                        if (tree.find((Comparable) (a)) instanceof TuplePointer) {
                                            tree.updateTuplePointer(((TuplePointer) (tree.find((Comparable) (a)))), new TuplePointer(((TuplePointer) (tree.find((Comparable) (a)))).getIdx() - 1, (tree.find((Comparable) (a)).getPagePath()), (Comparable) (a)));
                                            //((TuplePointer)( tree.find((Comparable)(a)))).setIdx(((TuplePointer)(tree.find((Comparable)(a)))).getIdx()-1);
                                        } else {
                                            OverFlowPage ovp = (OverFlowPage) readFile(tree.find((Comparable) (a)).getPagePath());
                                            for (int s = 0; s < ovp.getPointers().size(); s++) {
                                                //changed it again check it Khaletha k+1 badal k
                                                if (ovp.getPointers().get(s).getPagePath().equals("data/" + table + "page" + (j + 1) + ".class") && ovp.getPointers().get(s).getIdx() == (k + 1)) { //shelt && ovp.getPointers().get(s).getIdx()==((TuplePointer)(tree.find((Comparable)(pair.getValue())))).getIdx()
                                                    tree.updateTuplePointer(ovp.getPointers().get(s), new TuplePointer(ovp.getPointers().get(s).getIdx() - 1, "data/" + table + "page" + (j + 1) + ".class", (Comparable) (a)));
                                                    //ovp.getPointers().get(s).setIdx(ovp.getPointers().get(s).getIdx()-1);
                                                    // writeFile(tree.find((Comparable)(a)).getPagePath(),ovp);
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            BPTree tree = (BPTree) readFile("data/" + table + "indices" + columnsindex.get(l) + "Btree.class");
                            for (int k = 0; k < nextPage.getTuples().size(); k++) {
                                Set<Map.Entry<String, Object>> entries = nextPage.getTuples().get(k).entrySet();
                                for (Map.Entry<String, Object> pair : entries) {
                                    if (pair.getKey().equals(columnsindex.get(l))) {
                                        if (tree.find((Comparable) (pair.getValue())) instanceof TuplePointer) {
                                            tree.updateTuplePointer(((TuplePointer) (tree.find((Comparable) (pair.getValue())))), new TuplePointer(((TuplePointer) (tree.find((Comparable) (pair.getValue())))).getIdx() - 1, (tree.find((Comparable) (pair.getValue()))).getPagePath(), (Comparable) (pair.getValue())));
                                            //((TuplePointer)( tree.find((Comparable)(pair.getValue())))).setIdx(((TuplePointer)(tree.find((Comparable)(pair.getValue())))).getIdx()-1);
                                        } else {
                                            OverFlowPage ovp = (OverFlowPage) readFile(tree.find((Comparable) (pair.getValue())).getPagePath());
                                            for (int s = 0; s < ovp.getPointers().size(); s++) {
                                                //changed it again check it
                                                if (ovp.getPointers().get(s).getPagePath().equals("data/" + table + "page" + (j + 1) + ".class") && ovp.getPointers().get(s).getIdx() == (k + 1)) { //shelt && ovp.getPointers().get(s).getIdx()==((TuplePointer)(tree.find((Comparable)(pair.getValue())))).getIdx()
                                                    tree.updateTuplePointer(ovp.getPointers().get(s), new TuplePointer(ovp.getPointers().get(s).getIdx() - 1, "data/" + table + "page" + (j + 1) + ".class", (Comparable) (pair.getValue())));
                                                    //ovp.getPointers().get(s).setIdx(ovp.getPointers().get(s).getIdx()-1);
                                                    //writeFile(tree.find((Comparable)(pair.getValue())).getPagePath(),ovp);
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    //khalas
                }
            }
            if (tIndex != 0) {
                shiftPage(tIndex - 1, table, m, pagetotal, columnsindex, columnstype);
                Page p = (Page) readFile("data/" + table + "page" + m + ".class");
                p.getTuples().set(tIndex - 1, h);
                writeFile("data/" + table + "page" + m + ".class", p);
                //new code BPTREE
                for (int l = 0; l < columnsindex.size(); l++) {
                    if (columnstype.get(l).equals("java.awt.Polygon")) {
                        RTree tree = (RTree) readFile("data/" + table + "indices" + columnsindex.get(l) + "Rtree.class");
                        Set<Map.Entry<String, Object>> entries = h.entrySet();
                        for (Map.Entry<String, Object> pair : entries) {
                            if (pair.getKey().equals(columnsindex.get(l))) {
                                CPolygon a = new CPolygon((Polygon) pair.getValue());
                                tree.insert((Comparable) (a), "data/" + table + "page" + m + ".class", tIndex - 1);
                            }
                        }
                    } else {
                        BPTree tree = (BPTree) readFile("data/" + table + "indices" + columnsindex.get(l) + "Btree.class");
                        Set<Map.Entry<String, Object>> entries = h.entrySet();
                        for (Map.Entry<String, Object> pair : entries) {
                            if (pair.getKey().equals(columnsindex.get(l))) {
                                tree.insert((Comparable) (pair.getValue()), "data/" + table + "page" + m + ".class", tIndex - 1);
                            }
                        }
                    }
                    //khalas
                }
            }
        }
        // No empty page was found.
        else {
            shiftPage(tIndex, table, m, pagetotal, columnsindex, columnstype);
            Page p = (Page) readFile("data/" + table + "page" + m + ".class");
            p.getTuples().set(tIndex, h);
            writeFile("data/" + table + "page" + m + ".class", p);
            //new code BPTREE
            for (int l = 0; l < columnsindex.size(); l++) {
                if (columnstype.get(l).equals("java.awt.Polygon")) {
                    RTree tree = (RTree) readFile("data/" + table + "indices" + columnsindex.get(l) + "Rtree.class");
                    Set<Map.Entry<String, Object>> entries = h.entrySet();
                    for (Map.Entry<String, Object> pair : entries) {
                        if (pair.getKey().equals(columnsindex.get(l))) {
                            CPolygon a = new CPolygon((Polygon) pair.getValue());
                            tree.insert((Comparable) (a), "data/" + table + "page" + m + ".class", tIndex);
                        }
                    }
                } else {
                    BPTree tree = (BPTree) readFile("data/" + table + "indices" + columnsindex.get(l) + "Btree.class");
                    Set<Map.Entry<String, Object>> entries = h.entrySet();
                    for (Map.Entry<String, Object> pair : entries) {
                        if (pair.getKey().equals(columnsindex.get(l))) {
                            tree.insert((Comparable) (pair.getValue()), "data/" + table + "page" + m + ".class", tIndex);
                        }
                    }
                }
            }
            //khalas
        }
    }
// ---------------------------------------------------------------------------------------------------------------------

    /** Method 35.
     * @param strTableName     Table Name
     * @param htblColNameValue Hashtable of mappings between column name and column value.
     * @throws DBAppException Case 1: Column Value object does not match the column type in the CSV.\n
     *                        Case 2: Clustering Key was not in the hashtable, and would thus have to be null which is not allowed.\n
     *                        Case 3: One of the hashtable column names is not in the CSV and is thus wrong.
     */

    public void insertIntoTable(String strTableName, Hashtable<String, Object> htblColNameValue) throws DBAppException {

        // 1. Adding Touch Date column name and corresponding "Date" value to the input hashtable.
        Calendar cal = Calendar.getInstance();
        htblColNameValue.put("Touch Date", cal.getTime());

        // 2. Collecting CSV metadata for the table in an ArrayList of String arrays.
        ArrayList<String[]> csvData = getTableColumns(strTableName);
        //new code BPTREE
        ArrayList<String> columnsindex = new ArrayList<String>();
        ArrayList<String> columnstype = new ArrayList<String>();
        //KHALAS

//---------------- Checks that throw exceptions.
        try {
            Class columnClass;
            if (csvData.size() == htblColNameValue.size()) {
                for (String[] oneCsvRow : csvData) {

                    // Checks that the input hashtable contains the current CSV row column name.
                    if (htblColNameValue.containsKey(oneCsvRow[1])) {
                        columnClass = htblColNameValue.get(oneCsvRow[1]).getClass();
                        if (!(Class.forName(oneCsvRow[2]).equals(columnClass))) { // Checks that the current CSV row column type matches with the type of object in the input hashtable.
                            throw new DBAppException("Different class type for column name: " + oneCsvRow[1] + ".");
                        }
                    } else {
                        throw new DBAppException("Column name " + oneCsvRow[1] + " is not found in your table!");
                    }
                    //NEW CODE BPTREE
                    if (this.hasIndex(strTableName, oneCsvRow[1])) {
                        columnsindex.add(oneCsvRow[1]);
                        columnstype.add(oneCsvRow[2]);
                    }
                    //KHALAS
                }
            } else {
                throw new DBAppException("Number of columns incorrect!");
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

// ---------------- Actual insertion.
        ArrayList<String[]> mytables = (ArrayList<String[]>) readFile("data/tables.class");
        loop:
        for (int i = 0; i < mytables.size(); i++) {
            if (mytables.get(i)[0].equals(strTableName)) { // Found my table in the table ArrayList.
                if (mytables.get(i)[2].equals("0")) { // 0 Pages case.
                    Page p = new Page();
                    p.getTuples().add(htblColNameValue);

                    String[] y = {mytables.get(i)[0], mytables.get(i)[1], "1"};
                    mytables.set(i, y);

                    writeFile("data/" + strTableName + "page" + mytables.get(i)[2] + ".class", p);
                    writeFile("data/tables.class", mytables);
                    //new code BPTREE
                    for (int k = 0; k < columnsindex.size(); k++) {
                        if (columnstype.get(k).equals("java.awt.Polygon")) {
                            RTree tree = (RTree) readFile("data/" + strTableName + "indices" + columnsindex.get(k) + "Rtree.class");
                            Set<Map.Entry<String, Object>> entries = htblColNameValue.entrySet();
                            for (Map.Entry<String, Object> pair : entries) {
                                if (pair.getKey().equals(columnsindex.get(k))) {
                                    //index btbda2 mn 0 wla 1??
                                    CPolygon a = new CPolygon((Polygon) pair.getValue());
                                    tree.insert(((Comparable) a), "data/" + strTableName + "page" + mytables.get(i)[2] + ".class", 0);
                                    break;
                                }
                            }
                        } else {
                            BPTree tree = (BPTree) readFile("data/" + strTableName + "indices" + columnsindex.get(k) + "Btree.class");
                            Set<Map.Entry<String, Object>> entries = htblColNameValue.entrySet();
                            for (Map.Entry<String, Object> pair : entries) {
                                if (pair.getKey().equals(columnsindex.get(k))) {
                                    //index btbda2 mn 0 wla 1??
                                    tree.insert(((Comparable) pair.getValue()), "data/" + strTableName + "page" + mytables.get(i)[2] + ".class", 0);
                                    break;
                                }
                            }
                        }
                    }
                    //khalas


                    break loop;
                } else { // Existing pages case.
                    String key = mytables.get(i)[1];
                    String pnumber = mytables.get(i)[2];

                    int shift = -1;
                    Page currentPage = (Page) readFile("data/" + strTableName + "page" + 1 + ".class");
                    Vector<Hashtable<String, Object>> vector = currentPage.getTuples();
                    int m = 1;

                    pageloop:
                    for (m = 1; m <= (Integer.parseInt(pnumber)); m++) { // Loops on all pages.
                        currentPage = (Page) readFile("data/" + strTableName + "page" + m + ".class"); // Reads page.
                        vector = currentPage.getTuples();
                        int min = 0, max = vector.size() - 1;

                        tuplesloop:
                        while (min <= max) {
                            int mid = min + (max - min + 1) / 2;
                            if (!(vector.get(mid).get(key) instanceof Polygon)) {
                                if (((Comparable) vector.get(mid).get(key)).compareTo((Comparable) htblColNameValue.get(key)) <= 0) {
                                    min = mid + 1;
                                }
                                if (((Comparable) vector.get(mid).get(key)).compareTo((Comparable) htblColNameValue.get(key)) > 0) {
                                    shift = mid;
                                    max = mid - 1;

                                }
                            } else {
                                if (((Comparable) new CPolygon((Polygon) vector.get(mid).get(key))).compareTo(htblColNameValue.get(key)) <= 0) {
                                    min = mid + 1;
                                }
                                if (((Comparable) new CPolygon((Polygon) vector.get(mid).get(key))).compareTo(htblColNameValue.get(key)) > 0) {
                                    shift = mid;
                                    max = mid - 1;

                                }
                            }
                        }

                        if (shift > -1) break;
                    }
                    // A larger tuple was found in a page.
                    if (shift != -1) {
                        // Page is full.
                        if (vector.size() == currentPage.getN()) {
                            shiftpage2(htblColNameValue, shift, strTableName, m, pnumber, columnsindex, columnstype);

                        }
                        // Page is not full.
                        else {
                            shiftPage(shift, strTableName, m, pnumber, columnsindex, columnstype);
                            Page p = (Page) readFile("data/" + strTableName + "page" + m + ".class");
                            p.getTuples().set(shift, htblColNameValue);
                            //NEW CODE BPTREE
                            for (int k = 0; k < columnsindex.size(); k++) {
                                if (columnstype.get(k).equals("java.awt.Polygon")) {
                                    RTree tree = (RTree) readFile("data/" + strTableName + "indices" + columnsindex.get(k) + "Rtree.class");
                                    Set<Map.Entry<String, Object>> entries = htblColNameValue.entrySet();
                                    for (Map.Entry<String, Object> pair : entries) {
                                        if (pair.getKey().equals(columnsindex.get(k))) {
                                            CPolygon a = new CPolygon((Polygon) pair.getValue());
                                            tree.insert(((Comparable) a), "data/" + strTableName + "page" + m + ".class", shift);
                                            break;
                                        }
                                    }
                                } else {
                                    BPTree tree = (BPTree) readFile("data/" + strTableName + "indices" + columnsindex.get(k) + "Btree.class");
                                    Set<Map.Entry<String, Object>> entries = htblColNameValue.entrySet();
                                    for (Map.Entry<String, Object> pair : entries) {
                                        if (pair.getKey().equals(columnsindex.get(k))) {
                                            tree.insert(((Comparable) pair.getValue()), "data/" + strTableName + "page" + m + ".class", shift);

                                            break;
                                        }
                                    }
                                }
                            }
                            //KHALAS
                            writeFile("data/" + strTableName + "page" + m + ".class", p);
                        }
                    } else {// No tuple with larger clustering key was found.
                        // Page is full.
                        if (vector.size() == currentPage.getN()) {
                            Page p = new Page();
                            p.getTuples().add(htblColNameValue);
                            writeFile("data/" + strTableName + "page" + (m) + ".class", p);

                            String[] s = {mytables.get(i)[0], mytables.get(i)[1], m + ""};
                            mytables.set(i, s);
                            writeFile("data/tables.class", mytables);
                            //NEW CODE BPTREE
                            for (int k = 0; k < columnsindex.size(); k++) {
                                if (columnstype.get(k).equals("java.awt.Polygon")) {
                                    RTree tree = (RTree) readFile("data/" + strTableName + "indices" + columnsindex.get(k) + "Rtree.class");
                                    Set<Map.Entry<String, Object>> entries = htblColNameValue.entrySet();
                                    for (Map.Entry<String, Object> pair : entries) {
                                        if (pair.getKey().equals(columnsindex.get(k))) {
                                            CPolygon a = new CPolygon((Polygon) pair.getValue());
                                            tree.insert(((Comparable) a), "data/" + strTableName + "page" + m + ".class", 0);
                                            break;
                                        }
                                    }
                                } else {
                                    BPTree tree = (BPTree) readFile("data/" + strTableName + "indices" + columnsindex.get(k) + "Btree.class");
                                    Set<Map.Entry<String, Object>> entries = htblColNameValue.entrySet();
                                    for (Map.Entry<String, Object> pair : entries) {
                                        if (pair.getKey().equals(columnsindex.get(k))) {
                                            tree.insert(((Comparable) pair.getValue()), "data/" + strTableName + "page" + m + ".class", 0);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        //KHALAS
                        // Page is not full.
                        else {
                            currentPage.getTuples().add(htblColNameValue);
                            //new code BPTREE
                            for (int k = 0; k < columnsindex.size(); k++) {
                                if (columnstype.get(k).equals("java.awt.Polygon")) {
                                    RTree tree = (RTree) readFile("data/" + strTableName + "indices" + columnsindex.get(k) + "Rtree.class");
                                    Set<Map.Entry<String, Object>> entries = htblColNameValue.entrySet();
                                    for (Map.Entry<String, Object> pair : entries) {
                                        if (pair.getKey().equals(columnsindex.get(k))) {
                                            CPolygon a = new CPolygon((Polygon) pair.getValue());
                                            tree.insert(((Comparable) a), "data/" + strTableName + "page" + (m - 1) + ".class", currentPage.getTuples().size() - 1);
                                            break;
                                        }
                                    }
                                } else {
                                    BPTree tree = (BPTree) readFile("data/" + strTableName + "indices" + columnsindex.get(k) + "Btree.class");
                                    Set<Map.Entry<String, Object>> entries = htblColNameValue.entrySet();
                                    for (Map.Entry<String, Object> pair : entries) {
                                        if (pair.getKey().equals(columnsindex.get(k))) {
                                            tree.insert(((Comparable) pair.getValue()), "data/" + strTableName + "page" + (m - 1) + ".class", currentPage.getTuples().size() - 1);
                                            break;
                                        }
                                    }
                                }
                            }
                            //KHALAS
                            writeFile("data/" + strTableName + "page" + (m - 1) + ".class", currentPage);

                        }
                    }
                }
            }
        }
    }
}