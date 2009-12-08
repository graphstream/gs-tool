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
