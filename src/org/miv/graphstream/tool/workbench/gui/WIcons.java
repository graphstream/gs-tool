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

import org.miv.graphstream.tool.workbench.xml.WXmlConstants;
import org.miv.graphstream.tool.workbench.xml.WXmlHandler;
import org.miv.graphstream.tool.workbench.xml.WXElement;

public final class WIcons
	implements WXmlConstants
{
	private static final HashMap<String,ImageIcon> icons = new HashMap<String,ImageIcon>();
	
	static class IconHandler
		implements WXmlHandler.WXElementHandler
	{
		public void handle( WXElement wxe )
		{
			if( wxe.is(SPEC_ICON) )
			{
				String key, ressource;
				
				key 		= wxe.getAttribute(QNAME_GSWB_ICONS_ICON_KEY);
				ressource 	= wxe.getAttribute(QNAME_GSWB_ICONS_ICON_RESSOURCE);
				
				load(key,ressource);
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
	
	static void load( String key, String ressource )
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
	
	static void load()
	{
		java.io.InputStream in = null;
		
		if( System.getProperty("gswb.icons") != null )
		{
			in = ClassLoader.getSystemResourceAsStream( System.getProperty("gswb.icons") );
		}
		
		if( in == null )
		{
			in = ClassLoader.getSystemResourceAsStream(GSWB_ICONS_XML);
		}
		
		WXmlHandler.readXml( new IconHandler(),in);
	}
}
