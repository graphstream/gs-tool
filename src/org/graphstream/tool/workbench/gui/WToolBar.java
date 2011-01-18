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

import org.graphstream.tool.workbench.cli.CLI;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JToolBar;

public class WToolBar
	extends JToolBar
	implements ActionListener
{
	public static final long serialVersionUID = 0x040200000001L;
	
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
