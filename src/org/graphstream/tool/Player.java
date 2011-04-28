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
import java.io.InputStream;

import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSource;

public class Player extends Tool {

	public Player() {
		super("play", "", true, false);

		addSourceOption();
		addStyleOption(true);

		addOption("scala", i18n("option:scala"), true, ToolOption.OptionType.FLAG);
		addOption("nextAction", i18n("option:next_action"), true, Next.class);
		addOption("quality", i18n("option:quality"), true, ToolOption.OptionType.FLAG);
		addOption("antialiasing", i18n("option:antialiasing"), true,
				ToolOption.OptionType.FLAG);
		addOption("stepDelay", i18n("option:step_delay"), true,
				ToolOption.OptionType.FLAG);
		addOption("autolayout", i18n("option:autolayout"), true,
				ToolOption.OptionType.FLAG);

		setShortcuts(shortcuts);
	}

	/*
	 * (non-Javadoc)
	 * @see org.graphstream.tool.Tool#getDomain()
	 */
	public String getDomain() {
		return "org.graphstream.tool.i18n.player";
	}

	public boolean check() {
		if (!super.check())
			return false;

		boolean scala = getFlagOption("scala");

		if (scala) {
			try {
				Class.forName("org.graphstream.ui.j2dviewer.J2DGraphRenderer");
			} catch (ClassNotFoundException e) {
				err.printf("%s\n", i18n("error:scala_missing"));

				if (exitOnFailed)
					System.exit(1);

				return false;
			}
		}

		return true;
	}

	public void run() {
		boolean quality;
		boolean antialias;
		boolean autolayout;
		boolean scala;
		long stepDelay;
		String stylesheet;
		Next next;

		quality = getFlagOption("quality");
		antialias = getFlagOption("antialiasing");
		autolayout = getFlagOption("autolayout");
		scala = getFlagOption("scala");
		stepDelay = getIntOption("stepDelay", 10);
		next = getEnumOption("nextAction", Next.class, Next.STEP);
		stylesheet = getStyleSheet();

		if (scala)
			System.setProperty("gs.ui.renderer",
					"org.graphstream.ui.j2dviewer.J2DGraphRenderer");

		FileSource source = getSource(SourceFormat.DGS);
		InputStream in = getInput();
		DefaultGraph g = new DefaultGraph(name);

		source.addSink(g);

		if (quality)
			g.addAttribute("ui.quality", true);

		if (antialias)
			g.addAttribute("ui.antialias", true);

		if (stylesheet != null)
			g.addAttribute("ui.stylesheet", stylesheet);

		g.display(autolayout);

		try {
			source.begin(in);
		} catch (IOException e) {
			err.printf("%s\n", i18n("exception:io"));
			System.exit(1);
		}

		try {
			while (next == Next.STEP ? source.nextStep() : source.nextEvents()) {
				try {
					Thread.sleep(stepDelay);
				} catch (InterruptedException e1) {
					// Ignore
				}
			}
		} catch (IOException e) {
			err.printf("%s\n", i18n("exception:io"));
			System.exit(1);
		}

		try {
			source.end();
		} catch (IOException e) {
			err.printf("%s\n", i18n("exception:io"));
			System.exit(1);
		}

	}

	public static enum Next {
		STEP, EVENTS
	}

	private static final String[][] shortcuts = { { "-q", "--quality" },
			{ "-a", "--antialiasing" }, { "-dgs", "--format=DGS" },
			{ "-dot", "--format=DOT" }, { "-gml", "--format=GML" },
			{ "-edge", "--format=EDGE" }, { "-l", "--autolayout" },
			{ "-h", "--help" }, { "-slow", "--step-delay=1000" },
			{ "-fast", "--step-delay=50" }, { "-scala", "--scala-renderer" },
			{ "#1", "--source=%s" } };

	/**
	 * @param args
	 */
	public static void main(String... args) {
		Player player = new Player();
		player.init(args);
		player.run();
	}
}
