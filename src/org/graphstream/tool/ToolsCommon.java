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
package org.graphstream.tool;

/**
 * Common types and constants used in tools.
 */
public interface ToolsCommon {

	/**
	 * Defined an object that can be producing according to a set of options.
	 * 
	 */
	public static interface Optionable {
		/**
		 * Get the amount of options.
		 * 
		 * @return options count
		 */
		int getOptionCount();

		/**
		 * Get the key of an option.
		 * 
		 * @param i
		 *            index of the option
		 * @return the key associated to the index or null if invalid index
		 */
		String getOption(int i);

		/**
		 * Check is the given key is contained in the option.
		 * 
		 * @param key
		 *            key to check
		 * @return true is options contains this key
		 */
		boolean isValidOption(String key);
	}

	/**
	 * Enum of generators that can be used in tools.
	 */
	public static enum GeneratorType implements Optionable {
		PREFERENTIAL_ATTACHMENT(), DOROGOVTSEV_MENDES(), GRID(), INCOMPLETE_GRID(), RANDOM(), RANDOM_EUCLIDEAN(), RANDOM_FIXED_DEGREE_DYNAMIC_GRAPH(), FULL(), POINTS_OF_INTEREST(), BARABASI_ALBERT(
				"maxLinksPerStep"), CHVATAL(), FLOWER_SNARK(), LCF("lcf"), LOBSTER(), CATERPILLAR(), PETERSEN(), BALABAN_10_CAGE(), BALABAN_11_CAGE(), BIDIAKIS_CUBE(), BIGGS_SMITH(), CUBICAL(), DESARGUES(), DODECAHEDRAL(), DYCK(), F26A(), FOSTER(), FRANKLIN(), FRUCHT(), GRAY(), HARRIES(), HARRIES_WONG(), HEAWOOD(), LJUBLJANA(), MCGEE(), MOBIUS_KANTOR(), NAURU(), PAPPUS(), TETRAHEDRAL(), TRUNCATED_CUBICAL(), TRUNCATED_DODECAHEDRAL(), TRUNCATED_OCTAHEDRAL(), TRUNCATED_TETRAHEDRAL(), TUTTE_12_CAGE(), TUTTE_COXETER(), UTILITY(), WAGNER()

		;

		/*
		 * (non-Javadoc) List of options.
		 */
		private String[] options;

		GeneratorType(String... options) {
			this.options = options;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.tool.ToolsCommon.Optionable#getOptionCount()
		 */
		public int getOptionCount() {
			return options == null ? 0 : options.length;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.tool.ToolsCommon.Optionable#getOption(int)
		 */
		public String getOption(int i) {
			if (i < 0 || i >= getOptionCount())
				return null;

			return options[i];
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.tool.ToolsCommon.Optionable#isValidOption(java.lang
		 * .String)
		 */
		public boolean isValidOption(String key) {
			if (options == null)
				return false;

			for (int i = 0; i < options.length; i++)
				if (options[i].equals(key))
					return true;

			return false;
		}
	}

	/**
	 * Enum of sinks that can be used in tools.
	 */
	public static enum SinkFormat implements Optionable {
		DGS(true), TIKZ(false, "width", "height", "stylesheet", "layout"), DOT(
				true), GML(false), SVG(false), IMAGES(true, "prefix",
				"outputType", "outputPolicy", "resolution", "layoutPolicy",
				"quality", "stylesheet", "layoutStepPerFrame",
				"layoutStepAfterStabilization")

		;

		private boolean dynamic;
		private String[] options;

		private SinkFormat(boolean dynamic, String... options) {
			this.dynamic = dynamic;
			this.options = options;
		}

		/**
		 * Flag to indicate if this sink support the dynamics.
		 * 
		 * @return true if sink is dynamics-ready.
		 */
		public boolean hasDynamicSupport() {
			return dynamic;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.graphstream.tool.ToolsCommon.Optionable#isValidOption(java.lang
		 * .String)
		 */
		public boolean isValidOption(String key) {
			if (options == null)
				return false;

			for (int i = 0; i < options.length; i++)
				if (options[i].equals(key))
					return true;

			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.tool.ToolsCommon.Optionable#getOptionCount()
		 */
		public int getOptionCount() {
			if (options == null)
				return 0;
			return options.length;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.graphstream.tool.ToolsCommon.Optionable#getOption(int)
		 */
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

	public static final String SOURCE_KEY = "source";
	public static final String SOURCE_FORMAT_KEY = "source-format";
	public static final String SOURCE_OPTIONS_KEY = "source-options";
	public static final String SOURCE_DESCRIPTION = "";
	public static final String SOURCE_FORMAT_DESCRIPTION = "format of the source, use X=? to list choices";
	public static final String SOURCE_OPTIONS_DESCRIPTION = "options passed to the source";
	public static final String SINK_KEY = "sink";
	public static final String SINK_FORMAT_KEY = "sink-format";
	public static final String SINK_OPTIONS_KEY = "sink-options";
	public static final String SINK_DESCRIPTION = "";
	public static final String SINK_FORMAT_DESCRIPTION = "sink format, use X=? to see formats";
	public static final String SINK_OPTIONS_DESCRIPTION = "options given to the sink";
	public static final String GENERATOR_TYPE_KEY = "generator-type";
	public static final String GENERATOR_OPTIONS_KEY = "generator-options";
	public static final String GENERATOR_TYPE_DESCRIPTION = "type of generator, use X=? to see types";
	public static final String GENERATOR_OPTIONS_DESCRIPTION = "options given to the generator";
	public static final String STYLESHEET_KEY = "stylesheet";
	public static final String STYLESHEET_DESCRIPTION = "";
	public static final String HELP_KEY = "help";
	public static final String HELP_DESCRIPTION = "this help";

	public static final String INT_MATCHER = "\\d+";
	public static final String REAL_MATCHER = "\\d+([.]\\d+)?";
	public static final String BOOL_MATCHER = "true|false";
	public static final String OPTIONS_MATCHER = "([^=]+=[^=]+(;[^=]+=[^=])*)?";
	
	public static enum CheckResult {
		VALID, MISSING, INVALID, HELP
	}
}
