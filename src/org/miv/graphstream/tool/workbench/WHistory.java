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
package org.miv.graphstream.tool.workbench;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.miv.graphstream.graph.Edge;
import org.miv.graphstream.graph.Element;
import org.miv.graphstream.graph.Node;
import org.miv.graphstream.tool.workbench.Context;
import org.miv.graphstream.tool.workbench.event.NotificationListener.Notification;

public class WHistory
{
	public static interface HistoryAction
	{
		void redoAction();
		void undoAction();
	}
	
	public static int HISTORY_MAX_SIZE = 10;
	
	Context ctx;
	LinkedList<HistoryAction> history;
	int index;
	
	public WHistory( Context ctx )
	{
		history 	= new LinkedList<HistoryAction>();
		index   	= 0;
		this.ctx 	= ctx;
	}
	
	public void undo()
	{
		if( index < history.size() )
		{
			history.get(index).undoAction();
			index++;
			
			WNotificationServer.dispatch(Notification.historyUndo);
			
			if( index == 1 )
				WNotificationServer.dispatch(Notification.historyRedoEnable);
			if( index >= history.size() )
				WNotificationServer.dispatch(Notification.historyUndoDisable);
		}
	}
	
	public void redo()
	{
		if( index > 0 && index <= history.size() )
		{
			index--;
			history.get(index).redoAction();
			
			WNotificationServer.dispatch(Notification.historyRedo);
			
			if( index == history.size() - 1 )
				WNotificationServer.dispatch(Notification.historyUndoEnable);
			if( index == 0 )
				WNotificationServer.dispatch(Notification.historyRedoDisable);
		}
	}
	
	public void register( HistoryAction action )
	{
		while( index > 0 && history.size() > 0 )
		{
			history.poll();
			index--;
		}
		
		while( history.size() > HISTORY_MAX_SIZE )
			history.removeLast();
		
		index = 0;
		history.addFirst(action);
		
		WNotificationServer.dispatch(Notification.historyNew);
		
		if( history.size() == 1 )
			WNotificationServer.dispatch(Notification.historyUndoEnable);
	}
	
	public void registerAddNodeAction( Element node )
	{
		register( new AddNodeHistoryAction(ctx,node) );
	}
	
	public void registerDelNodeAction( Element node )
	{
		register( new DelNodeHistoryAction(ctx,node) );
	}
	
	public void registerAddEdgeAction( Element edge )
	{
		register( new AddEdgeHistoryAction(ctx,edge) );
	}
	
	public void registerDelEdgeAction( Element edge )
	{
		register( new DelEdgeHistoryAction(ctx,edge) );
	}
	
	public void registerPasteAction( WClipboard.ClipboardContent snapshot )
	{
		register( new PasteHistoryAction(ctx,snapshot) );
	}
	
	public HistoryAction getLast()
	{
		if( index < history.size() )
			return history.get(index);
		else
			return null;
	}
	
	public List<HistoryAction> getHistory()
	{
		return Collections.unmodifiableList(history);
	}
	
	public int getIndex()
	{
		return index;
	}
	
	public static abstract class AbstractAddElementHistoryAction
		implements HistoryAction
	{
		Context ctx;
		Element elt;
		
		public AbstractAddElementHistoryAction( Context ctx, Element elt )
		{
			this.ctx = ctx;
			this.elt = elt;
		}
		
		public void redoAction()
		{
			if( ! graphContains() )
			{
				Element 			tmp = addElement();
				Iterator<String> 	ite = elt.getAttributeKeyIterator();
				
				while( ite != null && ite.hasNext() )
				{
					String key = ite.next();
					tmp.addAttribute(key,elt.getAttribute(key));
				}
			}
		}
		
		public void undoAction()
		{
			delElement();
		}
		
		public String getId()
		{
			return elt.getId();
		}
		
		protected abstract boolean graphContains();
		protected abstract void delElement();
		protected abstract Element addElement();
	}
	
	public static class AddNodeHistoryAction
		extends AbstractAddElementHistoryAction
	{
		LinkedList<Edge> toRestore;
		
		public AddNodeHistoryAction( Context ctx, Element elt )
		{
			super(ctx,elt);
		}
		
		protected boolean graphContains()
		{
			return ctx.getGraph().getNode(elt.getId()) != null;
		}
		
		protected void delElement()
		{
			ctx.getGraph().removeNode(elt.getId());
		}
		
		protected Element addElement()
		{

			
			toRestore = new LinkedList<Edge>();
			
			Iterator<? extends Edge> ite = ((Node)elt).getEdgeIterator();
			
			while( ite != null && ite.hasNext() )
				toRestore.add(ite.next());
			
			System.err.printf( "%d edges to restore\n", toRestore.size() );

			Element e = ctx.getGraph().addNode(elt.getId());
			
			return e;
		}
	}
	
	public static class DelNodeHistoryAction
		extends AddNodeHistoryAction
	{
		public DelNodeHistoryAction( Context ctx, Element node )
		{
			super(ctx,node);
		}
		
		public void undoAction()
		{
			super.redoAction();
		}
		
		public void redoAction()
		{
			super.undoAction();
		}
	}
	
	public static class AddEdgeHistoryAction
		extends AbstractAddElementHistoryAction
	{
		String src;
		String trg;
		boolean directed;
		
		public AddEdgeHistoryAction( Context ctx, Element elt )
		{
			super(ctx,elt);
			
			src = ((Edge)elt).getSourceNode().getId();
			trg = ((Edge)elt).getTargetNode().getId();
			directed = ((Edge)elt).isDirected();
		}
		
		protected boolean graphContains()
		{
			return ctx.getGraph().getEdge(elt.getId()) != null;
		}
		
		protected void delElement()
		{
			ctx.getGraph().removeEdge(elt.getId());
		}
		
		protected Element addElement()
		{
			return ctx.getGraph().addEdge(elt.getId(),src,trg,directed);
		}
	}
	
	public static class DelEdgeHistoryAction
		extends AddEdgeHistoryAction
	{
		public DelEdgeHistoryAction(Context ctx,Element edge)
		{
			super(ctx,edge);
		}
		
		public void redoAction()
		{
			super.undoAction();
		}
		
		public void undoAction()
		{
			super.redoAction();
		}
	}
	
	public static class PasteHistoryAction
		implements HistoryAction
	{
		WClipboard.ClipboardContent snapshot;
		Context ctx;
		
		public PasteHistoryAction( Context ctx, WClipboard.ClipboardContent snapshot )
		{
			this.snapshot = snapshot;
			this.ctx = ctx;
		}
		
		public void undoAction()
		{
			for( Element e : snapshot )
			{
				if( e instanceof Node )
					ctx.getGraph().removeNode(e.getId());
				else
					ctx.getGraph().removeEdge(e.getId());
			}
		}
		
		public void redoAction()
		{
			for( Element e : snapshot )
			{
				if( e instanceof Node )
					ctx.getGraph().addNode(e.getId());
			}
			

			for( Element e : snapshot )
			{
				if( e instanceof Edge )
					ctx.getGraph().addEdge(e.getId(),
							((Edge) e).getSourceNode().getId(),
							((Edge) e).getTargetNode().getId(),
							((Edge) e).isDirected());
			}
		}
		
		public int size()
		{
			return snapshot.size();
		}
	}
}
