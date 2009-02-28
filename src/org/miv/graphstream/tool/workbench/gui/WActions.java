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

import org.miv.graphstream.tool.workbench.WCore.ActionMode;
import org.miv.graphstream.tool.workbench.cli.CLI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JToolBar;

public class WActions
	extends JToolBar
	implements ActionListener
{
	public static final long serialVersionUID = 0x00A00401L;
	
	public static final String OPT_ADD_NODE_ID = "actions.options.addnode.id";
	public static final String OPT_ADD_EDGE_ID = "actions.options.addedge.id";
	public static final String OPT_ADD_EDGE_DIRECTED = "actions.options.addedge.directed";
	public static final String OPT_ADD_EDGE_CYCLE = "actions.options.addedge.cycle";
	
	protected WGui gui;
	protected CLI cli;
	protected Map<Object,ActionMode> actions;
	protected Map<JButton,ActionAccessory> accessories;
	protected JPanel accessoryPanel;
	protected JDialog configureDialog;
	protected LinkedList<ChangeListener> changeListeners = new LinkedList<ChangeListener>();
	protected WOptions options;
	protected boolean showConfigure = false;
	
	public WActions( WGui gui )
	{
		super( "tools" );
		
		this.gui = gui;
		this.cli = gui.getCore().getCLI();
		this.actions = new HashMap<Object,ActionMode>();
		this.accessories = new HashMap<JButton,ActionAccessory>();
		this.accessoryPanel = new JPanel();
		this.options = new WOptions(gui);
		this.configureDialog = new JDialog();
		configureDialog.setTitle("Configure");
		configureDialog.add(accessoryPanel);
		
		Dimension buttonDim = new Dimension( 32,32 );
		accessoryPanel.setLayout( new BorderLayout() );
		ActionAccessory aa;
		
		JButton button = new JButton( WorkbenchUtils.getImageIcon( "action:select" ) );
		button.setPreferredSize( buttonDim );
		button.setBackground( WGui.background );
		button.setToolTipText( "selection mode" );
		button.addActionListener( this );
		actions.put( button, ActionMode.SELECT );
		aa = new ActionAccessory.SelectAccessory( cli );
		aa.setBackground( WGui.background );
		accessories.put( button, aa );
		add(button);
		
		add( new JToolBar.Separator() );
		
		button = new JButton( WorkbenchUtils.getImageIcon( "action:add_node" ) );
		button.setPreferredSize( buttonDim );
		button.setBackground( WGui.background );
		button.setToolTipText( "add nodes mode" );
		button.addActionListener( this );
		actions.put( button, ActionMode.ADD_NODE );
		aa = new ActionAccessory.AddNodeAccessory( cli );
		aa.setBackground( WGui.background );
		accessories.put( button, aa );
		add(button);
		
		button = new JButton( WorkbenchUtils.getImageIcon( "action:del_node" ) );
		button.setPreferredSize( buttonDim );
		button.setBackground( WGui.background );
		button.setToolTipText( "delete nodes" );
		button.addActionListener( this );
		actions.put( button, ActionMode.DEL_NODE );
		add(button);
		
		add( new JToolBar.Separator() );
		
		button = new JButton( WorkbenchUtils.getImageIcon( "action:add_edge" ) );
		button.setPreferredSize( buttonDim );
		button.setBackground( WGui.background );
		button.setToolTipText( "add edges mode" );
		button.addActionListener( this );
		actions.put( button, ActionMode.ADD_EDGE );
		aa = new ActionAccessory.AddEdgeAccessory( cli );
		aa.setBackground( WGui.background );
		accessories.put( button, aa );
		add(button);
		
		button = new JButton( WorkbenchUtils.getImageIcon( "action:del_edge" ) );
		button.setPreferredSize( buttonDim );
		button.setBackground( WGui.background );
		button.setToolTipText( "delete edges" );
		button.addActionListener( this );
		actions.put( button, ActionMode.DEL_EDGE );
		add(button);
		
		add( new JToolBar.Separator() );
		
		button = new JButton( WorkbenchUtils.getImageIcon( "term" ) );
		button.setPreferredSize( buttonDim );
		button.setBackground( WGui.background );
		button.setActionCommand( "open.terminal" );
		button.setToolTipText( "open a new cli-terminal" );
		button.addActionListener( this );
		add(button);
		
		add( new JToolBar.Separator() );
		
		button = new JButton( "configure" );
		button.setBackground( WGui.background );
		button.setActionCommand( "tool.configure" );
		button.setToolTipText( "configure this tool" );
		button.addActionListener( this );
		add(button);
	}
	
	public void addChangeListener( ChangeListener cl )
	{
		if( ! changeListeners.contains( cl ) )
			changeListeners.add( cl );
	}
	
	public void removeChangeListener( ChangeListener cl )
	{
		changeListeners.remove( cl );
	}
	
	protected void fireStateChanged()
	{
		ChangeEvent e = new ChangeEvent( this );
		for( ChangeListener cl: changeListeners )
			cl.stateChanged( e );
	}
	
	protected void setCurrentAction( Object current )
	{
		for( Object obj: actions.keySet() )
		{
			if( obj instanceof AbstractButton )
			{
				((AbstractButton)obj).setEnabled( obj != current );
			}
		}

		accessoryPanel.removeAll();
		
		if( accessories.containsKey( current ) )
		{
			accessoryPanel.add( accessories.get( current ), BorderLayout.CENTER );
			showConfigure = true;
		}
		else
		{
			showConfigure = false;
		}
		
		setPreferredSize( this.getLayout().preferredLayoutSize( this ) );
		fireStateChanged();
		
		if( configureDialog.isVisible() )
			configureTool();
	}
	
	protected void configureTool()
	{
		configureDialog.pack();
		configureDialog.setVisible(showConfigure);
	}
	
// ActionListener implementation
	
	public void actionPerformed( ActionEvent e )
	{
		if( actions.containsKey( e.getSource() ) )
		{
			cli.getCore().setActionMode( actions.get( e.getSource() ) );
			setCurrentAction( e.getSource() );
		}
		
		if( e.getActionCommand().startsWith( "@CLI:" ) )
			cli.execute( e.getActionCommand().substring(5) );
		else if( e.getActionCommand().equals( "open.terminal" ) )
			cli.getCore().openTerminal();
		else if( e.getActionCommand().equals( "open.graph" ) )
			WorkbenchUtils.openGraph( this, cli );
		else if( e.getActionCommand().equals( "save.graph" ) )
			cli.getCore().saveContext();
		else if( e.getActionCommand().equals( "new.graph" ) )
			WorkbenchUtils.newGraph( this, cli );
		else if( e.getActionCommand().equals( "tool.configure" ) )
			configureTool();
		else if( e.getActionCommand().equals("quit") )
			cli.getCore().exit();
		else if( e.getActionCommand().equals("options") )
			options.setVisible(true);
		else if( e.getActionCommand().equals("menu.help.about") )
			WAbout.whatAbout();
	}
}
