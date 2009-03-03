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

import org.miv.graphstream.tool.workbench.WCore.ActionMode;
import org.miv.graphstream.tool.workbench.cli.CLI;

import java.awt.BorderLayout;
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
	
	static class ActionSpec
	{
		String name;
		ActionMode mode;
		String accessory;
		String tooltip;
		
		public ActionSpec( String name, ActionMode mode )
		{
			this(name,mode,"@gettext(" + name + ")",null);
		}
		
		public ActionSpec( String name, ActionMode mode, String accessory )
		{
			this(name,mode,"@gettext(" + name + ")",accessory);
		}
		
		public ActionSpec( String name, ActionMode mode, String tooltip, String accessory )
		{
			this.name = name;
			this.mode = mode;
			this.tooltip = tooltip;
			this.accessory = accessory;
		}
		
		public void install( WActions actions )
		{
			JButton button = new JButton( WUtils.getImageIcon( name ) );
				button.setBackground( WGui.background );
				button.setToolTipText( WGetText.getTextLookup(tooltip) );
				button.addActionListener( actions );
			actions.actions.put( button, mode );
			
			if( accessory != null )
			{
				try
				{
					Class<?> clazz = Class.forName(accessory);
					java.lang.reflect.Constructor<?> co = clazz.getConstructor(CLI.class);
					actions.accessories.put( button, (ActionAccessory) co.newInstance(actions.cli) );
				}
				catch( Exception e )
				{
					e.printStackTrace();
				}
			}
			
			actions.add(button);
			WUtils.reloadOnLangChanged( button, tooltip, "setToolTipText" );
		}
	}
	
	private static final ActionSpec [] specs = {
		new ActionSpec( "action:select", ActionMode.SELECT, 
				"org.miv.graphstream.tool.workbench.gui.ActionAccessory$SelectAccessory" ),
		new ActionSpec( "action:node_info", ActionMode.INFO ),
		null,
		new ActionSpec( "action:node_add", ActionMode.ADD_NODE,
				"org.miv.graphstream.tool.workbench.gui.ActionAccessory$AddNodeAccessory" ),
		new ActionSpec( "action:node_del", ActionMode.DEL_NODE ),
		null,
		new ActionSpec( "action:edge_add", ActionMode.ADD_EDGE,
				"org.miv.graphstream.tool.workbench.gui.ActionAccessory$AddEdgeAccessory" ),
		new ActionSpec( "action:edge_del", ActionMode.DEL_NODE )
	};
	
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
		configureDialog.setLayout(new BorderLayout());
		configureDialog.add(accessoryPanel);
		
		//Dimension buttonDim = new Dimension( 32,32 );
		accessoryPanel.setLayout( new BorderLayout() );
		
		add( new JToolBar.Separator() );
		
		for( ActionSpec spec : specs )
		{
			if( spec == null )
				add( new JToolBar.Separator() );
			else
				spec.install(this);
		}
		
		add( new JToolBar.Separator() );
		
		JButton openTerminal = new JButton( WUtils.getImageIcon("term") );
			openTerminal.setActionCommand("open.terminal");
			openTerminal.addActionListener(this);
			openTerminal.setToolTipText( WGetText.getTextLookup("@gettext(action:open_terminal)") );
		add( openTerminal );
		WUtils.reloadOnLangChanged(openTerminal,"@gettext(action:open_terminal","setToolTipText");
		
		add( new JToolBar.Separator() );
		
		JButton configure = new JButton( WUtils.getImageIcon("action:configure") );
			configure.setActionCommand("tool.configure");
			configure.addActionListener(this);
			configure.setToolTipText( WGetText.getTextLookup("@gettext(action:configure)") );
		add( configure );
		WUtils.reloadOnLangChanged(configure,"@gettext(action:configure)","setToolTipText");
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
			WUtils.openGraph( this, cli );
		else if( e.getActionCommand().equals( "save.graph" ) )
			cli.getCore().saveContext();
		else if( e.getActionCommand().equals( "saveas.graph" ) )
			WUtils.selectFile(null,cli.getCore().getActiveContext());
		else if( e.getActionCommand().equals( "new.graph" ) )
			WUtils.newGraph( this, cli );
		else if( e.getActionCommand().equals( "tool.configure" ) )
			configureTool();
		else if( e.getActionCommand().equals("quit") )
			cli.getCore().exit();
		else if( e.getActionCommand().equals("options") )
			options.setVisible(true);
		else if( e.getActionCommand().equals("menu.help") )
			WHelp.showHelp();
		else if( e.getActionCommand().equals("menu.help.about") )
			WAbout.whatAbout();
		else if( e.getActionCommand().equals("menu.help.manual") )
			WUtils.informationMessage(null, "Visit: http:/graphstream.sourceforge.net/Manual.html");
		else if( e.getActionCommand().equals("menu.help.tutorials") )
			WUtils.informationMessage(null, "Visit: http:/graphstream.sourceforge.net/tutorials.html");
	}
}
