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

/**
 * Defines some edge operations like 'add' or 'del'.
 * 
 * @author Guilhelm Savin
 *
 */
public class EdgeOperations extends CLICommand
{
	private static final String PATTERN = "^(add|del)( directed)? edge \"(" + PATTERN_ID + ")\"(?: \"(" + PATTERN_ID + ")\" \"(" + PATTERN_ID + ")\")?(" + PATTERN_ATTRIBUTES + ")$";
	
	static
	{
		CLI.registerCommand( new EdgeOperations() );
	}
	
	protected EdgeOperations()
	{
		super( PATTERN );
		
		attributes.put( "action", 1 );
		attributes.put( "directed", 2 );
		attributes.put( "id", 3 );
		attributes.put( "src", 4 );
		attributes.put( "dst", 5 );
		attributes.put( "attributes", 6 );
		
		usage = "{add|del} [directed] edge \"<i>id</i>\" [\"<i>id<sub>n1</sub></i>\" \"<i>id<sub>n2</sub></i>\"] [attributes]";
	}
	
	@Override
	public String execute(CLI cli, String cmd)
	{
		CLICommandResult ccr = result( cmd );
		if( ! ccr.isValid() ) 	return createErrorMessage( "bad command" );
		if( cli.ctx == null ) 	return createErrorMessage( "no context" );
		
		String id = ccr.getAttribute( "id" );
		if( ccr.getAttribute( "action" ).equals( "add" ) )
		{
			if( ! ccr.hasAttribute( "src" ) || ! ccr.hasAttribute( "dst" ) ) return usage();
			cli.ctx.getGraph().addEdge( id, ccr.getAttribute( "src" ), ccr.getAttribute( "dst" ), 
					ccr.hasAttribute( "directed" ) );
			if( ccr.hasAttribute( "attributes" ) )
				fillAttributes( cli.ctx.getGraph().getEdge(id), ccr.getAttribute( "attributes" ) );
		}
		else
		{
			cli.ctx.getGraph().removeEdge(id);
		}
		
		return R_OK;
	}

}
