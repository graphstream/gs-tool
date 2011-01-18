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

import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.Iterator;

/**
 * Operators to list nodes or edges.
 * User can give a pattern to match id with.
 * 
 * @author Guilhelm Savin
 *
 */
public class ListOperators extends CLICommand
{
	public static final String PATTERN = "^list (nodes?|edges?)?(?: \"(.*)\")?$";
	
	static
	{
		CLI.registerCommand( new ListOperators() );
	}
	
	protected ListOperators()
	{
		super( PATTERN );
		
		attributes.put( "type", 1 );
		attributes.put( "pattern", 2 );
		
		usage = "list [node[s]|edge[s]] \"[<i>pattern</i>|<i>id</i>]\"";
	}
	
	@Override
	public String execute(CLI cli, String cmd)
	{
		CLICommandResult ccr = result( cmd );
		if( ! ccr.isValid() ) return usage();
		if( ! ccr.hasAttribute( "type" ) && ! ccr.hasAttribute( "pattern" ) ) return usage();
		
		if( cli.getCore().getActiveContext() == null )
			return createErrorMessage( "no context" );
		
		if( ccr.hasAttribute( "type" ) )
		{
			if( ! ccr.getAttribute( "type" ).endsWith( "s" ) )
				return listAttributes( cli, ccr );
			else if( ccr.getAttribute( "type" ).equals( "nodes" ) )
				return listNodes( cli, ccr );
			else
				return listEdges( cli, ccr );
		}
		
		return listElements( cli, ccr );
	}

	protected String listAttributes( CLI cli, CLICommandResult ccr )
	{
		Graph g = cli.core.getActiveContext() == null ? null : 
			cli.core.getActiveContext().getGraph();
		
		StringBuffer buffer = new StringBuffer();
		Element e = null;
		if( ( e = g.getNode( ccr.getAttribute( "pattern" ) ) ) != null )
		{
			buffer.append( "node" );
		}
		else if( ( e = g.getEdge( ccr.getAttribute( "pattern" ) ) ) != null )
		{
			buffer.append( "edge" );
		}
		
		if( e == null ) return createWarningMessage( "no element found" );
		
		buffer.append( " \"" + e.getId() + "\"\n" );
		
		Iterator<String> ite = e.getAttributeKeyIterator();
		if( ite!= null )
		{
			int s = 0;
			while( ite.hasNext() )
			{
				s = Math.max( s, ite.next().length() );
			}
			ite = e.getAttributeKeyIterator();
			while( ite.hasNext() )
			{
				String key = ite.next();
				buffer.append( String.format( " %" + s + "s = \"%s\"\n", 
						key, e.getAttribute( key ) ) );
			}
		}
		else buffer.append( " no attributes" );
		
		return buffer.toString();
	}
	
	protected String listElements( CLI cli, CLICommandResult ccr )
	{
		return listNodes( cli, ccr ) + "\n" + listEdges( cli, ccr );
	}
	
	protected String listNodes( CLI cli, CLICommandResult ccr )
	{
		Iterator<? extends Node> ite = 
			cli.core.getActiveContext().getGraph().getNodeIterator();
		
		if( ite == null ) return createWarningMessage( "no node" );
		
		StringBuffer buffer = new StringBuffer();
		int count = 0;
		String pattern = ccr.getAttribute( "pattern" );
		while( ite.hasNext() )
		{
			Node n = ite.next();
			if( pattern == null || n.getId().matches( pattern ) )
			{
				count++;
				buffer.append( " " ).append( n.getId() ).append( "\n" );
			}
		}
		if( count == 0 ) return createWarningMessage( "no matching node" );
		
		return count + " nodes\n" + buffer.toString();
	}
	
	protected String listEdges( CLI cli, CLICommandResult ccr )
	{
		Iterator<? extends Edge> ite = 
			cli.core.getActiveContext().getGraph().getEdgeIterator();
		
		if( ite == null ) return createWarningMessage( "no edge" );
		
		StringBuffer buffer = new StringBuffer();
		int count = 0;
		String pattern = ccr.getAttribute( "pattern" );
		while( ite.hasNext() )
		{
			Edge n = ite.next();
			if( pattern == null || n.getId().matches( pattern ) )
			{
				count++;
				buffer.append( " " ).append( n.getId() ).append( "\n" );
			}
		}
		if( count == 0 ) return createWarningMessage( "no matching edge" );
		
		return count + " edges\n" + buffer.toString();
	}
}
