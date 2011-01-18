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

import java.net.URL;
import java.util.HashMap;
import javax.swing.ImageIcon;

import org.graphstream.tool.workbench.xml.WXElement;
import org.graphstream.tool.workbench.xml.WXmlConstants;
import org.graphstream.tool.workbench.xml.WXmlHandler;

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
