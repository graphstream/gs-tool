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

import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Node;
import org.graphstream.tool.workbench.event.NotificationListener.Notification;

import java.util.Iterator;
import java.util.LinkedList;

public class WClipboard
{
	public static class ClipboardContent
		extends LinkedList<Element>
	{
		private static final long serialVersionUID = 0x010060010001L;
		
		public ClipboardContent()
		{
			
		}
		
		public ClipboardContent( ClipboardContent content )
		{
			super(content);
		}
		
		public Object clone()
		{
			return new ClipboardContent(this);
		}
	}
	
	ClipboardContent clipboard;
	
	public WClipboard()
	{
		clipboard = new ClipboardContent();
	}
	
	protected void internalCopy()
	{
		clipboard.clear();
		
		for( Element e : WCore.getCore().getActiveContext().getSelection() )
			clipboard.add(e);
	}
	
	public void copy()
	{
		internalCopy();
		
		WNotificationServer.dispatch( Notification.clipboardCopy );
	}
	
	public void cut()
	{
		internalCopy();
		
		Context ctx = WCore.getCore().getActiveContext();
		
		for( Element e : clipboard )
		{
			if( e instanceof Node )
			{
				ctx.getGraph().removeNode(e.getId());
			}
			else if( e instanceof Edge )
			{
				ctx.getGraph().removeEdge(e.getId());
			}
		}
		
		WNotificationServer.dispatch( Notification.clipboardCut );
	}
	
	public void paste()
	{
		Context ctx = WCore.getCore().getActiveContext();
		
		for( Element e : clipboard )
		{
			Element copied = null;
			
			if( e instanceof Node )
			{
				if( ctx.getGraph().getNode(e.getId()) == null )
				{
					copied = ctx.getGraph().addNode(e.getId());

					Iterator<String> ite = e.getAttributeKeyIterator();

					while( ite.hasNext() )
					{
						String key = ite.next();
						copied.addAttribute(key,e.getAttribute(key));
					}
				}
			}
		}
		
		for( Element e : clipboard )
		{
			Element copied = null;
			
			 if( e instanceof Edge )
			{
				if( ctx.getGraph().getEdge(e.getId()) == null )
				{
					copied = ctx.getGraph().addEdge(e.getId(),
						((Edge) e).getSourceNode().getId(),
						((Edge) e).getTargetNode().getId());
					
					Iterator<String> ite = e.getAttributeKeyIterator();
					
					while( ite.hasNext() )
					{
						String key = ite.next();
						copied.addAttribute(key,e.getAttribute(key));
					}
				}
			}
		}
		
		ctx.getHistory().registerPasteAction(getContentCopy());
		WNotificationServer.dispatch( Notification.clipboardPaste );
	}
	
	public ClipboardContent getContentCopy()
	{
		return (ClipboardContent) clipboard.clone();
	}
}
