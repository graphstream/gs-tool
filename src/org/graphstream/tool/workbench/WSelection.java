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

import java.util.HashSet;
import java.lang.Iterable;
import java.util.Iterator;
import java.util.LinkedList;

import org.graphstream.graph.Element;
import org.graphstream.tool.workbench.event.SelectionEvent;
import org.graphstream.tool.workbench.event.SelectionListener;
import org.graphstream.tool.workbench.event.NotificationListener.Notification;

public class WSelection
	implements Iterable<Element>
{
	HashSet<Element> 				selection;
	LinkedList<SelectionListener> 	listeners;
	Context							ctx;
	
	public WSelection( Context ctx )
	{
		selection = new HashSet<Element>();
		listeners = new LinkedList<SelectionListener>();
	}
	
	public void addSelectionListener( SelectionListener cl )
	{
		listeners.add(cl);
	}
	
	public void removeSelectionListener( SelectionListener cl )
	{
		listeners.remove(cl);
	}

	public void select()
	{
		selectNodes();
		selectEdges();
	}
	
	public void selectNodes()
	{
		if( ctx != null )
			select( ctx.getGraph().getNodeIterator() );
	}
	
	public void selectEdges()
	{
		if( ctx != null )
			select(ctx.getGraph().getEdgeIterator() );
	}
	
	public void select( Iterator<? extends Element> elements )
	{
		select( elements, ".*" );
	}
	
	public void select( Iterator<? extends Element> elements, String pattern )
	{
		while( elements.hasNext() )
		{
			Element e = elements.next();
			
			if( e.getId().matches(pattern) )
				select(e);
		}
	}
	
	public void select( Element e )
	{
		selection.add(e);
		selectDecoration(e);
		fireSelectionAdd(e);
	}
	
	public void unselect( Element e )
	{
		selection.remove(e);
		unselectDecoration(e);
		fireSelectionRemove(e);
	}
	
	public void unselect()
	{
		selection.clear();
		fireSelectionCleared();
	}
	
	public void unselectNodes()
	{
		if( ctx != null )
			unselect( ctx.getGraph().getNodeIterator() );
	}
	
	public void unselectEdges()
	{
		if( ctx != null )
			unselect( ctx.getGraph().getEdgeIterator() );
	}
	
	public void unselect( Iterator<? extends Element> elements )
	{
		unselect( elements, ".*" );
	}
	
	public void unselect( Iterator<? extends Element> elements, String pattern )
	{
		while( elements.hasNext() )
		{
			Element e = elements.next();
			
			if( e.getId().matches(pattern) )
				unselect(e);
		}
	}
	
	public void selectDecoration( Element e )
	{
		e.addAttribute( "ui.state", "selected" );
	}
	
	public void unselectDecoration( Element e )
	{
		e.removeAttribute( "ui.state" );
	}
	
	protected void fireSelectionAdd( Element e )
	{
		SelectionEvent se = new SelectionEvent( this, ctx,
				SelectionEvent.Type.ADD, e );
		
		for( int i = 0; i < listeners.size(); i++ )
			listeners.get(i).selectionAdd(se);
		
		WNotificationServer.dispatch( Notification.selectionAdd );
	}
	
	protected void fireSelectionRemove( Element e )
	{
		SelectionEvent se = new SelectionEvent( this, ctx,
				SelectionEvent.Type.REMOVE, e );
		
		for( int i = 0; i < listeners.size(); i++ )
			listeners.get(i).selectionRemove(se);
		
		if( selection.size() > 0 )
			WNotificationServer.dispatch( Notification.selectionRemove );
		else
			WNotificationServer.dispatch( Notification.selectionEmpty );
	}
	
	protected void fireSelectionCleared()
	{
		SelectionEvent se = new SelectionEvent( this, ctx,
				SelectionEvent.Type.CLEAR );
		
		for( int i = 0; i < listeners.size(); i++ )
			listeners.get(i).selectionCleared(se);
		
		WNotificationServer.dispatch( Notification.selectionEmpty );
	}
	
	public boolean contains( Element e )
	{
		return selection.contains(e);
	}
	
	public Iterator<Element> iterator()
	{
		return selection.iterator();
	}
}
