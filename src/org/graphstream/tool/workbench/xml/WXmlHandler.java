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
package org.graphstream.tool.workbench.xml;

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
	
	public static WXElement readXml( InputStream in )
	{
		return readXml(null,null,in);
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
