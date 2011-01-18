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
package org.graphstream.tool.workbench.cli;

import org.graphstream.graph.Element;
import org.graphstream.tool.workbench.Context;
import org.graphstream.ui.graphicGraph.GraphicGraph;

import java.awt.Color;
import java.util.Iterator;
import java.util.regex.*;

/**
 * Commands to handle action on element objects.
 * Allow to set or unset attribute for an element or a set of elements
 * by specifying a pattern id.
 * 
 * @author Guilhelm Savin
 *
 */
public class ElementOperators extends CLICommandPool
{
	/**
	 * Pattern for the whole pool.
	 */
	public static final String PATTERN = "^(un)?set attribute.*";
	/**
	 * Pattern for the SetCommand.
	 */
	public static final String CMD_SET_ATTRIBUTE = 
		"^set attribute \"(.*)\" (.*) for (nodes?|edges?)(?: \"(.*)\")?(?: on graph (?:\"(.*)\"|(\\d+)))?$";
	/**
	 * Pattern for the UnsetCommand.
	 */
	public static final String CMD_DEL_ATTRIBUTE = 
		"^unset attribute \"(.*)\" for (nodes?|edges?)(?: \"(.*)\")?(?: on graph (?:\"(.*)\"|(\\d+)))?$";
	
	protected static final String COLOR_PATTERN = "color\\((.*)\\)";
	protected static final String INTEGER_PATTERN = "\\d+";
	protected static final String LONG_PATTERN = INTEGER_PATTERN + "L";
	protected static final String DOUBLE_PATTERN = "\\d+[.]\\d+";
	protected static final String FLOAT_PATTERN = DOUBLE_PATTERN + "f";
	
	// Register command in CLI.
	static
	{
		CLI.registerCommand( new ElementOperators() );
	}
	
	/**
	 * Constructor of this commands pool.
	 *
	 */
	protected ElementOperators()
	{
		super( PATTERN );
		
		subcommands.add( new SetAttributeCommand() );
		subcommands.add( new UnsetAttributeCommand() );
	}
	/**
	 * Transform a string into an object.
	 * 
	 * @param val string representation of the object
	 * @return corresponding object
	 */
	protected Object parseValue( String val )
	{
		if( val.startsWith( "\"" ) && val.endsWith( "\"" ) )
			return val.substring( 1, val.length() - 2 );
		
		if( val.matches( COLOR_PATTERN ) )
			return parseColor( val );
		if( val.matches( INTEGER_PATTERN ) )
			return new Integer( val );
		if( val.matches( LONG_PATTERN ) )
			return new Long( val );
		if( val.matches( DOUBLE_PATTERN ) )
			return new Double( val );
		if( val.matches( FLOAT_PATTERN ) )
			return new Float( val );
		
		return val;
	}
	/**
	 * Transform a string into a color.
	 * 
	 * @param val string color representation
	 * @return a color
	 */
	protected Color parseColor( String val )
	{
		Matcher m = Pattern.compile( COLOR_PATTERN ).matcher( val );
		if( ! m.matches() ) return null;
		
		return GraphicGraph.convertColor( m.group(1) );
	}
	
// Subcommands
	
	/**
	 * Command used to set attribute of elements.
	 * 
	 * @author Guilhelm Savin
	 */
	class SetAttributeCommand extends CLICommand
	{
		public SetAttributeCommand()
		{
			super( CMD_SET_ATTRIBUTE );
			
			attributes.put( "key", 1 );
			attributes.put( "value", 2 );
			attributes.put( "type", 3 );
			attributes.put( "id", 4 );
			attributes.put( "graphid", 5 );
			attributes.put( "graphindex", 6 );
			
			usage = "set attribute \"<i>key</i>\" <i>value</i> for {node[s]|edge[s]} \"<i>id</i>[|<i>pattern</i>]\"";
		}
		
		@Override
		public String execute( CLI cli, String cmd )
		{
			CLICommandResult ccr = result( cmd );
			if( ! ccr.isValid() ) return usage();
			
			Object value = parseValue( ccr.getAttribute( "value" ) );
			String key = ccr.getAttribute( "key" );
			Context ctx = null;
			String id = ccr.hasAttribute( "id" ) ? ccr.getAttribute( "id" ) : null;
			
			if( ccr.hasAttribute( "graphid" ) || ccr.hasAttribute( "graphindex" ) )
			{
				if( ccr.hasAttribute( "graphid" ) )
					ctx = cli.core.getContext( ccr.getAttribute( "graphid" ) );
				else
					ctx = cli.core.getContext( new Integer(ccr.getAttribute( "graphindex" ) ) );
			}
			else
			{
				ctx = cli.getCore().getActiveContext();
			}
			
			if( ctx == null ) return createErrorMessage( "no context" );
			
			if( ccr.getAttribute( "type" ).endsWith( "s" ) )
			{
				Iterator<? extends Element> ite;
				if( ccr.getAttribute( "type" ).equals( "nodes" ) )
					ite = ctx.getGraph().getNodeIterator();
				else
					ite = ctx.getGraph().getEdgeIterator();
				
				if( ite == null ) return createWarningMessage( "no element" );
				
				while( ite.hasNext() )
				{
					Element e = ite.next();
					
					if( id == null ||  e.getId().matches( id ) )
						e.setAttribute( key, value );
				}
			}
			else
			{
				if( id == null ) return usage();
				
				Element e = null;
				if( ccr.getAttribute( "type" ).equals( "node" ) )
					e = ctx.getGraph().getNode( id );
				else
					e = ctx.getGraph().getEdge( id );
				
				if( e == null ) return createWarningMessage( "no element found" );
				
				e.setAttribute( key, value );
			}
			
			return R_OK;
		}
	}
	/**
	 * Command used to unset attribute of elements.
	 * 
	 * @author Guilhelm Savin
	 *
	 */
	class UnsetAttributeCommand extends CLICommand
	{
		public UnsetAttributeCommand()
		{
			super( CMD_DEL_ATTRIBUTE );
			
			attributes.put( "key", 1 );
			attributes.put( "type", 2 );
			attributes.put( "id", 3 );
			attributes.put( "graphid", 4 );
			attributes.put( "graphindex", 5 );
			
			usage = "unset attribute \"<i>key</i>\" <i>value</i> for {node[s]|edge[s]} \"<i>id</i>[|<i>pattern</i>]\"";
		}
		
		@Override
		public String execute( CLI cli, String cmd )
		{
			CLICommandResult ccr = result( cmd );
			if( ! ccr.isValid() ) return usage();
			
			String key = ccr.getAttribute( "key" );
			Context ctx = null;
			String id = ccr.hasAttribute( "id" ) ? ccr.getAttribute( "id" ) : null;
			
			if( ccr.hasAttribute( "graphid" ) || ccr.hasAttribute( "graphindex" ) )
			{
				if( ccr.hasAttribute( "graphid" ) )
					ctx = cli.core.getContext( ccr.getAttribute( "graphid" ) );
				else
					ctx = cli.core.getContext( new Integer(ccr.getAttribute( "graphindex" ) ) );
			}
			else
			{
				ctx = cli.getCore().getActiveContext();
			}
			
			if( ctx == null ) return createErrorMessage( "no context" );
			
			if( ccr.getAttribute( "type" ).endsWith( "s" ) )
			{
				Iterator<? extends Element> ite;
				if( ccr.getAttribute( "type" ).equals( "nodes" ) )
					ite = ctx.getGraph().getNodeIterator();
				else
					ite = ctx.getGraph().getEdgeIterator();
				
				if( ite == null ) return createWarningMessage( "no element" );
				
				while( ite.hasNext() )
				{
					Element e = ite.next();
					
					if( id == null ||  e.getId().matches( id ) )
						e.removeAttribute( key );
				}
			}
			else
			{
				if( id == null ) return usage();
				
				Element e = null;
				if( ccr.getAttribute( "type" ).equals( "node" ) )
					e = ctx.getGraph().getNode( id );
				else
					e = ctx.getGraph().getEdge( id );
				
				if( e == null ) return createWarningMessage( "no element found" );
				
				e.removeAttribute( key );
			}
			
			return R_OK;
		}
	}
}
