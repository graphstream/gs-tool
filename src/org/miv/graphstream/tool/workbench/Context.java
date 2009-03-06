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
package org.miv.graphstream.tool.workbench;

import javax.swing.DefaultBoundedRangeModel;

import org.miv.graphstream.tool.workbench.event.ContextListener;
import org.miv.graphstream.tool.workbench.event.SelectionListener;

import org.miv.graphstream.graph.Graph;

/**
 * Describes objects able to be a context.
 * Implementation is free to provide a larger choice
 * of context like remote contexts for example.
 * 
 * A context is defined by a Graph. It allows to have a
 * selection list of Element objects. So this list can
 * contains Node or Edge.
 * 
 * @author Guilhelm Savin
 *
 */
public interface Context
{
	/**
	 * Add a ContextListener to this context.
	 * 
	 * @param ce the listener
	 * @see org.miv.graphstream.tool.workbench.event.ContextListener
	 */
	void addContextListener( ContextListener ce );
	/**
	 * Remove a ContextListener.
	 * 
	 * @param ce the listener
	 * @see org.miv.graphstream.tool.workbench.event.ContextListener
	 */
	void removeContextListener( ContextListener ce );
	/**
	 * Add a SelectionListener listening to the selection list of
	 * this context.
	 * 
	 * @param sl the listener
	 * @see org.miv.graphstream.tool.workbench.event.SelectionListener
	 */
	void addSelectionListener( SelectionListener sl );
	/**
	 * Remove a SelectionListener.
	 * 
	 * @param sl the listener
	 * @see org.miv.graphstream.tool.workbench.event.SelectionListener
	 */
	void removeSelectionListener( SelectionListener sl );
	/**
	 * Get the graph which defines this context.
	 * 
	 * @return a graph
	 */
	Graph getGraph();
	/**
	 * Set the graph which defines this context.
	 * 
	 * @param graph new graph
	 */
	void setGraph( Graph graph );
	
	void readGraph( String path, String reader );
	
	boolean isReading();
	
	DefaultBoundedRangeModel getReaderProgressionModel();
	/**
	 * Get the Selection object of this content.
	 * @return the selection list
	 */
	WSelection getSelection();
	/**
	 * Get the History object of this content.
	 * @return the history
	 */
	WHistory getHistory();
	/**
	 * Enable or disable autolayout.
	 * 
	 * @param b
	 */
	void setAutolayout( boolean b );
	/**
	 * Is autolayout enabled ?
	 * 
	 * @return true is autolayout on this context
	 */
	boolean isAutolayout();
	/**
	 * Set the default file for reading or writing this context.
	 * 
	 * @param path
	 */
	void setDefaultFile( String path );
	/**
	 * Return the default pathname of the file in which the graph
	 * of this context will be stored.
	 * 
	 * @return pathname of the graph-file
	 */
	String getDefaultFile();
	/**
	 * Used to know if there are modifications on the graph.
	 * 
	 * @return true if graph has changed
	 */
	boolean hasChanged();
	/**
	 * Reset graph modification flag.
	 */
	void resetChanged();
}
