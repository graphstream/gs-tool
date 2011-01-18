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
package org.graphstream.tool.workbench;

import javax.swing.DefaultBoundedRangeModel;

import org.graphstream.graph.Graph;
import org.graphstream.tool.workbench.event.ContextListener;
import org.graphstream.tool.workbench.event.SelectionListener;


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
	 * @see org.graphstream.tool.workbench.event.ContextListener
	 */
	void addContextListener( ContextListener ce );
	/**
	 * Remove a ContextListener.
	 * 
	 * @param ce the listener
	 * @see org.graphstream.tool.workbench.event.ContextListener
	 */
	void removeContextListener( ContextListener ce );
	/**
	 * Add a SelectionListener listening to the selection list of
	 * this context.
	 * 
	 * @param sl the listener
	 * @see org.graphstream.tool.workbench.event.SelectionListener
	 */
	void addSelectionListener( SelectionListener sl );
	/**
	 * Remove a SelectionListener.
	 * 
	 * @param sl the listener
	 * @see org.graphstream.tool.workbench.event.SelectionListener
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
