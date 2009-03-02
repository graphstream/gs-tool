package org.miv.graphstream.tool.workbench.gui;

import org.miv.graphstream.tool.workbench.event.NotificationListener.Notification;
import org.miv.graphstream.tool.workbench.xml.WXElement;
import org.miv.graphstream.tool.workbench.xml.WXmlConstants;
import org.miv.graphstream.tool.workbench.xml.WXmlHandler;

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
	
	private static final Pattern getTextPattern = Pattern.compile("^@gettext\\((.*)\\)$" );
	
	public static String getTextLookup( String str )
	{
		Matcher m = getTextPattern.matcher(str);
		
		if( m.matches() )
		{
			return getText(m.group(1));
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
		"org/miv/graphstream/tool/workbench/ressources/gswb-gettext.xml";
	
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
