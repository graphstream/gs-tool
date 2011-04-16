package org.graphstream.tool;

public interface ToolsCommon {
	/**
	 * Type of generator used.
	 * 
	 */
	public static enum GeneratorType {
		PREFERENTIAL_ATTACHMENT, DOROGOVTSEV_MENDES, GRID, INCOMPLETE_GRID, RANDOM, RANDOM_EUCLIDEAN, RANDOM_FIXED_DEGREE_DYNAMIC_GRAPH, FULL, POINTS_OF_INTEREST
	}

	public static enum SinkFormat implements Optionable {
		DGS(true), TIKZ(false, "width", "height", "stylesheet", "layout"), DOT(true), GML(
				false), SVG(false), IMAGES(true, "prefix", "outputType",
				"outputPolicy", "resolution", "layoutPolicy", "quality",
				"stylesheet")

		;

		private boolean dynamic;
		private String[] options;

		private SinkFormat(boolean dynamic, String... options) {
			this.dynamic = dynamic;
			this.options = options;
		}

		public boolean hasDynamicSupport() {
			return dynamic;
		}

		public boolean isValidOption(String key) {
			if (options == null)
				return false;

			for (int i = 0; i < options.length; i++)
				if (options[i].equals(key))
					return true;

			return false;
		}

		public int getOptionCount() {
			if (options == null)
				return 0;
			return options.length;
		}

		public String getOption(int i) {
			if (options == null || i >= options.length || i < 0)
				return null;

			return options[i];
		}
	}

	public static enum SourceFormat implements Optionable {
		DGS(true), GML(false), DOT(false)

		;

		private String[] options;

		private SourceFormat(boolean dynamic, String... options) {
			this.options = options;
		}

		public boolean isValidOption(String key) {
			if (options == null)
				return false;

			for (int i = 0; i < options.length; i++)
				if (options[i].equals(key))
					return true;

			return false;
		}

		public int getOptionCount() {
			if (options == null)
				return 0;
			return options.length;
		}

		public String getOption(int i) {
			if (options == null || i >= options.length || i < 0)
				return null;

			return options[i];
		}
	}
	
	public static interface Optionable {
		int getOptionCount();
		String getOption(int i);
		boolean isValidOption(String key);
	}
}
