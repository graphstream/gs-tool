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
package org.graphstream.tool.workbench.event;

import org.graphstream.tool.workbench.Context;

import java.util.EventObject;

/**
 * Defines a context change event.
 * This event contains the new context and the object
 * which has fired this event.
 * 
 * @see org.graphstream.tool.workbench.Context
 * @see java.util.EventObject
 * 
 * @author Guilhelm Savin
 *
 */
public class ContextEvent extends EventObject
{
	/**
	 * Defines serial version uid.
	 */
	public static final long serialVersionUID = 0x030030000001L;
	
	/**
	 * New context.
	 */
	protected Context ctx;
	
	/**
	 * Build a new ContextChangeEvent.
	 * 
	 * @param source object which fired this event
	 * @param ctx new context
	 */
	public ContextEvent( Object source, Context ctx )
	{
		super( source );
		
		this.ctx = ctx;
	}
	
	/**
	 * Get the new context.
	 * 
	 * @return new context
	 */
	public Context getContext()
	{
		return this.ctx;
	}
}
