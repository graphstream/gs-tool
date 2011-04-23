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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

import org.graphstream.algorithm.generator.Generator;
import org.graphstream.stream.file.FileSink;
import org.graphstream.stream.file.FileSource;
import org.graphstream.tool.ToolOption.ToolEnumOption;
import org.graphstream.tool.ToolOption.Type;
import org.graphstream.tool.i18n.I18n;
import org.graphstream.tool.i18n.I18nSupport;

/**
 * Defines the base for tool. Tools just need to add their own option using the
 * <code>addOption(..)</code> method and to implement the <code>run()</code>. If
 * tests on options need to be added, one can override the <code>check()</code>
 * method without forget to call the super method in the new one.
 * 
 * @author Guilhelm Savin
 * 
 */
public abstract class Tool implements ToolsCommon, I18nSupport {
	/*
	 * Shared shortcuts.
	 */
	private static final String[][] commonShortcuts = { { "-h", "--help" } };

	/**
	 * Name of this tool.
	 */
	protected final String name;
	/**
	 * i18n-key of the tool description.
	 */
	protected final String description;

	/**
	 * Parsed options from args.
	 */
	protected ToolOption.ParsedOptions options;
	/**
	 * Map of options of this tool.
	 */
	protected HashMap<String, ToolOption> allowedOptions;
	/**
	 * Shortcuts of this tool.
	 */
	protected String[][] shortcuts;
	/**
	 * Define if the tool has an input.
	 */
	public final boolean hasInput;
	/**
	 * Define if the tool has an output.
	 */
	public final boolean hasOutput;
	/**
	 * Count of non-options args.
	 */
	protected int nonOptions;

	/**
	 * Stream used to push errors.
	 */
	protected PrintStream err;
	/**
	 * Define if the tool should exit when an error occured.
	 */
	protected boolean exitOnFailed;

	/**
	 * The i18n bundle used in this tool.
	 */
	protected ResourceBundle i18n;
	/**
	 * The locale of the tool.
	 */
	protected Locale locale;

	public Tool(String name, String description, boolean input, boolean output) {
		this.name = name;
		this.description = description;
		this.hasInput = input;
		this.hasOutput = output;
		this.err = System.err;
		this.nonOptions = 0;
		this.allowedOptions = new HashMap<String, ToolOption>();
		this.exitOnFailed = true;
		this.locale = Locale.getDefault();
		this.i18n = I18n.load(this);

		if (input)
			addSourceOption();

		if (output)
			addSinkOption();

		addHelpOption();
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return i18n("__description__");
	}

	/**
	 * Set the locale of this object.
	 * 
	 * @param localeStr
	 *            locale representation "lang[_country[_variant]]"
	 */
	public void setLocale(String localeStr) {
		String[] c = localeStr.split("_");

		if (c != null) {
			String lang = c[0];
			String country = c.length > 1 ? c[1] : "";
			String variant = c.length > 2 ? c[2] : "";
			Locale locale = null;

			for (Locale l : Locale.getAvailableLocales()) {
				if (l.getLanguage().equals(lang)
						&& l.getCountry().equals(country)
						&& l.getVariant().equals(variant))
					locale = l;
			}

			if (locale == null)
				locale = new Locale(lang, country, variant);

			setLocale(locale);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.tool.i18n.I18nSupport#getDomain()
	 */
	public String getDomain() {
		return "org.graphstream.tool.i18n.tool";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.tool.i18n.I18nSupport#getLocale()
	 */
	public Locale getLocale() {
		return locale;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.tool.i18n.I18nSupport#setLocale(java.util.Locale)
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
		this.i18n = I18n.load(this);
	}

	/**
	 * Print the i18n key inserting objects.
	 * 
	 * @param key
	 *            key of the i18n entry
	 * @param objects
	 *            objects to insert in the string
	 * @return the well formatted representation of the key
	 * 
	 * @see org.graphstream.tool.i18n.I18n#_(ResourceBundle, String, String...)
	 */
	protected String i18n(String key, String... objects) {
		return I18n._(i18n, key, objects);
	}

	/**
	 * Set the error stream of the tool.
	 * 
	 * @param err
	 *            the new error stream
	 */
	protected void setErr(PrintStream err) {
		this.err = err;
	}

	/**
	 * Set the shortcuts of this tool. When parsing options, args are first
	 * iterate. If one arg is equals to a shortcut, its value is replaced.
	 * 
	 * @param shortcuts
	 *            shortcuts of the tool
	 */
	protected void setShortcuts(String[][] shortcuts) {
		this.shortcuts = shortcuts;
	}

	/**
	 * Add the source option.
	 */
	protected void addSourceOption() {
		addOption(SOURCE_KEY, SOURCE_DESCRIPTION, true, Type.STRING);
		addOption(SOURCE_FORMAT_KEY, i18n(SOURCE_FORMAT_DESCRIPTION), true,
				SourceFormat.class);
		addOption(SOURCE_OPTIONS_KEY, i18n(SOURCE_OPTIONS_DESCRIPTION), true,
				Type.STRING);
	}

	/**
	 * Add the sink option.
	 */
	protected void addSinkOption() {
		addOption(SINK_KEY, SINK_DESCRIPTION, true, Type.STRING);
		addOption(SINK_FORMAT_KEY, i18n(SINK_FORMAT_DESCRIPTION), true,
				SinkFormat.class);
		addOption(SINK_OPTIONS_KEY, i18n(SINK_OPTIONS_DESCRIPTION), true,
				Type.STRING);
	}

	/**
	 * Add the generator option.
	 * 
	 * @param optional
	 *            true is the generator is optional
	 */
	protected void addGeneratorOption(boolean optional) {
		addOption(GENERATOR_TYPE_KEY, i18n(GENERATOR_TYPE_DESCRIPTION),
				optional, GeneratorType.class);
		addOption(GENERATOR_OPTIONS_KEY, "", optional, Type.STRING);
	}

	/**
	 * Add the style option.
	 * 
	 * @param optional
	 *            true is style is optional
	 */
	protected void addStyleOption(boolean optional) {
		addOption(STYLESHEET_KEY, i18n(STYLESHEET_DESCRIPTION), optional,
				Type.STRING);
	}

	/**
	 * Add the help option. This is called in the default constructor of tools.
	 */
	protected void addHelpOption() {
		addOption(HELP_KEY, i18n(HELP_DESCRIPTION), true, Type.FLAG);
	}

	/**
	 * Add the locale option.
	 */
	protected void addLocaleOption() {
		addOption(LOCALE_KEY, i18n(LOCALE_DESCRIPTION), true, Type.STRING);
	}

	/**
	 * Add a new option. Options have the form <code>--key[=value]</code> where
	 * key is a unique identifier. value can be a string, int, real, bool,
	 * options, enum. If there is no value, option is a flag.
	 * 
	 * @param key
	 *            the key of the option
	 * @param description
	 *            the description of the option
	 * @param optional
	 *            true is the option is optional
	 * @param type
	 *            the type of value or FLAG is there is no value
	 */
	protected void addOption(String key, String description, boolean optional,
			Type type) {
		ToolOption to = new ToolOption(key, description, optional, type);
		allowedOptions.put(key, to);
	}

	/**
	 * Add an option of type ENUM. See {@link #addOption(String, String,
	 * boolean, Class} for a full description of adding option.
	 * 
	 * @param <T>
	 *            type of the enum
	 * @param key
	 *            the key of the option
	 * @param description
	 *            the description of the option
	 * @param optional
	 *            true is the option is optional
	 * @param choices
	 *            class of the enum
	 */
	protected <T extends Enum<T>> void addOption(String key,
			String description, boolean optional, Class<T> choices) {
		ToolEnumOption<T> to = new ToolEnumOption<T>(key, description,
				optional, choices);
		allowedOptions.put(key, to);
	}

	/**
	 * Init the tool. This will remove shortcuts from args and then parse it.
	 * init() checks if a locale has been defined and enable it in this case.
	 * 
	 * @param args
	 *            the args to parse
	 */
	public void init(String... args) {
		options = new ToolOption.ParsedOptions();

		if (shortcuts != null)
			Tools.removeShortcuts(args, shortcuts);

		Tools.removeShortcuts(args, commonShortcuts);
		Tools.parseArgs(args, options);

		if (options.contains(LOCALE_KEY)) {
			String[] c = options.get(LOCALE_KEY).split("_");

			if (c != null) {
				String lang = c[0];
				String country = c.length > 1 ? c[1] : "";
				String variant = c.length > 2 ? c[2] : "";
				Locale locale = null;

				for (Locale l : Locale.getAvailableLocales()) {
					if (l.getLanguage().equals(lang)
							&& l.getCountry().equals(country)
							&& l.getVariant().equals(variant))
						locale = l;
				}

				if (locale == null)
					locale = new Locale(lang, country, variant);

				Locale.setDefault(locale);
				i18n = I18n.load(getDomain());
			}
		}

		if (options.isHelpNeeded()) {
			usage(System.out);
			System.exit(0);
		}

		check();
	}

	public Iterable<ToolOption> getEachToolOption() {
		return Collections.unmodifiableCollection(allowedOptions.values());
	}

	/**
	 * Get the source of the tool.
	 * 
	 * @param def
	 *            default format of the source if no format defined
	 * @return a file source according to the source format and options.
	 */
	public FileSource getSource(SourceFormat def) {
		SourceFormat format = getSourceFormat(def);
		String[][] sourceOptions = null;

		if (options.contains(SOURCE_OPTIONS_KEY))
			sourceOptions = Tools.getKeyValue(options.get(SOURCE_OPTIONS_KEY));

		return Tools.sourceFor(format, sourceOptions);
	}

	/**
	 * Get the source format.
	 * 
	 * @param def
	 *            default format if no format defined
	 * @return the source format
	 */
	public SourceFormat getSourceFormat(SourceFormat def) {
		if (options.contains(SOURCE_FORMAT_KEY))
			return options.getEnum(SOURCE_FORMAT_KEY, SourceFormat.class);

		return def;
	}

	/**
	 * Get the sink of the tool.
	 * 
	 * @param def
	 *            default format of the sink if no format defined
	 * @return a file sink according to the sink format and options.
	 */
	public FileSink getSink(SinkFormat def) {
		SinkFormat format = getSinkFormat(def);
		String[][] sinkOptions = null;

		if (options.contains(SINK_OPTIONS_KEY))
			sinkOptions = Tools.getKeyValue(options.get(SINK_OPTIONS_KEY));

		return Tools.sinkFor(format, sinkOptions);
	}

	/**
	 * Get the sink format.
	 * 
	 * @param def
	 *            default format if no format defined
	 * @return the sink format
	 */
	public SinkFormat getSinkFormat(SinkFormat def) {
		if (options.contains(SINK_FORMAT_KEY))
			return options.getEnum(SINK_FORMAT_KEY, SinkFormat.class);

		return def;
	}

	/**
	 * Get the generator type.
	 * 
	 * @param def
	 *            default type if no type defined
	 * @return the type of the generator.
	 */
	public Generator getGenerator(GeneratorType def) {
		GeneratorType format = def;
		String[][] generatorOptions = null;

		if (options.contains(GENERATOR_TYPE_KEY))
			format = options.getEnum(GENERATOR_TYPE_KEY, GeneratorType.class);

		if (options.contains(GENERATOR_OPTIONS_KEY))
			generatorOptions = Tools.getKeyValue(options
					.get(GENERATOR_OPTIONS_KEY));

		return Tools.generatorFor(format, generatorOptions);
	}

	/**
	 * Get the input of the program. If no file defined, System.in is used.
	 * 
	 * @return the input of the program.
	 */
	public InputStream getInput() {
		if (options.contains(SOURCE_KEY)) {
			String url = options.get(SOURCE_KEY);

			try {
				InputStream in = Tools.getFileOrUrlAsStream(url);
				return in;
			} catch (FileNotFoundException e) {
				err.printf("%s\n", i18n("exception:file_not_found", url));
				System.exit(1);
			}
		}

		return System.in;
	}

	/**
	 * Get the output of the program. If no file defined, System.out is used.
	 * 
	 * @return the output of the program.
	 */
	public OutputStream getOutput() {
		if (options.contains(SINK_KEY)) {
			String path = options.get(SINK_KEY);

			try {
				return new FileOutputStream(path);
			} catch (FileNotFoundException e) {
				err.printf("%s\n", i18n("exception:file_not_found", path));
				System.exit(1);
			}
		}

		return System.out;
	}

	/**
	 * Get the stylesheet. If no sheet defined, returns the empty string.
	 * 
	 * @return the stylesheet or ""
	 */
	public String getStyleSheet() {
		if (options.contains(STYLESHEET_KEY)) {
			String css = options.get(STYLESHEET_KEY);

			try {
				String content = Tools.loadFileContent(css);
				return content;
			} catch (FileNotFoundException e) {
				// Ignore
			} catch (IOException e) {
				err.printf("%s\n", i18n("error:get_stylesheet"));
				System.exit(1);
			}

			return css;
		}

		return "";
	}

	/**
	 * Get a flag option.
	 * 
	 * @param key
	 *            key of the option
	 * @return true is the flag is defined
	 */
	public boolean getFlagOption(String key) {
		return options.contains(key);
	}

	/**
	 * Get the value of a bool option.
	 * 
	 * @param key
	 *            key of the option
	 * @param def
	 *            default value
	 * @return value of option
	 */
	public boolean getBoolOption(String key, boolean def) {
		if (options.contains(key))
			return options.getBoolean(key);

		return def;
	}

	/**
	 * Get the value of an int option.
	 * 
	 * @param key
	 *            key of the option
	 * @param def
	 *            default value
	 * @return value of the option
	 */
	public int getIntOption(String key, int def) {
		if (options.contains(key))
			return options.getInt(key);

		return def;
	}

	/**
	 * Get the value of an real option.
	 * 
	 * @param key
	 *            key of the option
	 * @param def
	 *            default value
	 * @return value of the option
	 */
	public double getRealOption(String key, double def) {
		if (options.contains(key))
			return options.getDouble(key);

		return def;
	}

	/**
	 * Get the value of an enum option.
	 * 
	 * @param key
	 *            key of the option
	 * @param choices
	 *            class of the enum
	 * @param def
	 *            default value
	 * @return value of the option
	 */
	public <T extends Enum<T>> T getEnumOption(String key, Class<T> choices,
			T def) {
		if (options.contains(key))
			return options.getEnum(key, choices);

		return def;
	}

	/**
	 * Get the value of an optionsf option.
	 * 
	 * @param key
	 *            key of the option
	 * @return value of the option
	 */
	public String[][] getOptionsOption(String key) {
		if (options.contains(key))
			return options.getOptions(key);

		return null;
	}

	/**
	 * Check is options are valid. If exitOnFailed is set to true, system will
	 * exit if an error occured in this check.
	 * 
	 * @return true if the check success.
	 */
	public boolean check() {
		for (String key : options) {
			if (!allowedOptions.containsKey(key)) {
				err.printf("%s\n", i18n("error:unknown_option", key));

				if (exitOnFailed)
					System.exit(1);

				return false;
			}
		}

		for (ToolOption opt : allowedOptions.values()) {
			switch (options.check(opt)) {
			case INVALID:
				err.printf("%s\n", i18n("error:invalid_option", opt.key));

				if (exitOnFailed)
					System.exit(1);

				return false;
			case MISSING:
				err.printf("%s\n", i18n("error:missing_option", opt.key));

				if (exitOnFailed)
					System.exit(1);

				return false;
			case HELP:
				if (opt instanceof ToolEnumOption)
					Tools.printChoice(System.out,
							((ToolEnumOption<?>) opt).choices, "");
				System.exit(0);
			}
		}

		if (!options.checkNotOptions(nonOptions)) {
			err.printf("%s.\n", i18n("error:bad_arg_count"));
			usage(err);

			if (exitOnFailed)
				System.exit(1);

			return false;
		}

		return true;
	}

	/**
	 * Tools have to define this method. It defines the action of the tool.
	 */
	public abstract void run();

	/**
	 * Print usage of this tool on a stream.
	 * 
	 * @param out
	 *            the stream to print usage out.
	 */
	public void usage(PrintStream out) {
		out.printf("%s : java %s [OPTIONS]\n", i18n("Usage"), getClass()
				.getName());
		out.printf("\n%s\n", i18n("__description__"));
		out.printf("\n%s :\n", i18n("with", "OPTIONS"));

		int s = 0;

		for (ToolOption opt : allowedOptions.values())
			s = Math.max(s, opt.key.length());

		String format = String.format("\t--%%-%ds : %%s\n", s);

		for (ToolOption opt : allowedOptions.values())
			out.printf(format, opt.key, opt.description);

		if (shortcuts != null) {
			out.printf("\n%s:\n", i18n("option:shortcuts"));

			s = 0;

			for (int i = 0; i < shortcuts.length; i++)
				s = Math.max(s, shortcuts[i][0].length());

			format = String.format("\t%%-%ds : %%s\n", s);

			for (int i = 0; i < shortcuts.length; i++)
				out.printf(format, shortcuts[i][0], shortcuts[i][1]);
		}
	}
}
