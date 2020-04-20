package Indices;

import CodingRaptors.CPolygon;
import CodingRaptors.DBApp;

import java.io.File;
import java.util.ArrayList;


public class Leaf extends Node {

    String nextLeafPath;
    String prevLeafPath;
    ArrayList<Pointer> pointers;
    private static final long serialVersionUID = 4L;

    public Leaf(BPTree tree, String parent) {
        super(tree, parent);
        this.min = (tree.n + 1) / 2;
        pointers = new ArrayList<Pointer>();
    }


    public Leaf(RTree tree, String parent) {
        super(tree, parent);
        this.min = (tree.n + 1) / 2;
        pointers = new ArrayList<Pointer>();
    }


    public ArrayList<Pointer> getPointers(){
        return pointers;
    }
    public String getNextLeafPath() {
        return nextLeafPath;
    }
    public String getPrevLeafPath() {
        return prevLeafPath;
    }

    public boolean insertSorted(TuplePointer tp) {
        boolean useRtree = false;
        if(tp.key instanceof CPolygon) useRtree = true;
        boolean exists = false;
        int i;

        // Check that key exists.
        for (i = 0; i < pointers.size(); i++)
            if (pointers.get(i).key.compareTo(tp.key) == 0) {
                exists = true;
                break;
            }

        // Key exists.
        if (exists) {
            // Pointer.
            if (!(pointers.get(i) instanceof TuplePointer)) {
                OverFlowPage ovp = (OverFlowPage) DBApp.readFile(pointers.get(i).pagePath);
                ovp.pointers.add(tp);
                DBApp.writeObject(ovp, pointers.get(i).pagePath);
            }
            // TuplePointer.
            else {
                OverFlowPage ovp = new OverFlowPage();
                ovp.pointers.add(tp);
                ovp.pointers.add((TuplePointer) pointers.get(i));

                // RTree - BPTree check.
                if (this.rtree == null) {
                    BPTree tree1 = (BPTree) DBApp.readFile(tree.pathToTree + "Btree.class");
                    DBApp.writeObject(ovp, tree.pathToTree + "overflow" + (tree1.getNumOverFlow()) + ".class");
                    pointers.set(i, new Pointer(tree.pathToTree + "overflow" + (tree1.getNumOverFlow()) + ".class", pointers.get(i).key));
                } else if (this.tree == null) {
                    RTree rtree = (RTree) DBApp.readFile(this.rtree.pathToTree + "Rtree.class");
                    DBApp.writeObject(ovp, rtree.pathToTree + "overflow" + (rtree.getNumOverFlow()) + ".class");
                    pointers.set(i, new Pointer(rtree.pathToTree + "overflow" + (rtree.getNumOverFlow()) + ".class", pointers.get(i).key));
                }
                return true;
            }
        }
        // Key does not exist.
        else {
            i = pointers.size() - 1;
            while (i >= 0 && pointers.get(i).compareTo(tp) > 0) {
                i--;
            }
            pointers.add(i + 1, tp);

        }
        return false;
    }

    public ArrayList<Pointer> getSecondHalf() {
        int half = (int) Math.floor((max + 1) / 2.0);
        ArrayList<Pointer> secondHalf = new ArrayList<>();
        while (half < pointers.size()) {
            secondHalf.add(pointers.remove(half));
        }
        return secondHalf;

    }

    public int deleteKey(Object key) {
        for (int i = 0; i < pointers.size(); i++) {
            if (key instanceof CPolygon) {
                if ((pointers.get(i).key).compareTo(key) == 0) {
                    if (!(pointers.get(i) instanceof TuplePointer)) {
                        File f = new File(pointers.get(i).pagePath);
                        f.delete();
                    }
                    pointers.remove(i);
                    return i;
                }
            }
            else if (pointers.get(i).key.equals(key)) {
                if (!(pointers.get(i) instanceof TuplePointer)) {
                    File f = new File(pointers.get(i).pagePath);
                    f.delete();
                }
                pointers.remove(i);
                return i;
            }
        }
        return -1;
    }

    public void borrowTuple(BPTree tree, Leaf sibling, NonLeaf parent, boolean left, int parentIdx, Comparable dKey) {
        boolean willUpdateUpper = this.pointers.size() == 0 || this.pointers.get(0).key.compareTo(dKey) > 0;
        if (left) {
//			willUpdateUpper = false;
            Pointer toBeBorrwed = sibling.pointers.remove(sibling.pointers.size() - 1);
            this.pointers.add(0, toBeBorrwed);
            //update parent
            parent.entries.get(parentIdx - 1).key = toBeBorrwed.key;
        } else {
//			willUpdateUpper &= parentIdx == 0;
            Pointer toBeBorrwed = sibling.pointers.remove(0);
            this.pointers.add(toBeBorrwed);
            Comparable newParent = sibling.pointers.get(0).key;
            parent.entries.get(parentIdx).key = newParent;

//			if(willUpdateUpper)
            {

                if (parentIdx == 0) {

                    tree.updateUpper(dKey, toBeBorrwed.key, this.parent);
                } else if (willUpdateUpper)
                    parent.entries.get(parentIdx - 1).key = this.pointers.get(0).key;
            }
        }

    }

    //////////////////////////////RTREEEEEEEEEEEEEEE///////////
    public void borrowTuple(RTree tree, Leaf sibling, NonLeaf parent, boolean left, int parentIdx, Comparable dKey) {
        boolean willUpdateUpper = this.pointers.size() == 0 || ((CPolygon) (this.pointers.get(0).key)).compareTo((CPolygon) dKey) > 0;
        if (left) {
//			willUpdateUpper = false;
            Pointer toBeBorrwed = sibling.pointers.remove(sibling.pointers.size() - 1);
            this.pointers.add(0, toBeBorrwed);
            //update parent
            parent.entries.get(parentIdx - 1).key = toBeBorrwed.key;
        } else {
//			willUpdateUpper &= parentIdx == 0;
            Pointer toBeBorrwed = sibling.pointers.remove(0);
            this.pointers.add(toBeBorrwed);
            Comparable newParent = sibling.pointers.get(0).key;
            parent.entries.get(parentIdx).key = newParent;

//			if(willUpdateUpper)
            {

                if (parentIdx == 0) {

                    tree.updateUpper(dKey, toBeBorrwed.key, this.parent);
                } else if (willUpdateUpper)
                    parent.entries.get(parentIdx - 1).key = this.pointers.get(0).key;
            }
        }

    }
    ////////////////////////////////////////////


    public void mergeWithLeaf(BPTree tree, Leaf sibling, NonLeaf parent, int parentIdx, boolean left, Comparable dKey) {
        String tmpPath = null;
        boolean willUpdateUpper = this.pointers.size() == 0 || this.pointers.get(0).key.compareTo(dKey) > 0;
        Comparable newKey = null;
        if (left) {
            willUpdateUpper = false;
            this.pointers.addAll(0, sibling.pointers);
            if (parentIdx > 0) {
                if (parentIdx > 1) {
                    parent.entries.get(parentIdx - 2).right = parent.entries.get(parentIdx - 1).right;
                } else if (parent.entries.size() == 1) {
                    tmpPath = parent.entries.get(parentIdx - 1).right;
                }
                parent.entries.remove(parentIdx - 1);

            }
        } else {
            willUpdateUpper &= parentIdx == 0;
            sibling.pointers.addAll(0, this.pointers);

            if (parentIdx > 0) {
                parent.entries.get(parentIdx - 1).right = parent.entries.get(parentIdx).right;
            } else if (parent.entries.size() == 1) {
                tmpPath = parent.entries.get(parentIdx).right;
            }
            parent.entries.remove(parentIdx);
            newKey = sibling.pointers.get(0).key;
        }
        if (willUpdateUpper) {
            tree.updateUpper(dKey, newKey, this.parent);


        }
        tree.handleParent(parent, this.parent, tmpPath);


    }

    ////////////////////////////RTREEEEEEEEEEEEEEEEEEEEE////////////////////////////////////
    public void mergeWithLeaf(RTree tree, Leaf sibling, NonLeaf parent, int parentIdx, boolean left, Comparable dKey) {
        String tmpPath = null;
        boolean willUpdateUpper = this.pointers.size() == 0 || ((CPolygon) (this.pointers.get(0).key)).compareTo(dKey) > 0;
        Comparable newKey = null;
        if (left) {
            willUpdateUpper = false;
            this.pointers.addAll(0, sibling.pointers);
            if (parentIdx > 0) {
                if (parentIdx > 1) {
                    parent.entries.get(parentIdx - 2).right = parent.entries.get(parentIdx - 1).right;
                } else if (parent.entries.size() == 1) {
                    tmpPath = parent.entries.get(parentIdx - 1).right;
                }
                parent.entries.remove(parentIdx - 1);

            }
        } else {
            willUpdateUpper &= parentIdx == 0;
            sibling.pointers.addAll(0, this.pointers);

            if (parentIdx > 0) {
                parent.entries.get(parentIdx - 1).right = parent.entries.get(parentIdx).right;
            } else if (parent.entries.size() == 1) {
                tmpPath = parent.entries.get(parentIdx).right;
            }
            parent.entries.remove(parentIdx);
            newKey = sibling.pointers.get(0).key;
        }
        if (willUpdateUpper) {
            tree.updateUpper(dKey, newKey, this.parent);


        }
        tree.handleParent(parent, this.parent, tmpPath);


    }


    public String toString() {
        String res = "Start LEAF\n";

        for (Pointer tp : this.pointers) {
            res += tp.key + " ";
        }
        return res + "\nEnd LEAF\n";
    }


}
