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

import org.miv.graphstream.tool.workbench.WorkbenchCore;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class WorkbenchGUI extends JFrame implements ChangeListener
{
	public static final long serialVersionUID = 0x00A00501L;
	
	private static WorkbenchGUI INSTANCE = null;
	public static final void launchWorkbench()
	{
		if( INSTANCE == null ) INSTANCE = new WorkbenchGUI();
		
		INSTANCE.setVisible( true );
	}
	
	protected static Color background = new Color( 238, 238, 238 );
	
	protected ActionBox actionBox;
	protected InfoBox infoBox;
	protected WorkbenchMenuBar menuBar;
	protected WorkbenchCore core;
	protected WorkbenchDesktop desktop;
	protected JSplitPane splitPane;
	
	private WorkbenchGUI()
	{
		super( "GraphStream" );
		
		this.core    = new WorkbenchCore();
		this.core.setTerminalCloseAction( javax.swing.WindowConstants.DISPOSE_ON_CLOSE );
		this.actionBox = new ActionBox( core.getCLI() );
		this.menuBar = new WorkbenchMenuBar( this.actionBox );
		this.desktop = new WorkbenchDesktop( core.getCLI() );
		this.infoBox = new InfoBox( core.getCLI() );
		actionBox.addChangeListener( this );
		
		setJMenuBar( this.menuBar );
		setDefaultCloseOperation( EXIT_ON_CLOSE );
		
		splitPane = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
		splitPane.add( actionBox );
		splitPane.add( infoBox );
		
		setLayout( new BorderLayout() );
		add( splitPane, BorderLayout.CENTER );
		
		applyBackground( infoBox, actionBox, infoBox.graphInfo, infoBox.selectionTree );
		
		setIconImage( WorkbenchUtils.getImageIcon( "gs_logo" ).getImage() );
	    
		pack();
		splitPane.setDividerLocation( 0.25 );
		
		this.core.addWorkbenchListener( this.desktop );
	}
	
	public void stateChanged( ChangeEvent e )
	{
		splitPane.resetToPreferredSizes();
	}
	
	protected void applyBackground( Component ... carray )
	{
		if( carray == null ) return;
		for( Component c: carray )
		{
			c.setBackground( background );
		}
	}
	
// For tests
	
	public static void main( String [] args )
	{
		launchWorkbench();
	}
}
