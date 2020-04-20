package Indices;

import java.io.*;
import java.util.*;

import CodingRaptors.DBApp;
import CodingRaptors.Page;

public class BPTree implements Serializable {
    int n, countNodes, minLeaf, minNonLeaf, numOverFlow;
    Node root;
    String tableName, column;
    String pathToTree, rootPath;
    private static final long serialVersionUID = 2L;

    public BPTree(String tableName, String column) {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream("config/DBApp.properties"));
            n = Integer.parseInt(prop.getProperty("NodeSize"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.tableName = tableName;
        this.column = column;
        countNodes = 1;
        root = new Leaf(this, null);
        root.min = 1;
        minLeaf = (n + 1) / 2;
        minNonLeaf = (int) Math.ceil((n + 1) / 2.0) - 1;
        pathToTree = "data/" + tableName + "indices" + column;
        rootPath = pathToTree + "0.class";
        DBApp.writeObject(this, pathToTree + "Btree.class");
        DBApp.writeObject(root, rootPath);
    }


    public int getNumOverFlow() {
        return numOverFlow;
    }

// ---------------------------------------------------------------------------------------------------------------------
    public void insert(Comparable key, String pagePath, int idx) {

        String pathToLeaf = findLeaf(rootPath, key, null);
        Leaf leaf = (Leaf) DBApp.readFile(pathToLeaf);
        if (pathToLeaf.equals(rootPath)) {
            this.root = leaf;
        }
        TuplePointer newPointer = new TuplePointer(idx, pagePath, key);
        if (leaf.pointers.size() == 0) {
            leaf.pointers.add(newPointer);
            DBApp.writeObject(leaf, pathToLeaf);
            return;
        }
        if (leaf.insertSorted(newPointer)) numOverFlow++;

        if (leaf.pointers.size() > leaf.max) {
            Leaf newLeaf = new Leaf(this, leaf.parent);
            newLeaf.nextLeafPath = leaf.nextLeafPath;
            newLeaf.prevLeafPath = pathToLeaf;

            String newPath = pathToTree + countNodes++ + ".class";
            leaf.nextLeafPath = newPath;
            newLeaf.pointers = leaf.getSecondHalf();
            DBApp.writeObject(newLeaf, newPath);
            DBApp.writeObject(leaf, pathToLeaf);
            insertIntoNonLeaf(leaf.parent, new NodeEntry(newLeaf.pointers.get(0).key, pathToLeaf, newPath));

        }


        DBApp.writeObject(leaf, pathToLeaf);
        DBApp.writeObject(this, pathToTree + "Btree.class");


    }

// ---------------------------------------------------------------------------------------------------------------------
    private void insertIntoNonLeaf(String pathToNode, NodeEntry ne) {

        if (pathToNode == null) {

            NonLeaf root = new NonLeaf(this, null);

            {

                this.root.min = (int) Math.ceil((n + 1) / 2.0) - 1;


                DBApp.writeObject(this.root, this.rootPath);


            }

            root.min = 1;
            rootPath = pathToTree + countNodes++ + ".class";
            root.entries.add(ne);

            this.root = root;
            DBApp.writeObject(this, pathToTree + "Btree.class");
            DBApp.writeObject(root, rootPath);

            return;
        }

        NonLeaf nl = (NonLeaf) DBApp.readFile(pathToNode);

        nl.insertSorted(ne);


        if (nl.entries.size() > nl.max) {

            ArrayList<NodeEntry> nes = nl.getSecondHalf();

            NonLeaf newNode = new NonLeaf(this, nl.parent);
            String newPath = pathToTree + (countNodes++) + ".class";
            newNode.entries = nes;

            NodeEntry first = nes.remove(0);

            first.left = pathToNode;
            first.right = newPath;


            DBApp.writeObject(newNode, newPath);
            insertIntoNonLeaf(nl.parent, first);


        }
        DBApp.writeObject(nl, pathToNode);

    }

// ---------------------------------------------------------------------------------------------------------------------
    public void delete(Comparable key) {
        String pathToLeaf = findLeaf(rootPath, key, null);
        Leaf leaf = (Leaf) DBApp.readFile(pathToLeaf);
        int deletedIdx = leaf.deleteKey(key);


        if (leaf.parent == null) {
            DBApp.writeObject(leaf, pathToLeaf);
            return;
        }
        if (leaf.pointers.size() < leaf.min) {
            NonLeaf parent = (NonLeaf) DBApp.readFile(leaf.parent);

            LeftAndRightSiblings lrs = getSibLings(pathToLeaf, parent);

            String siblingLeft = lrs.sibLingLeft;
            String siblingRight = lrs.sibLingRight;
            int parentIdx = lrs.idx;

            Leaf leftLeaf = null;
            Leaf rightLeaf = null;
            if (siblingLeft != null) {

                // borrow from left

                leftLeaf = (Leaf) DBApp.readFile(siblingLeft);

                if (leftLeaf.pointers.size() > leftLeaf.min) {

                    leaf.borrowTuple(this, leftLeaf, parent, true, parentIdx, key);
                    DBApp.writeObject(leftLeaf, siblingLeft);
                } else if (siblingRight != null) {


                    rightLeaf = (Leaf) DBApp.readFile(siblingRight);
                    if (rightLeaf.pointers.size() > rightLeaf.min) {
                        leaf.borrowTuple(this, rightLeaf, parent, false, parentIdx, key);
                        DBApp.writeObject(rightLeaf, siblingRight);
                    } else {
                        leaf.mergeWithLeaf(this, leftLeaf, parent, parentIdx, true, key);
                        DBApp.writeObject(leftLeaf, siblingLeft);
                    }
                } else {
                    leaf.mergeWithLeaf(this, leftLeaf, parent, parentIdx, true, key);
                    DBApp.writeObject(leftLeaf, siblingLeft);
                }

            } else if (siblingRight != null) {

                // borrow from right
                rightLeaf = (Leaf) DBApp.readFile(siblingRight);
                if (rightLeaf.pointers.size() > rightLeaf.min) {
                    leaf.borrowTuple(this, rightLeaf, parent, false, parentIdx, key);
                    DBApp.writeObject(rightLeaf, siblingRight);
                } else {
                    leaf.mergeWithLeaf(this, rightLeaf, parent, parentIdx, false, key);
                    DBApp.writeObject(rightLeaf, siblingRight);
                }
            }

            DBApp.writeObject(leaf, pathToLeaf);
            DBApp.writeObject(parent, leaf.parent);


        } else {
            if (deletedIdx == 0) {
                Comparable newKey = leaf.pointers.get(0).key;
                updateUpper(key, newKey, leaf.parent);
            }
            DBApp.writeObject(leaf, pathToLeaf);

        }

        DBApp.writeObject(this, pathToTree + "Btree.class");

    }

// ---------------------------------------------------------------------------------------------------------------------
    protected void handleParent(NonLeaf currentNode, String pathToNode, String tmpPath) {
        if (pathToNode.equals(rootPath))
            this.root = currentNode;

        if (currentNode.entries.size() >= currentNode.min)
            return;
        if (currentNode.parent == null) {

            // current node is the root
            if (currentNode.entries.size() == 0) {


                this.root.min = (int) Math.ceil((this.n + 1) / 2.0) - 1;
                DBApp.writeObject(this.root, this.rootPath);

                this.rootPath = tmpPath;
                this.root = (Node) DBApp.readFile(tmpPath);


            }
        } else {

            NonLeaf parent = (NonLeaf) DBApp.readFile(currentNode.parent);

            LeftAndRightSiblings lrs = getSibLings(pathToNode, parent);
            String siblingLeft = lrs.sibLingLeft;
            String siblingRight = lrs.sibLingRight;
            int parentIdx = lrs.idx;
            NonLeaf leftNonLeaf = null;
            NonLeaf rightNonLeaf = null;
            if (siblingLeft != null) {

                // borrow from left
                leftNonLeaf = (NonLeaf) DBApp.readFile(siblingLeft);

                if (leftNonLeaf.entries.size() > this.minNonLeaf) {
//					

                    currentNode.borrow(leftNonLeaf, parent, true, parentIdx, tmpPath);
                    DBApp.writeObject(leftNonLeaf, siblingLeft);
                } else if (siblingRight != null) {


                    rightNonLeaf = (NonLeaf) DBApp.readFile(siblingRight);
                    if (rightNonLeaf.entries.size() > this.minNonLeaf) {
                        currentNode.borrow(rightNonLeaf, parent, false, parentIdx, tmpPath);
                        DBApp.writeObject(rightNonLeaf, siblingRight);
                    } else {
                        currentNode.mergeWithNonLeaf(this, leftNonLeaf, parent, true, parentIdx, tmpPath);
                        DBApp.writeObject(leftNonLeaf, siblingLeft);
                    }
                } else {


                    currentNode.mergeWithNonLeaf(this, leftNonLeaf, parent, true, parentIdx, tmpPath);


                    DBApp.writeObject(leftNonLeaf, siblingLeft);

                }

            } else if (siblingRight != null) {
                // borrow from right
                rightNonLeaf = (NonLeaf) DBApp.readFile(siblingRight);
                if (rightNonLeaf.entries.size() > rightNonLeaf.min) {
                    currentNode.borrow(rightNonLeaf, parent, false, parentIdx, tmpPath);
                    DBApp.writeObject(rightNonLeaf, siblingRight);
                } else {
                    currentNode.mergeWithNonLeaf(this, rightNonLeaf, parent, false, parentIdx, tmpPath);
                    DBApp.writeObject(rightNonLeaf, siblingRight);
                }
            } else {

            }
            DBApp.writeObject(currentNode, pathToNode);
            DBApp.writeObject(parent, currentNode.parent);
        }
    }

// ---------------------------------------------------------------------------------------------------------------------
    private static LeftAndRightSiblings getSibLings(String pathToNode, NonLeaf parent) {
        String siblingLeft = null;
        String siblingRight = null;
        int parentIdx = -1;
        for (int i = 0; i < parent.entries.size(); i++) {
            NodeEntry e = parent.entries.get(i);
            if (e.left.equals(pathToNode)) {
                parentIdx = i;
                if (i > 0)
                    siblingLeft = parent.entries.get(i - 1).left;
                siblingRight = e.right;
                break;
            }
        }

        if (parent.entries.get(parent.entries.size() - 1).right.equals(pathToNode)) {

            parentIdx = parent.entries.size();
            siblingLeft = parent.entries.get(parent.entries.size() - 1).left;
        }

        return new LeftAndRightSiblings(siblingLeft, siblingRight, parentIdx);
    }

// ---------------------------------------------------------------------------------------------------------------------
    static class LeftAndRightSiblings {
        String sibLingLeft;
        String sibLingRight;
        int idx;

        public LeftAndRightSiblings(String left, String right, int i) {
            this.sibLingLeft = left;
            this.sibLingRight = right;
            this.idx = i;

        }
    }

// ---------------------------------------------------------------------------------------------------------------------
    protected void updateUpper(Comparable oldKey, Comparable newKey, String pathToNode) {
        if (pathToNode == null)
            return;
        NonLeaf nextNode = (NonLeaf) DBApp.readFile(pathToNode);

        for (NodeEntry e : nextNode.entries) {
            if (e.key.equals(oldKey)) {
                e.key = newKey;
                DBApp.writeObject(nextNode, pathToNode);
                return;
            }
        }

        updateUpper(oldKey, newKey, nextNode.parent);
    }

// ---------------------------------------------------------------------------------------------------------------------
    public Pointer find(Comparable key) {
        Leaf r = (Leaf) DBApp.readFile(findLeaf(rootPath, key, null));
        for (int i = 0; i < r.pointers.size(); i++) {
            Pointer k = r.pointers.get(i);
            if (k.key.equals(key)) return r.pointers.get(i);
            else if (k.key.compareTo(key) > 0)
                break;
        }
        return null;
    }

// ---------------------------------------------------------------------------------------------------------------------
    public void updateKey(TuplePointer tp, Comparable oldKey, Comparable newKey) {
        TuplePointer tuplepointer = tp;
        tuplepointer.setKey(oldKey);

        deletePointer(tuplepointer);
        insert(newKey, tp.pagePath, tp.idx);
    }
// ---------------------------------------------------------------------------------------------------------------------
    public void deletePointer(TuplePointer tp) {
        String leafPath = findLeaf(rootPath, tp.key, null);
        Leaf r = (Leaf) DBApp.readFile(leafPath);
        ArrayList<Pointer> pointer = r.pointers;

        for (int i = 0; i < pointer.size(); i++) {
            if (pointer.get(i) instanceof TuplePointer) {
                if (pointer.get(i).getPagePath().equals(tp.getPagePath()) && ((TuplePointer) pointer.get(i)).getIdx() == tp.getIdx())
                    delete(tp.key);
            } else {
                OverFlowPage ovp = (OverFlowPage) DBApp.readFile(pointer.get(i).getPagePath());
                ArrayList<TuplePointer> arr = ovp.getPointers();
                for (int j = 0; j < arr.size(); j++)
                    if (arr.get(j).getIdx() == tp.getIdx() && arr.get(j).getPagePath().equals(tp.getPagePath()))
                        arr.remove(j);

                ovp.setPointers(arr);
                DBApp.writeObject(ovp, pointer.get(i).getPagePath());

                if (ovp.getPointers().size() == 1) {
                    delete(tp.key);
                    insert(ovp.getPointers().get(0).getKey(), ovp.getPointers().get(0).getPagePath(), ovp.getPointers().get(0).getIdx());
                }
            }
        }


    }

// ---------------------------------------------------------------------------------------------------------------------
    public void updateTuplePointer(TuplePointer oldPointer, TuplePointer newPointer) {
        String leafPath = findLeaf(rootPath, oldPointer.key, null);
        Leaf r = (Leaf) DBApp.readFile(leafPath);

        for (int i = 0; i < r.pointers.size(); i++) {
            Pointer p = r.pointers.get(i);
            // TuplePointer.
            if (p instanceof TuplePointer) {
                if ((((TuplePointer) p).idx == oldPointer.idx) &&
                        (p.pagePath.equals(oldPointer.pagePath))) {

                    ((TuplePointer) p).idx = newPointer.idx;
                    p.pagePath = newPointer.pagePath;

                    r.pointers.set(i, p);
                    DBApp.writeObject(r, leafPath);
                }
            }
            // Pointer.
            else {
                OverFlowPage ovp = (OverFlowPage) DBApp.readFile(p.pagePath);

                for (int j = 0; j < ovp.pointers.size(); j++) {
                    TuplePointer op = ovp.pointers.get(j);
                    if ((op.pagePath.equals(oldPointer.pagePath)) &&
                            (op.idx == oldPointer.idx)) {

                        op.pagePath = newPointer.pagePath;
                        op.idx = newPointer.idx;

                        ovp.pointers.set(j, op);
                    }
                }

                DBApp.writeObject(ovp, p.pagePath);
            }
        }
    }
// ---------------------------------------------------------------------------------------------------------------------
    private String findLeaf(String current, Object key, String parent) {
        Node cur = (Node) DBApp.readFile(current);
        cur.parent = parent;
        DBApp.writeObject(cur, current);

        if (cur instanceof Leaf) {
            return current;
        }

        NonLeaf curr = (NonLeaf) cur;
        String path = "";
        for (int i = 0; i < curr.entries.size(); i++) {
            if (curr.entries.get(i).key.compareTo(key) > 0) {
                path = curr.entries.get(i).left;
                break;
            } else if (i == curr.entries.size() - 1) {
                path = curr.entries.get(i).right;
            }

        }
        return findLeaf(path, key, current);
    }

// ---------------------------------------------------------------------------------------------------------------------
    public String toString() {
        return printTree(rootPath);
    }

// ---------------------------------------------------------------------------------------------------------------------
    private String printTree(String path) {

        Node n = (Node) DBApp.readFile(path);
        String res = "";
        if (n instanceof Leaf) {
            Leaf l = (Leaf) n;
            res += "LEAF: [";

            for (int i = 0; i < l.max; i++) {
                if (i < l.pointers.size()) res += l.pointers.get(i).key.toString();
                else res += " ";
                if (i < l.max - 1) res += "|";
            }

            res += "]\n";
            return res;
        }

        NonLeaf nl = (NonLeaf) n;
        res += "[";
        for (int i = 0; i < nl.max; i++) {
            if (i < nl.entries.size()) res += nl.entries.get(i).key.toString();
            else res += " ";
            if (i < nl.max - 1) res += "|";
        }
        res += "]\n";
        res += printTree(nl.entries.get(0).left);

        for (NodeEntry e : nl.entries) {
            res += printTree(e.right);
        }

        return res;
    }

// ---------------------------------------------------------------------------------------------------------------------
    public Leaf getLeaf(Comparable key) {
        return (Leaf) DBApp.readFile(findLeaf(rootPath, key, null));
    }
// ---------------------------------------------------------------------------------------------------------------------
    public Leaf getLeftmostLeaf(){
        Node root = (Node) DBApp.readFile(this.rootPath);
        if(root instanceof Leaf)
            return (Leaf) root;
        else{
            Node nl = root;
            while(nl instanceof NonLeaf){
                nl = (Node) DBApp.readFile(((NonLeaf) nl).getEntries().get(0).left);
            }
            return (Leaf) nl;
        }
    }
}

