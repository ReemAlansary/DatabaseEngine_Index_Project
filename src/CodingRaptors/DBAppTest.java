package CodingRaptors;

import Indices.*;

import java.awt.*;
import java.util.Hashtable;

public class DBAppTest {
    public static void main(String[] args) throws DBAppException {
        //Table creation
        String strTableName = "Student";
        DBApp dbApp = new DBApp();
        Hashtable htblColNameType = new Hashtable();
        htblColNameType.put("id", "java.lang.Integer");
        htblColNameType.put("name", "java.lang.String");
        htblColNameType.put("gpa", "java.lang.Double");
        htblColNameType.put("age", "java.lang.Integer");
        htblColNameType.put("polygon", "java.awt.Polygon");
        dbApp.createTable(strTableName, "age", htblColNameType);

        // Data entry.
        // 1
        Hashtable htblColNameValue = new Hashtable();
        htblColNameValue.put("id", new Integer(70));
        htblColNameValue.put("name", new String("Monica"));
        htblColNameValue.put("gpa", new Double(0.95));
        htblColNameValue.put("age", new Integer(1));
        htblColNameValue.put("polygon", new Polygon(new int[]{1, 1, 2, 2}, new int[]{1, 2, 1, 2}, 4));     //area=1.0
        dbApp.insertIntoTable(strTableName, htblColNameValue);

        // 2
        htblColNameValue.clear();
        htblColNameValue.put("id", new Integer(70));
        htblColNameValue.put("name", new String("Marina"));
        htblColNameValue.put("gpa", new Double(0.95));
        htblColNameValue.put("age", new Integer(1));
        htblColNameValue.put("polygon", new Polygon(new int[]{3, 3, 4, 4}, new int[]{1, 2, 1, 2}, 4));     //area=1.0
        dbApp.insertIntoTable(strTableName, htblColNameValue);

        // 3
        htblColNameValue.clear();
        htblColNameValue.put("id", new Integer(30));
        htblColNameValue.put("name", new String("Sarah"));
        htblColNameValue.put("gpa", new Double(0.88));
        htblColNameValue.put("age", new Integer(5));
        htblColNameValue.put("polygon", new Polygon(new int[]{1, 1, 2, 2}, new int[]{1, 2, 1, 2}, 4));    //area=1;
        dbApp.insertIntoTable(strTableName, htblColNameValue);

        // 4
        htblColNameValue.clear();
        htblColNameValue.put("id", new Integer(80));
        htblColNameValue.put("name", new String("Ahmed Noor"));
        htblColNameValue.put("gpa", new Double(0.88));
        htblColNameValue.put("age", new Integer(6));
        htblColNameValue.put("polygon", new Polygon(new int[]{1, 1, 2, 2}, new int[]{1, 2, 1, 2}, 4)); //area = 1
        dbApp.insertIntoTable(strTableName, htblColNameValue);

        // 5
        htblColNameValue.clear();
        htblColNameValue.put("id", new Integer(40));
        htblColNameValue.put("name", new String("Zaky Noor"));
        htblColNameValue.put("gpa", new Double(0.88));
        htblColNameValue.put("age", new Integer(4));
        htblColNameValue.put("polygon", new Polygon(new int[]{1, 1, 2, 2}, new int[]{1, 2, 1, 2}, 4)); //area = 1
        dbApp.insertIntoTable(strTableName, htblColNameValue);


        // 6
        htblColNameValue.clear();     //3entered
        htblColNameValue.put("id", new Integer(70));
        htblColNameValue.put("name", new String("Marina"));
        htblColNameValue.put("gpa", new Double(4.0));
        htblColNameValue.put("age", new Integer(1));
        htblColNameValue.put("polygon", new Polygon(new int[]{1, 1, 2, 2}, new int[]{20, 50, 20, 50}, 4));  //area=30.0
        dbApp.insertIntoTable(strTableName, htblColNameValue);

        // 7
        htblColNameValue.clear();
        htblColNameValue.put("id", new Integer(70));
        htblColNameValue.put("name", new String("Sarah"));
        htblColNameValue.put("gpa", new Double(0.95));
        htblColNameValue.put("age", new Integer(1));
        htblColNameValue.put("polygon", new Polygon(new int[]{1, 1, 2, 2}, new int[]{60, 90, 60, 90}, 4));   //area=30.0
        dbApp.insertIntoTable(strTableName, htblColNameValue);

        // 8
        htblColNameValue.clear();
        htblColNameValue.put("id", new Integer(50));
        htblColNameValue.put("name", new String("John Noor"));
        htblColNameValue.put("gpa", new Double(0.95));
        htblColNameValue.put("age", new Integer(2));
        htblColNameValue.put("polygon", new Polygon(new int[]{1, 1, 2, 2}, new int[]{60, 90, 60, 90}, 4));   //area=30.0
        dbApp.insertIntoTable(strTableName, htblColNameValue);

        // 9
        htblColNameValue.clear();
        htblColNameValue.put("id", new Integer(60));
        htblColNameValue.put("name", new String("Zaky Noor"));
        htblColNameValue.put("gpa", new Double(0.88));
        htblColNameValue.put("age", new Integer(8));
        htblColNameValue.put("polygon", new Polygon(new int[]{15, 15, 30, 30}, new int[]{1, 3, 1, 3}, 4));   // area=30.0
        dbApp.insertIntoTable(strTableName, htblColNameValue);

        // 10
        htblColNameValue.clear();
        htblColNameValue.put("id", new Integer(20));
        htblColNameValue.put("name", new String("Zaky Noor"));
        htblColNameValue.put("gpa", new Double(1.5));
        htblColNameValue.put("age", new Integer(3));
        htblColNameValue.put("polygon", new Polygon(new int[]{25, 25, 50, 50}, new int[]{10, 25, 10, 25}, 4));     //area=375;
        dbApp.insertIntoTable(strTableName, htblColNameValue);

        // 11
        htblColNameValue.clear();
        htblColNameValue.put("id", new Integer(80));
        htblColNameValue.put("name", new String("Ahmed Noor"));
        htblColNameValue.put("gpa", new Double(0.88));
        htblColNameValue.put("age", new Integer(2));
        htblColNameValue.put("polygon", new Polygon(new int[]{10, 10, 70, 70}, new int[]{10, 30, 10, 30}, 4));    //area=1200
        dbApp.insertIntoTable(strTableName, htblColNameValue);

        // 12
        htblColNameValue.clear();
        htblColNameValue.put("id", new Integer(80));
        htblColNameValue.put("name", new String("Sarah"));
        htblColNameValue.put("gpa", new Double(0.88));
        htblColNameValue.put("age", new Integer(2));
        htblColNameValue.put("polygon", new Polygon(new int[]{60, 60, 90, 90}, new int[]{20, 60, 20, 60}, 4));    //area=1200
        dbApp.insertIntoTable(strTableName, htblColNameValue);

        //Index creation
        dbApp.createBTreeIndex("Student", "id");
        dbApp.createBTreeIndex("Student", "name");
        dbApp.createBTreeIndex("Student", "age");
        dbApp.createRTreeIndex("Student", "polygon");

        Page p1 = (Page) DBApp.readFile("data/Studentpage1.class");
        System.out.println(p1.getTuples());
        Page p2 = (Page) DBApp.readFile("data/Studentpage2.class");
        System.out.println(p2.getTuples());
        Page p3 = (Page) DBApp.readFile("data/Studentpage3.class");
        System.out.println(p3.getTuples());
//--------------------------------------------------------
        System.out.println("AGE");
        BPTree treeage = (BPTree) DBApp.readFile("data/StudentindicesageBtree.class");
        System.out.println(treeage);

        for (int i = 1; i <= 8; i++) {
            if (i == 7) continue;
            Pointer p = treeage.find(new Integer(i));
            if (p instanceof TuplePointer) System.out.println(p);
            else {
                OverFlowPage ovp = (OverFlowPage) DBApp.readFile(p.getPagePath());
                for (TuplePointer tp : ovp.getPointers()) System.out.println(tp);
            }
        }
//--------------------------------------------------------
        System.out.println("ID");
        BPTree treeid = (BPTree) DBApp.readFile("data/StudentindicesidBtree.class");
        System.out.println(treeid);

        for (int i = 20; i <= 80; i += 10) {
            Pointer p = treeid.find(new Integer(i));
            if (p instanceof TuplePointer) System.out.println(p);
            else {
                OverFlowPage ovp = (OverFlowPage) DBApp.readFile(p.getPagePath());
                for (TuplePointer tp : ovp.getPointers()) System.out.println(tp);
            }
        }
//--------------------------------------------------------
        System.out.println("NAME");
        BPTree treename = (BPTree) DBApp.readFile("data/StudentindicesnameBtree.class");
        System.out.println(treename);

        Pointer pMonica = treename.find(new String("Monica"));
        if (pMonica instanceof TuplePointer) System.out.print(pMonica);
        else {
            OverFlowPage ovpMonica = (OverFlowPage) DBApp.readFile(pMonica.getPagePath());
            for (TuplePointer tp : ovpMonica.getPointers()) System.out.println(tp);
        }

        Pointer pMarina = treename.find(new String("Marina"));
        if (pMarina instanceof TuplePointer) System.out.print(pMarina);
        else {
            OverFlowPage ovpMarina = (OverFlowPage) DBApp.readFile(pMarina.getPagePath());
            for (TuplePointer tp : ovpMarina.getPointers()) System.out.println(tp);
        }

        Pointer pSarah = treename.find(new String("Sarah"));
        if (pSarah instanceof TuplePointer) System.out.print(pSarah);
        else {
            OverFlowPage ovpSarah = (OverFlowPage) DBApp.readFile(pSarah.getPagePath());
            for (TuplePointer tp : ovpSarah.getPointers()) System.out.println(tp);
        }

        Pointer pJohn = treename.find(new String("John Noor"));
        if (pJohn instanceof TuplePointer) System.out.print(pJohn);
        else {
            OverFlowPage ovpJohn = (OverFlowPage) DBApp.readFile(pJohn.getPagePath());
            for (TuplePointer tp : ovpJohn.getPointers()) System.out.println(tp);
        }

        Pointer pAhmed = treename.find(new String("Ahmed Noor"));
        if (pAhmed instanceof TuplePointer) System.out.print(pAhmed);
        else {
            OverFlowPage ovpAhmed = (OverFlowPage) DBApp.readFile(pAhmed.getPagePath());
            for (TuplePointer tp : ovpAhmed.getPointers()) System.out.println(tp);
        }

        Pointer pZaky = treename.find(new String("Zaky Noor"));
        if (pZaky instanceof TuplePointer) System.out.print(pZaky);
        else {
            OverFlowPage ovpZaky = (OverFlowPage) DBApp.readFile(pZaky.getPagePath());
            for (TuplePointer tp : ovpZaky.getPointers()) System.out.println(tp);
        }
//--------------------------------------------------------
        System.out.println("POLYGON");
        RTree treepolygon = (RTree) DBApp.readFile("data/StudentindicespolygonRtree.class");
        System.out.println(treepolygon);
        OverFlowPage ovp = (OverFlowPage) DBApp.readFile(treepolygon.find(new CPolygon(new Polygon(new int[]{1, 1, 2, 2}, new int[]{1, 2, 1, 2}, 4))).getPagePath());
//        for (TuplePointer tp : ovp.getPointers()) {
//            CPolygon cp = (CPolygon) tp.getKey();
//            System.out.println(CPolygon.sameCPol(cp, new CPolygon(new Polygon(new int[]{1, 1, 2, 2}, new int[]{1, 2, 1, 2}, 4))));
//            System.out.println(cp.coordinatesToString());
        //       }

        // Insertion
        Hashtable<String, Object> toInsert = new Hashtable<>();
        toInsert.put("name", new String("Marina"));
        toInsert.put("age", new Integer(3));
        toInsert.put("gpa", new Double(5.0));
        toInsert.put("polygon", new Polygon(new int[]{3, 3, 4, 4}, new int[]{1, 2, 1, 2}, 4));
        toInsert.put("id", 100);


        // Deletion
//        Hashtable<String, Object> toDelete = new Hashtable<>();
//        toInsert.put("name", new String("Marina"));
//        dbApp.deleteFromTable(strTableName, toDelete);

        p1 = (Page) DBApp.readFile("data/Studentpage1.class");
        System.out.println(p1.getTuples());
        p2 = (Page) DBApp.readFile("data/Studentpage2.class");
        System.out.println(p2.getTuples());
        p3 = (Page) DBApp.readFile("data/Studentpage3.class");
        System.out.println(p3.getTuples());
        Page p4 = (Page) DBApp.readFile("data/Studentpage4.class");
        System.out.println(p4.getTuples());

        System.out.println("AGE");
        treeage = (BPTree) DBApp.readFile("data/StudentindicesageBtree.class");
        System.out.println(treeage);

        for (int i = 1; i <= 8; i++) {
            if (i == 7) continue;
            Pointer p = treeage.find(new Integer(i));
            if (p == null) System.out.println(i + " is deleted");
            else if (p instanceof TuplePointer) System.out.println(p);
            else {
                ovp = (OverFlowPage) DBApp.readFile(p.getPagePath());
                for (TuplePointer tp : ovp.getPointers()) System.out.println(tp);
            }
        }

        System.out.println("ID");
        treeid = (BPTree) DBApp.readFile("data/StudentindicesidBtree.class");
        System.out.println(treeid);

        for (int i = 20; i <= 80; i += 10) {
            Pointer p = treeid.find(new Integer(i));
            if (p == null) System.out.println(i + " is deleted");
            else if (p instanceof TuplePointer) System.out.println(p);
            else {
                OverFlowPage ovp1 = (OverFlowPage) DBApp.readFile(p.getPagePath());
                for (TuplePointer tp : ovp1.getPointers()) System.out.println(tp);
            }
        }

        Pointer onehundred = treeid.find(new Integer(100));
        if (onehundred == null) System.out.println("100 is deleted");
        else if (onehundred instanceof TuplePointer) System.out.println(onehundred);
        else {
            OverFlowPage onehundredovp = (OverFlowPage) DBApp.readFile(onehundred.getPagePath());
            for (TuplePointer tp : onehundredovp.getPointers()) System.out.println(tp);
        }
//--------------------------------------------------------
        System.out.println("NAME");
        treename = (BPTree) DBApp.readFile("data/StudentindicesnameBtree.class");
        System.out.println(treename);

        pMonica = treename.find(new String("Monica"));
        if (pMonica == null) System.out.println("Monica is deleted");
        else if (pMonica instanceof TuplePointer) System.out.print(pMonica);
        else {
            OverFlowPage ovpMonica = (OverFlowPage) DBApp.readFile(pMonica.getPagePath());
            for (TuplePointer tp : ovpMonica.getPointers()) System.out.println(tp);
        }

        pMarina = treename.find(new String("Marina"));
        if (pMarina == null) System.out.println("Marina is deleted");
        else if (pMarina instanceof TuplePointer) System.out.print(pMarina);
        else {
            OverFlowPage ovpMarina = (OverFlowPage) DBApp.readFile(pMarina.getPagePath());
            for (TuplePointer tp : ovpMarina.getPointers()) System.out.println(tp);
        }

        pSarah = treename.find(new String("Sarah"));
        if (pSarah == null) System.out.println("Sarah is deleted");
        else if (pSarah instanceof TuplePointer) System.out.print(pSarah);
        else {
            OverFlowPage ovpSarah = (OverFlowPage) DBApp.readFile(pSarah.getPagePath());
            for (TuplePointer tp : ovpSarah.getPointers()) System.out.println(tp);
        }

        pJohn = treename.find(new String("John Noor"));
        if (pJohn == null) System.out.println("John Noor is deleted");
        else if (pJohn instanceof TuplePointer) System.out.print(pJohn);
        else {
            OverFlowPage ovpJohn = (OverFlowPage) DBApp.readFile(pJohn.getPagePath());
            for (TuplePointer tp : ovpJohn.getPointers()) System.out.println(tp);
        }

        pAhmed = treename.find(new String("Ahmed Noor"));
        if (pAhmed == null) System.out.println("Ahmed Noor is deleted");
        else if (pAhmed instanceof TuplePointer) System.out.print(pAhmed);
        else {
            OverFlowPage ovpAhmed = (OverFlowPage) DBApp.readFile(pAhmed.getPagePath());
            for (TuplePointer tp : ovpAhmed.getPointers()) System.out.println(tp);
        }

        pZaky = treename.find(new String("Zaky Noor"));
        if (pZaky == null) System.out.println("Zaky Noor is deleted");
        else if (pZaky instanceof TuplePointer) System.out.print(pZaky);
        else {
            OverFlowPage ovpZaky = (OverFlowPage) DBApp.readFile(pZaky.getPagePath());
            for (TuplePointer tp : ovpZaky.getPointers()) System.out.println(tp);
        }

        Pointer pNoor = treename.find(new String("Noor"));
        if (pNoor == null) System.out.println("Noor is deleted");
        else if (pNoor instanceof TuplePointer) System.out.println(pNoor);
        else {
            OverFlowPage ovpNoor = (OverFlowPage) DBApp.readFile(pNoor.getPagePath());
            for (TuplePointer tp : ovpNoor.getPointers()) System.out.println(tp);
        }
//--------------------------------------------------------
        System.out.println("POLYGON");
        treepolygon = (RTree) DBApp.readFile("data/StudentindicespolygonRtree.class");
        System.out.println(treepolygon);
    }
}
