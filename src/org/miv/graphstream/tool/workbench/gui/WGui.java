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

import org.miv.graphstream.tool.workbench.WCore;
import org.miv.graphstream.tool.workbench.WAlgorithm;
import org.miv.graphstream.tool.workbench.WAlgorithmLoader;
import org.miv.graphstream.tool.workbench.event.NotificationListener.Notification;
import org.miv.graphstream.tool.workbench.gui.WGetText.GetTextHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class WGui 
	extends JFrame
	implements ChangeListener
{
	public static final long serialVersionUID = 0x00A00501L;
	
	private static WGui INSTANCE = null;
	public static final void launchWorkbench()
	{
		if( INSTANCE == null ) INSTANCE = new WGui();
		
		INSTANCE.setVisible( true );
	}
	
	protected static Color background = new Color( 238, 238, 238 );
	
	protected WActions actionBox;
	protected InfoBox infoBox;
	protected WMenuBar menuBar;
	protected WCore core;
	protected WDesktop desktop;
	protected SelectionTree selectionTree;
	protected WDialog dialogSelection;
	protected HashMap<String,WDialog> dialogs;
	
	private WGui()
	{
		super( "GraphStream" );
		
		WGetText.load();
		
		this.dialogs = new HashMap<String,WDialog>();
		this.core    = WCore.getCore();
		this.core.setTerminalCloseAction( javax.swing.WindowConstants.DISPOSE_ON_CLOSE );
		this.actionBox = new WActions( this );
		this.menuBar = new WMenuBar( this.actionBox );
		this.desktop = new WDesktop( core.getCLI() );
		this.infoBox = new InfoBox( core.getCLI() );
		actionBox.addChangeListener( this );
		
		this.selectionTree = new SelectionTree( core.getCLI() );
		core.addSelectionListener( this.selectionTree );
		
		JPanel tmp = new JPanel();
		tmp.setPreferredSize(new java.awt.Dimension( 150, 300 ));
		tmp.setLayout( new BorderLayout() );
		JScrollPane scroll = new JScrollPane(selectionTree);
		tmp.add( scroll, BorderLayout.CENTER );
		
		WDialogManager.init(this);
		dialogs.put( "selection", 	new WDialog( "Selection", tmp ) );
		dialogs.put( "graph-infos",	new WDialog( "Graph Infos", new GraphInfo(core.getCLI()) ));
		
		setJMenuBar( this.menuBar );
		setDefaultCloseOperation( EXIT_ON_CLOSE );
		
		setLayout( new BorderLayout() );
		add( actionBox, BorderLayout.NORTH );
		
		//applyBackground( infoBox, actionBox, infoBox.graphInfo, infoBox.selectionTree );
		
		setIconImage( WUtils.getImageIcon( "gs_logo" ).getImage() );
		
		this.core.addWorkbenchListener( this.desktop );
		
		loadAlgorithms();
		WNotificationServer.init(core);
		WHelp.init();
	    
		pack();
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
	
	public void stateChanged( ChangeEvent e )
	{
	}
	
	protected void applyBackground( Component ... carray )
	{
		if( carray == null ) return;
		for( Component c: carray )
		{
			c.setBackground( background );
		}
	}
	
// Launch the workbench
	
	public static String licence()
	{
		return 
			"This program is free software: you can redistribute it and/or modify\n" +
			"it under the terms of the GNU General Public License as published by\n" +
			"the Free Software Foundation, either version 3 of the License, or\n" +
	    	"(at your option) any later version.\n\n" +
	    	"This program is distributed in the hope that it will be useful,\n" +
	    	"but WITHOUT ANY WARRANTY; without even the implied warranty of\n" +
	    	"MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n" +
	    	"GNU General Public License for more details.\n\n" +
	    	"You should have received a copy of the GNU General Public License\n" +
	    	"along with this program.  If not, see <http://www.gnu.org/licenses/>\n";

	}
	
	public static void main( String [] args )
	{
		System.err.printf( "%s\n", licence() );
		
	    try
	    {
	    	//UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
	    	UIManager.setLookAndFeel("org.jvnet.substance.skin.SubstanceRavenLookAndFeel");
	    	
			JFrame.setDefaultLookAndFeelDecorated(true);
			JDialog.setDefaultLookAndFeelDecorated(true);
	    }
	    catch (Exception e)
	    {
	    	System.out.println("unable to load LookAndFeel\n" +
	    			"To use substance skins, please add substance-lite.jar in\nyour classpath.");
	    }
	    
	    javax.swing.SwingUtilities.invokeLater( new Runnable()
	    {
	    	public void run()
	    	{
	    		launchWorkbench();
	    	}
	    } );
	}
}
