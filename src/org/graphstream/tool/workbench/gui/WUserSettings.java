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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;

import java.text.DateFormat;

import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.HashMap;

import org.graphstream.tool.workbench.xml.WXElement;
import org.graphstream.tool.workbench.xml.WXmlConstants;
import org.graphstream.tool.workbench.xml.WXmlHandler;

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
		try
		{
			wus.load();

			if( wus.getLookAndFeel() != null )
				WGui.setWorkbenchLookAndFeel( wus.getLookAndFeel() );
		
			if( wus.getLocale() != null )
				WGetText.setLocale(wus.getLocale());
		}
		catch( Exception e )
		{
			System.err.printf( "failed to load user settings\n" );
		}
	}
	
	public static void saveUserSettings()
	{
		wus.save();
	}
	
	public static void deleteUserSettings()
	{
		File settings = getUserSettingsFile();
		
		if( settings.exists() )
			settings.delete();
	}
	
	public static File getUserSettingsFile()
	{
		String home = System.getProperty("user.home");
		String sep  = System.getProperty("file.separator");
		
		if( home == null )
			return null;
		
		return new File( home + sep + ".graphstream" + sep + "gswb-settings.xml" );
	}
	
	public static List<FileHistory> getFileHistory()
	{
		return Collections.unmodifiableList(wus.history);
	}
	
	public static void newFileUsed( String path )
	{
		for( FileHistory fh : wus.history )
		{
			if( fh.pathname.equals(path) )
			{
				fh.date = DateFormat.getDateTimeInstance().getCalendar().getTime();
				Collections.sort(wus.history);
				return;
			}
		}
		
		wus.history.add( new FileHistory(path,DateFormat.getDateTimeInstance().getCalendar().getTime()) );
		Collections.sort(wus.history);
		
		saveUserSettings();
	}
	
	String 					lookAndFeel;
	Locale 					locale;
	LinkedList<FileHistory>	history;
	boolean					fullmode;
	HashMap<String,String>	settings;
	
	private WUserSettings()
	{
		lookAndFeel = null;
		locale		= Locale.getDefault();
		settings	= new HashMap<String,String>();
		history		= new LinkedList<FileHistory>();
	}
	
	public String getLookAndFeel()
	{
		return lookAndFeel;
	}
	
	public Locale getLocale()
	{
		return locale;
	}
	
	public void setLookAndFeel( String laf )
	{
		lookAndFeel = laf;
	}
	
	public void setLocale( Locale l )
	{
		locale = l;
	}
	
	public void load()
	{
		File settings = getUserSettingsFile();
		
		try
		{
			if( settings.exists() )
				load( new FileInputStream(settings) );
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
				else if( wxe.is(SPEC_HISTORY) )
				{
					history.clear();
					Iterator<WXElement> files = wxe.iteratorOnChildren();
					
					while( files.hasNext() )
					{
						WXElement file = files.next();
						
						if( file.is(SPEC_HISTORY_FILE) )
						{
							
							String pathname = file.getAttribute(QNAME_GSWB_SETTINGS_HISTORY_FILE_PATHNAME);
							String date = file.getAttribute(QNAME_GSWB_SETTINGS_HISTORY_FILE_DATE);
							
							FileHistory fh = new FileHistory(pathname,date);
							history.add(fh);
						}
					}
					
					Collections.sort(history);
				}
			}
		}
	}
	
	public void save()
	{
		File settings = getUserSettingsFile();
		
		try
		{
			if( ! settings.exists() )
				settings.getParentFile().mkdirs();
			
			FileWriter out = new FileWriter(settings);
			
			out.write( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" );
			out.write( "<gswb:settings xmlns=\"org/miv/graphstream\" xmlns:gswb=\"workbench\">\n" );
			
			out.write( String.format( "\t<setting name=\"%s\" value=\"%s\"/>\n", 
					"skin",	lookAndFeel ) );
			out.write( String.format( "\t<setting name=\"%s\" value=\"%s\"/>\n",
					"locale", WGetText.getLocale().getLanguage() + "," + 
					WGetText.getLocale().getCountry() + "," + WGetText.getLocale().getVariant() ));
			
			out.write( "\t<history>\n" );
			for( FileHistory fh : history )
				out.write( String.format( "\t\t<file pathname=\"%s\" date=\"%s\"/>\n", fh.pathname,
						DateFormat.getDateTimeInstance().format(fh.date)) );
			out.write( "\t</history>\n" );
			
			out.write( "</gswb:settings>\n" );
			
			out.flush();
			out.close();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	public static class FileHistory
		implements Comparable<FileHistory>
	{
		String 	pathname;
		Date 	date;
		
		public FileHistory( String pathname, Date date )
		{
			this.pathname = pathname;
			this.date = date;
		}
		
		public FileHistory( String pathname, String date )
		{
			this.pathname = pathname;
			
			try
			{
				this.date = DateFormat.getDateTimeInstance().parse(date);
			}
			catch( Exception e )
			{
				this.date = null;
				e.printStackTrace();
			}
		}
		
		public String getPathName()
		{
			return pathname;
		}
		
		public Date getDate()
		{
			return date;
		}
		
		public int compareTo( FileHistory fh )
		{
			return date.compareTo(fh.date);
		}
	}
}
