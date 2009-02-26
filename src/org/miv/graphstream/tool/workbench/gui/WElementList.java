package org.miv.graphstream.tool.workbench.gui;

import org.miv.graphstream.graph.Element;
import org.miv.graphstream.graph.Node;
import org.miv.graphstream.tool.workbench.Context;
import org.miv.graphstream.tool.workbench.WCore;
import org.miv.graphstream.tool.workbench.event.ContextEvent;
import org.miv.graphstream.tool.workbench.event.ContextListener;
import org.miv.graphstream.tool.workbench.event.ContextChangeListener;
import org.miv.graphstream.tool.workbench.event.ContextListener.ElementOperation;

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
	private static final long serialVersionUID = 0x0001L;
	
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
		return new WElementList( core, ElementOperation.NodeAdded, 
				ElementOperation.NodeRemoved );
	}
	
	public static WElementList createEdgeList( WCore core )
	{
		return new WElementList( core, ElementOperation.EdgeAdded, 
				ElementOperation.EdgeRemoved );
	}
	
	WCore core;
	LinkedList<String> elements;
	ElementsModel nmodel;
	ElementOperation add;
	ElementOperation del;
	
	protected WElementList( WCore core, ElementOperation add, ElementOperation del )
	{
		this.core = core;
		this.elements = new LinkedList<String>();
		this.nmodel = new ElementsModel();
		this.add = add;
		this.del = del;
		
		setModel(nmodel);
		core.addContextListener(this);
		
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
			Iterator<? extends Node> ite = ctx.getGraph().getNodeIterator();
			
			while( ite.hasNext() )
				elements.add( ite.next().getId() );
		
			nmodel.fireContentsChanged( 0, elements.size() );
		}
	}
	
	public void contextAutolayoutChanged( ContextEvent ce )
	{
	}
	
	public void contextElementOperation( ContextEvent ce, Element e, 
			ElementOperation op, Object data )
	{
		if( op == add )
		{
			if( ! elements.contains(e.getId()) ) elements.add(e.getId());
			nmodel.fireIntervalAdded(elements.size()-1,elements.size());
		}
		else if( op == del )
		{
			int index = elements.indexOf(e.getId());
			if( index != -1 )
			{
				elements.remove(index);
				nmodel.fireIntervalRemoved(index,index+1);
			}
		}
		
	}
}
