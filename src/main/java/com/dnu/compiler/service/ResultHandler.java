package com.dnu.compiler.service;

import java.util.Map;

/**
 * Created by Mike on 1/18/2016.
 */
public class ResultHandler {

    private static StringBuilder builder = new StringBuilder();

    public static void append(String s) {
        builder.append('\n').append(s);
    }

    public static void defaultAppend(String s) {
        builder.append(s);
    }

    public static String getResult(String text) {
        Lexical.init();
        Syntax.init();
        Semantic.init();
        init();
        append("------------------------ЛЕКСИЧЕСКИЙ АНАЛИЗАТОР-------------------------------------");
        Lexical.Parse(text);
        append("------------------------СИНТАКСИЧЕСКИЙ АНАЛИЗАТОР----------------------------------");
        append("\n");
        Syntax.Parse();
        append("\n----------------------СЕМАНТИЧЕСКИЙ АНАЛИЗАТОР-----------------------------------");
        append("\n--------------------------------ТАБЛИЦА------------------------------------------");
        for (Map.Entry<String, String> t : Semantic.GetTable().entrySet())
            append(t.getKey() + " - " + t.getValue());
        append("\n-----------------------КОД НА ЯЗЫКЕ ТЕТРАД---------------------------------------");
        append(Semantic.GetCode());
        return builder.toString();
    }

    public static void init() {
        builder = new StringBuilder();
    }

}
