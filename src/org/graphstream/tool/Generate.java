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

import java.io.IOException;
import java.io.Writer;

import org.graphstream.algorithm.generator.Generator;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.ElementSink;
import org.graphstream.stream.file.FileSink;

/**
 * Helper to generate graph in command line.
 * 
 * @author Guilhelm Savin
 * 
 */
public class Generate extends Tool implements ToolsCommon {

	public Generate() {
		super("generate", null, false, true);

		addGeneratorOption(true);

		addOption("size", i18n("option:size"), true, ToolOption.OptionType.INT);
		addOption("iteration", i18n("option:iteration"), true,
				ToolOption.OptionType.INT);
		addOption("delay", i18n("option:delay"), true,
				ToolOption.OptionType.INT);
		addOption("export", i18n("option:export"), true,
				ToolOption.OptionType.FLAG);
		addOption("force", i18n("option:force"), true,
				ToolOption.OptionType.FLAG);

		setShortcuts(shortcuts);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.tool.Tool#getDomain()
	 */
	public String getDomain() {
		return "org.graphstream.tool.i18n.generate";
	}

	public void check() throws ToolInitializationException {
		super.check();

		int size = getIntOption("size", 0);
		int iteration = getIntOption("iteration", 0);
		boolean export = getFlagOption("export");
		boolean force = getFlagOption("force");

		if (size == 0 && iteration == 0 && !force)
			throw new ToolInitializationException(i18n("error:infinite"));

		if (!export && !getSinkFormat(SinkFormat.DGS).hasDynamicSupport()
				&& !force)
			throw new ToolInitializationException(i18n("error:not_dynamics",
					getSinkFormat(SinkFormat.DGS).name()));
	}

	public void run() throws ToolExecutionException {
		int size = 0;
		long delay = 0;
		int iteration = 0;
		boolean export = false;

		size = getIntOption("size", 0);
		iteration = getIntOption("iteration", 0);
		delay = getIntOption("delay", 0);
		export = getFlagOption("export");

		boolean loop = true;
		int ite = 0;

		FileSink sink = getSink(SinkFormat.DGS);
		Writer out = getOutput();
		Generator gen = getGenerator(GeneratorType.BARABASI_ALBERT);
		ElementCounter counter = new ElementCounter();
		Graph exportGraph = null;

		gen.addElementSink(counter);

		if (export) {
			exportGraph = new DefaultGraph("export");
			gen.addSink(exportGraph);
		} else {
			gen.addSink(sink);

			try {
				sink.begin(out);
			} catch (IOException e1) {
				throw new ToolExecutionException(e1, i18n("exception:io"));
			}
		}

		gen.begin();

		do {
			gen.nextEvents();

			ite++;

			loop = (iteration <= 0 || ite < iteration)
					&& (size <= 0 || counter.getNodeCount() < size);

			try {
				if (!export)
					sink.flush();
			} catch (IOException e1) {
				// Ignore
			}

			if (delay > 0) {
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					loop = false;
				}
			}
		} while (loop);

		gen.end();

		try {
			if (export) {
				sink.writeAll(exportGraph, out);
			} else {
				sink.end();
			}
		} catch (IOException e) {
			throw new ToolExecutionException(e, i18n("exception:io"));
		}
	}

	public static void main(String... args) {
		Generate gen = new Generate();
		
		ToolRunner runner = new ToolRunner(gen, args);
		runner.addListener(gen);
		
		try {
			runner.start().waitEndOfExecution();
		} catch (InterruptedException e) {
			// Ignore
		}
	}

	private static final String[][] shortcuts = {
			{ "-pa", "--generator-type=PREFERENTIAL_ATTACHMENT" },
			{ "-dm", "--generator-type=DOROGOVTSEV_MENDES" },
			{ "-f", "--generator-type=FULL" },
			{ "-r", "--generator-type=RANDOM" },
			{ "-g", "--generator-type=GRID" }, { "-dgs", "--sink-format=DGS" },
			{ "-dot", "--sink-format=DOT" }, { "-gml", "--sink-format=GML" },
			{ "-tikz", "--sink-format=TIKZ" },
			{ "-i", "--sink-format=IMAGES" }, { "-e", "--export" },
			{ "-H", "--size=100" }, { "-K", "--size=1000" },
			{ "-M", "--size=1000000" }, { "#1", "--sink=%s" } };

	private static class ElementCounter implements ElementSink {

		int nodes = 0;

		// int edges = 0;

		public int getNodeCount() {
			return nodes;
		}

		/*
		 * public int getEdgeCount() { return edges; }
		 */
		public void nodeAdded(String sourceId, long timeId, String nodeId) {
			nodes++;
		}

		public void nodeRemoved(String sourceId, long timeId, String nodeId) {
			nodes--;
		}

		public void edgeAdded(String sourceId, long timeId, String edgeId,
				String fromNodeId, String toNodeId, boolean directed) {
			// edges++;
		}

		public void edgeRemoved(String sourceId, long timeId, String edgeId) {
			// edges--;
		}

		public void graphCleared(String sourceId, long timeId) {
			nodes = 0;
			// edges = 0;
		}

		public void stepBegins(String sourceId, long timeId, double step) {
		}
	}
}
