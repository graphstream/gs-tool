package org.graphstream.tool;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedList;

public interface ToolsCommon {
	/**
	 * Type of generator used.
	 * 
	 */
	public static enum GeneratorType {
		PREFERENTIAL_ATTACHMENT, DOROGOVTSEV_MENDES, GRID, INCOMPLETE_GRID, RANDOM, RANDOM_EUCLIDEAN, RANDOM_FIXED_DEGREE_DYNAMIC_GRAPH, FULL, POINTS_OF_INTEREST
	}

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

	public static class Options {
		HashMap<String, String> keyValues;
		LinkedList<String> notOptions;

		public Options() {
			keyValues = new HashMap<String, String>();
			notOptions = new LinkedList<String>();
		}

		public void registerOption(String key, String value) {
			keyValues.put(key, value);
		}

		public void registerNotOption(String arg) {
			notOptions.add(arg);
		}

		public boolean checkAllowedOptions(String... options) {
			if (options == null)
				return keyValues.size() == 0;

			for (String key : keyValues.keySet()) {
				boolean found = false;

				for (int i = 0; i < options.length; i++) {
					if (options[i].equals(key)) {
						found = true;
						break;
					}
				}

				if (!found)
					return false;
			}

			return true;
		}

		public boolean check(String key, boolean canNotExist,
				boolean canBeNull, String matches, PrintStream err,
				boolean exitOnFailed) {
			if (!keyValues.containsKey(key)) {
				if (canNotExist)
					return true;

				err.printf("Missing options: %s.\n", key);

				if (exitOnFailed)
					System.exit(1);

				return false;
			}

			String value = keyValues.get(key);

			if (value == null && !canBeNull) {
				err.printf("Missing value for \"%s\".\n", key);

				if (exitOnFailed)
					System.exit(1);

				return false;
			}

			if (value != null && matches != null && !value.matches(matches)) {
				err.printf("Invalid value format for \"%s\".\n", key);

				if (exitOnFailed)
					System.exit(1);

				return false;
			}

			return true;
		}

		public <T extends Enum<T>> boolean checkEnum(String key,
				boolean canNotExist, boolean canBeNull, Class<T> choices,
				PrintStream err, boolean exitOnFailed) {
			boolean r = check(key, canNotExist, canBeNull, null, err,
					exitOnFailed);

			if (!r)
				return false;

			String value = keyValues.get(key);

			if (value != null && choices != null) {
				T[] t = choices.getEnumConstants();
				boolean found = false;

				for (int i = 0; i < t.length; i++)
					if (t[i].name().equals(value))
						found = true;

				if (!found) {
					if (!value.equals("?"))
						err.printf("Invalid value for \"%s\".\n");

					Tools.printChoice(err, choices, "");

					if (value.equals("?") || exitOnFailed)
						System.exit(1);

					return false;
				}
			}

			return true;
		}

		public boolean checkNotOptions(int min, int max, PrintStream err,
				boolean exitOnFailed) {
			if (notOptions.size() < min || notOptions.size() > max) {
				err.printf("Invalid amount of args.\n");

				if (exitOnFailed)
					System.exit(1);

				return false;
			}

			return true;
		}
		
		public boolean checkSourceOptions(PrintStream err, boolean exitOnFailed) {
			boolean r = true;
			
			r = r && checkEnum(SOURCE_FORMAT_KEY, true, false, SourceFormat.class, err, exitOnFailed);
			r = r && check(SOURCE_OPTIONS_KEY, true, true, OPTIONS_MATCHER, err, exitOnFailed);
			
			return r;
		}
		
		public boolean checkSinkOptions(PrintStream err, boolean exitOnFailed) {
			boolean r = true;
			
			r = r && checkEnum(SINK_FORMAT_KEY, true, false, SinkFormat.class, err, exitOnFailed);
			r = r && check(SINK_OPTIONS_KEY, true, true, OPTIONS_MATCHER, err, exitOnFailed);
			
			return r;
		}
		
		public boolean checkGeneratorOptions(PrintStream err, boolean exitOnFailed) {
			boolean r = true;
			
			r = r && checkEnum(GENERATOR_TYPE_KEY, true, false, GeneratorType.class, err, exitOnFailed);
			r = r && check(GENERATOR_OPTIONS_KEY, true, true, OPTIONS_MATCHER, err, exitOnFailed);
			
			return r;
		}
		
		public boolean checkHelp(PrintStream err, boolean exitOnFailed) {
			return check(HELP_KEY, true, true, null, err, exitOnFailed);
		}

		public boolean isHelpNeeded() {
			return keyValues.containsKey(HELP_KEY);
		}

		public boolean contains(String key) {
			return keyValues.containsKey(key);
		}

		public int getInt(String key) {
			return Integer.parseInt(keyValues.get(key));
		}

		public long getLong(String key) {
			return Long.parseLong(keyValues.get(key));
		}

		public double getDouble(String key) {
			return Double.parseDouble(keyValues.get(key));
		}

		public String get(String key) {
			return keyValues.get(key);
		}

		public <T extends Enum<T>> T getEnum(String key, Class<T> type) {
			String value = keyValues.get(key);

			for (T t : type.getEnumConstants())
				if (t.name().equals(value))
					return t;

			return null;
		}

		public int getNotOptionsCount() {
			return notOptions.size();
		}
		
		public String getNotOption(int i) {
			return notOptions.get(i);
		}
	}

	public static final String SOURCE_FORMAT_KEY = "source-format";
	public static final String SOURCE_OPTIONS_KEY = "source-options";
	public static final String SINK_FORMAT_KEY = "sink-format";
	public static final String SINK_OPTIONS_KEY = "sink-options";
	public static final String GENERATOR_TYPE_KEY = "generator-type";
	public static final String GENERATOR_OPTIONS_KEY = "generator-options";
	public static final String HELP_KEY = "help";

	public static final String INT_MATCHER = "\\d+";
	public static final String BOOL_MATCHER = "true|false";
	public static final String OPTIONS_MATCHER = "([^=]+=[^=]+(;[^=]+=[^=])*)?";
}
