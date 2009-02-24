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

import org.miv.graphstream.tool.workbench.cli.CLI;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class InfoBox extends JPanel
{
	public static final long serialVersionUID = 0x00A00601L;

	protected JTabbedPane tabs;
	protected GraphInfo graphInfo;
	protected SelectionTree selectionTree;
	
	public InfoBox( CLI cli )
	{
		this.tabs = new JTabbedPane();
		this.graphInfo = new GraphInfo( cli );
		this.selectionTree = new SelectionTree( cli );
		
		cli.getCore().addSelectionListener( this.selectionTree );
		
		setPreferredSize( new Dimension( 100, 250 ) );
		tabs.addTab( "Graph", graphInfo );
		tabs.addTab( "Selection", selectionTree );
		
		setLayout( new BorderLayout() );
		add( tabs, BorderLayout.CENTER );
	}
}
