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
package org.graphstream.tool.workbench;

import org.graphstream.graph.Graph;
import org.graphstream.graph.GraphFactory;

/**
 * A factory to create context.
 * 
 * @author Guilhelm Savin
 *
 */
public class ContextFactory
{
	/**
	 * The GraphFactory used to create graphs.
	 */
	private static final GraphFactory GRAPH_FACTORY = new GraphFactory();
	
	/**
	 * Create a new context.
	 * Graph will be created using GraphFactory.
	 * 
	 * @param ctxClass context's class
	 * @param gid graph id
	 * @param graphClass graph's class
	 * @return a context
	 */
	public static Context createContext( String ctxClass, String gid, String graphClass )
	{
		return createContext( ctxClass, GRAPH_FACTORY.newInstance( gid, graphClass ) );
	}
	/**
	 * Create a context defined by a given graph.
	 * 
	 * @param ctxClass context's class
	 * @param graph a graph
	 * @return a context defining by <i>graph</i>
	 */
	public static Context createContext( String ctxClass, Graph graph )
	{
		try
		{
			Context ctx = (Context) Class.forName( ctxClass ).newInstance();
			ctx.setGraph( graph );
			
			return ctx;
		}
		catch( InstantiationException e )
		{
			System.err.println( "ContextFactory newInstance InstantiationException : "
			        + e.getMessage() );
		}
		catch( ClassNotFoundException e )
		{
			System.err.println( "ContextFactory newInstance ClassNotFoundException : "
			        + e.getMessage() );
		}
		catch( IllegalAccessException e )
		{
			System.err.println( "ContextFactory newInstance IllegalAccessException : "
			        + e.getMessage() );
		}
		
		return null;
	}
}
