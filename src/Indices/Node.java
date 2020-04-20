package Indices;

import java.io.Serializable;

public class Node implements Serializable{
	BPTree tree;
	RTree rtree;
	int max, min;
	String parent;
	private static final long serialVersionUID = 3L;

	public Node(BPTree tree, String parent)
	{
		this.parent = parent;
		this.tree = tree;
		this.max = tree.n;
	}

	public Node(RTree tree, String parent)
	{
		this.parent = parent;
		this.rtree = tree;
		this.max = tree.n;
	}
}
