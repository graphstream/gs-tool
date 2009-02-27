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

package org.miv.graphstream.tool.workbench.gui;

import org.miv.graphstream.ui.layout.Layout;
import org.miv.graphstream.ui.layout.LayoutRunner;
import org.miv.graphstream.graph.Element;
import org.miv.graphstream.graph.Node;
import org.miv.graphstream.graph.Edge;
import org.miv.graphstream.tool.workbench.Context;
import org.miv.graphstream.tool.workbench.WorkbenchEnvironment;
import org.miv.graphstream.tool.workbench.WCore.ActionMode;
import org.miv.graphstream.tool.workbench.cli.CLI;
import org.miv.graphstream.tool.workbench.event.SelectionEvent;
import org.miv.graphstream.tool.workbench.event.WorkbenchListener;
import org.miv.graphstream.tool.workbench.event.ContextEvent;
import org.miv.graphstream.tool.workbench.event.ContextListener;
import org.miv.graphstream.ui.graphicGraph.GraphicNode;
import org.miv.graphstream.ui.swing.SwingGraphViewer;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class WDesktop implements WorkbenchListener, WindowFocusListener
{
	public static final long serialVersionUID = 0x00A00701L;
	
	public static final String styleSheet = 
		"url('org/miv/graphstream/tool/workbench/gui/workbenchgui.css')";
	
	protected Map<Context,ContextFrame> iframes = new HashMap<Context,ContextFrame>();
	protected CLI cli;
	
	public WDesktop( CLI cli )
	{
		this.cli = cli;
	}
	
	public void add( Context ctx )
	{
		if( iframes.containsKey( ctx ) ) return;
		iframes.put( ctx, new ContextFrame( ctx ) );
		//show( ctx );
	}
	
	public void remove( Context ctx )
	{
		if( iframes.containsKey( ctx ) ) hide( ctx );
		iframes.remove( ctx );
	}
	
	public void show( Context ctx )
	{
		if( ! iframes.containsKey( ctx ) ) add( ctx );
		iframes.get( ctx ).setVisible( true );
	}
	
	public void hide( Context ctx )
	{
		if( iframes.containsKey( ctx ) )
			iframes.get( ctx ).setVisible( false );
	}
	
	public void select( Context ctx )
	{
		Window w = iframes.get( ctx );
		
		if( ! w.hasFocus() )
		{
			w.toFront();
		}
	}
	
	protected WDesktop _this_()
	{
		return this;
	}
	
// WorkbenchListener implementation
	
	public void contextAdded( ContextEvent ce )
	{
		if( ce.getContext() != null )
		{
			add( ce.getContext() );
			show( ce.getContext() );
		}
	}
	
	public void contextRemoved( ContextEvent ce )
	{
		remove( ce.getContext() );
	}
	
	public void contextChanged( ContextEvent ce )
	{
		if( ce.getContext() != null )
		{
			show( ce.getContext() );
			select( ce.getContext() );
		}
	}
	
	public void contextShow( ContextEvent ce )
	{
		if( ce.getContext() != null )
			show( ce.getContext() );
	}
	
// WindowFocusListener implementation
	
	public void windowGainedFocus( WindowEvent e )
	{
		Window w = e.getWindow();
		
		if( w instanceof ContextFrame )
		{
			ContextFrame cf = (ContextFrame) w;
			cli.execute( String.format( "select graph \"%s\"", cf.ctx.getGraph().getId() ) );
		}
	}
	
	public void windowLostFocus( WindowEvent e)
	{
		
	}
	
// Stuff
	
	class ContextFrame extends JFrame 
		implements ContextListener, WindowListener, MouseListener, MouseWheelListener
	{
		public static final long serialVersionUID = 0x00A00801L;
		
		protected Context ctx;
		protected SwingGraphViewer viewer;
		protected String edgeSelection = null;
		protected EdgeSelectionHandler edgeSelectionHandler;
		protected int automaticNodeIdCount = 0;
		protected int automaticEdgeIdCount = 0;
		protected String lastNodeSelected = null;
		
		public ContextFrame( Context ctx )
		{
			super( ctx.getGraph().getId() );
			
			edgeSelectionHandler = new EdgeSelectionHandler();
			
			this.viewer = new SwingGraphViewer( ctx.getGraph(), ctx.getAutolayout(), true );
			this.ctx = ctx;
			
			Component c = this.viewer.getSwingComponent();
			add( c );
			c.addMouseListener( this );
			c.addMouseWheelListener( this );
			
			pack();
			
			addWindowFocusListener( _this_() );
			addWindowListener( this );
			
			setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
			
			this.ctx.addContextListener( this );
			
			ctx.getGraph().addAttribute( "ui.stylesheet", styleSheet );
		}
		
		protected void nodeClicked( String id )
		{
			Element e = ctx.getGraph().getNode( id );
			if( e == null ) return;
			
			if( e.hasAttribute( "ui.state" ) && 
					e.getAttribute( "ui.state" ).equals( "selected" ) )
			{
				e.removeAttribute( "ui.state" );
				ctx.removeElementFromSelection( e );
			}
			else
			{
				e.addAttribute( "ui.state", "selected" );
				ctx.addElementToSelection( e );
			}
		}
		
		protected void edgeSelectionMode( String id )
		{
			if( edgeSelectionHandler.getNodeId().equals( id ) )
			{
				Element e = edgeSelectionHandler.get();
				if( e != null )
				{
					if( e.hasAttribute( "ui.state" ) && 
							e.getAttribute( "ui.state" ).equals( "selected" ) )
					{
						e.removeAttribute( "ui.state" );
						ctx.removeElementFromSelection( e );
					}
					else
					{
						e.addAttribute( "ui.state", "selected" );
						ctx.addElementToSelection( e );
					}
				}
				edgeSelectionHandler.reset( null );
			}
			else
			{
				edgeSelectionHandler.reset( id );
			}
		}
		
		protected void actionSelection( MouseEvent e )
		{
			org.miv.graphstream.ui.swing.Context sctx = viewer.renderer.getContext();
			
			GraphicNode n = viewer.renderer.getGraph().findNode( 
					sctx.xPixelsToGu( e.getX() ),
					sctx.yPixelsToGu( e.getY() ) );
			
			if( n != null )
			{
				switch( e.getButton() )
				{
				case MouseEvent.BUTTON1: nodeClicked( n.getId() ); break;
				case MouseEvent.BUTTON2: edgeSelectionMode( n.getId() ); break;
				}
			}
		}
		
		protected void actionAddNode( MouseEvent e )
		{
			org.miv.graphstream.ui.swing.Context sctx = viewer.renderer.getContext();
			WorkbenchEnvironment env = cli.getCore().getEnvironment();
			
			String id = null;
			if( env.containsKey( WActions.OPT_ADD_NODE_ID ) )
				id = env.getString( WActions.OPT_ADD_NODE_ID );
			if( id == null ) id = "node#%n";
			id = WorkbenchUtils.getAutomaticNodeId( ctx, id );
			
			try
			{
				Node n = ctx.getGraph().addNode( id );
				n.setAttribute( "x", sctx.xPixelsToGu( e.getX() ) );
				n.setAttribute( "y", sctx.yPixelsToGu( e.getY() ) );
			}
			catch( Exception ex )
			{
				WorkbenchUtils.errorMessage( this, "cannot create node\n" + ex.getMessage() );
			}
		}
		
		protected void actionAddEdge( MouseEvent e )
		{
			org.miv.graphstream.ui.swing.Context sctx = viewer.renderer.getContext();
			WorkbenchEnvironment env = cli.getCore().getEnvironment();
			
			GraphicNode n = viewer.renderer.getGraph().findNode( 
					sctx.xPixelsToGu( e.getX() ),
					sctx.yPixelsToGu( e.getY() ) );
			
			if( n != null )
			{
				if( lastNodeSelected == null )
					lastNodeSelected = n.getId();
				else
				{
					String id = null;
					if( env.containsKey( WActions.OPT_ADD_EDGE_ID ) )
						id = env.getString( WActions.OPT_ADD_EDGE_ID );
					if( id == null ) id = "edge#%n";
					id = WorkbenchUtils.getAutomaticEdgeId( ctx, id );
					
					Boolean directed = env.getBoolean( WActions.OPT_ADD_EDGE_DIRECTED );
					if( directed == null ) directed = false;
					
					try
					{
						ctx.getGraph().addEdge( id, lastNodeSelected, n.getId(), directed );
						lastNodeSelected = n.getId();
					}
					catch( Exception ex )
					{
						WorkbenchUtils.errorMessage( this, 
								"cannot create edge\n" + ex.getMessage() );
					}
					
					Boolean cycle = env.getBoolean( WActions.OPT_ADD_EDGE_CYCLE );
					if( cycle != null && ! cycle )
						lastNodeSelected = null;
				}
			}
		}
		
		protected void actionDelNode( MouseEvent e )
		{
			org.miv.graphstream.ui.swing.Context sctx = viewer.renderer.getContext();
			
			GraphicNode n = viewer.renderer.getGraph().findNode( 
					sctx.xPixelsToGu( e.getX() ),
					sctx.yPixelsToGu( e.getY() ) );
			
			if( n != null )
			{
				ctx.getGraph().removeNode( n.getId() );
			}
		}
		
		protected void actionDelEdge( MouseEvent e )
		{
			org.miv.graphstream.ui.swing.Context sctx = viewer.renderer.getContext();
			
			GraphicNode n = viewer.renderer.getGraph().findNode( 
					sctx.xPixelsToGu( e.getX() ),
					sctx.yPixelsToGu( e.getY() ) );
			
			String id = n == null ? null : n.getId();
			
			if( edgeSelectionHandler.getNodeId().equals( id ) )
			{
				Edge elt = edgeSelectionHandler.get();
				if( elt != null )
				{
					ctx.getGraph().removeEdge( elt.getId() );
				}
				edgeSelectionHandler.reset( null );
			}
			else
			{
				edgeSelectionHandler.reset( id );
			}
		}
		
	// ContextListener implementation
		
		public void contextAutolayoutChanged( ContextEvent ce )
		{
			if( ce.getContext() != ctx ) return;
			
			if( ( viewer.getLayoutRemote() != null ) != ctx.getAutolayout() )
			{
				if( ! ctx.getAutolayout() )
					viewer.setLayout( (Layout) null );
				else
					viewer.setLayout( LayoutRunner.newDefaultLayout() );
			}
		}
		
		public void contextElementOperation( ContextEvent ce, Element e, 
				ElementOperation op, Object data )
		{
			
		}

	// SelectionListener implementation
		
		/**
		 * @see org.miv.graphstream.tool.workbench.event.SelectionListener
		 */
		public void selectionAdd( SelectionEvent e )
		{
			e.getElement().addAttribute( "ui.selected", "" );
		}
		/**
		 * @see org.miv.graphstream.tool.workbench.event.SelectionListener
		 */
		public void selectionRemove( SelectionEvent e )
		{
			e.getElement().removeAttribute( "ui.selected" );
		}
		/**
		 * @see org.miv.graphstream.tool.workbench.event.SelectionListener
		 */
		public void selectionCleared( SelectionEvent e )
		{
			
		}
		
	// WindowListener implementation
		
		public void windowClosing(WindowEvent e)
		{
			if( ctx.hasChanged() )
			{
				int r = JOptionPane.showConfirmDialog( 
						this, "Save changes ?", "Save",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE );
				
				switch( r )
				{
				case JOptionPane.YES_OPTION: 
				{
					if( ctx.getDefaultFile() == null )
						WorkbenchUtils.selectFile( this, ctx );
					
					if( ! cli.getCore().saveContext( ctx ) )
					{
						JOptionPane.showMessageDialog(
								this, "unable to save the graph", 
								"error", JOptionPane.ERROR_MESSAGE );
						return;
					}
					
					break;
				}
				case JOptionPane.CANCEL_OPTION: return;
				}
			}
			
			setVisible( false );
			cli.getCore().removeContext( ctx );
		}
		
		public void windowActivated(WindowEvent e)
		{}
		public void windowClosed(WindowEvent e)
		{}
		public void windowDeactivated(WindowEvent e)
		{}
		public void windowDeiconified(WindowEvent e)
		{}
		public void windowIconified(WindowEvent e)
		{}
		public void windowOpened(WindowEvent e) 
		{}
		
	// MouseWheelListener implementation
		
		public void mouseWheelMoved( MouseWheelEvent e )
		{
			if( e.getWheelRotation() >= 0 )
				edgeSelectionHandler.next();
			else
				edgeSelectionHandler.previous();
		}
		
	// MouseListener implementation
		
		public void mouseClicked(MouseEvent e)
		{
			if( cli.getCore().getActionMode() == ActionMode.SELECT )
				actionSelection( e );
			else if( cli.getCore().getActionMode() == ActionMode.ADD_NODE )
				actionAddNode( e );
			else if( cli.getCore().getActionMode() == ActionMode.ADD_EDGE )
				actionAddEdge( e );
			else if( cli.getCore().getActionMode() == ActionMode.DEL_NODE )
				actionDelNode( e );
			else if( cli.getCore().getActionMode() == ActionMode.DEL_EDGE )
				actionDelEdge( e );
		}
		
		public void mouseEntered(MouseEvent e)
		{
			
		}
		
		public void mouseExited(MouseEvent e)
		{
			
		}
		
		public void mousePressed(MouseEvent e)
		{
			
		}
		
		public void mouseReleased(MouseEvent e) 
		{
			
		}
		
	// More stuff !
		
		class EdgeSelectionHandler implements ListIterator<Edge>
		{
			protected Node nodeStart = null;
			protected int index = -1;
			protected LinkedList<Edge> edges = new LinkedList<Edge>();
			protected Edge lastSelected = null;
			protected Object lastClass = null;
			
			public EdgeSelectionHandler()
			{
			}
			
			public EdgeSelectionHandler( String startId )
			{
				reset( startId );
			}
			
			public void reset( String startId )
			{
				resetLastSelected();
				edges.clear();
				
				if( startId != null )
				{
					nodeStart = ctx.getGraph().getNode( startId );
					if( nodeStart == null ) return;
					Iterator<? extends Edge> ite = nodeStart.getEdgeIterator();
					if( ite == null ) return;
					
					while( ite.hasNext() ) 
						edges.add( ite.next() );
				}
				else
				{
					nodeStart = null;
					index = -1;
				}
			}
			
			public String getNodeId()
			{
				return nodeStart != null ? nodeStart.getId() : "";
			}
			
			protected void resetLastSelected()
			{
				if( lastSelected != null )
				{
					if( lastClass == null )
						lastSelected.removeAttribute( "ui.class" );
					else
						lastSelected.changeAttribute( "ui.class", lastClass );
				}
				lastSelected = null;
				lastClass = null;
			}
			
			protected void preSelectEdge()
			{
				resetLastSelected();
				
				if( index >= 0 && edges.size() > 0 )
				{
					lastSelected = edges.get( index );
					if( lastSelected.hasAttribute( "ui.class" ) )
						lastClass = lastSelected.getAttribute( "ui.class" );
					lastSelected.setAttribute( "ui.class", "preselect" );
				}
			}
			
			public boolean hasNext()
			{
				return edges.size() > 0;
			}
			
			public int nextIndex()
			{
				if( edges.size() == 0 )
					return 0;
				return ( index + 1 ) % edges.size();
			}
			
			public Edge next()
			{
				index = nextIndex();
				if( edges.size() > 0 )
				{
					preSelectEdge();
					return edges.get(index);
				}
				return null;
			}
			
			public boolean hasPrevious()
			{
				return edges.size() > 0;
			}
			
			public int previousIndex()
			{
				if( edges.size() == 0 )
					return 0;
				return ( index - 1 + edges.size() ) % edges.size();
			}
			
			public Edge previous()
			{
				index = previousIndex();
				if( edges.size() > 0 )
				{
					preSelectEdge();
					return edges.get(index);
				}
				return null;
			}
			
			public Edge get()
			{
				if( index >= 0 && edges.size() > 0 )
					return edges.get( index );
				return null;
			}
			
			public void add( Edge e )
			{}
			
			public void remove()
			{}
			
			public void set( Edge e )
			{}
		}
	}
}
