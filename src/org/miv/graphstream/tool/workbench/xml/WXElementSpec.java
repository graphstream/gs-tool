/**
 * 
 */
package org.miv.graphstream.tool.workbench.xml;

import java.util.HashMap;
import java.util.HashSet;

public class WXElementSpec
	implements WXmlConstants
{
	private static final HashMap<String,WXElementSpec> specs = new HashMap<String,WXElementSpec>();
	
	public static WXElementSpec getSpecifications( String qName )
	{
		return specs.get(qName);
	}
	
	String qName;
	
	HashMap<String,Boolean> attributes;
	HashSet<WXElementSpec>	children;
	
	public WXElementSpec( String qName )
	{
		this.qName	= qName;
		attributes 	= new HashMap<String,Boolean>();
		children	= new HashSet<WXElementSpec>();
		
		specs.put(qName,this);
	}
	
	public WXElementSpec declareAttributes( String ... qNames )
	{
		for( String qName : qNames )
			attributes.put( qName, true );
		
		return this;
	}
	
	public WXElementSpec declareOptionnalAttributes( String ... qNames )
	{
		for( String qName : qNames )
			attributes.put( qName, false );
		
		return this;
	}
	
	public WXElementSpec declareChildren( WXElementSpec ... specs )
	{
		for( WXElementSpec spec : specs )
			children.add( spec );
		
		return this;
	}
}