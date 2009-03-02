package org.miv.graphstream.tool.workbench;

import org.miv.graphstream.graph.Element;
import org.miv.graphstream.graph.Node;
import org.miv.graphstream.graph.Edge;
import org.miv.graphstream.tool.workbench.event.NotificationListener.Notification;

import java.util.Iterator;
import java.util.LinkedList;

public class WClipboard
{
	LinkedList<Element> clipboard;
	
	public WClipboard()
	{
		clipboard = new LinkedList<Element>();
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
		
		WNotificationServer.dispatch( Notification.clipboardPaste );
	}
}
