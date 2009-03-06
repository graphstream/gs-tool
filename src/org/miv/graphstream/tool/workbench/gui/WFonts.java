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
