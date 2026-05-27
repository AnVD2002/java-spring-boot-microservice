package com.project.common_lib_service.utils;

import java.text.MessageFormat;

public class StringUtils {

    public static String formatMessage(String message, Object... args) {
        return MessageFormat.format(message, args);
    }

    public static String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

    public static final String BREAK_LINE = "\n";
    public static final String TAB = "\t";
    public static final char STAR = '*';
    public static final String PERCENT = "%";
    public static final String APOSTROPHE = "'";
    public static final String HYPHEN_UNDER = "_";
    public static final String SQUARE_BRACKET_OPEN = "[";
    public static final String SQUARE_BRACKET_CLOSE = "]";
    public static final String BACKSLASH_SQUARE_BRACKET_OPEN = "\\\\[";
    public static final String BACKSLASH_SQUARE_BRACKET_CLOSE = "\\\\]";
    public static final String BACKSLASH_APOSTROPHE = "\\\\'";
    public static final String ESCAPE = " ESCAPE '\\' COLLATE LATIN1_GENERAL_100_CS_AS_SC_UTF8 ";
    public static final String BACKSLASH_PERCENT = "\\\\%";
    public static final String BACKSLASH_UNDERSCORE = "\\\\_";
    public static final String EQUAL = "=";
    public static final String COMMA = ",";
    public static final String SEMICOLON = ";";
    public static final String SPACE = " ";
    public static final String FIVE_STAR = "*****";
    public static final String SEPARATION = "___";
    public static final String MINUS = "-";
    public static final String COLON = ":";
    public static final String DOT = ".";
    public static final String AND = "&";
    public static final String SLASH_RIGHT = "/";
    public static final String SLASH_LEFT = "\\";
    public static final String EMPTY = "";
    public static final String HYPHEN = "-";
    public static final String XLSX = ".xlsx";
    public static final String NEWLINE_CHARACTER = "<br/>";
    public static final String APPLICATION = "application";
    public static final String DEFAULT_PAGE = "0";
    public static final String DEFAULT_LIMIT = "50";
    public static final Integer ZERO = 0;
    public static final Integer ONE = 1;
    public static final Integer TWO = 2;
    public static final Integer THREE = 3;
    public static final Integer FOUR = 4;
    public static final Integer FIVE = 5;
    public static final Integer SIX = 6;
    public static final Integer SEVEN = 7;
    public static final Integer EIGHT = 8;
    public static final Integer TINYINT_DEFAULT_VALUE = 9;
    public static final Integer INTEGER_DEFAULT_VALUE = -1;
    public static final Integer MAX_SIZE_LIST_PARAMETERS = 1000;
}
