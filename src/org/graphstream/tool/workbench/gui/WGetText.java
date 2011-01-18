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

import org.graphstream.tool.workbench.WNotificationServer;
import org.graphstream.tool.workbench.event.NotificationListener.Notification;
import org.graphstream.tool.workbench.xml.WXElement;
import org.graphstream.tool.workbench.xml.WXmlConstants;
import org.graphstream.tool.workbench.xml.WXmlHandler;

import java.io.InputStream;

import java.util.HashMap;
import java.util.Locale;
import java.util.LinkedList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class WGetText
	implements WXmlConstants
{
	private static final LinkedList<Locale> enabledLocales = new LinkedList<Locale>();
	
	public static String getLang()
	{
		return Locale.getDefault().getLanguage();
	}
	
	public static Locale getLocale()
	{
		return Locale.getDefault();
	}
	
	public static void setLocale( Locale l )
	{
		if( enabledLocales.contains(l) && ! l.equals(Locale.getDefault()) )
		{
			Locale.setDefault(l);
			load();
			
			WNotificationServer.dispatch(Notification.langChanged);
			
			System.err.printf( "set lang \"%s\"\n", getLang() );
		}
	}
	
	public static java.util.Collection<Locale> getEnabledLocales()
	{
		return java.util.Collections.unmodifiableCollection(enabledLocales);
	}
	
	private static final HashMap<String,String> texts = new HashMap<String,String>();
	
	public static String getText( String key )
	{
		if( texts.containsKey(key) )
			return texts.get(key);
		else
			return key;
	}
	
	private static final Pattern getTextPattern = Pattern.compile("^(.*)?@gettext\\(([^\\)]*)\\)(.*)?$" );
	
	public static String getTextLookup( String str )
	{
		Matcher m = getTextPattern.matcher(str);
		
		if( m.matches() )
		{
			
			return 
				( m.group(1) != null ? m.group(1) : "" ) + 
				getText(m.group(2)) + 
				( m.group(3) != null ? m.group(3) : "" );
		}
		else if( texts.containsKey(str) )
		{
			return texts.get(str);
		}
		else
		{
			return str;
		}
	}
	
	private static void registerEntry( String key, String text )
	{
		texts.put(key,text);
	}
	
	static class GetTextChecker
		implements WXmlHandler.WXElementChecker
	{
		public boolean check( WXElement wxe )
		{
			if( wxe.getAttribute(QNAME_GSWB_GETTEXT_LANG) != null )
			{
				if( wxe.getAttribute(QNAME_GSWB_GETTEXT_LANG).equals(getLang()) ) 
					return true;
				else return false;
			}
			else return true;
		}
	}
	
	static class GetTextHandler
		implements WXmlHandler.WXElementHandler
	{
		public void handle( WXElement wxe )
		{
			if( wxe.is( SPEC_GETTEXT_LOCALE ) )
			{
				String language, country, variant;
				Locale l;
				
				language = wxe.getAttribute(QNAME_GSWB_GETTEXT_LOCALE_LANGUAGE);
				country  = wxe.getAttribute(QNAME_GSWB_GETTEXT_LOCALE_COUNTRY);
				variant  = wxe.getAttribute(QNAME_GSWB_GETTEXT_LOCALE_VARIANT);
				
				if( language != null )
				{
					if( country != null && variant != null )
						l = new Locale(language,country,variant);
					else if( country != null )
						l = new Locale(language,country);
					else
						l = new Locale(language);
					
					enabledLocales.add(l);
				}
			}
			else if(( wxe.is(SPEC_GETTEXT_ENTRY)) )
			{
				if( wxe.getAttribute(QNAME_GSWB_GETTEXT_LANG).equals(getLang()) )
					registerEntry( 
						wxe.getAttribute(QNAME_GSWB_GETTEXT_ENTRY_NAME),
						wxe.getAttribute(QNAME_GSWB_GETTEXT_ENTRY_VALUE) );
			}
		}
	}
	
	private static final String GSWB_GETTEXT_XML = 
		"org/miv/graphstream/tool/workbench/xml/gswb-gettext.xml";
	
	public static void load()
	{
		texts.clear();
		enabledLocales.clear();
		
		load(getLang());
	}
	
	static void load( String lang )
	{
		WXmlHandler.readXml( new GetTextHandler(), new GetTextChecker(),
				ClassLoader.getSystemResourceAsStream(GSWB_GETTEXT_XML) );
	}
	
	public static WXElement readGetTextXml( WXmlHandler.WXElementHandler wxHandler,
			InputStream in )
	{
		return WXmlHandler.readXml(wxHandler,new GetTextChecker(),in);
	}
}
