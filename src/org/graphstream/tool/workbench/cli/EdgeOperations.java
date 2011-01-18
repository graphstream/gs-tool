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
			if( ! ccr.hasAttribute( "src" ) || ! ccr.hasAttribute( "dst" ) )
				return usage();
			
			Element e = cli.ctx.getGraph().addEdge( id, 
					ccr.getAttribute( "src" ), 
					ccr.getAttribute( "dst" ), 
					ccr.hasAttribute( "directed" ) );
			cli.ctx.getHistory().registerAddEdgeAction(e);
			
			if( ccr.hasAttribute( "attributes" ) )
				fillAttributes( cli.ctx.getGraph().getEdge(id), ccr.getAttribute( "attributes" ) );
		}
		else
		{
			Element e = cli.ctx.getGraph().removeEdge(id);
			cli.ctx.getHistory().registerDelEdgeAction(e);
		}
		
		return R_OK;
	}

}
