/*
 * Copyright 2006 - 2011 
 *     Julien Baudry	<julien.baudry@graphstream-project.org>
 *     Antoine Dutot	<antoine.dutot@graphstream-project.org>
 *     Yoann Pigné		<yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin	<guilhelm.savin@graphstream-project.org>
 * 
 * This file is part of GraphStream <http://graphstream-project.org>.
 * 
 * GraphStream is a library whose purpose is to handle static or dynamic
 * graph, create them from scratch, file or any source and display them.
 * 
 * This program is free software distributed under the terms of two licenses, the
 * CeCILL-C license that fits European law, and the GNU Lesser General Public
 * License. You can  use, modify and/ or redistribute the software under the terms
 * of the CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
 * URL <http://www.cecill.info> or under the terms of the GNU LGPL as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C and LGPL licenses and that you accept their terms.
 */
package org.graphstream.tool.workbench.gui;

import org.graphstream.graph.Element;
import org.graphstream.tool.workbench.Context;
import org.graphstream.tool.workbench.WCore;
import org.graphstream.tool.workbench.event.ContextChangeListener;
import org.graphstream.tool.workbench.event.ContextEvent;
import org.graphstream.tool.workbench.event.ContextListener;

import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public class WElementList
	extends JList
	implements ContextChangeListener, ContextListener
{
	private static final long serialVersionUID = 0x0400D0000001L;
	
	private static ElementsModel NODE_MODEL = null;
	private static ElementsModel EDGE_MODEL = null;
	
	public static ElementsModel getNodeModel()
	{
		return NODE_MODEL;
	}
	
	public static ElementsModel getEdgeModel()
	{
		return EDGE_MODEL;
	}
	
	class ElementsModel
		implements ListModel
	{
		LinkedList<ListDataListener> listeners;
		
		public ElementsModel()
		{
			listeners = new LinkedList<ListDataListener>();
		}
		
		public Object getElementAt(int index)
		{
			return WElementList.this.elements.get(index);
		}
		
		public int getSize()
		{
			return WElementList.this.elements.size();
		}
		
		public void addListDataListener(ListDataListener l)
		{
			listeners.add(l);
		}
		
		public void removeListDataListener(ListDataListener l)
		{
			listeners.remove(l);
		}
		
		protected void fireContentsChanged( int from, int to )
		{
			ListDataEvent lde = new ListDataEvent( WElementList.this, 
					ListDataEvent.CONTENTS_CHANGED, from, to );
			
			for( ListDataListener ldl : listeners )
				ldl.contentsChanged(lde);
		}
		
		protected void fireIntervalAdded( int from, int to )
		{
			ListDataEvent lde = new ListDataEvent( WElementList.this, 
					ListDataEvent.INTERVAL_ADDED, from, to );
			
			for( ListDataListener ldl : listeners )
				ldl.intervalAdded(lde);
		}
		
		protected void fireIntervalRemoved( int from, int to )
		{
			ListDataEvent lde = new ListDataEvent( WElementList.this, 
					ListDataEvent.INTERVAL_REMOVED, from, to );
			
			for( ListDataListener ldl : listeners )
				ldl.intervalRemoved(lde);
		}
	}
	
	public static WElementList createNodeList( WCore core )
	{
		WElementList wel = new WElementList( core, Type.NodeList, NODE_MODEL );
		
		if( NODE_MODEL == null )
			NODE_MODEL = wel.nmodel;
		
		return wel;
	}
	
	public static WElementList createEdgeList( WCore core )
	{
		WElementList wel = new WElementList( core, Type.EdgeList, EDGE_MODEL );
		
		if( EDGE_MODEL == null )
			EDGE_MODEL = wel.nmodel;
		
		return wel;
	}
	
	public static enum Type
	{
		NodeList,
		EdgeList
	}
	
	WCore core;
	LinkedList<String> elements;
	ElementsModel nmodel;
	GraphOperation add;
	GraphOperation del;
	Type type;
	
	protected WElementList( WCore core, Type type, ElementsModel nmodel )
	{
		
		this.core = core;
		this.elements = new LinkedList<String>();
		
		if( nmodel == null )
		{
			this.nmodel = new ElementsModel();
			core.addContextListener(this);
			core.addContextChangeListener(this);
		}
		else
		{
			this.nmodel = nmodel;
		}
		
		this.type = type;
		
		if( type == Type.NodeList )
		{
			this.add = GraphOperation.NodeAdded;
			this.del = GraphOperation.NodeRemoved;
		}
		else if( type == Type.EdgeList )
		{
			this.add = GraphOperation.EdgeAdded;
			this.del = GraphOperation.EdgeRemoved;
		}
		
		setModel(this.nmodel);
		
		rebuild();
	}
	
	public void contextChanged( ContextEvent ce )
	{
		rebuild();
	}
	
	protected void rebuild()
	{
		Context ctx = core.getActiveContext();
		
		elements.clear();
		
		if( ctx != null )
		{
			Iterator<? extends Element> ite = null;
			
			if( type == Type.NodeList )
				ite = ctx.getGraph().getNodeIterator();
			else if( type == Type.EdgeList )
				ite = ctx.getGraph().getEdgeIterator();
			
			while( ite.hasNext() )
				elements.add( ite.next().getId() );
		
			nmodel.fireContentsChanged( 0, elements.size() );
		}
	}
	
	public void contextAutolayoutChanged( ContextEvent ce )
	{
	}
	
	public void contextGraphOperation( ContextEvent ce, GraphOperation op, Object data )
	{
		if( op == add || op == del )
		{
			String id = (String) data;//((Element)data).getId();

			if( id == null )
				return;

			if( op == add )
			{
				if( ! elements.contains(id) ) elements.add(id);
				nmodel.fireIntervalAdded(elements.size()-1,elements.size());
			}
			else if( op == del )
			{
				int index = elements.indexOf(id);
				if( index != -1 )
				{
					elements.remove(index);
					nmodel.fireIntervalRemoved(index,index+1);
				}
			}
		}
	}
}
