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

import org.miv.graphstream.graph.Edge;
import org.miv.graphstream.graph.Element;
import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.GraphListener;
import org.miv.graphstream.graph.Node;
import org.miv.graphstream.tool.workbench.WorkbenchCore;
import org.miv.graphstream.tool.workbench.cli.CLI;
import org.miv.graphstream.tool.workbench.event.*;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.DefaultComboBoxModel;

/**
 * Panel which will be used to display graph informations.
 * 
 * @author Guilhelm Savin
 *
 */
public class GraphInfo extends JPanel 
	implements ContextChangeListener, GraphListener, ItemListener
{
	public static final long serialVersionUID = 0x00A00A01L;
	
	protected WorkbenchCore core;
	protected CLI			cli;
	protected JLabel		graphId;
	protected JLabel		nodesCount;
	protected JLabel		edgesCount;
	protected Graph			listeningGraph;
	protected GraphsModel	graphsModel;
	
	@SuppressWarnings("unused")
	private GraphInfo()
	{
		throw new Error( "bad way" );
	}
	
	public GraphInfo( CLI cli )
	{
		this.cli = cli;
		this.core = cli.getCore();
		this.listeningGraph = null;
		
		JPanel tmp = new JPanel();
		tmp.setLayout( new GridLayout( 4, 1 ) );
		
		this.graphId = new JLabel();
		this.nodesCount = new JLabel();
		this.edgesCount = new JLabel();
		this.graphsModel = new GraphsModel();
		
		JComboBox comboBox = new JComboBox( graphsModel );
		comboBox.addItemListener( this );
		
		tmp.add( comboBox );
		tmp.add( graphId );
		tmp.add( nodesCount );
		tmp.add( edgesCount );
		
		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab( "Nodes", new JPanel() );
		tabs.addTab( "Edges", new JPanel() );
		
		setLayout( new BorderLayout() );
		
		add( tmp, BorderLayout.NORTH );
		add( tabs, BorderLayout.CENTER );
		
		updateGraphInformation();
		
		core.addContextChangeListener( this );
		core.addWorkbenchListener( graphsModel );
		
		setPreferredSize( new java.awt.Dimension( 150, 300 ) );
	}
	
	protected void updateGraphInformation()
	{
		if( core.getActiveContext() != null && core.getActiveContext().getGraph() != null )
		{
			Graph g = core.getActiveContext().getGraph();
			
			setGraphId( g.getId() );
			setNodesCount( g.getNodeCount() );
			setEdgesCount( g.getEdgeCount() );
		}
		else
		{
			resetInformations();
		}
	}
	
	protected void resetInformations()
	{
		graphId.setText( "" );
		nodesCount.setText( "" );
		edgesCount.setText( "" );
	}
	
	protected void setGraphId( String id )
	{
		graphId.setText( "id: " + id );
	}
	
	protected void setNodesCount( int n )
	{
		nodesCount.setText( String.format( "nodes: %d", n ) );
	}
	
	protected void setEdgesCount( int n )
	{
		edgesCount.setText( String.format( "edges: %d", n ) );
	}
	
// ContextChangeListener implementation
	
	public void contextChanged( ContextEvent e )
	{
		Graph g = e.getContext() == null ? null : e.getContext().getGraph();
		
		if( g != listeningGraph )
		{
			if( listeningGraph != null ) 
				listeningGraph.removeGraphListener( this );
			
			if( g != null )
				g.addGraphListener( this );
			
			listeningGraph = g;
		}
		
		updateGraphInformation();
	}
	
// GraphListener implementation
	
	public void afterNodeAdd( Graph graph, Node node )
	{
		updateGraphInformation();
	}

	public void afterEdgeAdd( Graph graph, Edge edge )
	{
		updateGraphInformation();
	}

	public void beforeNodeRemove( Graph graph, Node node )
	{
		Graph g = core.getActiveContext().getGraph();
		setNodesCount( g.getNodeCount() - 1 );
	}
	
	public void beforeEdgeRemove( Graph graph, Edge edge )
	{
		Graph g = core.getActiveContext().getGraph();
		setEdgesCount( g.getEdgeCount() - 1 );
	}
	
	public void beforeGraphClear( Graph graph ) {}
	public void attributeChanged( Element element, String attribute,
			Object oldValue, Object newValue ) {}
	
// ItemListener implementation
	
	public void itemStateChanged( ItemEvent e )
	{
		if( graphsModel.getSelectedItem() == null ) return;
		cli.execute( String.format( "select graph \"%s\"", graphsModel.getSelectedItem() ) );
	}
	
// Model used in ComboBox
	
	/**
	 * Model used to managed graph.
	 * 
	 * @author Guilhelm Savin
	 * 
	 */
	class GraphsModel extends DefaultComboBoxModel
		implements WorkbenchListener
	{
		public static final long serialVersionUID = 0x00A00B01L;
		
		public void contextAdded( ContextEvent ce )
		{
			if( ce.getContext() != null )
				addElement( ce.getContext().getGraph().getId() );
		}
		
		public void contextRemoved( ContextEvent ce )
		{
			if( ce.getContext() != null )
				removeElement( ce.getContext().getGraph().getId() );
		}
		
		public void contextChanged( ContextEvent ce )
		{
			if( ce.getContext() != null )
				setSelectedItem( ce.getContext().getGraph().getId() );
			else
				setSelectedItem( null );
		}
		
		public void contextShow( ContextEvent ce )
		{
			
		}
	}

	public void stepBegins(Graph graph, double time)
	{
	}
}
