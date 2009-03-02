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
 * Defines objects which are listening to selection event.
 * Events can be :
 * <ul>
 * <li>add an element to the selection</li>
 * <li>remove an element from the selection</li>
 * <li>clear the selection</li>
 * </ul>
 * 
 * @see org.miv.graphstream.tool.workbench.SelectionEvent
 * 
 * @author Guilhelm Savin
 *
 */
public interface SelectionListener
{
	/**
	 * Called when an element is added to the selection list.
	 * 
	 * @param e object describing the event
	 */
	public void selectionAdd( SelectionEvent e );
	/**
	 * Called when an element is removed from the selection list.
	 * 
	 * @param e object describing the event
	 */
	public void selectionRemove( SelectionEvent e );
	/**
	 * Called when the selection is cleared.
	 * 
	 * @param e object describing the event
	 */
	public void selectionCleared( SelectionEvent e );
}
