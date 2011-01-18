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
package org.graphstream.tool.workbench.gui;

import org.graphstream.tool.workbench.Context;
import org.graphstream.tool.workbench.WCore;
import org.graphstream.tool.workbench.WCore.ActionMode;
import org.graphstream.tool.workbench.cli.CLI;
import org.graphstream.tool.workbench.event.ContextChangeListener;
import org.graphstream.tool.workbench.event.ContextEvent;

import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class WActionAccessory
	extends JDialog 
{
	public static final long serialVersionUID = 0x040030000001L;
	
	static void load()
	{
		if( accessories == null )
			accessories = new WActionAccessory();
	}
	
	private static WActionAccessory accessories = new WActionAccessory();
	
	public static void showAccessory()
	{
		ActionMode current = WCore.getCore().getActionMode();
		
		accessories.setMode(current);
		accessories.setVisible(true);
	}
	
	protected Map<ActionMode,Integer> modeToTabsIndex;
	protected JTabbedPane tabs;
	
	public WActionAccessory()
	{
		this( "Configure" );
	}
	
	public WActionAccessory( String title )
	{
		setTitle(title);
		
		modeToTabsIndex	= new HashMap<ActionMode,Integer>();
		tabs 			= new JTabbedPane();
		
		setLayout( new BorderLayout() );
		add( tabs, BorderLayout.CENTER );
		
		tabs.addTab( 
				null,//WGetText.getText("action:edgeadd"), 
				WIcons.getIcon("action:select"), 
				new SelectAccessory() );
		modeToTabsIndex.put(ActionMode.SELECT,0);
		
		tabs.addTab( 
				null,//WGetText.getText("action:nodeadd"), 
				WIcons.getIcon("action:nodeadd"), 
				new AddNodeAccessory() );
		modeToTabsIndex.put(ActionMode.ADD_NODE,1);
		
		tabs.addTab( 
				null,//WGetText.getText("action:edgeadd"), 
				WIcons.getIcon("action:edgeadd"), 
				new AddEdgeAccessory() );
		modeToTabsIndex.put(ActionMode.ADD_EDGE,2);
		
		tabs.setPreferredSize( new java.awt.Dimension(300,150) );
		
		pack();
	}
	
	public void setMode( ActionMode mode )
	{
		if( modeToTabsIndex.containsKey(mode) )
			tabs.setSelectedIndex(modeToTabsIndex.get(mode));
	}
	/*
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
	*/
	static class AddNodeAccessory
		extends JPanel
		implements DocumentListener, ContextChangeListener
	{
		public static final long serialVersionUID = 0x040030010001L;
		
		protected JTextField nodeIdFormat = new JTextField( 15 );
		
		public AddNodeAccessory()
		{
			//nodeIdFormat = addTextOption( "id ", "id", "node#%n" );
			nodeIdFormat = new JTextField( "node#%n" );
			
			GridBagLayout bag = new GridBagLayout();
			GridBagConstraints c = new GridBagConstraints();
			setLayout(bag);
			
			JLabel l = new JLabel( "id: " );
			
			c.gridy = 0;
			c.gridx = 0;
			c.fill = GridBagConstraints.HORIZONTAL;
			bag.setConstraints(l,c);
			add(l);
			
			c.gridx = 1;
			c.weightx = 2.0;
			c.gridwidth = GridBagConstraints.REMAINDER;
			bag.setConstraints(nodeIdFormat,c);
			add(nodeIdFormat);
			
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
	
	static class AddEdgeAccessory
		extends JPanel
		implements ChangeListener, DocumentListener, ContextChangeListener
	{
		public static final long serialVersionUID = 0x040030020001L;
		
		protected JCheckBox cycle, directed;
		protected JTextField edgeIdFormat;
		
		public AddEdgeAccessory()
		{
			GridBagLayout bag = new GridBagLayout();
			GridBagConstraints c = new GridBagConstraints();
			setLayout(bag);
			
			edgeIdFormat 	= new JTextField( "edge#%n" );
			cycle			= new JCheckBox( WGetText.getTextLookup("@gettext(misc:cycle) ?"), false );
			directed		= new JCheckBox( WGetText.getTextLookup("@gettext(misc:directed) ?"), false );
			
			WUtils.reloadOnLangChanged(cycle,"@gettext(misc:cycle) ?","setText");
			WUtils.reloadOnLangChanged(directed,"@gettext(misc:directed) ?","setText");
			
			JLabel l = new JLabel( "id: " );
			
			c.gridy = 0;
			c.gridx = 0;
			c.fill = GridBagConstraints.HORIZONTAL;
			bag.setConstraints(l,c);
			add(l);
			
			c.gridx = 1;
			c.weightx = 2.0;
			c.gridwidth = GridBagConstraints.REMAINDER;
			bag.setConstraints(edgeIdFormat,c);
			add(edgeIdFormat);
			
			c.gridy++;
			bag.setConstraints(cycle,c);
			add(cycle);
			
			c.gridy++;
			bag.setConstraints(directed,c);
			add(directed);
			
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
	
	static class SelectAccessory
		extends JPanel
		implements ActionListener
	{
		public static final long serialVersionUID = 0x040030030001L;
		
		//protected JButton pattern;
		protected JTextField pattern;
		protected JCheckBox  nodes;
		protected JCheckBox  edges;
		
		public SelectAccessory()
		{
			GridBagLayout bag = new GridBagLayout();
			GridBagConstraints c = new GridBagConstraints();
			setLayout(bag);
			
			JLabel l = new JLabel( "pattern: " );
			pattern 	= new JTextField( ".*" );
			nodes		= new JCheckBox( WGetText.getText("nodes") );
			edges		= new JCheckBox( WGetText.getText("edges") );
			
			WUtils.reloadOnLangChanged(nodes,"nodes","setText");
			WUtils.reloadOnLangChanged(edges,"edges","setText");
			
			c.gridy = 0;
			c.gridx = 0;
			c.fill = GridBagConstraints.HORIZONTAL;
			bag.setConstraints(l,c);
			add(l);
			
			c.gridx = 1;
			c.weightx = 2.0;
			c.gridwidth = GridBagConstraints.REMAINDER;
			bag.setConstraints(pattern,c);
			add(pattern);
			
			c.gridy++;
			c.gridx = 0;
			c.gridwidth = GridBagConstraints.RELATIVE;
			c.weightx = 0;
			bag.setConstraints(nodes,c);
			add(nodes);
			
			c.gridy++;
			bag.setConstraints(edges,c);
			add(edges);
			
			JButton button = new JButton( WGetText.getText("edit:select") );
			button.addActionListener(this);
			
			WUtils.reloadOnLangChanged(button,"edit:select","setText");
			
			c.gridy=1;
			c.gridx=1;
			c.gridheight = 2;
			c.weightx = 2.0;
			bag.setConstraints(button,c);
			add(button);
		}
		
		public void actionPerformed( ActionEvent e )
		{
			if( nodes.isSelected() || edges.isSelected() )
			{
				String cmd = "select all";
				
				if( ! ( nodes.isSelected() && edges.isSelected() ) )
				{
					if( nodes.isSelected() )
						cmd += " nodes";
					else
						cmd += " edges";
				}
				
				cmd += " \"" + pattern.getText() + "\"";
				
				String error = WCore.getCore().getCLI().execute( cmd );
				if( CLI.isErrorMessage( error ) )
					WUtils.errorMessage( this, CLI.getMessage( error ) );
				else if( CLI.isWarningMessage( error ) )
					WUtils.warningMessage( this, CLI.getMessage( error ) );
			}
		}
	}
}
