/**
 * 
 */
package org.graphstream.tool.workbench.xml;

import java.util.Iterator;
import java.util.HashMap;
import java.util.LinkedList;

public class WXElement
{
	public static class Attribute
	{
		String name;
		String value;
		
		public Attribute( String name, String value )
		{
			this.name = name;
			this.value = value;
		}
	}
	
	public class WXElementIterator
		implements Iterator<WXElement>
	{
		int index;
		WXElementIterator childIterator;
		
		public WXElementIterator()
		{
			index 			= -1;
			childIterator 	= null;
		}
		
		public boolean hasNext()
		{
			if( index == -1 )
				return true;
			
			if( childIterator != null && childIterator.hasNext() )
				return true;
			
			if( index < children.size() )
				return true;
			
			return false;
		}
		
		public void remove()
		{}
		
		public WXElement next()
		{
			if( index == -1 )
			{
				index = 0;
				return WXElement.this;
			}
			
			if( childIterator != null )
			{
				if( childIterator.hasNext() )
					return childIterator.next();
				else if(  index < children.size() )
				{
					childIterator = children.get(index++).iterator();
					return childIterator.next();
				}
				else
					return null;
			}
			else
			{
				if( index < children.size() )
				{
					childIterator = children.get(index++).iterator();
					return childIterator.next();
				}
				else
					return null;
			}
		}
		
		public void skipCurrentChildren()
		{
			if( index == 0 && childIterator == null )
			{
				index = children.size();
			}
			else if( childIterator != null )
			{
				childIterator.skipCurrentChildren();	
			}
		}
	}
	
	WXElementSpec 				spec;
	
	HashMap<String,Attribute> 	attributes;
	LinkedList<WXElement>		children;
	
	StringBuffer				content;
	
	public WXElement( String qName )
	{
		spec = WXElementSpec.getSpecifications(qName);
		
		if( spec == null )
		{
			System.err.printf( "can not find spec for \"%s\"\n", qName );
		}
		
		attributes 	= new HashMap<String,Attribute>();
		children	= new LinkedList<WXElement>();
		content		= new StringBuffer();
	}
	
	public void addAttribute( String name, String value )
	{
		attributes.put( name, new Attribute(name,value) );
	}
	
	public void addChild( WXElement child )
	{
		children.addLast(child);
	}
	
	public void addContent( char [] ch, int offset, int length )
	{
		content.append(ch,offset,length);
	}
	
	public String getContent()
	{
		return content.toString();
	}
	
	public String getAttribute( String key )
	{
		if( ! attributes.containsKey(key) )
			return null;
		
		return attributes.get(key).value;
	}
	
	public String getQName()
	{
		return spec.qName;
	}
	
	public WXElement getChild( WXElementSpec spec )
	{
		if( children.isEmpty() )
			return null;
		
		for( int i = 0; i < children.size(); i++ )
		{
			if( children.get(i).is(spec) )
				return children.get(i);
		}
		
		return null;
	}
	
	public WXElement [] getChildren( WXElementSpec spec )
	{
		WXElement [] r = new WXElement [0];
		LinkedList<WXElement> tmp = new LinkedList<WXElement>();
		
		for( int i = 0; i < children.size(); i++ )
		{
			if( children.get(i).is(spec) )
				tmp.addLast( children.get(i) );
		}
		
		return tmp.toArray(r);
	}
	
	public boolean is( WXElementSpec spec )
	{
		return this.spec != null && this.spec.qName.equals(spec.qName);
	}
	
	public WXElementIterator iterator()
	{
		return new WXElementIterator();
	}
	
	public Iterator<WXElement> iteratorOnChildren()
	{
		return children.iterator();
	}
}