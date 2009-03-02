package org.miv.graphstream.tool.workbench;

import java.io.InputStream;

import org.miv.graphstream.tool.workbench.gui.WGetText;
import org.miv.graphstream.tool.workbench.xml.WXmlConstants;
import org.miv.graphstream.tool.workbench.xml.WXmlHandler;
import org.miv.graphstream.tool.workbench.xml.WXElement;

public class WAlgorithmLoader
	implements WXmlConstants
{
	protected static String ALGORITHMS_XML = "org/miv/graphstream/tool/workbench/xml/gs-algorithms.xml";
	
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
