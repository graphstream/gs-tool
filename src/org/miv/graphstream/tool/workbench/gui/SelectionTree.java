/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package org.miv.graphstream.tool.workbench.gui;

import org.miv.graphstream.graph.Element;
import org.miv.graphstream.graph.Node;

import org.miv.graphstream.tool.workbench.Context;
import org.miv.graphstream.tool.workbench.cli.CLI;
import org.miv.graphstream.tool.workbench.event.ContextChangeListener;
import org.miv.graphstream.tool.workbench.event.ContextEvent;
import org.miv.graphstream.tool.workbench.event.SelectionListener;
import org.miv.graphstream.tool.workbench.event.SelectionEvent;

import java.util.Map;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.Iterator;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;

public class SelectionTree extends JTree 
	implements ContextChangeListener, SelectionListener
{
	public static final long serialVersionUID = 0x00A00C01L;
	
	protected CLI cli;
	protected SelectionTreeNode selection;
	protected SelectionTreeModel model;
	protected Context ctx;
	protected Map<String,SelectionTreeNode> nodes;
	
	public SelectionTree( CLI cli )
	{
		this.cli = cli;
		this.ctx = null;
		this.selection = new SelectionTreeNode( "selection", false );
		this.model = new SelectionTreeModel( this.selection );
		this.nodes = new HashMap<String,SelectionTreeNode>();
		
		cli.getCore().addContextChangeListener( this );
		
		setModel( this.model );
		setRootVisible( false );
		setCellRenderer( new SelectionTreeRenderer() );
	}
	
	public void contextChanged( ContextEvent e )
	{
		updateSelectionTree();
	}
	
	protected void updateSelectionTree()
	{
		if( cli.getCore().getActiveContext() == null )
		{
			selection.removeAllChildren();
			model.reload(selection);
			return;
		}
		if( ctx == cli.getCore().getActiveContext() ) return;
		
		if( ctx != null )
			selection.removeAllChildren();
		
		ctx = cli.getCore().getActiveContext();
		nodes.clear();
		
		for( Element e: ctx.getSelection() )
			addElement( e );
		
		model.reload( selection );
	}
	
	protected void addElement( Element e )
	{
		if( nodes.containsKey( e.getId() ) ) return;
		
		SelectionTreeNode node = new SelectionTreeNode( e, false );
		selection.add( node );
		nodes.put( e.getId(), node );
		
		Iterator<String> ite = e.getAttributeKeyIterator();
		if( ite != null )
		while( ite.hasNext() )
		{
			String key = ite.next();
			node.add( new SelectionTreeNode( 
					String.format( "%s = %s", key, e.getAttribute( key ) ), true ) );
		}
		
		model.reload( selection );
	}
	
	protected void removeElement( Element e )
	{
		if( ! nodes.containsKey( e.getId() ) ) return;
		
		selection.remove( nodes.get( e.getId() ) );
		nodes.remove( e.getId() );
		
		model.reload( selection );
	}
	
// SelectionListener implementation
	
	public void selectionAdd( SelectionEvent e )
	{
		if( e.getElement() != null )		
			addElement( e.getElement() );
	}
	
	public void selectionRemove( SelectionEvent e )
	{
		if( e.getElement() != null )		
			removeElement( e.getElement() );
	}
	
	public void selectionCleared( SelectionEvent e )
	{
		updateSelectionTree();
	}
	
// Stuff needed to work !
	
	class SelectionTreeModel extends DefaultTreeModel
	{
		public static final long serialVersionUID = 0x00A00D01L;
		
		public SelectionTreeModel( SelectionTreeNode root )
		{
			super( root );
		}
	}
	
	class SelectionTreeNode extends JTree.DynamicUtilTreeNode
	{
		public static final long serialVersionUID = 0x00A00E01L;
		
		@SuppressWarnings("unchecked")
		public SelectionTreeNode( Object val, boolean isLeaf )
		{
			super( val, isLeaf ? null : new Hashtable() );
		}
		
		public boolean isNode()
		{
			return this.userObject instanceof Node;
		}
		
		public String getId()
		{
			if( userObject instanceof Element )
			{
				return ((Element) userObject).getId();
			}
			
			return "";
		}
	}
	
	class SelectionTreeRenderer implements TreeCellRenderer
	{
		 public Component getTreeCellRendererComponent(
				 JTree tree, Object value, boolean selected, boolean expanded, 
				 boolean leaf, int row, boolean hasFocus ) 
		 {
			 if( leaf )
				 return new JLabel( 
						 value.toString(), 
						 WorkbenchUtils.getImageIcon( "key_16" ), 
						 SwingConstants.LEFT );
			 
			 if( value instanceof SelectionTreeNode )
			 {
				 SelectionTreeNode stn = (SelectionTreeNode) value;
				 if( stn.isNode() )
					 return new JLabel( stn.getId(), 
							 WorkbenchUtils.getImageIcon( "node_16" ), SwingConstants.LEFT );
				 else
					 return new JLabel( stn.getId(), 
							 WorkbenchUtils.getImageIcon( "edge_16" ), SwingConstants.LEFT );
			 }
			 return new JLabel( value.toString() );
		 }
	}
}
