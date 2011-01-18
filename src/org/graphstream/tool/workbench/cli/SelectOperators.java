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

import java.util.Iterator;

public class SelectOperators
	extends CLICommandPool
{
	public static final String PATTERN = "^((select|unselect) (all ?)?(nodes?|edges?)?|show selection).*$";
	
	static
	{
		CLI.registerCommand( new SelectOperators() );
	}
	
	protected SelectOperators()
	{
		super( PATTERN );
		
		subcommands.add( new Select() );
		subcommands.add( new Show() );
	}
	
	class Select extends CLICommand
	{
		public Select()
		{
			super( "^((?:un)?select) (all ?)?(nodes?|edges?)?(?: \"(.*)\")?$" );
			
			attributes.put( "action", 1 );
			attributes.put( "all", 2 );
			attributes.put( "type", 3 );
			attributes.put( "id",   4 );
			
			usage = "select [all] {node[s],edge[s]} [\"id|pattern\"]";
		}
		
		protected void action( Context ctx, boolean select, 
				Iterator<? extends Element> ite, String pattern )
		{
			if( ite == null ) 
			{
				if( select )
					ctx.getSelection().select();
				else
					ctx.getSelection().unselect();
			}
			else if( pattern == null )
			{
				if( select )
					ctx.getSelection().select( ite );
				else
					ctx.getSelection().unselect( ite );
			}
			else
			{
				if( select )
					ctx.getSelection().select( ite, pattern );
				else
					ctx.getSelection().unselect( ite, pattern );
			}
		}
		
		public String execute( CLI cli, String cmd )
		{
			CLICommandResult ccr = result( cmd );
			if( ! ccr.isValid() ) return usage();
			
			Context ctx = cli.core.getActiveContext();
			
			if( ctx == null )
				return "no context";
			
			if( ccr.hasAttribute( "all" ) )
			{
				boolean select = ccr.getAttribute( "action" ).equals( "select" );
				String pattern = ccr.hasAttribute( "id" ) ? ccr.getAttribute( "id" ) : null;
				
				if( ! ccr.hasAttribute( "type" ) || ccr.getAttribute( "type" ).equals( "nodes" ) )
					action( ctx, select, 
							ctx.getGraph().getNodeIterator(), 
							pattern );
				if( ! ccr.hasAttribute( "type" ) || ccr.getAttribute( "type" ).equals( "edges" ) )
					action( ctx, select, 
							ctx.getGraph().getEdgeIterator(), 
							pattern );
				
				return R_OK;
			}
			
			if( ! ccr.hasAttribute( "id" ) )
				return "no id given";
			
			Element e = null;
			if( ccr.getAttribute( "type" ).equals( "node" ) )
			{
				e = ctx.getGraph().getNode( ccr.getAttribute( "id" ) );
			}
			else
			{
				e = ctx.getGraph().getEdge( ccr.getAttribute( "id" ) );
			}
			
			if( e == null ) return "bad id";
			
			if( ccr.getAttribute( "action" ).equals( "select" ) )
				ctx.getSelection().select(e);
			else
				ctx.getSelection().unselect(e);
			
			return R_OK;
		}
	}
	
	class Show extends CLICommand
	{
		public Show()
		{
			super( "^show selection$" );
			
			usage = "show selection";
		}
		
		public String execute( CLI cli, String cmd )
		{
			CLICommandResult ccr = result( cmd );
			if( ! ccr.isValid() ) return usage();
			if( cli.core.getActiveContext() == null ) return "no context";
			
			StringBuffer buffer = new StringBuffer();
			
			for( Element e: cli.core.getActiveContext().getSelection() )
				buffer.append( e.getId() ).append( "\n" );
			
			return buffer.toString();
		}
	}
}
