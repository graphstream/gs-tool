package org.miv.graphstream.tool.workbench;

import java.io.InputStream;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class WAlgorithmLoader
{
	protected static String ALGORITHMS_XML = "org/miv/graphstream/tool/workbench/gs-algorithms.xml";
	
	static class AlgorithmHandler extends DefaultHandler
	{
		boolean rooted = false;
		WAlgorithm current = null;
		StringBuffer desc;
		boolean readingDesc = false;
		
		public AlgorithmHandler()
		{
		}
		
		public void startElement (String uri, String localName,
			      String qName, Attributes atts)
			throws SAXException
		{
			if( readingDesc )
			{
				desc.append("<" + qName );
				for( int i = 0; i < atts.getLength(); i++ )
				{
					desc.append( String.format( " %s=\"%s\"", atts.getQName(i), atts.getValue(i) ));
				}
				desc.append(">");
			}
			else if( qName.equals("gs-algorithms") )
				rooted = true;
			else if( qName.equals("algorithm") )
			{
				if( ! rooted )
					throw new SAXException( "not rooted" );
				if( current != null )
					throw new SAXException( "algorithm in an algorithm !" );
				
				String clazz, name, category;
				clazz = atts.getValue("class");
				name  = atts.getValue("name");
				category = atts.getValue("category");
				
				if( clazz == null )
					throw new SAXException( "need a class for an algorithm" );
				
				if( name == null )
					name = "unknown";
				if( category == null )
					category = "default";
				
				current = new WAlgorithm( clazz, name, category );
				desc    = new StringBuffer();
			}
			else if( qName.equals("parameter") )
			{
				if( ! rooted )
					throw new SAXException( "not rooted" );
				if( current == null )
					throw new SAXException( "not in an algorithm" );
				
				String name;
				Class<?> type;
				
				name = atts.getValue("name");
				try
				{
					String t = atts.getValue("type");
					
					if( t.equals("int") )
						type = Integer.TYPE;
					else if( t.equals("float") )
						type = Float.TYPE;
					else if( t.equals("double") )
						type = Double.TYPE;
					else if( t.equals("long") )
						type = Long.TYPE;
					else if( t.equals("boolean") )
						type = Boolean.TYPE;
					else if( t.equals("char") )
						type = Character.TYPE;
					else
						type = Class.forName(t);
				}
				catch( Exception e )
				{
					e.printStackTrace();
					return;
				}
				
				current.addParameter(name,type);
			}
			else if( qName.equals("description") )
				readingDesc = true;
		}
		
		public void characters(char[] ch, int start, int length)
		{
			if( readingDesc )
				desc.append(ch,start,length);
		}
		
		public void endElement(String uri, String localName, String qName)
		{
			if( readingDesc && ! qName.equals("description") )
			{
					desc.append("</" + qName + ">");
			}
			else if( qName.equals("algorithm") )
			{
				WAlgorithm.register(current);
				
				current = null;
				desc    = null;
			}
			else if( qName.equals("description") )
			{
				if( current == null )
					return;
				
				current.setDescription(desc.toString());
				readingDesc = false;
			}
		}
	}
	
	public static void load()
	{
		WAlgorithm.ALGORITHMS.clear();
		
		InputStream systemAlgorithms = ClassLoader.getSystemResourceAsStream(ALGORITHMS_XML);
		if( systemAlgorithms == null )
			System.err.printf( "can not locate ressource: %s\n", ALGORITHMS_XML );
		else
			load(systemAlgorithms);
	}
	
	public static void load( InputStream src )
	{
		XMLReader rx;
		
		try
		{
			rx = XMLReaderFactory.createXMLReader();
			rx.setContentHandler(new AlgorithmHandler());
			rx.parse(new InputSource(src));
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
}
