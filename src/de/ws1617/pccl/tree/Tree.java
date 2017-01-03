package de.ws1617.pccl.tree;

import java.util.ArrayList;
import java.util.List;

/**
 * A general implementation of a tree.
 * 
 * @author bjoern
 *
 * @param <T>
 */
public class Tree<T> {

	private T data;

	private List<Tree<T>> children;

	/**
	 * Constructs a tree with the given data and without children.
	 * 
	 * @param data
	 *            the node's data.
	 */
	public Tree(T data) {
		this.data = data;
	}

	public void addChild(Tree<T> c) {
		if (this.children == null) {
			this.children = new ArrayList<Tree<T>>();
		}
		this.children.add(c);
	}

	/**
	 * Constructs a tree with the given data and the given dependent nodes.
	 * 
	 * @param data
	 *            the node's data.
	 * @param children
	 *            the children.
	 */
	public Tree(T data, List<Tree<T>> children) {
		this.data = data;
		this.children = children;
	}

	/**
	 * @return the data
	 */
	public T getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(T data) {
		this.data = data;
	}

	/**
	 * @return the children
	 */
	public List<Tree<T>> getChildren() {
		return children;
	}

	/**
	 * @param children
	 *            the children to set
	 */
	public void setChildren(List<Tree<T>> children) {
		this.children = children;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((children == null) ? 0 : children.hashCode());
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tree other = (Tree) obj;
		if (children == null) {
			if (other.children != null)
				return false;
		} else if (!children.equals(other.children))
			return false;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return data + " " + children;
	}

}
