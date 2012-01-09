package org.realtors.rets.retsexplorer.retstabbedpane;

import java.util.Enumeration;

import javax.swing.tree.TreeNode;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.realtors.rets.common.metadata.MetaObject;
import org.realtors.rets.common.metadata.MetadataType;
import org.realtors.rets.common.metadata.types.MTable;
import org.realtors.rets.ext.RetsFieldNameType;

import com.google.common.base.Supplier;
import com.google.common.collect.Iterators;

public class MetaObjectTreeNode implements TreeNode {

	// Member variables
	private MetaObject object;
	private MetadataType type;
	private MetadataTypeTreeNode parent;

	// Cached information
	private transient MetadataType[] childTypes;
	private transient TreeNode[] childNodes;
	private final Supplier<RetsFieldNameType> producer;

	public MetaObjectTreeNode(Supplier<RetsFieldNameType> producer,MetaObject object, MetadataType type, MetadataTypeTreeNode parent) {
		this.producer = producer;
		this.object = object;
		this.type = type;
		this.parent = parent;
	}

	// ---- OBJECT IMPL
	@Override
	public String toString() {
		return this.getActionCommand();
	}

	// ---- SUPPORT METHODS
	private MetadataType[] getChildTypes() {
		if (this.childTypes == null)
			this.childTypes = this.object.getChildTypes();
		return this.childTypes;
	}

	private TreeNode[] getChildNodes() {
		if (this.childNodes == null) {
			MetadataType[] types = getChildTypes();
			this.childNodes = new MetadataTypeTreeNode[types.length];
			for (int i = 0; i < this.childNodes.length; i++) {
				this.childNodes[i] = new MetadataTypeTreeNode(this.producer,types[i], this.object, this);
			}
		}
		return this.childNodes;
	}

	// ---- TREE NODE IMPL
	public Enumeration children() {
		return Iterators.asEnumeration(Iterators.forArray(getChildNodes()));
	}

	public String getActionCommand() {
		if (this.object instanceof MTable)
			return StringUtils.trimToEmpty(this.producer.get().getFieldName((MTable) this.object));
		return StringUtils.trimToEmpty(this.object.getId());
	}

	public boolean getAllowsChildren() {
		return !ArrayUtils.isEmpty(getChildTypes());
	}

	public TreeNode getChildAt(int childIndex) {
		return this.getChildNodes()[childIndex];
	}

	public int getChildCount() {
		return ArrayUtils.getLength(getChildTypes());
	}

	public int getId() {
		return System.identityHashCode(this.object);
	}

	public int getIndex(TreeNode node) {
		// brute force iterate over child type objects and find the one that
		// matches the MetadataType of node
		MetadataTypeTreeNode childNode = (MetadataTypeTreeNode) node;
		MetadataType childType = childNode.getType();
		for (int i = 0; i < getChildTypes().length; i++) {
			MetadataType t = getChildTypes()[i];
			if (t.equals(childType))
				return i;
		}
		return -1;
	}

	public TreeNode getParent() {
		return this.parent;
	}

	public boolean isLeaf() {
		return ArrayUtils.isEmpty(getChildTypes());
	}

	public MetadataType getMetadataType() {
		return this.type;
	}

	public MetaObject getMetaObject() {
		return this.object;
	}
}
