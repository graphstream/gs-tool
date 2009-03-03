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

import java.net.URL;
import java.util.HashMap;
import javax.swing.ImageIcon;

public final class WIcons
{
	private static final HashMap<String,ImageIcon> icons = new HashMap<String,ImageIcon>();
	
	static class IconEntry
	{
		String key;
		String ressource;
		
		public IconEntry( String key, String ressource )
		{
			this.key 		= key;
			this.ressource 	= ressource;
		}
		
		public void load()
		{
			URL url = ClassLoader.getSystemResource( ressource );
			
			if( url != null )
			{
				ImageIcon ii = new ImageIcon( url );
				
				if( ii != null )
				{
					icons.put(key,ii);
				}
			}
		}
	}
	
	public static ImageIcon getIcon( String key )
	{
		return icons.get(key);
	}
	
	public static boolean hasIcon( String key )
	{
		return icons.containsKey(key);
	}
	
	static void load()
	{
		IconEntry [] entries =
		{
				new IconEntry( "node", "org/miv/graphstream/tool/workbench/ressources/node32.png" ),
				new IconEntry( "edge", "org/miv/graphstream/tool/workbench/ressources/edge32.png" ),
				new IconEntry( "action:node_add", "org/miv/graphstream/tool/workbench/ressources/nodeadd32.png" ),
				new IconEntry( "action:node_del", "org/miv/graphstream/tool/workbench/ressources/nodedelete32.png" ),
				new IconEntry( "action:node_info", "org/miv/graphstream/tool/workbench/ressources/nodeinfo32.png" ),
				new IconEntry( "action:select",  "org/miv/graphstream/tool/workbench/ressources/select32.png"  ),
				new IconEntry( "action:edge_add", "org/miv/graphstream/tool/workbench/ressources/edgeadd32.png" ),
				new IconEntry( "action:edge_del", "org/miv/graphstream/tool/workbench/ressources/edgedelete32.png" ),
				new IconEntry( "action:edge_info", "org/miv/graphstream/tool/workbench/ressources/edgeinfo32.png" ),
				new IconEntry( "action:configure", "org/miv/graphstream/tool/workbench/ressources/configure32.png" ),
				new IconEntry( "term", "org/miv/graphstream/tool/workbench/ressources/term_32.png" ),
				new IconEntry( "gs_logo", "org/miv/graphstream/tool/workbench/ressources/gs_logo.png" ),
				new IconEntry( "key",  "org/miv/graphstream/tool/workbench/ressources/key_16.png"  ),
				new IconEntry( "file:new", "org/miv/graphstream/tool/workbench/ressources/filenew.png" ),
				new IconEntry( "file:open", "org/miv/graphstream/tool/workbench/ressources/fileopen.png" ),
				new IconEntry( "file:save", "org/miv/graphstream/tool/workbench/ressources/filesave.png" ),
				new IconEntry( "file:saveas", "org/miv/graphstream/tool/workbench/ressources/filesaveas.png" ),
				new IconEntry( "exit", "org/miv/graphstream/tool/workbench/ressources/exit.png" ),
				new IconEntry( "edit:undo", "org/miv/graphstream/tool/workbench/ressources/undo.png" ),
				new IconEntry( "edit:redo", "org/miv/graphstream/tool/workbench/ressources/redo.png" ),
				new IconEntry( "edit:copy", "org/miv/graphstream/tool/workbench/ressources/editcopy.png" ),
				new IconEntry( "edit:cut", "org/miv/graphstream/tool/workbench/ressources/editcut.png" ),
				new IconEntry( "edit:paste", "org/miv/graphstream/tool/workbench/ressources/editpaste.png" ),
				new IconEntry( "help", "org/miv/graphstream/tool/workbench/ressources/help.png" ),
				new IconEntry( "splash", "org/miv/graphstream/tool/workbench/ressources/splash.png" ),
				new IconEntry( "gears", "org/miv/graphstream/tool/workbench/ressources/gears32.png" )
		};
		
		for( IconEntry entry : entries )
			entry.load();
	}
}
