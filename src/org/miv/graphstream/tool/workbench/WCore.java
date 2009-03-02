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

package org.miv.graphstream.tool.workbench;

import org.miv.graphstream.graph.Element;
import org.miv.graphstream.graph.Node;
import org.miv.graphstream.graph.Edge;
import org.miv.graphstream.tool.workbench.cli.CLI;
import org.miv.graphstream.tool.workbench.event.*;

import java.io.IOException;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Core of the workbench.
 * It contains all context and can create new one.
 * 
 * @author Guilhelm Savin
 *
 */
public class WCore
	implements ContextListener, SelectionListener
{
	public static final String DEFAULT_GRAPH_CLASS = "org.miv.graphstream.graph.implementations.DefaultGraph";
	public static final String DEFAULT_CONTEXT_CLASS = "org.miv.graphstream.tool.workbench.DefaultContext";
	
	private static final WCore core = new WCore();
	
	public static WCore getCore()
	{
		return core;
	}
	
	public static enum ActionMode
	{
		NONE,
		ADD_NODE,
		ADD_EDGE,
		DEL_NODE,
		DEL_EDGE,
		SELECT
	}
	
	private LinkedList<Context> 				ctxs;
	private LinkedList<String> 					ctxsid;
	private int 								activeCtx 				= -1;
	private String 								activeCtxId 			= null;
	private int 								createdCtx 				= 0;
	private LinkedList<ContextChangeListener> 	contextChangeListeners 	= new LinkedList<ContextChangeListener>();
	private LinkedList<ContextListener> 		contextListeners 		= new LinkedList<ContextListener>();
	private LinkedList<WorkbenchListener> 		workbenchListeners 		= new LinkedList<WorkbenchListener>();
	private LinkedList<SelectionListener> 		selectionListeners 		= new LinkedList<SelectionListener>();
	private CLI 								cli;
	private int 								terminalCloseAction 	= javax.swing.JFrame.EXIT_ON_CLOSE;
	private ActionMode 							actionMode;
	private WorkbenchEnvironment 				env;
	private WClipboard							clipboard 				= new WClipboard();
	private WHistory							history					= new WHistory();
	
	private WCore()
	{
		cli    = new CLI( this );
		ctxs   = new LinkedList<Context>();
		ctxsid = new LinkedList<String>();
		env    = new WorkbenchEnvironment();
		
		addContextChangeListener( cli );
	}
	
// Public Access
	
	public CLI getCLI()
	{
		return cli;
	}
	
	public WorkbenchEnvironment getEnvironment()
	{
		return env;
	}
	
	public Context getActiveContext()
	{
		return activeCtx < 0 ? null : ctxs.get(activeCtx);
	}
	
	public int getActiveContextNumber()
	{
		return activeCtx;
	}
	
	public WClipboard getClipboard()
	{
		return clipboard;
	}
	
	public void exit()
	{
		System.exit(0);
	}
	
	public Context createNewContext()
	{
		return createNewContext( DEFAULT_CONTEXT_CLASS, 
				String.format( "graph#%d", createdCtx++ ) );
	}
	
	public Context createNewContext( String clazz, String gid )
	{
		return createNewContext( clazz, gid, DEFAULT_GRAPH_CLASS );
	}
	
	public Context createNewContext( String clazz, String gid, String graphClass )
	{
		Context ctx = ContextFactory.createContext( clazz, gid, graphClass );
		registerContext(ctx);
		
		fireContextAdded( ctx );
		
		return ctx;
	}
	
	public Context createNewGraph()
	{
		return createNewGraph( String.format( "graph#%d", createdCtx++ ) );
	}
	
	public Context createNewGraph( String clazz, boolean isClass )
	{
		return createNewGraph( String.format( "graph#%d", createdCtx++ ), clazz );
	}
	
	public Context createNewGraph( String gid )
	{
		return createNewGraph( gid, DEFAULT_GRAPH_CLASS );
	}
	
	public Context createNewGraph( String gid, String clazz )
	{
		return createNewContext( DEFAULT_CONTEXT_CLASS, gid, clazz );
	}
	
	public void removeContext( Context ctx )
	{
		unregisterContext( ctx );
		fireContextRemoved( ctx );
		
		if( activeCtx >= ctxs.size() )
			activeCtx = ctxs.size() - 1;
		
		fireContextChanged();
	}
	
	public CLITerm openTerminal()
	{
		CLITerm term = new CLITerm( cli );
		term.setCloseAction( terminalCloseAction );
		term.display();
		
		return term;
	}
	
	public void setTerminalCloseAction( int i )
	{
		this.terminalCloseAction = i;
	}
	
	public int getTerminalCloseAction()
	{
		return this.terminalCloseAction;
	}
	
	public int getContextCount()
	{
		return ctxs.size();
	}
	
	public String getContextId( int i )
	{
		return i<0 ? null : i>=ctxs.size() ? null : ctxsid.get(i);
	}
	
	public Context getContext( int i )
	{
		return i<0 ? null : i>=ctxs.size() ? null : ctxs.get(i);
	}
	
	public Context getContext( String id )
	{
		return getContext( ctxsid.indexOf( id ) );
	}
	
	public void selectContext( String id )
	{
		int i = ctxsid.indexOf( id );
		
		if( i>=0 )
			selectContext(i);
	}
	
	public void selectContext( int i )
	{
		if( i<0 || i>=ctxs.size() )
			return;
		
		if( activeCtxId == null || ! activeCtxId.equals(ctxsid.get(i)) )
		{
			ctxs.get(activeCtx).removeContextListener(this);
			ctxs.get(activeCtx).getSelection().removeSelectionListener(this);
			activeCtx = i;
			activeCtxId = ctxsid.get(i);
			ctxs.get(activeCtx).addContextListener(this);
			ctxs.get(activeCtx).getSelection().addSelectionListener(this);
			fireContextChanged();
		}
	}
	
	public void showContext( Context ctx )
	{
		fireContextShow( ctx );
	}
	
	public boolean saveContext()
	{
		return saveContext( getActiveContext() );
	}
	
	public boolean saveContext( Context ctx )
	{
		if( ctx == null || ctx.getDefaultFile() == null ) return false;
		
		try
		{
			ctx.getGraph().write( ctx.getDefaultFile() );
		}
		catch( IOException e )
		{
			return false;
		}
		
		return true;
	}
	
	public void addNodeToSelection( String id )
	{
		if( activeCtx >= 0 )
		{
			Element e = getActiveContext().getGraph().getNode( id );
			if( e != null )
				addElementToSelection( e );
		}
	}
	
	public void addEdgeToSelection( String id )
	{
		if( activeCtx >= 0 )
		{
			Element e = getActiveContext().getGraph().getEdge( id );
			if( e != null )
				addElementToSelection( e );
		}
	}
	
	public void removeNodeFromSelection( String id )
	{
		if( activeCtx >= 0 )
		{
			Element e = getActiveContext().getGraph().getNode( id );
			if( e != null )
				removeElementFromSelection( e );
		}
	}
	
	public void removeEdgeFromSelection( String id )
	{
		if( activeCtx >= 0 )
		{
			Element e = getActiveContext().getGraph().getEdge( id );
			if( e != null )
				removeElementFromSelection( e );
		}
	}
	
	public void addElementToSelection( Element e )
	{
		getActiveContext().getSelection().select(e);
	}
	
	public void removeElementFromSelection( Element e )
	{
		getActiveContext().getSelection().unselect(e);
	}
	
	public void clearSelection()
	{
		getActiveContext().getSelection().unselect();
	}
	
	public void setActionMode( ActionMode am )
	{
		this.actionMode = am;
	}
	
	public ActionMode getActionMode()
	{
		return this.actionMode;
	}
	
// ContextListener operations

	/**
	 * Add a ContextListener to the active context.
	 * 
	 * @param cl the ContextListener
	 */
	public void addContextListener( ContextListener cl )
	{
		this.contextListeners.add(cl);
	}
	
	/**
	 * Remove a ContextListener from the active context.
	 * 
	 * @param cl the ContextListener
	 */
	public void removeContextListener( ContextListener cl )
	{
		this.contextListeners.remove(cl);
	}
	
	/**
	 * @see org.miv.graphstream.tool.workbench.event.ContextListener
	 */
	public void contextAutolayoutChanged( ContextEvent ce )
	{
		for( ContextListener cl : contextListeners )
			cl.contextAutolayoutChanged(ce);
	}
	
	/**
	 * @see org.miv.graphstream.tool.workbench.event.ContextListener
	 */
	public void contextElementOperation( ContextEvent ce, Element e, 
			ElementOperation op, Object data )
	{
		for( ContextListener cl : contextListeners )
			cl.contextElementOperation(ce,e,op,data);
	}
	
// ContextChangeListener operations
	
	protected void fireContextChanged()
	{
		ContextEvent cce = new ContextEvent( this, 
				activeCtx < ctxs.size() && activeCtx >= 0 ? ctxs.get( activeCtx ) : null );
		
		for( ContextChangeListener ccl : contextChangeListeners )
			ccl.contextChanged(cce);
		for( WorkbenchListener wl: workbenchListeners )
			wl.contextChanged( cce );
	}
	
	protected void fireContextAdded( Context ctx )
	{
		ContextEvent ce = new ContextEvent( this, ctx );
		
		for( WorkbenchListener wl: workbenchListeners )
			wl.contextAdded( ce );
	}
	
	protected void fireContextRemoved( Context ctx )
	{
		ContextEvent ce = new ContextEvent( this, ctx );
		
		for( WorkbenchListener wl: workbenchListeners )
			wl.contextRemoved( ce );
	}
	
// SelectionListener operations
	
	public void addSelectionListener( SelectionListener sl )
	{
		this.selectionListeners.addLast( sl );
	}
	
	public void removeSelectionListener( SelectionListener sl )
	{
		this.selectionListeners.remove( sl );
	}
	
	public void selectionAdd( SelectionEvent se )
	{
		for( SelectionListener sl: selectionListeners )
			sl.selectionAdd( se );
	}
	
	public void selectionRemove( SelectionEvent se )
	{
		for( SelectionListener sl: selectionListeners )
			sl.selectionRemove( se );
	}
	
	public void selectionCleared( SelectionEvent se )
	{
		for( SelectionListener sl: selectionListeners )
			sl.selectionCleared( se );
	}
	
	
// Clipboard operations
	
	public void selectionCopy()
	{
		clipboard.copy();
	}
	
	public void selectionCut()
	{
		clipboard.cut();
	}
	
	public void selectionPaste()
	{
		clipboard.paste();
	}
	
// Add/remove listeners

	public void addContextChangeListener( ContextChangeListener ccl )
	{
		this.contextChangeListeners.addLast( ccl );
	}
	
	public void removeContextChangeListener( ContextChangeListener ccl )
	{
		this.contextChangeListeners.remove( ccl );
	}
	
	public void addWorkbenchListener( WorkbenchListener wl )
	{
		this.workbenchListeners.addLast( wl );
	}
	
	public void removeWorkbenchListener( WorkbenchListener wl )
	{
		this.workbenchListeners.remove( wl );
	}
	
// Fired methods
	
	protected void fireContextShow( Context ctx )
	{
		ContextEvent ce = new ContextEvent( this, ctx );
		
		for( WorkbenchListener wl: workbenchListeners )
			wl.contextShow( ce );
	}
	
// Private Access
	
	private synchronized void registerContext( Context ctx )
	{
		if( ctxs.contains( ctx ) ) return;
		
		String id = ctx.getGraph().getId();
		
		ctx.addContextListener( this );
		ctxs.addLast( ctx );
		ctxsid.addLast( id );
		
		if( ctxs.size() == 1 )
		{
			activeCtx = 0;
			ctxs.get(activeCtx).addContextListener(this);
			ctxs.get(activeCtx).getSelection().addSelectionListener(this);
			fireContextChanged();
		}
	}
	
	private synchronized void unregisterContext( Context ctx )
	{
		if( ctx == null || ! ctxs.contains( ctx ) ) return;
		
		int id = ctxs.indexOf( ctx );
		ctxs.remove( id );
		ctxsid.remove( id );
		ctx.removeContextListener(this);
		ctx.getSelection().removeSelectionListener(this);
	}
}
