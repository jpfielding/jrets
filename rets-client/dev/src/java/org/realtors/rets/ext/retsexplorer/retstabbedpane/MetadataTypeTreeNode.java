package org.realtors.rets.ext.retsexplorer.retstabbedpane;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.swing.tree.TreeNode;

import org.realtors.rets.common.metadata.MetaObject;
import org.realtors.rets.common.metadata.MetadataType;
import org.realtors.rets.common.metadata.types.MTable;
import org.realtors.rets.ext.util.RetsFieldNameType;

import com.google.common.base.Supplier;
import com.google.common.collect.Iterators;

public class MetadataTypeTreeNode implements TreeNode {
	private MetadataType type;
	private MetaObject object;
	private TreeNode parentNode;
	private Supplier<RetsFieldNameType> producer;

	private Collection children;
	private LinkedList childNodes;

	public MetadataTypeTreeNode(Supplier<RetsFieldNameType> producer, MetadataType type, MetaObject parentObject, TreeNode parentNode) {
		this.type = type;
		this.object = parentObject;
		this.parentNode = parentNode;
		this.producer = producer;
	}

	private Collection getChildren() {
		if (this.children == null) {
			this.children = this.object.getChildren(this.type);
			if (this.children == null) {
				this.children = Collections.emptyList();
				return this.children;
			}
			LinkedList<MetaObject> tmp = new LinkedList<MetaObject>(
					this.children);
			// Sort our children
			Collections.sort(tmp, new Comparator<MetaObject>() {
				public int compare(MetaObject o1, MetaObject o2) {
					return o1.getId().compareTo(o2.getId());
				}
			});
			this.children = tmp;
		}
		return this.children;

	}

	private List getChildNodes() {
		if (this.childNodes == null) {
			this.childNodes = new LinkedList();
			for (Object o : this.getChildren()) {
				MetaObject childObject = (MetaObject) o;
				this.childNodes.add(new MetaObjectTreeNode(this.producer,childObject, this.type, this));
			}
		}
		return this.childNodes;
	}

	@Override
	public String toString() {
		return this.getActionCommand();
	}

	public Enumeration children() {
		return Iterators.asEnumeration(getChildNodes().iterator());
	}

	public String getActionCommand() {
		if (this.object instanceof MTable)
			return this.producer.get().getFieldName((MTable) this.object);
		return this.type.name();
	}

	public boolean getAllowsChildren() {
		return true;
	}

	public TreeNode getChildAt(int childIndex) {
		return (TreeNode) getChildNodes().get(childIndex);
	}

	public int getChildCount() {
		return getChildren().size();
	}

	public int getId() {
		return System.identityHashCode(this);
	}

	public int getIndex(TreeNode node) {
		return getChildNodes().indexOf(node);
	}

	public TreeNode getParent() {
		return this.parentNode;
	}

	public boolean isLeaf() {
		return getChildren().isEmpty();
	}

	public MetaObject getObject() {
		return this.object;
	}

	public MetadataType getType() {
		return this.type;
	}
}
