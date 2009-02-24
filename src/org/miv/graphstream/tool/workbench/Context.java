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

import org.miv.graphstream.graph.Element;
import org.miv.graphstream.graph.Graph;

import java.util.List;

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
	public void addContextListener( ContextListener ce );
	
	public void removeContextListener( ContextListener ce );
	/**
	 * Get the graph which defines this context.
	 * 
	 * @return a graph
	 */
	public Graph getGraph();
	/**
	 * Set the graph which defines this context.
	 * 
	 * @param graph new graph
	 */
	void setGraph( Graph graph );
	/**
	 * This will add an element to the selection list.
	 * 
	 * @param e element to add
	 */
	public void addElementToSelection( Element e );
	/**
	 * This will remove an element from the selection list.
	 * 
	 * @param e element to remove
	 */
	public void removeElementFromSelection( Element e );
	/**
	 * Get the selection list.
	 * 
	 * @return the selection list
	 */
	public List<Element> getSelection();
	/**
	 * Clear the selection.
	 */
	public void clearSelection();
	/**
	 * Enable or disable autolayout.
	 * 
	 * @param b
	 */
	public void setAutolayout( boolean b );
	/**
	 * Is autolayout enabled ?
	 * 
	 * @return true is autolayout on this context
	 */
	public boolean getAutolayout();
	/**
	 * Set the default file for reading or writing this context.
	 * 
	 * @param path
	 */
	public void setDefaultFile( String path );
	
	public String getDefaultFile();
	
	public boolean hasChanged();
	
	public void resetChanged();
}
