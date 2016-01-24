package com.dnu.compiler.service;

import java.util.*;

public class Lexical {
    private static HashMap<String, String> keyWords;
    private static HashMap<String, String> predefinedWords;
    private static HashMap<String, String> specialSymbols;
    private final static String NUMBERS = "0123456789";
    private static Map<String, String> table = new LinkedHashMap<String, String>();
    public static StringBuilder tokensString = new StringBuilder();

    static {
        keyWords = new HashMap<String, String>();
        keyWords.put("AND", "and_LC");
        keyWords.put("ARRAY", "array_LC");
        keyWords.put("BEGIN", "begin_LC");
        keyWords.put("CONST", "const_LC");
        keyWords.put("DIV", "div_LC");
        keyWords.put("DO", "do_LC");
        keyWords.put("ELSE", "else_LC");
        keyWords.put("END", "end_LC");
        keyWords.put("FOR", "for_LC");
        keyWords.put("IF", "if_LC");
        keyWords.put("MOD", "mod_LC");
        keyWords.put("NOT", "not_LC");
        keyWords.put("OF", "of_LC");
        keyWords.put("OR", "or_LC");
        keyWords.put("PROGRAM", "program_LC");
        keyWords.put("THEN", "then_LC");
        keyWords.put("TO", "to_LC");
        keyWords.put("TYPE", "type_LC");
        keyWords.put("VAR", "var_LC");
        keyWords.put("WHILE", "while_LC");

        predefinedWords = new HashMap<String, String>();
        predefinedWords.put("CHAR", "char_LC");
        predefinedWords.put("INTEGER", "integer_LC");

        specialSymbols = new HashMap<String, String>();
        specialSymbols.put("+", "AOP");
        specialSymbols.put("-", "AOP");
        specialSymbols.put("*", "AOP");
        specialSymbols.put("=", "LOP");
        specialSymbols.put("<", "LOP");
        specialSymbols.put("<=", "LOP");
        specialSymbols.put(">", "LOP");
        specialSymbols.put(">=", "LOP");
        specialSymbols.put("<>", "LOP");
        specialSymbols.put(".", "DOT");
        specialSymbols.put(",", "COMMA");
        specialSymbols.put(":", "COLON");
        specialSymbols.put(";", "SEMICOLON");
        specialSymbols.put(":=", "ASSIGN");
        specialSymbols.put("..", "DOUBLE_COMMA");
        specialSymbols.put("(", "LPAR");
        specialSymbols.put(")", "RPAR");
        specialSymbols.put("[", "SQLPAR");
        specialSymbols.put("]", "SQRPAR");
    }

    public static void Parse(String inputProgram) {
        int length = inputProgram.length();
        if (length > 0) {
            boolean isChar = false;
            int comment = 0, charConst = 0;
            StringBuilder currWord = new StringBuilder();
            for (int i = 0; i < length; i++) {
                char currChar = inputProgram.charAt(i);
                if (currChar == '{') ++comment;
                if (currChar == '}') --comment;
                if (currChar == '\'' && comment == 0) {
                    isChar = true;
                    if (charConst == 0) {
                        ++charConst;
                        if (currWord.length() > 0) {
                            checkCurrentWord(currWord.toString());
                            currWord.delete(0, currWord.length());
                        }
                    } else --charConst;
                }
                if (isChar && comment == 0) {
                    currWord.append(currChar);
                    if (charConst == 0) {
                        if (currWord.length() < 4) {
                            table.put(currWord.toString(), "CHR");
                            tokensString.append(currWord);
                        } else {
                            table.put(currWord.substring(0, 2) + "'", "CHR");
                            tokensString.append(currWord.substring(0, 2));
                            tokensString.append("'");
                        }

                        tokensString.append(" ");
                        currWord.delete(0, currWord.length());
                        isChar = false;
                    }
                }

                if (comment == 0 && charConst == 0) {
                    currWord.append(currChar);
                    if (currChar >= 'A' && currChar <= 'Z' || currChar >= 'a'&& currChar <= 'z' || currChar == '_' || NUMBERS.contains(String.valueOf(currChar))) {
                        if (i == length - 1 || (i + 1 < length && inputProgram.charAt(i + 1) == ' ' || inputProgram.charAt(i + 1) == '\n' || inputProgram.charAt(i + 1) == '\r' || isSpecialSymbols(String.valueOf(inputProgram.charAt(i + 1))))) {
                            if (isKeyWord(currWord.toString())) {
                                table.put(currWord.toString().toUpperCase(), "Ключевое слово " + currWord.toString().toUpperCase());
                                tokensString.append(currWord.toString().toUpperCase());
                                tokensString.append(" ");
                                currWord.delete(0, currWord.length());
                            } else if (isPredefinedWord(currWord.toString())) {
                                table.put(currWord.toString().toUpperCase(), "Предопределнное слово " + currWord.toString().toUpperCase());
                                tokensString.append(currWord.toString().toUpperCase());
                                tokensString.append(" ");
                                currWord.delete(0, currWord.length());
                            } else {
                                if ((NUMBERS.contains(String.valueOf(currWord.charAt(0))) && isNAN(currWord.toString()))) {
                                    checkCurrentWord(currWord.toString());
                                } else {
                                    if (!isNAN(currWord.toString())) {
                                        table.put(currWord.toString(), "INT");
                                        tokensString.append(currWord);
                                        tokensString.append(" ");
                                    }else {
                                        table.put(currWord.toString(), "ID");
                                        tokensString.append(currWord);
                                        tokensString.append(" ");
                                    }
                                }
                                currWord.delete(0, currWord.length());
                            }
                        }
                    } else if (isSpecialSymbols(currWord.toString())) {
                        if (i + 1 < length && isSpecialSymbols(String.valueOf(inputProgram.charAt(i + 1)))) {
                            if (isSpecialSymbols(currWord.toString() + inputProgram.charAt(i + 1))) {
                                currWord.append(inputProgram.charAt(i + 1));
                                ++i;
                            }
                        }

                        table.put(currWord.toString(), specialSymbols.get(currWord.toString()));
                        tokensString.append(currWord);
                        tokensString.append(" ");
                        currWord.delete(0, currWord.length());
                    } else {
                        if (currChar >= 'А' && currChar <= 'Я' || currChar >= 'а' && currChar <= 'я') {
                            ResultHandler.append("Введен символ кириллицы!");
                            printRestOfInputProgram(inputProgram, i);
                            return;
                        }

                        currWord.delete(0, currWord.length());
                    }
                }
            }

            for (Map.Entry<String, String> t : table.entrySet())
                ResultHandler.append(t.getKey() + " - " + t.getValue());
        } else  {
            ResultHandler.append("Вы ввели пустой текст!");
            return;
        }
    }

    private static boolean isKeyWord(String word) {
        return keyWords.containsKey(word.toUpperCase());
    }

    private static boolean isPredefinedWord(String word) {
        return predefinedWords.containsKey(word.toUpperCase());
    }

    private static boolean isSpecialSymbols(String word) {
        return specialSymbols.containsKey(word);
    }

    private static boolean isNAN(String nan) {
        for (int i = 0; i < nan.length(); i++) {
            char c = nan.charAt(i);
            if (c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' || c == '_')
                return true;
        }
        return false;
    }

    private static void checkCurrentWord(String currWord) {
        StringBuilder number = new StringBuilder(), word = new StringBuilder();

        for (int j = 0; j < currWord.length(); j++) {
            if (NUMBERS.contains(String.valueOf(currWord.charAt(j)))) {
                if ((word.length() > 0 && !NUMBERS.contains(String.valueOf(word.charAt(0))))) {
                    word.append(currWord.charAt(j));
                    if (j == currWord.length() - 1 || (j + 1 < currWord.length() && specialSymbols.containsKey(String.valueOf(currWord.charAt(j + 1))))) {
                        table.put(word.toString(), "ID");
                        tokensString.append(currWord);
                        tokensString.append(" ");
                        word.delete(0, word.length());
                    }
                    continue;
                } else {
                    number.append(currWord.charAt(j));
                }
                if (j == currWord.length() - 1 || (j + 1 < currWord.length() && !NUMBERS.contains(String.valueOf(currWord.charAt(j + 1)))) && !isNAN(number.toString())) {
                    table.put(number.toString(), "INT");
                    tokensString.append(currWord);
                    tokensString.append(" ");
                    number.delete(0, number.length());
                }
            } else if (currWord.charAt(j) >= 'A' && currWord.charAt(j) <= 'Z' || currWord.charAt(j) >= 'a'&& currWord.charAt(j) <= 'z' || currWord.charAt(j) == '_') {
                word.append(currWord.charAt(j));
                if (j == currWord.length() - 1 || (j + 1 < currWord.length() && specialSymbols.containsKey(String.valueOf(currWord.charAt(j + 1)))) && isNAN(word.toString())) {
                    table.put(word.toString(), "ID");
                    tokensString.append(currWord);
                    tokensString.append(" ");
                    word.delete(0, word.length());
                }
            }
        }
    }

    private static void printRestOfInputProgram(String inputProgram, int index) {
        for (int i = index; i < inputProgram.length(); i++) {
            System.out.print(inputProgram.charAt(i));
        }
    }

    public static Map<String, String> getTable() {
        return table;
    }

    public static void init() {
        table = new LinkedHashMap<String, String>();
        tokensString = new StringBuilder();

        keyWords = new HashMap<String, String>();
        keyWords.put("AND", "and_LC");
        keyWords.put("ARRAY", "array_LC");
        keyWords.put("BEGIN", "begin_LC");
        keyWords.put("CONST", "const_LC");
        keyWords.put("DIV", "div_LC");
        keyWords.put("DO", "do_LC");
        keyWords.put("ELSE", "else_LC");
        keyWords.put("END", "end_LC");
        keyWords.put("FOR", "for_LC");
        keyWords.put("IF", "if_LC");
        keyWords.put("MOD", "mod_LC");
        keyWords.put("NOT", "not_LC");
        keyWords.put("OF", "of_LC");
        keyWords.put("OR", "or_LC");
        keyWords.put("PROGRAM", "program_LC");
        keyWords.put("THEN", "then_LC");
        keyWords.put("TO", "to_LC");
        keyWords.put("TYPE", "type_LC");
        keyWords.put("VAR", "var_LC");
        keyWords.put("WHILE", "while_LC");

        predefinedWords = new HashMap<String, String>();
        predefinedWords.put("CHAR", "char_LC");
        predefinedWords.put("INTEGER", "integer_LC");

        specialSymbols = new HashMap<String, String>();
        specialSymbols.put("+", "AOP");
        specialSymbols.put("-", "AOP");
        specialSymbols.put("*", "AOP");
        specialSymbols.put("=", "LOP");
        specialSymbols.put("<", "LOP");
        specialSymbols.put("<=", "LOP");
        specialSymbols.put(">", "LOP");
        specialSymbols.put(">=", "LOP");
        specialSymbols.put("<>", "LOP");
        specialSymbols.put(".", "DOT");
        specialSymbols.put(",", "COMMA");
        specialSymbols.put(":", "COLON");
        specialSymbols.put(";", "SEMICOLON");
        specialSymbols.put(":=", "ASSIGN");
        specialSymbols.put("..", "DOUBLE_COMMA");
        specialSymbols.put("(", "LPAR");
        specialSymbols.put(")", "RPAR");
        specialSymbols.put("[", "SQLPAR");
        specialSymbols.put("]", "SQRPAR");
    }
}