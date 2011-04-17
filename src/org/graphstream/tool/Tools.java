package org.graphstream.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;

import org.graphstream.algorithm.generator.DorogovtsevMendesGenerator;
import org.graphstream.algorithm.generator.FullGenerator;
import org.graphstream.algorithm.generator.Generator;
import org.graphstream.algorithm.generator.GridGenerator;
import org.graphstream.algorithm.generator.IncompleteGridGenerator;
import org.graphstream.algorithm.generator.PointsOfInterestGenerator;
import org.graphstream.algorithm.generator.PreferentialAttachmentGenerator;
import org.graphstream.algorithm.generator.RandomEuclideanGenerator;
import org.graphstream.algorithm.generator.RandomFixedDegreeDynamicGraphGenerator;
import org.graphstream.algorithm.generator.RandomGenerator;
import org.graphstream.stream.file.FileSink;
import org.graphstream.stream.file.FileSinkDGS;
import org.graphstream.stream.file.FileSinkDOT;
import org.graphstream.stream.file.FileSinkGML;
import org.graphstream.stream.file.FileSinkImages;
import org.graphstream.stream.file.FileSinkSVG;
import org.graphstream.stream.file.FileSinkTikZ;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceDGS;
import org.graphstream.stream.file.FileSourceDOT;
import org.graphstream.stream.file.FileSourceGML;

public class Tools implements ToolsCommon {
	/**
	 * Get a source of format with given options.
	 */
	public static FileSource sourceFor(SourceFormat format, String[][] options) {
		FileSource source = null;

		switch (format) {
		case DGS:
			source = new FileSourceDGS();
			break;
		case DOT:
			source = new FileSourceDOT();
			break;
		case GML:
			source = new FileSourceGML();
			break;
		}

		return source;
	}

	/**
	 * Get a sink of format with given options.
	 * 
	 * @param format
	 *            format of the sink
	 * @param options
	 *            options passed to the sink
	 * @return a sink
	 */
	public static FileSink sinkFor(SinkFormat format, String[][] options)
			throws IllegalArgumentException {
		FileSink sink = null;

		switch (format) {
		case DGS:
			sink = new FileSinkDGS();
			break;
		case DOT:
			sink = new FileSinkDOT();
			break;
		case GML:
			sink = new FileSinkGML();
			break;
		case IMAGES: {
			String prefix = "";
			FileSinkImages.OutputType outputType = FileSinkImages.OutputType.JPG;
			FileSinkImages.Resolution resolution = FileSinkImages.Resolutions.VGA;
			FileSinkImages.OutputPolicy policy = FileSinkImages.OutputPolicy.ByStepOutput;
			FileSinkImages.Quality quality = FileSinkImages.Quality.MEDIUM;
			FileSinkImages.LayoutPolicy layout = FileSinkImages.LayoutPolicy.NoLayout;
			String stylesheet = null;
			int spf = -1;
			int sas = -1;

			if (options != null) {
				for (int i = 0; i < options.length; i++) {
					if (options[i][0].equals("prefix")) {
						prefix = options[i][1];
					} else if (options[i][0].equals("outputType")) {
						try {
							outputType = FileSinkImages.OutputType
									.valueOf(options[i][1]);
						} catch (IllegalArgumentException e) {
							System.err
									.printf("Invalid outputType. Use one of:\n");
							for (FileSinkImages.OutputType t : FileSinkImages.OutputType
									.values())
								System.err.printf("- %s\n", t);
							System.exit(1);
						}
					} else if (options[i][0].equals("resolution")) {
						if (options[i][1].matches("\\d+x\\d+")) {
							int width = Integer.parseInt(options[i][1]
									.substring(0, options[i][1].indexOf('x')));
							int height = Integer.parseInt(options[i][1]
									.substring(options[i][1].indexOf('x') + 1));

							resolution = new FileSinkImages.CustomResolution(
									width, height);
						} else {
							try {
								resolution = FileSinkImages.Resolutions
										.valueOf(options[i][1]);
							} catch (IllegalArgumentException e) {
								System.err
										.printf("Invalid outputType. Use \"withxheight\" or one of:\n");
								for (FileSinkImages.Resolutions t : FileSinkImages.Resolutions
										.values())
									System.err.printf("- %s\n", t);
								System.exit(1);
							}
						}
					} else if (options[i][0].equals("outputPolicy")) {
						try {
							policy = FileSinkImages.OutputPolicy
									.valueOf(options[i][1]);
						} catch (IllegalArgumentException e) {
							System.err.printf("Invalid policy. Use one of:\n");
							for (FileSinkImages.OutputPolicy t : FileSinkImages.OutputPolicy
									.values())
								System.err.printf("- %s\n", t);
							System.exit(1);
						}
					} else if (options[i][0].equals("layoutPolicy")) {
						try {
							layout = FileSinkImages.LayoutPolicy
									.valueOf(options[i][1]);
						} catch (IllegalArgumentException e) {
							System.err
									.printf("Invalid layout policy. Use one of:\n");
							for (FileSinkImages.LayoutPolicy t : FileSinkImages.LayoutPolicy
									.values())
								System.err.printf("- %s\n", t);
							System.exit(1);
						}
					} else if (options[i][0].equals("stylesheet")) {
						try {
							stylesheet = loadFileContent(options[i][1]);
						} catch (IOException e) {
							System.err
									.printf("Can not load stylesheet content : \"%s\".\n",
											options[i][1]);
							System.err.printf("Error is %s : %s\n", e
									.getClass().getName(), e.getMessage());
							System.exit(1);
						}
					} else if (options[i][0].equals("layoutStepPerFrame")) {
						if (options[i][1].matches("\\d+"))
							spf = Integer.parseInt(options[i][1]);
						else {
							System.err.printf("Bad stepPerFrame value : %s\n",
									options[i][1]);
							System.exit(1);
						}
					} else if (options[i][0]
							.equals("layoutStepAfterStabilization")) {
						if (options[i][1].matches("\\d+"))
							sas = Integer.parseInt(options[i][1]);
						else {
							System.err.printf(
									"Bad stepAfterStabilization value : %s\n",
									options[i][1]);
							System.exit(1);
						}
					}
				}
			}

			sink = new FileSinkImages(prefix, outputType, resolution, policy);

			((FileSinkImages) sink).setQuality(quality);
			((FileSinkImages) sink).setLayoutPolicy(layout);

			if (stylesheet != null)
				((FileSinkImages) sink).setStyleSheet(stylesheet);

			if (spf >= 0)
				((FileSinkImages) sink).setLayoutStepPerFrame(spf);

			if (sas >= 0)
				((FileSinkImages) sink).setLayoutStepAfterStabilization(sas);
		}
			break;
		case SVG:
			sink = new FileSinkSVG();
			break;
		case TIKZ: {
			sink = new FileSinkTikZ();

			boolean layout = false;
			double width = Double.NaN, height = Double.NaN;
			String css = null;

			if (options != null) {
				for (int o = 0; o < options.length; o++) {
					if (options[o][0].equals("layout")) {
						layout = Boolean.parseBoolean(options[o][1]);
					} else if (options[o][0].equals("stylesheet")) {
						css = getCSS(options[o][1]);
					} else if (options[o][0].equals("width")) {
						width = Double.parseDouble(options[o][1]);
					} else if (options[o][0].equals("height")) {
						height = Double.parseDouble(options[o][1]);
					}
				}
			}

			((FileSinkTikZ) sink).setLayout(layout);

			if (!Double.isNaN(width))
				((FileSinkTikZ) sink).setWidth(width);
			if (!Double.isNaN(height))
				((FileSinkTikZ) sink).setHeight(height);

			((FileSinkTikZ) sink).setCSS(css);
		}
			break;
		}

		return sink;
	}

	/**
	 * Create a generator type with given options.
	 * 
	 * @param type
	 *            type of generator
	 * @param options
	 *            options passed to the generator.
	 * @return a generator
	 */
	public static Generator generatorFor(GeneratorType type, String[][] options) {
		Generator gen = null;

		switch (type) {
		case PREFERENTIAL_ATTACHMENT:
			gen = new PreferentialAttachmentGenerator();
			break;
		case DOROGOVTSEV_MENDES:
			gen = new DorogovtsevMendesGenerator();
			break;
		case GRID:
			gen = new GridGenerator();
			break;
		case INCOMPLETE_GRID:
			gen = new IncompleteGridGenerator();
			break;
		case RANDOM:
			gen = new RandomGenerator();
			break;
		case RANDOM_EUCLIDEAN:
			gen = new RandomEuclideanGenerator();
			break;
		case RANDOM_FIXED_DEGREE_DYNAMIC_GRAPH:
			gen = new RandomFixedDegreeDynamicGraphGenerator();
			break;
		case FULL:
			gen = new FullGenerator();
			break;
		case POINTS_OF_INTEREST:
			gen = new PointsOfInterestGenerator();
			break;
		}

		return gen;
	}

	public static String getCSS(String url) {
		try {
			return loadFileContent(url);
		} catch (IOException e) {
			return url;
		}
	}

	/**
	 * Load file/url content into one string.
	 * 
	 * @param url
	 *            url or path
	 * @return content
	 * @throws IOException
	 */
	public static String loadFileContent(String url) throws IOException {
		Reader reader = null;
		File f = new File(url);

		if (f.exists())
			reader = new FileReader(f);
		else {
			InputStream in = Generate.class.getClassLoader()
					.getResourceAsStream(url);
			if (in != null)
				reader = new InputStreamReader(in);
		}

		if (reader == null)
			throw new FileNotFoundException(url);

		StringBuilder builder = new StringBuilder();
		char[] buffer = new char[128];
		int r;

		while ((r = reader.read(buffer, 0, 128)) > 0)
			builder.append(buffer, 0, r);

		return builder.toString();
	}

	public static String[][] getKeyValue(String full)
			throws IllegalArgumentException {
		String[] options = full.split("\\s*;\\s*");
		String[][] finalOptions = null;

		if (options != null) {
			finalOptions = new String[options.length][2];
			for (int i = 0; i < options.length; i++) {
				if (options[i].indexOf('=') > 0) {
					finalOptions[i][0] = options[i].substring(0,
							options[i].indexOf('='));
					finalOptions[i][1] = options[i].substring(options[i]
							.indexOf('=') + 1);
				} else {
					throw new IllegalArgumentException(options[i]);
				}
			}
		} else {
			throw new NullPointerException();
		}

		return finalOptions;
	}

	public static <T extends Enum<T>> void printChoice(PrintStream out,
			Class<T> choices, String prefix) {
		if (prefix == null)
			prefix = "";

		out.printf("%s%s values:\n", prefix, choices.getSimpleName());
		for (T c : choices.getEnumConstants())
			out.printf("%s  - %s\n", prefix, c);
	}

	public static boolean check(Optionable opt, String[][] options) {
		if (options == null)
			return true;

		for (int i = 0; i < options.length; i++)
			if (!opt.isValidOption(options[i][0]))
				return false;

		return true;
	}

	public static boolean checkSourceOptions(Options options, PrintStream err,
			boolean exitOnFailed) {
		boolean r = true;

		r = r
				&& options.checkEnum(SOURCE_FORMAT_KEY, true, false,
						SourceFormat.class, err, exitOnFailed);
		r = r
				&& options.check(SOURCE_OPTIONS_KEY, true, true,
						OPTIONS_MATCHER, err, exitOnFailed);

		return r;
	}

	public static boolean checkSinkOptions(Options options, PrintStream err,
			boolean exitOnFailed) {
		boolean r = true;

		r = r
				&& options.checkEnum(SINK_FORMAT_KEY, true, false,
						SinkFormat.class, err, exitOnFailed);
		r = r
				&& options.check(SINK_OPTIONS_KEY, true, true, OPTIONS_MATCHER,
						err, exitOnFailed);

		return r;
	}

	public static boolean checkGeneratorOptions(Options options,
			PrintStream err, boolean exitOnFailed) {
		boolean r = true;

		r = r
				&& options.checkEnum(GENERATOR_TYPE_KEY, true, false,
						GeneratorType.class, err, exitOnFailed);
		r = r
				&& options.check(GENERATOR_OPTIONS_KEY, true, true,
						OPTIONS_MATCHER, err, exitOnFailed);

		return r;
	}

	public static void removeShortcuts(String[] args, String[][] shortcuts) {
		for (int k = 0; k < args.length; k++) {
			for (int l = 0; l < shortcuts.length; l++) {
				if (shortcuts[l][0].equals(args[k])) {
					args[k] = shortcuts[l][1];
					break;
				}
			}
		}
	}

	public static void parseArgs(String[] args, Options options) {
		for (int k = 0; k < args.length; k++) {
			if (args[k].matches("^--\\w+(-\\w+)*(=.*)?$")) {
				int idx = args[k].indexOf('=');
				String key;
				String value;

				if (idx < 0) {
					key = args[k].substring(2);
					value = null;
				} else {
					key = args[k].substring(2, idx);
					value = args[k].substring(idx + 1).trim();
				}

				if (value != null && value.matches("^\".*\"$"))
					value = value.substring(1, value.length() - 1);

				options.registerOption(key, value);
			} else {
				options.registerNotOption(args[k]);
			}
		}
	}

	public static InputStream getInput(String url) throws IOException {
		File f = new File(url);
		InputStream in = null;

		if (!f.exists()) {
			in = Convert.class.getResourceAsStream(url);

			if (in == null)
				throw new FileNotFoundException(String.format(
						"Input file \"%s\" does not exists.\n", url));
		} else
			in = new FileInputStream(f);

		return in;
	}
}
