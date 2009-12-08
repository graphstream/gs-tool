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
