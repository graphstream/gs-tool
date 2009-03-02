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

import org.miv.graphstream.tool.workbench.Context;

/**
 * Defines some node operations like 'add' or 'del'.
 * 
 * @author Guilhelm Savin
 *
 */
public class NodeOperations extends CLICommand
{
	private static final String PATTERN = "^(add|del) node \"(" + PATTERN_ID + ")\"(" + PATTERN_ATTRIBUTES + ")?$";
	
	static
	{
		CLI.registerCommand( new NodeOperations() );
	}
	
	protected NodeOperations()
	{
		super( PATTERN );
		
		attributes.put( "action", 1 );
		attributes.put( "id", 2 );
		attributes.put( "attributes", 3 );
		
		usage = "{add|del} node \"id\" [attributes]";
	}
	
	@Override
	public String execute(CLI cli, String cmd)
	{
		CLICommandResult ccr = result( cmd );
		if( ! ccr.isValid() ) 	return "bad command";
		if( cli.ctx == null ) 	return "no context";
		if( ! ccr.hasAttribute( "id" ) 
			|| ! ccr.hasAttribute( "action" ) ) 	return usage();
		
		String id = ccr.getAttribute( "id" );
		if( ccr.getAttribute( "action" ).equals( "add" ) )
			cli.ctx.getGraph().addNode( id );
		else
		{
			Context ctx = cli.ctx;
			ctx.getGraph().removeNode( id );
		}
		
		if( ccr.hasAttribute( "attributes" ) )
			fillAttributes( cli.ctx.getGraph().getNode(id), ccr.getAttribute( "attributes" ) );
		
		return R_OK;
	}

}
