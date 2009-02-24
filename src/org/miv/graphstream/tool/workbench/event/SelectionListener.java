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
