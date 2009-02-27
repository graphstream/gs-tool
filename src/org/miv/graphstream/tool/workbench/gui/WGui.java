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


import org.miv.graphstream.tool.workbench.WCore;
import org.miv.graphstream.tool.workbench.WAlgorithm;
import org.miv.graphstream.tool.workbench.WAlgorithmLoader;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JComponent;
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
	
	protected ActionBox actionBox;
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
		
		this.dialogs = new HashMap<String,WDialog>();
		this.core    = new WCore();
		this.core.setTerminalCloseAction( javax.swing.WindowConstants.DISPOSE_ON_CLOSE );
		this.actionBox = new ActionBox( core.getCLI() );
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
		
		applyBackground( infoBox, actionBox, infoBox.graphInfo, infoBox.selectionTree );
		
		setIconImage( WorkbenchUtils.getImageIcon( "gs_logo" ).getImage() );
	    
		pack();
		
		this.core.addWorkbenchListener( this.desktop );
		
		loadAlgorithms();
	}
	
	WMenuBar getWMenuBar()
	{
		return menuBar;
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
	
	public static void main( String [] args )
	{
		
	    try
	    {
	    	UIManager.setLookAndFeel("org.jvnet.substance.skin.SubstanceRavenGraphiteGlassLookAndFeel");
	    	
			JFrame.setDefaultLookAndFeelDecorated(true);
			JDialog.setDefaultLookAndFeelDecorated(true);
	    }
	    catch (Exception e)
	    {
	    	System.out.println("unable to load LookAndFeel");
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
