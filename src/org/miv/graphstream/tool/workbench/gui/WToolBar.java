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

import org.miv.graphstream.tool.workbench.cli.CLI;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JToolBar;

public class WToolBar
	extends JToolBar
	implements ActionListener
{
	public static final long serialVersionUID = 0x00A00401L;
	
	public static final String OPT_ADD_NODE_ID = "actions.options.addnode.id";
	public static final String OPT_ADD_EDGE_ID = "actions.options.addedge.id";
	public static final String OPT_ADD_EDGE_DIRECTED = "actions.options.addedge.directed";
	public static final String OPT_ADD_EDGE_CYCLE = "actions.options.addedge.cycle";
	
	private static final String [] specs = {
		"action:select",
		"action:info",
		null,
		"action:nodeadd",
		"action:nodedel",
		null,
		"action:edgeadd",
		"action:edgedel",
		null,
		"action:configure",
		null,
		"system:terminal"
	};
	
	protected WGui gui;
	protected CLI cli;
	protected boolean showConfigure = false;
	
	public WToolBar( WGui gui )
	{
		super( "tools" );
		
		this.gui = gui;
		this.cli = gui.getCore().getCLI();
		
		add( new JToolBar.Separator() );
		
		for( String spec : specs )
		{
			if( spec == null )
				add( new JToolBar.Separator() );
			else
				add( WActions.getAction(spec) );
		}
	}
	
// ActionListener implementation
	
	public void actionPerformed( ActionEvent e )
	{
		if( e.getActionCommand().startsWith( "@CLI:" ) )
			cli.execute( e.getActionCommand().substring(5) );
		else if( e.getActionCommand().equals("menu.help.about") )
			WAbout.whatAbout();
		else if( e.getActionCommand().equals("menu.help.manual") )
			WUtils.informationMessage(null, "Visit: http:/graphstream.sourceforge.net/Manual.html");
		else if( e.getActionCommand().equals("menu.help.tutorials") )
			WUtils.informationMessage(null, "Visit: http:/graphstream.sourceforge.net/tutorials.html");
	}
}
