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

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Node;
import org.graphstream.tool.workbench.Context;
import org.graphstream.tool.workbench.event.NotificationListener.Notification;

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
	
	public void registerDelNodeAction( Element node, LinkedList<Edge> edges )
	{
		register( new DelNodeHistoryAction(ctx,node,edges) );
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
	
	protected static void copyAttributes( HashMap<String,Object> map, Element trg )
	{
		Iterator<String> ite = map.keySet().iterator();
		
		while( ite != null && ite.hasNext() )
		{
			String key = ite.next();
			trg.addAttribute(key,map.get(key));
		}
	}
	
	public static abstract class AbstractAddElementHistoryAction
		implements HistoryAction
	{
		Context ctx;
		String id;
		HashMap<String,Object> attributes;
		
		public AbstractAddElementHistoryAction( Context ctx, Element elt )
		{
			this.ctx = ctx;
			this.id = elt.getId();
			this.attributes = new HashMap<String,Object>();
		}
		
		public void redoAction()
		{
			if( ! graphContains() )
			{
				Element 			tmp = addElement();
				copyAttributes(attributes,tmp);
			}
		}
		
		public void undoAction()
		{
			delElement();
		}
		
		public String getId()
		{
			return id;
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
			return ctx.getGraph().getNode(id) != null;
		}
		
		protected void delElement()
		{
			ctx.getGraph().removeNode(id);
		}
		
		protected Element addElement()
		{
			return ctx.getGraph().addNode(id);
		}
	}
	
	public static class DelNodeHistoryAction
		extends AddNodeHistoryAction
	{
		LinkedList<DelEdgeHistoryAction> edges;
		
		public DelNodeHistoryAction( Context ctx, Element node, LinkedList<Edge> edges )
		{
			super(ctx,node);
			
			this.edges = new LinkedList<DelEdgeHistoryAction>();
			
			for( Edge e : edges )
				this.edges.add( new DelEdgeHistoryAction(ctx,e) );
		}
		
		public void undoAction()
		{
			super.redoAction();
			
			for( DelEdgeHistoryAction deha : edges )
				deha.undoAction();
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
			return ctx.getGraph().getEdge(id) != null;
		}
		
		protected void delElement()
		{
			ctx.getGraph().removeEdge(id);
		}
		
		protected Element addElement()
		{
			return ctx.getGraph().addEdge(id,src,trg,directed);
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
