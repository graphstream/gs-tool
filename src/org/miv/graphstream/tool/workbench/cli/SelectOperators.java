/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package org.miv.graphstream.tool.workbench.cli;

import org.miv.graphstream.graph.Element;

import java.util.Iterator;

public class SelectOperators extends CLICommandPool
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
		
		protected void action( CLI cli, boolean select, 
				Iterator<? extends Element> ite, String pattern )
		{
			if( ite == null ) return;
			
			while( ite.hasNext() )
			{
				Element e = ite.next();
				if( pattern != null )
				{
					if( e.getId().matches( pattern ) )
					{
						if( select )
							cli.core.addElementToSelection( e );
						else
							cli.core.removeElementFromSelection( e );
					}
				}
				else
				{
					if( select )
						cli.core.addElementToSelection( e );
					else
						cli.core.removeElementFromSelection( e );
				}
			}
		}
		
		public String execute( CLI cli, String cmd )
		{
			CLICommandResult ccr = result( cmd );
			if( ! ccr.isValid() ) return usage();
			if( cli.core.getActiveContext() == null ) return "no context";
			
			if( ccr.hasAttribute( "all" ) )
			{
				boolean select = ccr.getAttribute( "action" ).equals( "select" );
				String pattern = ccr.hasAttribute( "id" ) ? ccr.getAttribute( "id" ) : null;
				
				if( ! ccr.hasAttribute( "type" ) || ccr.getAttribute( "type" ).equals( "nodes" ) )
					action( cli, select, 
							cli.getCore().getActiveContext().getGraph().getNodeIterator(), 
							pattern );
				if( ! ccr.hasAttribute( "type" ) || ccr.getAttribute( "type" ).equals( "edges" ) )
					action( cli, select, 
							cli.getCore().getActiveContext().getGraph().getEdgeIterator(), 
							pattern );
				
				return R_OK;
			}
			
			if( ! ccr.hasAttribute( "id" ) ) return "no id given";
			
			Element e = null;
			if( ccr.getAttribute( "type" ).equals( "node" ) )
			{
				e = cli.core.getActiveContext().getGraph().getNode( ccr.getAttribute( "id" ) );
			}
			else
			{
				e = cli.core.getActiveContext().getGraph().getEdge( ccr.getAttribute( "id" ) );
			}
			
			if( e == null ) return "bad id";
			
			if( ccr.getAttribute( "action" ).equals( "select" ) )
				cli.core.addElementToSelection( e );
			else
				cli.core.removeElementFromSelection( e );
			
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
