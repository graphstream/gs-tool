/*
 * This file is part of GraphStream.
 * 
 * GraphStream is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * GraphStream is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with GraphStream.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright 2006 - 2009
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */
package org.graphstream.tool.workbench;

import org.graphstream.graph.Element;
import org.graphstream.graph.Graph;
import org.graphstream.graph.GraphListener;
import org.graphstream.io.GraphReader;
import org.graphstream.tool.workbench.event.ContextEvent;
import org.graphstream.tool.workbench.event.ContextListener;
import org.graphstream.tool.workbench.event.SelectionEvent;
import org.graphstream.tool.workbench.event.SelectionListener;
import org.graphstream.tool.workbench.event.ContextListener.GraphOperation;


import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.DefaultBoundedRangeModel;

/**
 * Default implementation of a Context.
 * 
 * @author Guilhelm Savin
 *
 */
public class DefaultContext
	implements Context, GraphListener
{
	/**
	 * Graph defining this context.
	 */
	protected Graph graph;
	/**
	 * Selection list.
	 */
	protected WSelection selection;
	/**
	 * History of this context.
	 */
	protected WHistory history;
	/**
	 * Autolayout flag.
	 */
	protected boolean autolayout;
	/**
	 * The ContextListener listenning to this context.
	 */
	protected ConcurrentLinkedQueue<ContextListener> contextListeners = new ConcurrentLinkedQueue<ContextListener>();
	/**
	 * The SelectionListener listenning to the selection list of this
	 * context.
	 */
	protected ConcurrentLinkedQueue<SelectionListener> selectionListeners = new ConcurrentLinkedQueue<SelectionListener>();
	/**
	 * The path of the file storing the graph.
	 */
	protected String path;
	/**
	 * The change flag.
	 */
	protected boolean changed;
	
	protected final WGraphReader reader = new WGraphReader();
	
	/**
	 * Default constructor.
	 * Should not be used.
	 */
	public DefaultContext()
	{
		this( null );
	}
	/**
	 * Build a context from a given graph.
	 * 
	 * @param graph the graph wrapped by this context.
	 */
	public DefaultContext( Graph graph )
	{
		this.graph = graph;
		this.selection = new WSelection(this);
		this.history = new WHistory(this);
		this.autolayout = false;
		this.path = null;
		this.changed = false;
		
		if( graph != null )
			this.graph.addGraphListener( this );
	}
	/**
	 * @see org.graphstream.tool.workbench.Context#getGraph()
	 */
	public Graph getGraph()
	{
		return graph;
	}
	/**
	 * @see org.graphstream.tool.workbench.Context#getSelection()
	 */
	public WSelection getSelection()
	{
		return selection;
	}
	/**
	 * @see org.graphstream.tool.workbench.Context#getHistory()
	 */
	public WHistory getHistory()
	{
		return history;
	}
	/**
	 * @see org.graphstream.tool.workbench.Context#setGraph(Graph)
	 */
	public void setGraph(Graph graph)
	{
		if( graph == null ) return;
		
		if( this.graph != null )
			this.graph.removeGraphListener( this );
		
		this.graph = graph;
		this.graph.addGraphListener( this );
		
		changed = false;
		
		selection.unselect();//clearSelection();
	}
	/**
	 * @see org.graphstream.tool.workbench.Context#setAutolayout(boolean)
	 */
	public void setAutolayout( boolean b )
	{
		this.autolayout = b;
		fireAutolayoutChanged();
	}
	/**
	 * @see org.graphstream.tool.workbench.Context#isAutolayout()
	 */
	public boolean isAutolayout()
	{
		return this.autolayout;
	}
	/**
	 * @see org.graphstream.tool.workbench.Context#setDefaultFile(String)
	 */
	public void setDefaultFile( String path )
	{
		this.path = path;
	}
	/**
	 * @see org.graphstream.tool.workbench.Context#getDefaultFile()
	 */
	public String getDefaultFile()
	{
		return path;
	}
	
	public void readGraph( String path, String readerClass )
	{
		if( readerClass == null )
		{
			reader.read(path,this);
			fireGraphOperation(GraphOperation.ReadBegin,path);
		}
		else
		{
			try
			{
				reader.read((GraphReader) Class.forName(readerClass).newInstance(),path,this);
				fireGraphOperation(GraphOperation.ReadBegin,path);
			}
			catch( Exception e )
			{
				e.printStackTrace();
				reader.read(path,this);
				fireGraphOperation(GraphOperation.ReadBegin,path);
			}
		}
	}
	
	public boolean isReading()
	{
		return reader.isReading();
	}
	
	public DefaultBoundedRangeModel getReaderProgressionModel()
	{
		return reader.progressionModel;
	}
	
	/**
	 * @see org.graphstream.tool.workbench.Context#addContextListener(ContextListener)
	 */
	public void addContextListener( ContextListener cl )
	{
		contextListeners.add( cl );
	}
	/**
	 * @see org.graphstream.tool.workbench.Context#removeContextListener(ContextListener)
	 */
	public void removeContextListener( ContextListener cl )
	{
		contextListeners.remove( cl );
	}
	/**
	 * @see org.graphstream.tool.workbench.Context#addSelectionListener(SelectionListener)
	 */
	public void addSelectionListener( SelectionListener sl )
	{
		selectionListeners.add(sl);
	}
	/**
	 * @see org.graphstream.tool.workbench.Context#removeSelectionListener(SelectionListener)
	 */
	public void removeSelectionListener( SelectionListener sl )
	{
		selectionListeners.remove(sl);
	}
	/**
	 * @see org.graphstream.tool.workbench.Context#hasChanged()
	 */
	public boolean hasChanged()
	{
		return changed;
	}
	/**
	 * @see org.graphstream.tool.workbench.Context#resetChanged()
	 */
	public void resetChanged()
	{
		changed = false;
	}
	
// Fire methods for ContextListener
	
	/**
	 * Fire listeners that autolayout has changed.
	 */
	protected void fireAutolayoutChanged()
	{
		ContextEvent ce = new ContextEvent( this, this );
		
		for( ContextListener cl: contextListeners )
			cl.contextAutolayoutChanged( ce );
	}
	/**
	 * Fire listeners that an operation has be done on the graph.
	 * 
	 * @param e source element
	 * @param op operation type
	 * @param data data relative to the operation
	 */
	protected void fireGraphOperation( GraphOperation op, Object data )
	{
		ContextEvent ce = new ContextEvent( this, this );
		
		for( ContextListener cl: contextListeners )
			cl.contextGraphOperation(ce,op,data);
	}
	/**
	 * Fire listeners that an element has been added to the selection list.
	 * 
	 * @param elt element added
	 */
	protected void fireSelectionAdded( Element elt )
	{
		SelectionEvent e = new SelectionEvent( this, this, SelectionEvent.Type.ADD, elt );
		
		for( SelectionListener sl: selectionListeners )
			sl.selectionAdd(e);
	}
	/**
	 * Fire listeners that an element has been removed to the selection list.
	 * 
	 * @param elt element removed
	 */
	protected void fireSelectionRemoved( Element elt )
	{
		SelectionEvent e = new SelectionEvent( this, this, SelectionEvent.Type.REMOVE, elt );
		
		for( SelectionListener sl: selectionListeners )
			sl.selectionRemove(e);
	}
	/**
	 * Fire listeners that selection list has been cleared.
	 */
	protected void fireSelectionCleared()
	{
		SelectionEvent e = new SelectionEvent( this, this, SelectionEvent.Type.CLEAR );
		
		for( SelectionListener sl: selectionListeners )
			sl.selectionCleared(e);
	}
			
	
// GraphListener implementation

	/**
	 * @see org.graphstream.graph.GraphListener
	 */
	public void nodeAdded( String graphId, String nodeId )
	{
		changed = true;
		fireGraphOperation(GraphOperation.NodeAdded,nodeId);
	}
	/**
	 * @see org.graphstream.graph.GraphListener
	 */
	public void edgeAdded( String graphId, String edgeId, String src, 
			String trg, boolean directed )
	{
		changed = true;
		fireGraphOperation(GraphOperation.EdgeAdded,edgeId);
	}
	/**
	 * @see org.graphstream.graph.GraphListener
	 */
	public void edgeRemoved( String graphId, String edgeId )
	{
		if( selection.contains( graph.getEdge(edgeId)) )
			selection.unselect( graph.getEdge(edgeId));
		
		fireGraphOperation(GraphOperation.EdgeRemoved,edgeId);
	}
	/**
	 * @see org.graphstream.graph.GraphListener
	 */
	public void nodeRemoved( String graphId, String nodeId )
	{
		if( selection.contains( graph.getNode(nodeId)) )
			selection.unselect( graph.getNode(nodeId));
		
		fireGraphOperation(GraphOperation.NodeRemoved,nodeId);
	}
	/**
	 * @see org.graphstream.graph.GraphListener
	 */
	public void graphCleared( String graphId )
	{
		selection.unselect();
		fireGraphOperation(GraphOperation.GraphClear,null);
	}
	/**
	 * @see org.graphstream.graph.GraphListener
	 */
	public void stepBegins(String graphId, double time)
	{
	}
	
	public void edgeAttributeAdded(String graphId, String edgeId,
			String attribute, Object value) {
		changed = true;
	}
	public void edgeAttributeChanged(String graphId, String edgeId,
			String attribute, Object oldValue, Object newValue) {
		changed = true;
	}
	public void edgeAttributeRemoved(String graphId, String edgeId,
			String attribute) {
		changed = true;
	}
	public void graphAttributeAdded(String graphId, String attribute,
			Object value) {
		changed = true;
	}
	public void graphAttributeChanged(String graphId, String attribute,
			Object oldValue, Object newValue) {
		changed = true;
	}
	public void graphAttributeRemoved(String graphId, String attribute) {
		changed = true;
	}
	public void nodeAttributeAdded(String graphId, String nodeId,
			String attribute, Object value) {
		changed = true;
	}
	public void nodeAttributeChanged(String graphId, String nodeId,
			String attribute, Object oldValue, Object newValue) {
		changed = true;
	}
	public void nodeAttributeRemoved(String graphId, String nodeId,
			String attribute) {
		changed = true;
	}
}
