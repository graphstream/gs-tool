package org.miv.graphstream.tool.workbench.event;

import org.miv.graphstream.tool.workbench.WAlgorithm;

public interface AlgorithmListener
{
	void algorithmStart( WAlgorithm algo );
	void algorithmError( WAlgorithm algo, String error );
	void algorithmEnd( WAlgorithm algo );
}
