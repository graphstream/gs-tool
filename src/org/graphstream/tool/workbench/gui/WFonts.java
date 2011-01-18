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

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public final class WFonts
{
	private static final HashMap<String,Font> fonts = new HashMap<String,Font>();
	
	static class FontEntry
	{
		String 	key;
		String 	ressource;
		int 	style;
		float 	size;
		
		public FontEntry( String key, String ressource, int style, float size )
		{
			this.key 		= key;
			this.ressource 	= ressource;
			this.style 		= style;
			this.size 		= size;
		}
		
		public void load()
			throws IOException, FontFormatException
		{
			InputStream in 		= ClassLoader.getSystemResourceAsStream(ressource);
			Font 		font 	= Font.createFont(Font.TRUETYPE_FONT,in);
			
			font = font.deriveFont(style,size);
			
			fonts.put(key,font);
		}
	}
	
	public static Font getFont( String key )
	{
		return fonts.get(key);
	}
	
	public static boolean hasFont( String key )
	{
		return fonts.containsKey(key);
	}
	
	static void load()
	{
		FontEntry [] entries = {
			//new FontEntry( "dialog:title", "org/miv/graphstream/tool/workbench/ressources/fonts/Gputeks-Bold.ttf", 
			//		Font.BOLD, 14.0f ),
			//new FontEntry( "dialog:infos", "org/miv/graphstream/tool/workbench/ressources/fonts/Gputeks-Regular.ttf",
			//		Font.PLAIN, 14.0f ),
			//new FontEntry( "default", "org/miv/graphstream/tool/workbench/ressources/fonts/Gputeks-Regular.ttf",
			//		Font.PLAIN, 14.0f )
			//new FontEntry( "default", "org/miv/graphstream/tool/workbench/ressources/fonts/AG-Stencil.ttf",
			//		Font.PLAIN, 16.0f )
		};
		
		for( FontEntry entry : entries )
		{
			try
			{
				entry.load();
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
		}
		
		if( ! fonts.containsKey("dialog:title") )
			fonts.put("dialog:title", Font.decode("Arial-BOLD-14") );
		if( ! fonts.containsKey("dialog:infos") )
			fonts.put("dialog:title", Font.decode("Arial-PLAIN-12") );
		if( ! fonts.containsKey("default") )
			fonts.put("dialog:title", Font.decode("Arial-PLAIN-12") );
	}
}
