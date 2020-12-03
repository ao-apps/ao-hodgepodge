/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010, 2011, 2013, 2016, 2019, 2020  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aocode-public.
 *
 * aocode-public is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aocode-public is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aocode-public.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.swing;

import com.aoindustries.i18n.Resources;
import com.aoindustries.util.tree.Node;
import com.aoindustries.util.tree.Tree;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

/**
 * When requested, it will recursively synchronize its children to an externally-
 * provided tree.  The is useful when the data source is obtained elsewhere and
 * the tree-based component needs to be synchronized.
 *
 * Since these updates may occur while a user is manipulating the components,
 * only the minimum number of changes to the elements is made.  Thus selections
 * and other aspects of the component remain intact.  Most importantly, if nothing
 * in the list has changed, the component is not changed.
 *
 * @author  AO Industries, Inc.
 */
public class SynchronizingMutableTreeNode<E> extends DefaultMutableTreeNode {

	private static final Resources RESOURCES = Resources.getResources(SynchronizingMutableTreeNode.class.getPackage());

	private static final long serialVersionUID = 7316928657213073513L;

	public SynchronizingMutableTreeNode() {
		super();
	}

	public SynchronizingMutableTreeNode(Object userObject) {
		super(userObject);
	}

	public SynchronizingMutableTreeNode(Object userObject, boolean allowsChildren) {
		super(userObject, allowsChildren);
	}

	/**
	 * Synchronizes the children of this node with the roots of the provided
	 * tree while adding and removing only a minimum number of nodes.
	 * Comparisons are performed using equals on the value objects.  This must
	 * be called from the Swing event dispatch thread.
	 */
	public void synchronize(DefaultTreeModel treeModel, Tree<E> tree) throws IOException, SQLException {
		assert SwingUtilities.isEventDispatchThread() : RESOURCES.getMessage("assert.notRunningInSwingEventThread");
		synchronize(treeModel, tree.getRootNodes());
	}

	/**
	 * Synchronizes the children of this node with the provided children
	 * while adding and removing only a minimum number of nodes.
	 * Comparisons are performed using equals on the value objects.  This must
	 * be called from the Swing event dispatch thread.
	 *
	 * @param  children  If children is null, then doesn't allow children.
	 */
	@SuppressWarnings("unchecked")
	public void synchronize(DefaultTreeModel treeModel, List<Node<E>> children) throws IOException, SQLException {
		assert SwingUtilities.isEventDispatchThread() : RESOURCES.getMessage("assert.notRunningInSwingEventThread");
		if(children==null) {
			// No children allowed
			while(getChildCount() > 0) {
				treeModel.removeNodeFromParent((MutableTreeNode)getChildAt(getChildCount()-1));
			}
			if(getAllowsChildren()) {
				setAllowsChildren(false);
				treeModel.reload(this);
			}
		} else {
			// Children allowed
			if(!getAllowsChildren()) {
				setAllowsChildren(true);
				treeModel.reload(this);
			}

			// Update the children minimally
			int size = children.size();
			for(int index=0; index<size; index++) {
				Node<E> child = children.get(index);
				E value = child.getValue();
				SynchronizingMutableTreeNode<E> synchronizingNode;
				if(index>=getChildCount()) {
					synchronizingNode = new SynchronizingMutableTreeNode<>(value);
					treeModel.insertNodeInto(synchronizingNode, this, index);
				} else {
					synchronizingNode = (SynchronizingMutableTreeNode<E>)getChildAt(index);
					if(!synchronizingNode.getUserObject().equals(value)) {
						// Objects don't match
						// If this object is found further down the list, then delete up to that object
						int foundIndex = -1;
						for(int searchIndex = index+1, count=getChildCount(); searchIndex<count; searchIndex++) {
							synchronizingNode = (SynchronizingMutableTreeNode<E>)getChildAt(searchIndex);
							if(synchronizingNode.getUserObject().equals(value)) {
								foundIndex = searchIndex;
								break;
							}
						}
						if(foundIndex!=-1) {
							for(int removeIndex=foundIndex-1; removeIndex>=index; removeIndex--) {
								treeModel.removeNodeFromParent((MutableTreeNode)getChildAt(removeIndex));
							}
							// synchronizingNode already contains the right node
						} else {
							// Otherwise, insert in the current index
							synchronizingNode = new SynchronizingMutableTreeNode<>(value);
							treeModel.insertNodeInto(synchronizingNode, this, index);
						}
					}
				}
				// Recursively synchronize the children
				synchronizingNode.synchronize(treeModel, child.getChildren());
			}
			// Remove any extra children
			while(getChildCount() > size) {
				treeModel.removeNodeFromParent((MutableTreeNode)getChildAt(getChildCount()-1));
			}
		}
	}
}
