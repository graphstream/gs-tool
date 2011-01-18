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

import org.graphstream.tool.workbench.WAlgorithm;
import org.graphstream.tool.workbench.WAlgorithmLoader;
import org.graphstream.tool.workbench.WCore;
import org.graphstream.tool.workbench.WNotificationServer;
import org.graphstream.tool.workbench.event.NotificationListener.Notification;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;

import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

public class WGui 
	extends JFrame
{
	public static final long serialVersionUID = 0x040110000001L;
	
	private static WGui gui = null;
	
	public static void init()
	{
		if( gui == null )
			gui = new WGui();
	}
	
	public static void loadOptions()
	{
		WOptions.init(gui);
	}
	
	public static void display()
	{
		if( gui != null )
			gui.setVisible(true);
	}
	
	public static void setWorkbenchLookAndFeel( String name )
	{
		 try
		 {
			 UIManager.setLookAndFeel(name);

			 JFrame.setDefaultLookAndFeelDecorated(true);
			 JDialog.setDefaultLookAndFeelDecorated(true);
		 }
		 catch (Exception e)
		 {
			 System.out.println("unable to load LookAndFeel\n" +
			 "To use substance skins, please add substance-lite.jar in\nyour classpath.");
		 }
	}
	
	protected static Color background = new Color( 238, 238, 238 );
	
	protected WToolBar 					actionBox;
	protected WMenuBar 					menuBar;
	protected WCore 					core;
	protected WDesktop 					desktop;
	protected WSelectionGUI 			selectionTree;
	protected WDialog 					dialogSelection;
	protected HashMap<String,WDialog> 	dialogs;
	
	private WGui()
	{
		super( "GraphStream" );
		
		this.dialogs = new HashMap<String,WDialog>();
		this.core    = WCore.getCore();
		this.core.setTerminalCloseAction( javax.swing.WindowConstants.DISPOSE_ON_CLOSE );
		this.actionBox = new WToolBar( this );
		this.menuBar = new WMenuBar( this.actionBox );
		this.desktop = new WDesktop( this, core.getCLI() );
		
		this.selectionTree = new WSelectionGUI();
		
		JPanel tmp = new JPanel();
		tmp.setPreferredSize(new java.awt.Dimension( 150, 300 ));
		tmp.setLayout( new BorderLayout() );
		JScrollPane scroll = new JScrollPane(selectionTree);
		tmp.add( scroll, BorderLayout.CENTER );
		
		setJMenuBar( this.menuBar );
		setDefaultCloseOperation( EXIT_ON_CLOSE );
		setResizable(false);
		setLayout( new BorderLayout() );
		add( actionBox, BorderLayout.NORTH );
		
		setIconImage( WUtils.getImageIcon( "gs_logo" ).getImage() );
		
		WDialogManager.init(this);
		dialogs.put( "selection", 	new WDialog( this, "Selection", tmp ) );
		dialogs.put( "graph-infos",	new WDialog( this, "Graph Infos", new WGraphInfo(core.getCLI()) ));
		dialogs.put( "history",		new WDialog( this, "History", new WHistoryGUI() ));
		
		this.core.addWorkbenchListener( this.desktop );
		
		loadAlgorithms();
	    
		pack();
		
		/*
		 * Dispatch start events.
		 */
		WNotificationServer.dispatch( Notification.clipboardEmpty );
		WNotificationServer.dispatch( Notification.historyUndoDisable );
		WNotificationServer.dispatch( Notification.historyRedoDisable );
		WNotificationServer.dispatch( Notification.noContext );
	}
	
	public void setSkin( String name )
	{
		
	}
	
	WMenuBar getWMenuBar()
	{
		return menuBar;
	}
	
	public void setFullMode( boolean fullMode )
	{
		desktop.setFullMode(fullMode);
		
		if( fullMode )
		{
			add( desktop, BorderLayout.CENTER );
			add( dialogs.get("graph-infos").getContentPane(), BorderLayout.WEST );
			add( dialogs.get("selection").getContentPane(), BorderLayout.EAST );
		
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			setSize( toolkit.getScreenSize() );
			setLocation(0,0);
		
			WNotificationServer.dispatch(Notification.fullMode);
		}
		else
		{
			remove(desktop);
			remove(dialogs.get("graph-infos").getContentPane());
			remove(dialogs.get("selection").getContentPane());
			
			pack();
			
			WNotificationServer.dispatch(Notification.normalMode);
		}
	}
	
	public WCore getCore()
	{
		return core;
	}
	
	protected void loadAlgorithms()
	{
		WAlgorithmLoader.load();
		
		if( System.getProperty("graphstream.user.algorithms") != null )
			WAlgorithmLoader.load(ClassLoader.getSystemResourceAsStream(
					System.getProperty("graphstream.user.algorithms")));
		
		WAlgorithmManager.init(this);
		Iterator<WAlgorithm> ite = WAlgorithm.algorithms();
		
		while( ite.hasNext() )
			WAlgorithmManager.registerAlgorithm( new WAlgorithmGUI(core.getCLI(),ite.next()) );
	}
	
	protected void applyBackground( Component ... carray )
	{
		if( carray == null ) return;
		for( Component c: carray )
		{
			c.setBackground( background );
		}
	}
}
