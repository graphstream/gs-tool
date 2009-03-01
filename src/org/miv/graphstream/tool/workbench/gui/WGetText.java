package org.miv.graphstream.tool.workbench.gui;

import org.miv.graphstream.tool.workbench.event.NotificationListener.Notification;

import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class WGetText
{
	private static String lang = "fr";
	private static final String [] langs = { "en", "fr" };
	
	public static String getLang()
	{
		return lang;
	}
	
	public static void setLang( String lang )
	{
		if( ! WGetText.lang.equals(lang) )
		{
			WGetText.lang = lang;
			load();
			System.err.printf( "set lang \"%s\"\n", WGetText.lang );
			WNotificationServer.dispatch(Notification.langChanged);
		}
	}
	
	public static int getLangCount()
	{
		return langs.length;
	}
	
	public static String getLang( int i )
	{
		return langs [i];
	}
	
	/**
	 * An XML handler allowing to filter language of file.
	 * 
	 * For example, with the following part of file :
	 * <code>
	 * &lt;gettext lang="en"&gt;This is a test&lt;/gettext&gt;
	 * &lt;gettext lang="fr"&gt;Ceci est un test&lt;/gettext&gt;
	 * </code>
	 * if the handler language is "fr", only the second block will pass
	 * throught the filter.
	 *
	 */
	public static abstract class GetTextHandler
		extends DefaultHandler
	{
		/**
		 * The GetText tag.
		 */
		public static final String GETTEXT_QNAME = "gettext";
		
		String 	lang;
		int 	jump;
		
		/**
		 * Build a new handler based on default language.
		 */
		public GetTextHandler()
		{
			this(getLang());
		}
		/**
		 * Build a new handler for a specific language.
		 * 
		 * @param lang
		 */
		public GetTextHandler( String lang )
		{
			this.lang = lang.toLowerCase();
			this.jump = 0;
		}
		
		public void startElement (String uri, String localName,
			      String qName, Attributes atts)
			throws SAXException
		{
			if( qName.equals(GETTEXT_QNAME) )
			{
				if( ! lang.equals( atts.getValue("lang").toLowerCase() ) )
					jump++;
			}
			else if( atts.getValue("lang") != null && 
					! atts.getValue("lang").toLowerCase().equals(lang) )
			{
				jump++;
			}
			else if( jump == 0 )
				startElementFiltered(uri,localName,qName,atts);
			else
				jump++;
		}
		
		public void characters(char[] ch, int start, int length)
		{
			if( jump == 0 )
				charactersFiltered(ch,start,length);
		}
		
		public void endElement(String uri, String localName, String qName)
		{
			if( jump > 0 )
				jump--;
			else
				endElementFiltered(uri,localName,qName);
		}
		/**
		 * This is the filtered method link to Handler#startElement().
		 * 
		 * @param uri
		 * @param localName
		 * @param qName
		 * @param atts
		 */
		abstract void startElementFiltered( String uri, String localName,
				String qName, Attributes atts )
			throws SAXException;	
		/**
		 * This is the filtered method link to Handler#characters().
		 * 
		 * @param ch
		 * @param start
		 * @param length
		 */
		abstract void charactersFiltered( char [] ch, int start, int length );
		/**
		 * This is the filtered method link to Handler#endElement().
		 * 
		 * @param uri
		 * @param localName
		 * @param qName
		 */
		abstract void endElementFiltered( String uri, String localName, String qName );
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
	
	static class TextEntriesHandler
		extends GetTextHandler
	{
		boolean 		readingEntry;
		String  		entryName;
		StringBuffer 	entryText;
		
		public TextEntriesHandler( String lang )
		{
			super(lang);
		}
		
		public void startElementFiltered( String uri, String localName,
				String qName, Attributes atts )
			throws SAXException
		{
			if( qName.equals("entry") )
			{
				readingEntry 	= true;
				entryName		= atts.getValue("name");
				entryText		= new StringBuffer();
				
				if( atts.getValue("value") != null )
					entryText.append(atts.getValue("value"));
			}
		}
		
		public void charactersFiltered( char [] ch, int start, int length )
		{
			if( readingEntry )
				entryText.append(ch,start,length);
		}
		
		public void endElementFiltered( String uri, String localName, String qName )
		{
			if( qName.equals("entry") )
			{
				readingEntry = false;
				registerEntry(entryName,entryText.toString());
				entryName = null;
				entryText = null;
			}
		}
	}
	
	private static final String GSWB_GETTEXT_XML = 
		"org/miv/graphstream/tool/workbench/ressources/gswb-gettext.xml";
	
	static void load()
	{
		texts.clear();
		load(getLang());
	}
	
	static void load( String lang )
	{
		XMLReader rx;
		
		try
		{
			rx = XMLReaderFactory.createXMLReader();
			rx.setContentHandler(new TextEntriesHandler(lang));
			rx.parse(new InputSource(ClassLoader.getSystemResourceAsStream(GSWB_GETTEXT_XML)));
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
}
