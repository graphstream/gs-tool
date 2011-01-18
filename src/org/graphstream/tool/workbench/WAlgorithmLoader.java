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

import java.io.InputStream;

import org.graphstream.tool.workbench.gui.WGetText;
import org.graphstream.tool.workbench.xml.WXElement;
import org.graphstream.tool.workbench.xml.WXmlConstants;
import org.graphstream.tool.workbench.xml.WXmlHandler;

public class WAlgorithmLoader
	implements WXmlConstants
{
	protected static String ALGORITHMS_XML = "org/miv/graphstream/tool/workbench/xml/gswb-algorithms.xml";
	
	static class AlgorithmHandler
		implements WXmlHandler.WXElementHandler
	{
		public void handle( WXElement wxe )
		{
			if( wxe.is(SPEC_ALGORITHM) )
			{
				String clazz, name, category,description;
				WXElement [] parameters;
				
				clazz 		= wxe.getAttribute(QNAME_GSWB_ALGORITHMS_ALGORITHM_CLASS);
				name  		= wxe.getAttribute(QNAME_GSWB_ALGORITHMS_ALGORITHM_NAME);
				category 	= wxe.getAttribute(QNAME_GSWB_ALGORITHMS_ALGORITHM_CATEGORY);
				description = wxe.getChild(SPEC_ALGORITHM_DESCRIPTION).getContent();
				parameters  = wxe.getChildren(SPEC_ALGORITHM_PARAMETER);
				
				if( name == null )
					name = "unknown";
				if( category == null )
					category = "default";
				
				WAlgorithm algorithm = new WAlgorithm( clazz, name, category );
				algorithm.setDescription(description);
				
				if( parameters != null )
				{
					for( WXElement param : parameters )
					{
						String pname, ptype, pdef;
						
						pname = param.getAttribute(QNAME_GSWB_ALGORITHMS_ALGORITHM_PARAMETER_NAME);
						ptype = param.getAttribute(QNAME_GSWB_ALGORITHMS_ALGORITHM_PARAMETER_TYPE);
						pdef  = param.getAttribute(QNAME_GSWB_ALGORITHMS_ALGORITHM_PARAMETER_DEFAULT);
						
						if( pdef == null )
							algorithm.addParameter(pname,ptype);
						else
							algorithm.addParameter(pname,ptype,pdef);
					}
				}
				
				WAlgorithm.register(algorithm);
			}
		}
	}
	
	public static void load()
	{
		WAlgorithm.ALGORITHMS.clear();
		
		InputStream systemAlgorithms = ClassLoader.getSystemResourceAsStream(ALGORITHMS_XML);
		if( systemAlgorithms == null )
			System.err.printf( "can not locate ressource: %s\n", ALGORITHMS_XML );
		else
			load(systemAlgorithms);
	}
	
	public static void load( InputStream src )
	{
		WGetText.readGetTextXml( new AlgorithmHandler(),src);
	}
}
