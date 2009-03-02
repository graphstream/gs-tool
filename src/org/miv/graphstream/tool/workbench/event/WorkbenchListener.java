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
package org.miv.graphstream.tool.workbench.event;

/**
 * Defines the workbench listener.
 * 
 * @author Guilhelm Savin
 *
 */
public interface WorkbenchListener extends ContextChangeListener
{
	/**
	 * This is called when a context is added to the core.
	 * 
	 * @param e object which defines this event
	 */
	public void contextAdded( ContextEvent e );
	/**
	 * This is called when a context is removed from the core.
	 * 
	 * @param e object which defines this event
	 */
	public void contextRemoved( ContextEvent e );
	
	public void contextShow( ContextEvent e );
}
