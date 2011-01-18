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
package org.graphstream.tool.workbench.xml;

public interface WXmlConstants
{
	public static final String QNAME_GSWB_GETTEXT 							= "gswb:gettext";
	public static final String QNAME_GSWB_GETTEXT_CONTAINER					= "container";
	public static final String QNAME_GSWB_GETTEXT_CONTAINER_NAME			= "name";
	public static final String QNAME_GSWB_GETTEXT_ENTRY 					= "entry";
	public static final String QNAME_GSWB_GETTEXT_ENTRY_NAME				= "name";
	public static final String QNAME_GSWB_GETTEXT_ENTRY_VALUE				= "value";
	public static final String QNAME_GSWB_GETTEXT_LANG						= "lang";
	public static final String QNAME_GSWB_GETTEXT_LOCALE					= "locale";
	public static final String QNAME_GSWB_GETTEXT_LOCALE_LANGUAGE			= "language";
	public static final String QNAME_GSWB_GETTEXT_LOCALE_COUNTRY			= "country";
	public static final String QNAME_GSWB_GETTEXT_LOCALE_VARIANT			= "variant";
	
	public static final WXElementSpec SPEC_GETTEXT_LOCALE = new WXElementSpec(
			QNAME_GSWB_GETTEXT_LOCALE).declareAttributes(
					QNAME_GSWB_GETTEXT_LOCALE_LANGUAGE,
					QNAME_GSWB_GETTEXT_LOCALE_COUNTRY,
					QNAME_GSWB_GETTEXT_LOCALE_VARIANT );
	
	public static final WXElementSpec SPEC_GETTEXT_ENTRY = new WXElementSpec(
			QNAME_GSWB_GETTEXT_ENTRY).declareAttributes(
					QNAME_GSWB_GETTEXT_ENTRY_NAME,
					QNAME_GSWB_GETTEXT_ENTRY_VALUE).declareOptionnalAttributes(
					QNAME_GSWB_GETTEXT_LANG);
	
	public static final WXElementSpec SPEC_GETTEXT_CONTAINER = new WXElementSpec(
			QNAME_GSWB_GETTEXT_CONTAINER).declareAttributes(
					QNAME_GSWB_GETTEXT_CONTAINER_NAME);
	
	public static final WXElementSpec SPEC_GETTEXT = new WXElementSpec(
			QNAME_GSWB_GETTEXT).declareChildren(
					SPEC_GETTEXT_LOCALE,
					SPEC_GETTEXT_ENTRY,
					SPEC_GETTEXT_CONTAINER);
	
	public static final String QNAME_GSWB_MENUBAR							= "gswb:menubar";
	public static final String QNAME_GSWB_MENU_MENU							= "menu";
	public static final String QNAME_GSWB_MENU_MENU_NAME					= "name";
	public static final String QNAME_GSWB_MENU_MENU_ID						= "id";
	public static final String QNAME_GSWB_MENU_MENU_ICON					= "icon";
	public static final String QNAME_GSWB_MENU_MENU_DISABLEON				= "disableOn";
	public static final String QNAME_GSWB_MENU_MENU_ENABLEON				= "enableOn";
	public static final String QNAME_GSWB_MENU_ITEM							= "item";
	public static final String QNAME_GSWB_MENU_ITEM_NAME					= "name";
	public static final String QNAME_GSWB_MENU_ITEM_TYPE					= "type";
	public static final String QNAME_GSWB_MENU_ITEM_COMMAND					= "command";
	public static final String QNAME_GSWB_MENU_ITEM_DISABLEON				= "disableOn";
	public static final String QNAME_GSWB_MENU_ITEM_ENABLEON				= "enableOn";
	public static final String QNAME_GSWB_MENU_ITEM_ICON					= "icon";
	public static final String QNAME_GSWB_MENU_ITEM_STROKEKEY				= "strokeKey";
	public static final String QNAME_GSWB_MENU_ITEM_STROKEMODIFIER			= "strokeModifier";
	public static final String QNAME_GSWB_MENU_SEPARATOR					= "separator";
	
	public static final WXElementSpec SPEC_MENU_SEPARATOR = new WXElementSpec(
			QNAME_GSWB_MENU_SEPARATOR);
	
	public static final WXElementSpec SPEC_MENU_ITEM = new WXElementSpec(
			QNAME_GSWB_MENU_ITEM).declareAttributes(
					QNAME_GSWB_MENU_ITEM_NAME,
					QNAME_GSWB_MENU_ITEM_TYPE,
					QNAME_GSWB_MENU_ITEM_COMMAND,
					QNAME_GSWB_MENU_ITEM_ICON,
					QNAME_GSWB_MENU_ITEM_STROKEKEY,
					QNAME_GSWB_MENU_ITEM_STROKEMODIFIER);
	
	public static final WXElementSpec SPEC_MENU_MENU = new WXElementSpec(
			QNAME_GSWB_MENU_MENU).declareChildren(
					SPEC_MENU_ITEM,
					SPEC_MENU_SEPARATOR).declareAttributes(
					QNAME_GSWB_MENU_MENU_NAME,
					QNAME_GSWB_MENU_MENU_ID,
					QNAME_GSWB_MENU_MENU_ICON);
	
	public static final WXElementSpec SPEC_MENU = new WXElementSpec(
			QNAME_GSWB_MENUBAR).declareChildren(SPEC_MENU_MENU);

	public static final String QNAME_GSWB_ALGORITHMS						= "gswb:algorithms";
	public static final String QNAME_GSWB_ALGORITHMS_ALGORITHM 				= "algorithm";
	public static final String QNAME_GSWB_ALGORITHMS_ALGORITHM_NAME			= "name";
	public static final String QNAME_GSWB_ALGORITHMS_ALGORITHM_CLASS		= "class";
	public static final String QNAME_GSWB_ALGORITHMS_ALGORITHM_CATEGORY		= "category";
	public static final String QNAME_GSWB_ALGORITHMS_ALGORITHM_DESCRIPTION 	= "description";
	public static final String QNAME_GSWB_ALGORITHMS_ALGORITHM_PARAMETER 	= "parameter";
	public static final String QNAME_GSWB_ALGORITHMS_ALGORITHM_PARAMETER_NAME = "name";
	public static final String QNAME_GSWB_ALGORITHMS_ALGORITHM_PARAMETER_TYPE = "type";
	public static final String QNAME_GSWB_ALGORITHMS_ALGORITHM_PARAMETER_DEFAULT = "default";
	
	public static final WXElementSpec SPEC_ALGORITHM_PARAMETER = new WXElementSpec(
			QNAME_GSWB_ALGORITHMS_ALGORITHM_PARAMETER).declareAttributes(
					QNAME_GSWB_ALGORITHMS_ALGORITHM_PARAMETER_NAME,
					QNAME_GSWB_ALGORITHMS_ALGORITHM_PARAMETER_TYPE );
	
	public static final WXElementSpec SPEC_ALGORITHM_DESCRIPTION = new WXElementSpec(
			QNAME_GSWB_ALGORITHMS_ALGORITHM_DESCRIPTION);
	
	public static final WXElementSpec SPEC_ALGORITHM = new WXElementSpec(
			QNAME_GSWB_ALGORITHMS_ALGORITHM).declareAttributes(
					QNAME_GSWB_ALGORITHMS_ALGORITHM_NAME,
					QNAME_GSWB_ALGORITHMS_ALGORITHM_CLASS,
					QNAME_GSWB_ALGORITHMS_ALGORITHM_CATEGORY).declareChildren(
					SPEC_ALGORITHM_DESCRIPTION,
					SPEC_ALGORITHM_PARAMETER);
	
	public static final WXElementSpec SPEC_ALGORITHMS = new WXElementSpec(
			QNAME_GSWB_ALGORITHMS).declareChildren( SPEC_ALGORITHM );
	
	public static final String QNAME_GSWB_HELP								= "gswb:help";
	public static final String QNAME_GSWB_HELP_SECTION						= "section";
	public static final String QNAME_GSWB_HELP_SECTION_NAME					= "name";
	public static final String QNAME_GSWB_HELP_SUBSECTION					= "subsection";
	public static final String QNAME_GSWB_HELP_SUBSECTION_NAME				= "name";
	
	public static final WXElementSpec SPEC_HELP_SUBSECTION = new WXElementSpec(
			QNAME_GSWB_HELP_SUBSECTION).declareAttributes(
					QNAME_GSWB_HELP_SUBSECTION_NAME);
	
	public static final WXElementSpec SPEC_HELP_SECTION = new WXElementSpec(
			QNAME_GSWB_HELP_SECTION).declareAttributes(
					QNAME_GSWB_HELP_SECTION_NAME).declareChildren(
					SPEC_HELP_SUBSECTION);
	
	public static final WXElementSpec SPEC_HELP = new WXElementSpec(
			QNAME_GSWB_HELP).declareChildren(SPEC_HELP_SECTION);
	
	public static final String QNAME_GSWB_SETTINGS							= "gswb:settings";
	public static final String QNAME_GSWB_SETTINGS_SETTING				 	= "setting";
	public static final String QNAME_GSWB_SETTINGS_SETTING_NAME				= "name";
	public static final String QNAME_GSWB_SETTINGS_SETTING_VALUE			= "value";
	public static final String QNAME_GSWB_SETTINGS_HISTORY					= "history";
	public static final String QNAME_GSWB_SETTINGS_HISTORY_FILE				= "file";
	public static final String QNAME_GSWB_SETTINGS_HISTORY_FILE_PATHNAME	= "pathname";
	public static final String QNAME_GSWB_SETTINGS_HISTORY_FILE_DATE		= "date";
	
	public static final WXElementSpec SPEC_SETTING = new WXElementSpec(
			QNAME_GSWB_SETTINGS_SETTING).declareAttributes(
					QNAME_GSWB_SETTINGS_SETTING_NAME,
					QNAME_GSWB_SETTINGS_SETTING_VALUE);
	
	public static final WXElementSpec SPEC_HISTORY_FILE = new WXElementSpec(
			QNAME_GSWB_SETTINGS_HISTORY_FILE).declareAttributes(
					QNAME_GSWB_SETTINGS_HISTORY_FILE_PATHNAME,
					QNAME_GSWB_SETTINGS_HISTORY_FILE_DATE);
			
	public static final WXElementSpec SPEC_HISTORY = new WXElementSpec(
			QNAME_GSWB_SETTINGS_HISTORY).declareChildren(SPEC_HISTORY_FILE);
	
	public static final WXElementSpec SPEC_SETTINGS = new WXElementSpec(
			QNAME_GSWB_SETTINGS).declareChildren(SPEC_SETTING,SPEC_HISTORY);
	
	public static final String GSWB_ICONS_XML = "org/miv/graphstream/tool/workbench/xml/gswb-icons.xml";
	
	public static final String QNAME_GSWB_ICONS								= "gswb:icons";
	public static final String QNAME_GSWB_ICONS_ICON						= "icon";
	public static final String QNAME_GSWB_ICONS_ICON_KEY					= "key";
	public static final String QNAME_GSWB_ICONS_ICON_RESSOURCE				= "ressource";
	
	public static final WXElementSpec SPEC_ICON = new WXElementSpec(
			QNAME_GSWB_ICONS_ICON).declareAttributes(
					QNAME_GSWB_ICONS_ICON_KEY,
					QNAME_GSWB_ICONS_ICON_RESSOURCE);
	public static final WXElementSpec SPEC_ICONS = new WXElementSpec(
			QNAME_GSWB_ICONS).declareChildren(SPEC_ICON);
}
