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

package org.miv.graphstream.tool.workbench;

import org.miv.graphstream.tool.workbench.event.ContextListener;
import org.miv.graphstream.tool.workbench.event.ContextListener.ElementOperation;
import org.miv.graphstream.tool.workbench.event.ContextEvent;
import org.miv.graphstream.tool.workbench.event.SelectionListener;
import org.miv.graphstream.tool.workbench.event.SelectionEvent;

import org.miv.graphstream.graph.Edge;
import org.miv.graphstream.graph.Element;
import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.GraphListener;
import org.miv.graphstream.graph.Node;

import java.util.Collections;
import java.util.List;
import java.util.LinkedList;

/**
 * Default implementation of a Context.
 * 
 * @author Guilhelm Savin
 *
 */
public class DefaultContext implements Context, GraphListener
{
	/**
	 * Graph defining this context.
	 */
	protected Graph graph;
	/**
	 * Selection list.
	 */
	protected LinkedList<Element> selection;
	
	protected boolean autolayout;
	
	protected LinkedList<ContextListener> contextListeners = new LinkedList<ContextListener>();
	
	protected LinkedList<SelectionListener> selectionListeners = new LinkedList<SelectionListener>();
	
	protected String path;
	
	protected boolean changed;
	
	public DefaultContext()
	{
		this( null );
	}
	
	public DefaultContext( Graph graph )
	{
		this.graph = graph;
		this.selection = new LinkedList<Element>();
		this.autolayout = false;
		this.path = null;
		this.changed = false;
		
		if( graph != null )
			this.graph.addGraphListener( this );
	}
	
	public void addElementToSelection(Element e)
	{
		if( selection.contains( e ) ) return;
		
		selection.addLast( e );
		fireSelectionAdded( e );
	}

	public void removeElementFromSelection(Element e)
	{
		selection.remove( e );
		fireSelectionRemoved( e );
	}

	public void clearSelection()
	{
		selection.clear();
		fireSelectionCleared();
	}

	public Graph getGraph()
	{
		return graph;
	}

	public List<Element> getSelection()
	{
		return Collections.unmodifiableList( selection );
	}

	public void setGraph(Graph graph)
	{
		if( graph == null ) return;
		
		if( this.graph != null )
			this.graph.removeGraphListener( this );
		
		this.graph = graph;
		this.graph.addGraphListener( this );
		
		changed = false;
		
		clearSelection();
	}

	public void setAutolayout( boolean b )
	{
		this.autolayout = b;
		fireAutolayoutChanged();
	}
	
	public boolean getAutolayout()
	{
		return this.autolayout;
	}
	
	public void setDefaultFile( String path )
	{
		this.path = path;
	}
	
	public String getDefaultFile()
	{
		return path;
	}
	
	public void addContextListener( ContextListener cl )
	{
		contextListeners.add( cl );
	}
	
	public void removeContextListener( ContextListener cl )
	{
		contextListeners.remove( cl );
	}
	
	public void addSelectionListener( SelectionListener sl )
	{
		selectionListeners.add(sl);
	}
	
	public void removeSelectionListener( SelectionListener sl )
	{
		selectionListeners.remove(sl);
	}
	
	public boolean hasChanged()
	{
		return changed;
	}
	
	public void resetChanged()
	{
		changed = false;
	}
	
// Fire methods for ContextListener
	
	protected void fireAutolayoutChanged()
	{
		ContextEvent ce = new ContextEvent( this, this );
		
		for( ContextListener cl: contextListeners )
			cl.contextAutolayoutChanged( ce );
	}
	
	protected void fireElementOperation( Element e, ElementOperation op, Object data )
	{
		ContextEvent ce = new ContextEvent( this, this );
		
		for( ContextListener cl: contextListeners )
			cl.contextElementOperation(ce,e,op,data);
	}
	
	protected void fireSelectionAdded( Element elt )
	{
		SelectionEvent e = new SelectionEvent( this, this, SelectionEvent.Type.ADD, elt );
		
		for( SelectionListener sl: selectionListeners )
			sl.selectionAdd(e);
	}
	
	protected void fireSelectionRemoved( Element elt )
	{
		SelectionEvent e = new SelectionEvent( this, this, SelectionEvent.Type.REMOVE, elt );
		
		for( SelectionListener sl: selectionListeners )
			sl.selectionRemove(e);
	}
	
	protected void fireSelectionCleared()
	{
		SelectionEvent e = new SelectionEvent( this, this, SelectionEvent.Type.CLEAR );
		
		for( SelectionListener sl: selectionListeners )
			sl.selectionCleared(e);
	}
			
	
// GraphListener implementation

	public void afterNodeAdd( Graph graph, Node node )
	{
		changed = true;
		fireElementOperation(node,ElementOperation.NodeAdded,null);
	}

	public void afterEdgeAdd( Graph graph, Edge edge )
	{
		changed = true;
		fireElementOperation(edge,ElementOperation.EdgeAdded,null);
	}

	public void attributeChanged( Element element, String attribute,
			Object oldValue, Object newValue )
	{
		changed = true;
	}

	public void beforeEdgeRemove( Graph graph, Edge edge )
	{
		if( selection.contains( edge ) )
		{
			removeElementFromSelection( edge );
		}
		fireElementOperation(edge,ElementOperation.EdgeRemoved,null);
	}

	public void beforeNodeRemove( Graph graph, Node node )
	{
		if( selection.contains( node ) )
		{
			removeElementFromSelection( node );
		}
		fireElementOperation(node,ElementOperation.NodeRemoved,null);
	}
	
	public void beforeGraphClear( Graph graph )
	{}

	public void stepBegins(Graph graph, double time)
	{
	}
}
