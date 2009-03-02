package org.miv.graphstream.tool.workbench.xml;

import java.io.InputStream;

import java.util.LinkedList;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class WXmlHandler
	extends DefaultHandler
{
	public static interface WXElementHandler
	{
		void handle( WXElement wxe );
	}
	
	public static interface WXElementChecker
	{
		boolean check( WXElement wxe );
	}
	
	LinkedList<WXElement> 	elements;
	WXElement				root;
	WXElementHandler		wxHandler;
	WXElementChecker		wxChecker;
	int 					jump;
	
	public WXmlHandler( WXElementHandler wxHandler, WXElementChecker wxChecker )
	{
		this( wxHandler );
		
		this.wxChecker = wxChecker;
	}
	
	public WXmlHandler( WXElementHandler wxHandler )
	{
		elements = new LinkedList<WXElement>();
		
		this.wxHandler 	= wxHandler;
		this.wxChecker 	= null;
		this.jump		= 0;
	}
	
	public void startElement( String uri, String localName,
			String qName, Attributes atts )
		throws SAXException
	{
		if( jump > 0 )
		{
			jump++;
		}
		else
		{
			WXElement e = new WXElement(qName);
		
			for( int i = 0; i < atts.getLength(); i++ )
				e.addAttribute(atts.getQName(i), atts.getValue(i));
		
			if( wxChecker != null && ! wxChecker.check(e) )
			{
				jump++;
			}
			else
			{
				if( root == null )
					root = e;
				
				if( elements.size() > 0 )
					elements.peek().addChild(e);
				
				elements.addFirst(e);
			}
		}
	}
	
	public void characters( char [] ch, int start, int length )
	{
		if( jump == 0 )
			elements.peek().addContent(ch,start,length);
	}
	
	public void endElement( String uri, String localName, String qName )
	{
		if( jump > 0 )
		{
			jump--;
		}
		else
		{
			WXElement wxe = elements.poll();
		
			if( wxHandler != null )
				wxHandler.handle(wxe);
		}
	}
	
	public InputSource resolveEntity( String publicId, String systemId )
		throws SAXException, java.io.IOException
	{
		if( systemId.startsWith("jar:") )
		{
			return new InputSource( ClassLoader.getSystemResourceAsStream(
					systemId.substring("jar:".length())));
		}
		
		return super.resolveEntity(publicId, systemId);
	}
	
	public static WXElement readXml( WXElementHandler wxHandler, InputStream in )
	{
		return readXml( wxHandler, null, in );
	}
	
	public static WXElement readXml( WXElementChecker wxChecker, InputStream in )
	{
		return readXml( null, wxChecker, in );
	}

	@SuppressWarnings("unused")
	public static WXElement readXml( WXElementHandler wxHandler, 
			WXElementChecker wxChecker, InputStream in )
	{
		WXElementSpec __used_to_load__ = WXElementSpec.SPEC_ALGORITHM;
		
		XMLReader rx;
		WXmlHandler handler = new WXmlHandler(wxHandler,wxChecker);
		
		try
		{
			rx = XMLReaderFactory.createXMLReader();
			rx.setContentHandler(handler);
			rx.setEntityResolver(handler);
			rx.parse(new InputSource(in));
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		
		return handler.root;
	}
}
