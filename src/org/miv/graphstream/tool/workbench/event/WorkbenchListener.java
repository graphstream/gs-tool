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
