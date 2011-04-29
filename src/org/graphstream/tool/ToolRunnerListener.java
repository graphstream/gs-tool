package org.graphstream.tool;

public interface ToolRunnerListener {
	void executionStart(Tool t);
	void initializationFailed(Tool t, ToolInitializationException e);
	void executionFailed(Tool t, ToolExecutionException e);
	void executionSuccess(Tool t);
}
