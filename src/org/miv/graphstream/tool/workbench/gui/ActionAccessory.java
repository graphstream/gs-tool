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

import org.miv.graphstream.tool.workbench.Context;
import org.miv.graphstream.tool.workbench.cli.CLI;
import org.miv.graphstream.tool.workbench.event.ContextEvent;
import org.miv.graphstream.tool.workbench.event.ContextChangeListener;

import java.awt.Color;
import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class ActionAccessory extends JPanel 
{
	public static final long serialVersionUID = 0x00A01401L;
	
	protected GridLayout layout;
	protected Map<String,Component> components;
	protected CLI cli;
	
	public ActionAccessory( CLI cli )
	{
		this( cli, "Accessory" );
	}
	
	public ActionAccessory( CLI cli, String title )
	{
		this.cli = cli;
		components = new HashMap<String,Component>();
		
		layout = new GridLayout();
		setLayout( layout );
		setBorder( javax.swing.BorderFactory.createTitledBorder(title) );
	}
	
	protected synchronized void registerComponent( String id, Component c, int elements )
	{
		components.put( id, c );
		layout.setRows( components.size() );
	}
	
	public void addOption( String id, Component c )
	{
		
	}
	
	public JCheckBox addBooleanOption( String title, String id, boolean defaultValue )
	{
		JCheckBox box = new JCheckBox( title, defaultValue );
		registerComponent( id, box, 1 );
		add( box );
		setPreferredSize( layout.preferredLayoutSize( this ) );
		revalidate();
		
		return box;
	}
	
	public JTextField addTextOption( String title, String id, String defaultValue )
	{
		JTextField jtf = new JTextField( defaultValue, 15 );
		JPanel tmp = new JPanel();
		tmp.setLayout( new BorderLayout() );
		tmp.add( new JLabel( title ), BorderLayout.WEST );
		tmp.add( jtf, BorderLayout.CENTER );
		registerComponent( id, jtf, 2 );
		add( tmp );
		setPreferredSize( layout.preferredLayoutSize( this ) );
		revalidate();
		
		return jtf;
	}
	
	public JSlider addIntegerOption( String title, String id, int min, int max, int def )
	{
		JSlider slider = new JSlider( min, max, def );
		JPanel tmp = new JPanel();
		tmp.setLayout( new BorderLayout() );
		tmp.add( new JLabel( title ), BorderLayout.WEST );
		tmp.add( slider, BorderLayout.CENTER );
		registerComponent( id, slider, 2 );
		add( tmp );
		setPreferredSize( layout.preferredLayoutSize( this ) );
		revalidate();
		
		return slider;
	}
	
	public JButton addButton( String title, String id )
	{
		JButton button = new JButton( title );
		registerComponent( id, button, 1 );
		add( button );
		setPreferredSize( layout.preferredLayoutSize( this ) );
		revalidate();
		
		return button;
	}
	
	static class AddNodeAccessory extends ActionAccessory
		implements DocumentListener, ContextChangeListener
	{
		public static final long serialVersionUID = 0x00A01501L;
		
		protected JTextField nodeIdFormat = new JTextField( 15 );
		
		public AddNodeAccessory( CLI cli )
		{
			super( cli, "Add node options" );
			
			nodeIdFormat = addTextOption( "id ", "id", "node#%n" );
			
			nodeIdFormat.getDocument().addDocumentListener( this );
			nodeIdFormat.setToolTipText( "specify nodes id.\nyou can use \"%n\" to insert" +
					" an automatic numbering in the id." );
			insertUpdate( null );
			
			cli.getCore().addContextChangeListener( this );
		}
		
		protected void checkId()
		{
			if( nodeIdFormat.getText().contains( "%n" ) )
				nodeIdFormat.setForeground( Color.BLACK );
			if( cli.getCore().getActiveContext() != null )
			{
				Context ctx = cli.getCore().getActiveContext(); 
				if( ctx.getGraph().getNode( nodeIdFormat.getText() ) != null )
					nodeIdFormat.setForeground( Color.RED );
				else
					nodeIdFormat.setForeground( Color.BLACK );
			}
			else
				nodeIdFormat.setForeground( Color.BLACK );
		}
		
	// DocumentListener implementation
			
		public void changedUpdate(DocumentEvent e)
		{}
			
		public void insertUpdate(DocumentEvent e)
		{
			cli.getCore().getEnvironment().put( 
					WActions.OPT_ADD_NODE_ID, nodeIdFormat.getText() );
			checkId();
		}
			
		public void removeUpdate(DocumentEvent e) 
		{
			cli.getCore().getEnvironment().put( 
					WActions.OPT_ADD_NODE_ID, nodeIdFormat.getText() );
			checkId();
		}
		
	// ContextChangeListener implementation
		
		public void contextChanged( ContextEvent e )
		{
			checkId();
		}
	}
	
	static class AddEdgeAccessory extends ActionAccessory
		implements ChangeListener, DocumentListener, ContextChangeListener
	{
		public static final long serialVersionUID = 0x00A01601L;
		
		protected JCheckBox cycle, directed;
		protected JTextField edgeIdFormat;
		
		public AddEdgeAccessory( CLI cli )
		{
			super( cli, "Add edge options" );
			
			cycle = addBooleanOption( "Cycle ?", "cycle", false );
			directed = addBooleanOption( "Directed ?", "directed", false );
			edgeIdFormat = addTextOption( "id ", "id", "edge#%n" );
			
			cycle.addChangeListener( this );
			directed.addChangeListener( this );
			edgeIdFormat.getDocument().addDocumentListener( this );
			insertUpdate( null );
			stateChanged( null );
			
			cli.getCore().addContextChangeListener( this );
		}
		
		protected void checkId()
		{
			if( edgeIdFormat.getText().contains( "%n" ) )
				edgeIdFormat.setForeground( Color.BLACK );
			if( cli.getCore().getActiveContext() != null )
			{
				Context ctx = cli.getCore().getActiveContext(); 
				if( ctx.getGraph().getEdge( edgeIdFormat.getText() ) != null )
					edgeIdFormat.setForeground( Color.RED );
				else
					edgeIdFormat.setForeground( Color.BLACK );
			}
			else
				edgeIdFormat.setForeground( Color.BLACK );
		}
		
	// ChangeListener implementation
		
		public void stateChanged( ChangeEvent e )
		{
			cli.getCore().getEnvironment().put( 
					WActions.OPT_ADD_EDGE_CYCLE, cycle.isSelected() );
			cli.getCore().getEnvironment().put( 
					WActions.OPT_ADD_EDGE_DIRECTED, directed.isSelected() );
		}
		
	// DocumentListener implementation
		
		public void changedUpdate(DocumentEvent e)
		{}
		
		public void insertUpdate(DocumentEvent e)
		{
			cli.getCore().getEnvironment().put( 
					WActions.OPT_ADD_EDGE_ID, edgeIdFormat.getText() );
			checkId();
		}
		
		public void removeUpdate(DocumentEvent e) 
		{
			cli.getCore().getEnvironment().put( 
					WActions.OPT_ADD_EDGE_ID, edgeIdFormat.getText() );
			checkId();
		}
		
	// ContextChangeListener implementation
			
		public void contextChanged( ContextEvent e )
		{
			checkId();
		}
	}
	
	static class SelectAccessory extends ActionAccessory
		implements ActionListener
	{
		public static final long serialVersionUID = 0x00A01801L;
		
		protected JButton pattern;
		
		public SelectAccessory( CLI cli )
		{
			super( cli, "Selection options" );
			
			pattern = addButton( "select pattern", "pattern" );
			pattern.addActionListener( this );
		}
		
		public void actionPerformed( ActionEvent e )
		{
			if( e != null && e.getSource() == pattern )
				WorkbenchUtils.selectPattern( this, cli );
		}
	}
}
