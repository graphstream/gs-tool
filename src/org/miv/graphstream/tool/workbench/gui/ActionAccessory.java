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
package org.miv.graphstream.tool.workbench.gui;

import org.miv.graphstream.tool.workbench.Context;
import org.miv.graphstream.tool.workbench.WCore;
import org.miv.graphstream.tool.workbench.WCore.ActionMode;
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
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class ActionAccessory
	extends JDialog 
{
	public static final long serialVersionUID = 0x00A01401L;
	
	private static final HashMap<ActionMode,ActionAccessory> accessories = new HashMap<ActionMode,ActionAccessory>();
	
	static void load()
	{
		accessories.put( ActionMode.ADD_NODE, 	new AddNodeAccessory() );
		accessories.put( ActionMode.ADD_EDGE, 	new AddEdgeAccessory() );
		accessories.put( ActionMode.SELECT, 	new SelectAccessory() );
	}
	
	public static void showAccessory()
	{
		ActionMode current = WCore.getCore().getActionMode();
		
		if( accessories.containsKey(current) )
			accessories.get(current).setVisible(true);
	}
	
	protected GridLayout layout;
	protected Map<String,Component> components;
	
	public ActionAccessory()
	{
		this( "Accessory" );
	}
	
	public ActionAccessory( String title )
	{
		setTitle(title);
		
		components = new HashMap<String,Component>();
		
		layout = new GridLayout();
		setLayout( layout );
		//setBorder( javax.swing.BorderFactory.createTitledBorder(title) );
		pack();
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
		//revalidate();
		pack();
		
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
		//revalidate();
		pack();
		
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
		//revalidate();
		pack();
		
		return slider;
	}
	
	public JButton addButton( String title, String id )
	{
		JButton button = new JButton( title );
		registerComponent( id, button, 1 );
		add( button );
		setPreferredSize( layout.preferredLayoutSize( this ) );
		//revalidate();
		pack();
		
		return button;
	}
	
	static class AddNodeAccessory extends ActionAccessory
		implements DocumentListener, ContextChangeListener
	{
		public static final long serialVersionUID = 0x00A01501L;
		
		protected JTextField nodeIdFormat = new JTextField( 15 );
		
		public AddNodeAccessory()
		{
			super( "Add node options" );
			
			nodeIdFormat = addTextOption( "id ", "id", "node#%n" );
			
			nodeIdFormat.getDocument().addDocumentListener( this );
			nodeIdFormat.setToolTipText( "specify nodes id.\nyou can use \"%n\" to insert" +
					" an automatic numbering in the id." );
			insertUpdate( null );
			
			WCore.getCore().addContextChangeListener( this );
		}
		
		protected void checkId()
		{
			if( nodeIdFormat.getText().contains( "%n" ) )
				nodeIdFormat.setForeground( Color.BLACK );
			if( WCore.getCore().getActiveContext() != null )
			{
				Context ctx = WCore.getCore().getActiveContext(); 
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
			WCore.getCore().getEnvironment().put( 
					WToolBar.OPT_ADD_NODE_ID, nodeIdFormat.getText() );
			checkId();
		}
			
		public void removeUpdate(DocumentEvent e) 
		{
			WCore.getCore().getEnvironment().put( 
					WToolBar.OPT_ADD_NODE_ID, nodeIdFormat.getText() );
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
		
		public AddEdgeAccessory()
		{
			super( "Add edge options" );
			
			cycle = addBooleanOption( "Cycle ?", "cycle", false );
			directed = addBooleanOption( "Directed ?", "directed", false );
			edgeIdFormat = addTextOption( "id ", "id", "edge#%n" );
			
			cycle.addChangeListener( this );
			directed.addChangeListener( this );
			edgeIdFormat.getDocument().addDocumentListener( this );
			insertUpdate( null );
			stateChanged( null );
			
			WCore.getCore().addContextChangeListener( this );
		}
		
		protected void checkId()
		{
			if( edgeIdFormat.getText().contains( "%n" ) )
				edgeIdFormat.setForeground( Color.BLACK );
			if( WCore.getCore().getActiveContext() != null )
			{
				Context ctx = WCore.getCore().getActiveContext(); 
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
			WCore.getCore().getEnvironment().put( 
					WToolBar.OPT_ADD_EDGE_CYCLE, cycle.isSelected() );
			WCore.getCore().getEnvironment().put( 
					WToolBar.OPT_ADD_EDGE_DIRECTED, directed.isSelected() );
		}
		
	// DocumentListener implementation
		
		public void changedUpdate(DocumentEvent e)
		{}
		
		public void insertUpdate(DocumentEvent e)
		{
			WCore.getCore().getEnvironment().put( 
					WToolBar.OPT_ADD_EDGE_ID, edgeIdFormat.getText() );
			checkId();
		}
		
		public void removeUpdate(DocumentEvent e) 
		{
			WCore.getCore().getEnvironment().put( 
					WToolBar.OPT_ADD_EDGE_ID, edgeIdFormat.getText() );
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
		
		public SelectAccessory()
		{
			super( "Selection options" );
			
			pattern = addButton( "select pattern", "pattern" );
			pattern.addActionListener( this );
		}
		
		public void actionPerformed( ActionEvent e )
		{
			if( e != null && e.getSource() == pattern )
				WUtils.selectPattern( this, WCore.getCore().getCLI() );
		}
	}
}
