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
package org.miv.graphstream.tool.workbench.cli;

import java.util.Map;
import java.util.HashMap;

import java.util.regex.*;

import org.miv.graphstream.graph.Element;

/**
 * Defines commands which can be executed by a CLI.
 * 
 * @author Guilhelm Savin
 *
 */
public abstract class CLICommand
{
	public static final String PATTERN_ID = "[\\w\\d_-[.]#:]+";
	public static final String PATTERN_KEY = "[\\w\\d_-[.]]+";
	public static final String PATTERN_VAL = "(?:\".+\"|\\d+[.]\\d+f?|\\d+L?)";
	public static final String PATTERN_ATTRIBUTES = "(?: " + PATTERN_KEY + "=" + PATTERN_VAL + ")*";
	public static final String PATTERN_FILE = "[\\w\\d [.]-_\\/\\\\:]+";
	
	public static final String R_OK = "";
	
	static final String createErrorMessage( String message )
	{
		return CLI.ERROR_PREFIX + message;
	}
	
	static final String createWarningMessage( String message )
	{
		return CLI.WARNING_PREFIX + message;
	}
	
	protected Pattern pattern;
	protected Map<String,Integer> attributes;
	protected String usage = "undefined";
	
	public CLICommand( String pattern )
	{
		this.pattern = Pattern.compile( pattern );
		this.attributes = new HashMap<String,Integer>();
	}
	
	public abstract String execute( CLI cli, String cmd );
	
	public CLICommandResult result( String cmd )
	{
		return new CLICommandResult( cmd );
	}
	
	public boolean isValid( String cmd )
	{
		return result( cmd ).isValid();
	}
	
	public String usage()
	{
		return usage;
	}
	/**
	 * 
	 * @param e
	 * @param attributes
	 */
	protected void fillAttributes( Element e, String attributes )
	{
		attributes = attributes.trim();
		
		Matcher m = Pattern.compile( "(" + PATTERN_KEY + ")=(" + PATTERN_VAL + ")" ).matcher( attributes );
		
		while( m.find() )
		{
			String key = m.group(1);
			String val = m.group(2);
			
			if( val.startsWith( "\"" ) && val.endsWith( "\"" ) )
			{
				e.addAttribute( key, val.substring( 1, val.length() - 1 ) );
			}
			else if( val.indexOf(".") != -1 )
			{
				try
				{
					if( val.endsWith( "f" ) )
						e.addAttribute( key, new Float( val ) );
					else
						e.addAttribute( key, new Double( val ) );
				}
				catch( Exception ex )
				{
					e.addAttribute( key, val );
				}
			}
			else
			{
				try
				{
					if( val.endsWith( "L" ) )
						e.addAttribute( key, new Long( val ) );
					else
						e.addAttribute( key, new Integer( val ) );
				}
				catch( Exception ex )
				{
					e.addAttribute( key, val );
				}
			}
		}
	}
	
	class CLICommandResult
	{
		Matcher m;
		
		public CLICommandResult( String str )
		{
			m = pattern.matcher( str );
		}
		
		public boolean isValid()
		{
			return m.matches();
		}
		
		public boolean hasAttribute( String key )
		{
			return attributes.containsKey( key ) && m.group( attributes.get( key ) ) != null;
		}
		
		public String getAttribute( String key )
		{
			if( ! attributes.containsKey( key ) ) return null;
			return m.group( attributes.get( key ) );
		}
	}
}
