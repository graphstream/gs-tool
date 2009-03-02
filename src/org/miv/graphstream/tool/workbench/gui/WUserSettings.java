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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Locale;
import java.util.HashMap;

import org.miv.graphstream.tool.workbench.xml.WXElement;
import org.miv.graphstream.tool.workbench.xml.WXmlConstants;
import org.miv.graphstream.tool.workbench.xml.WXmlHandler;

public class WUserSettings
	implements WXmlConstants
{
	private static final WUserSettings wus = new WUserSettings();
	
	public static WUserSettings getUserSettings()
	{
		return wus;
	}
	
	public static void loadUserSettings()
	{
		wus.load();

		if( wus.getLookAndFeel() != null )
			WGui.setWorkbenchLookAndFeel( wus.getLookAndFeel() );
		
		if( wus.getLocale() != null )
			WGetText.setLocale(wus.getLocale());
	}
	
	public static void saveUserSettings()
	{
		wus.save();
	}
	
	String 					lookAndFeel;
	Locale 					locale;
	HashMap<String,String>	settings;
	
	private WUserSettings()
	{
		lookAndFeel = null;
		locale		= Locale.getDefault();
		settings	= new HashMap<String,String>();
	}
	
	public String getLookAndFeel()
	{
		return lookAndFeel;
	}
	
	public Locale getLocale()
	{
		return locale;
	}
	
	public void load()
	{
		File settings = new File(String.format( "%s/.graphstream/gswb-settings.xml",
				System.getenv().get("HOME") ));
		
		try
		{
			if( settings.exists() )
			{
				load( new FileInputStream(settings) );
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	public void load( InputStream in )
	{
		WXElement wxe = WXmlHandler.readXml(in);
		
		if( wxe != null && wxe.is(SPEC_SETTINGS) )
		{
			Iterator<WXElement> ite = wxe.iteratorOnChildren();
			
			while( ite.hasNext() )
			{
				wxe = ite.next();
				
				if( wxe.is(SPEC_SETTING) )
				{
					String name, value;
					
					name  = wxe.getAttribute(QNAME_GSWB_SETTINGS_SETTING_NAME);
					value = wxe.getAttribute(QNAME_GSWB_SETTINGS_SETTING_VALUE);
					
					if( name.equals("skin") )
					{
						lookAndFeel = value;
					}
					else if( name.equals("locale") )
					{
						String [] loc = value.split(",");
						Locale locale = null;
						
						switch(loc.length)
						{
						case 1:	locale = new Locale(loc[0]); 				break;
						case 2: locale = new Locale(loc[0],loc[1]); 		break;
						case 3: locale = new Locale(loc[0],loc[1],loc[3]); 	break;
						}
						
						if( locale != null )
							this.locale = locale;
					}
					else settings.put(name,value);
				}
			}
		}
	}
	
	public void save()
	{
		File settings = new File(String.format( "%s/.graphstream/gswb-settings.xml",
				System.getenv().get("HOME") ));
		
		try
		{
			if( ! settings.exists() )
				settings.getParentFile().mkdirs();
			
			FileWriter out = new FileWriter(settings);
			
			out.write( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" );
			out.write( "<!DOCTYPE gswb PUBLIC \"-//W3C//DTD HTML 4.01 Frameset//EN\" " +
					"\"jar:org/miv/graphstream/tool/workbench/xml/graphstream-workbench.dtd\">\n" );
			out.write( "<gswb:settings xmlns=\"org/miv/graphstream\" xmlns:gswb=\"workbench\">\n" );
			
			out.write( String.format( "\t<setting name=\"%s\" value=\"%s\"/>\n", 
					"skin",	lookAndFeel ) );
			out.write( String.format( "\t<setting name=\"%s\" value=\"%s\"/>\n",
					"locale", WGetText.getLocale().getLanguage() + "," + 
					WGetText.getLocale().getCountry() + "," + WGetText.getLocale().getVariant() ));
			
			out.write( "</gswb:settings>\n" );
			
			out.flush();
			out.close();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
}
