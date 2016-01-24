package com.dnu.compiler.service;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Stack;

public class Semantic {
    private static int labelIndex = 0;
    private static int tetradIndex = 0;
    private static int subRageIndex = 0;
    private static int attrArrayIndex = 0;
    private static String NUMBERS = "1234567890";
    private static Stack<String> ss1 = new Stack<String>();
    private static Stack<String> ss2 = new Stack<String>();
    private static Stack<String> ss3 = new Stack<String>();
    private static Stack<String> ss4 = new Stack<String>();
    private static StringBuilder code = new StringBuilder();
    private static Map<String, String> table = new LinkedHashMap<String, String>();

    public static void FirstPoint(String lexeme) {
        if (table.containsKey(lexeme)) {
            ResultHandler.append("Семантическая ошибка! Не уникальное имя константы!");
            return;
        }

        table.put(lexeme, Lexical.getTable().get(lexeme) + " - AtrConst");
        ss1.push(lexeme);
    }

    public static void SecondPoint() {
        String value = ss1.pop();
        String name = ss1.pop();
        table.put(name, table.get(name) + " | " + value);

        if (Lexical.getTable().get(value).equals("INT")) {
            table.put(value, Lexical.getTable().get(value) + " - AtrInt | " + Integer.parseInt(value));
        } else {
            table.put(value, Lexical.getTable().get(value) + " - AtrStrConst");
        }
    }

    public static void ThirdPoint(String lexeme) {
        ss1.push(lexeme);
    }

    public static void FourthPoint(String lexeme) {
        if (table.containsKey(lexeme)) {
            ResultHandler.append("Семантическая ошибка! Не уникальное имя типа!");
            return;
        }

        table.put(lexeme, Lexical.getTable().get(lexeme) + " - AtrUserType");
        ss1.push(lexeme);
    }

    public static void FifthPoint(String lexeme) {
        if (!table.containsKey(lexeme)) {
            ResultHandler.append("Семантическая ошибка! Не определенное имя!");
            return;
        }

        String attr = table.get(lexeme).split(" - ")[1].split(" \\| ")[0];
        if (!attr.equals("AtrUserType") && !attr.equals("AtrStType")) {
            ResultHandler.append("Семантическая ошибка! Не имеет типа!");
            return;
        }

        ss1.push(lexeme);
        ss2.push(table.get(lexeme).split(" - ")[1].split(" \\| ")[2]);
    }

    public static void SixthPoint(String lexeme) {
        ss1.push(lexeme);
    }

    public static void SeventhPoint(String lexeme) {
        int bottom = Integer.parseInt(ss1.pop());
        int top = Integer.parseInt(lexeme);

        if (bottom > top) {
            ResultHandler.append("Семантическая ошибка! Нижняя граница больше верхней!");
            return;
        }

        if (!table.containsKey("INTEGER")) {
            table.put("INTEGER", "Предопределенное слово INTEGER - AtrStType | 2 | -32768 | 32767");
            table.put("-32768", "INT - AtrInt | " + Integer.toBinaryString(-32768));
            table.put("32767", "INT - AtrInt | " + Integer.toBinaryString(32767));
        }

        table.put(String.valueOf(bottom), "INT" + " - " + "AtrInt" + " | " + Integer.toBinaryString(bottom));
        table.put(String.valueOf(top), "INT" + " - " + "AtrInt" + " | " + Integer.toBinaryString(top));

        ++subRageIndex;
        String subRage = "#SubRage" + subRageIndex;
        table.put(subRage, " " + " - " + subRage + " | " + "INTEGER" + " | " + bottom + " | " + top);

        if (table.get(ss1.peek()).split(" \\| ").length < 4) {
            table.put(ss1.peek(), table.get(ss1.peek()) + " | " + subRage + " | 2 | " + bottom + " | " + top);
        }

        ss1.push(subRage);
    }

    public static void EighthPoint(String lexeme) {
        if (!table.containsKey(lexeme)) {
            ResultHandler.append("Семантическая ошибка! Не определенное имя!");
            return;
        }

        String atr = table.get(lexeme).split(" - ")[1].split(" \\| ")[0];
        if (!atr.equals("AtrUserType") && !atr.equals("AtrStType")) {
            ResultHandler.append("Семантическая ошибка! Не имеет типа!");
            return;
        }

        if (table.get(lexeme).split(" \\| ").length < 4 || table.get(lexeme).split(" - ")[1].split(" \\| ")[3].equals("NIL")) {
            ResultHandler.append("Семантическая ошбика! Тип индекса не пронумерован");
            return;
        }

        ss1.push(lexeme);

        int max = Integer.parseInt(table.get(lexeme).split(" - ")[1].split(" \\| ")[4]);
        int min = Integer.parseInt(table.get(lexeme).split(" - ")[1].split(" \\| ")[3]);
        ss2.push(String.valueOf((max - min) + 1));
    }

    public static void NinthPoint(String lexeme) {
        SeventhPoint(lexeme);

        String subRage = "#SubRage" + subRageIndex;
        int bottom = Integer.parseInt(table.get(subRage).split(" - ")[1].split(" \\| ")[2]);
        int top = Integer.parseInt(table.get(subRage).split(" - ")[1].split(" \\| ")[3]);
        ss2.push(String.valueOf((top - bottom) + 1));
    }

    public static void TenthPoint(String lexeme) {
        String typeOfElement;
        if (lexeme.equals("INTEGER") || lexeme.equals("CHAR")) {
            typeOfElement = lexeme;
        } else {
            typeOfElement = ss1.pop();
        }

        String lengthOfElement = ss2.pop();
        String typeOfIndex = ss1.pop();

        String countOfElement;
        if (ss2.size() > 0) {
            countOfElement = ss2.pop();
        } else {
            if (lexeme.equals("ARRAY") && table.get(ss1.peek()).split(" - ")[1].split(" \\| ").length >= 3) {
                countOfElement = table.get(ss1.peek()).split(" \\| ")[2];
            } else if (typeOfElement.equals("INTEGER") || typeOfElement.equals("CHAR")) {
                setInt();
                setChar();

                countOfElement = table.get(lexeme).split(" \\| ")[1];
            } else {
                countOfElement = "2";
            }
        }

        ++attrArrayIndex;
        String atrArray = "#AtrArray" + attrArrayIndex;
        table.put(atrArray, " " + " - " + atrArray + " | " + typeOfIndex + " | " + typeOfElement);

        ss1.push(atrArray);
        ss2.push(String.valueOf((Integer.parseInt(lengthOfElement) * Integer.parseInt(countOfElement))));
    }

    public static void EleventhPoint(String lexeme) {
        setInt();
        setChar();

        String typeOfExpression = ss1.pop();

        String lengthOfType = "NIL";
        if (ss2.size() > 0) {
            lengthOfType = ss2.pop();
        }

        String nameOfType;
        if (ss1.size() == 0) {
            nameOfType = lexeme;
        } else {
            nameOfType = ss1.pop();
        }

        if (nameOfType.equals("INTEGER") || nameOfType.equals("CHAR")) {
            String length = table.get(nameOfType).split(" \\| ")[1];
            table.put(typeOfExpression, "ID - AtrUserType | " + length + " | " + nameOfType + " | NIL | NIL");
        }

        if (!lengthOfType.equals("NIL")) {
            if (table.get(typeOfExpression).split(" \\| ").length != 5) {
                table.put(nameOfType, "ID - AtrUserType | " + typeOfExpression + " | " + lengthOfType + " | NIL | NIL");
            } else {
                String MIN = table.get(typeOfExpression).split(" \\| ")[3], MAX = table.get(typeOfExpression).split(" \\| ")[4];
                table.put(nameOfType, "ID - AtrUserType | " + typeOfExpression + " | " + lengthOfType + " | " + MIN + " | " + MAX);
            }
        }
    }

    public static void TwelfthPoint(String lexeme) {
        if (table.containsKey(lexeme)) {
            ResultHandler.append("Семантическая ошибка! Не уникальное имя переменной!");
            return;
        }

        table.put(lexeme, Lexical.getTable().get(lexeme) + " - " + "AtrVar");
        ss1.push("NIL");
        ss1.push(lexeme);
    }

    public static void ThirteenthPoint(String lexeme) {
        if (table.containsKey(lexeme)) {
            ResultHandler.append("Семантическая ошибка! Не уникальное имя переменной!");
            return;
        }

        table.put(lexeme, Lexical.getTable().get(lexeme) + " - " + "AtrVar");
        ss1.push(lexeme);
    }

    public static void FourteenthPoint(String lexeme) {
        setInt();
        setChar();

        if (ss2.size() == 0) {
            if (table.get(ss1.peek()).split(" - ")[1].split(" \\| ").length >= 2) {
                ss2.push(table.get(ss1.peek()).split(" - ")[1].split(" \\| ")[1]);
            } else {
                ss2.push(lexeme);
            }
        }

        String typeOfExpression = ss1.pop();
        String lengthOfType = ss2.pop();

        if (lengthOfType.equals("INTEGER")) {
            table.put(typeOfExpression, "ID - AtrVar | 2 | " + lengthOfType);
            typeOfExpression = "2";
        } else if (lengthOfType.equals("CHAR")) {
            table.put(typeOfExpression, "ID - AtrVar | 1 | " + lengthOfType);
            typeOfExpression = "1";
        }

        while (!ss1.peek().equals("NIL")) {
            if (NUMBERS.contains(String.valueOf(lengthOfType.charAt(0)))) {
                String temp = lengthOfType;
                lengthOfType = typeOfExpression;
                typeOfExpression = temp;
            }

            String varID = ss1.pop();
            table.put(varID, "ID - AtrVar | " + typeOfExpression + " | " + lengthOfType);
        }
        ss1.pop();
    }

    public static void FifteenthPoint(String lexeme) {
        ss1.push(lexeme);
    }

    public static void SixteenthPoint(String lexeme, String nextLexeme) {
        if (!table.containsKey(lexeme)) {
            ResultHandler.append("Семантическая ошибка! Не определенное имя!");
            return;
        }

        if (table.get(lexeme).split(" - ")[1].split(" \\| ")[0].equals("AtrConst") && nextLexeme.equals(":=")) {
            ResultHandler.append("Семантическая ошибка! Константе нельзя присваивать значение!");
            return;
        }

        String atr = table.get(lexeme).split(" - ")[1].split(" \\| ")[0];
        if (!atr.equals("AtrVar") && !atr.equals("AtrConst") || table.get(lexeme).split(" - ").length < 1) {
            ResultHandler.append("Семантическая ошибка! Имя не переменной и не имя константы!");
            return;
        }

        ss1.push(lexeme);
    }

    public static void SeventeenthPoint(String lexeme) {
        ss1.push(lexeme);
    }

    public static void EighteenPoint() {
        if (ss1.size() >= 3) {
            String rightOperand = ss1.pop();
            String operation = ss1.pop();
            String leftOperand = ss1.pop();
            String typeOfResult = ResultType(operation, leftOperand, rightOperand);
            if (typeOfResult == null) {
                return;
            }
            checkType(typeOfResult);

            String length;
            if (!typeOfResult.equals("NIL")) length = table.get(typeOfResult).split(" \\| ")[1];
            else length = "NIL";

            ++tetradIndex;
            String tetrad = "#t" + tetradIndex;

            addGaps();
            code.append(tetrad).append(" := ").append(leftOperand).append(" ").append(operation).append(" ").append(rightOperand).append("\n");

            if (!length.equals("NIL")) {
                table.put(tetrad, leftOperand + " " + operation + " " + rightOperand + " - AtrVar | " + length + " | " + typeOfResult);
            } else {
                table.put(tetrad, leftOperand + " " + operation + " " + rightOperand + " - AtrVar");
            }
            ss1.push(tetrad);
        }
    }

    public static void NineteenthPoint(){
        if (ss1.size() >= 2) {
            String value = ss1.pop();
            String id = ss1.pop();

            if (!CheckAssignment(id, value)) {
                ResultHandler.append("Семантическая ошибка! Несоответсвие типов!");
                return;
            }

            addGaps();
            code.append(id).append(" := ").append(value).append("\n");

            table.put(id, table.get(id).split(" \\| ")[0] + " | " + table.get(id).split(" \\| ")[1] + " | " + table.get(id).split(" \\| ")[2] + " | " + value);
        } else {
            ResultHandler.append("Семантическая ошибка! Неверное выражение!");
            return;
        }
    }

    public static void Twentieth() {
        ++labelIndex;
        String label = "#l" + labelIndex;
        ss3.push(label);
    }

    public static void TwentyFirst() {
        if (ss1.size() >= 3) {
            String leftOperand = ss1.remove(0);
            String operation = ss1.remove(0);
            String rightOperand = ss1.remove(0);
            String typeOfResult = ResultType(operation, leftOperand, rightOperand);
            if (typeOfResult == null) {
                return;
            }
            String length;
            if (!typeOfResult.equals("NIL"))
                length = table.get(typeOfResult).split(" \\| ")[1];
            else length = "NIL";

            ++tetradIndex;
            String tetrad = "#t" + tetradIndex;

            addGaps();
            code.append(tetrad).append(" := ").append(leftOperand).append(" ").append(operation).append(" ").append(rightOperand).append("\n");

            if (!length.equals("NIL")) {
                table.put(tetrad, leftOperand + " " + operation + " " + rightOperand + " - AtrVar | " + length + " | " + typeOfResult);
            } else {
                table.put(tetrad, leftOperand + " " + operation + " " + rightOperand + " - AtrVar");
            }
            ss1.push(tetrad);

            TwentyFirstLoop();

            addGaps();
            code.append("IF " + "#t").append(tetradIndex).append(" = false GO #l").append(1 + labelIndex).append("\n");
        } else {
            ResultHandler.append("Семантическая ошибка! Неверное выражение!");
            return;
        }
    }


    private static void TwentyFirstLoop() {
        int ss1Size = ss1.size() - 1;
        String first = ss1.remove(ss1Size);
        while (ss1Size != 0) {

            String leftOperand = first;
            String operation = ss1.remove(0);
            String rightOperand = ss1.remove(0);

            ++tetradIndex;
            String tetrad = "#t" + tetradIndex;

            addGaps();
            code.append(tetrad).append(" := ").append(leftOperand).append(" ").append(operation).append(" ").append(rightOperand).append("\n");

            table.put(tetrad, leftOperand + " " + operation + " " + rightOperand + " - AtrVar");

            first = tetrad;
            ss1Size = ss1.size();
        }
    }

    public static void TwentySecond() {
        addGaps();
        code.append("GO ").append(ss3.peek()).append("\n");
        addGaps();
        code.append("#l").append(1 + labelIndex).append(" : ").append("\n");
    }

    public static void TwentyThird() {
        addGaps();
        code.append(ss3.peek()).append(" : ").append("\n");
        ss3.pop();
    }

    public static void TwentyFourth() {
        ++labelIndex;
        String label = "#l" + labelIndex;
        ss3.push(label);
        addGaps();
        code.append(label).append(" : ").append("\n");
    }

    public static void TwentyFifth(String lexeme) {
        if (!table.containsKey(lexeme)) {
            ResultHandler.append("Семантическая ошибка! Не определенное имя!");
            return;
        }

        if (!table.get(lexeme).split(" - ")[1].split(" \\| ")[0].equals("AtrVar")) {
            ResultHandler.append("Семантическая ошибка! Имя не переменной!");
            return;
        }

        if (!table.get(lexeme).split(" \\| ")[2].equals("INTEGER")) {
            ResultHandler.append("Семантическая ошибка! Недопустимый тип параметра цикла!");
            return;
        }

        ++labelIndex;
        ss3.push("#l" + labelIndex);

        ss4.push(lexeme);
    }

    public static void TwentySixth() {
        String initialValue = ss1.pop();

        if (!CheckAssignment(ss4.peek(), initialValue)) {
            ResultHandler.append("Семантическая ошибка! Несоответсвие типов!");
            return;
        }

        addGaps();
        code.append(ss4.peek()).append(" := ").append(initialValue).append("\n");
        addGaps();
        code.append(ss3.peek()).append(" : ").append("\n");
    }

    public static void TwentySeventh() {
        String finalValue = ss1.pop();
        addGaps();
        code.append("IF ").append(ss4.peek()).append(" > ").append(finalValue).append(" GO #l").append(1 + labelIndex).append("\n");
    }

    public static void TwentyEighth() {
        String param = ss4.pop();
        addGaps();
        code.append(param).append(" := ").append(param).append(" + 1 ").append("\n");
        addGaps();
        code.append("GO ").append(ss3.peek()).append("\n");
        addGaps();
        code.append("#l").append(1 + labelIndex).append(" : ").append("\n");
        ss3.pop();
    }

    private static String ResultType(String operation, String leftOperand, String rightOperand) {
        String typeOfLeftOperand = CheckTypeOfOperand(leftOperand);
        String typeOfRightOperand = CheckTypeOfOperand(rightOperand);

        boolean checkIntOperation = operation.equals("+") || operation.equals("-") || operation.equals("*") || operation.equals("OR") || operation.equals("DIV") || operation.equals("MOD") || operation.equals("AND");

        if (typeOfLeftOperand.equals("INTEGER") && typeOfRightOperand.equals("INTEGER")) {
            if (checkIntOperation) {
                return "INTEGER";
            } else {
                return "NIL";
            }
        } else if (typeOfLeftOperand.equals("CHAR") && typeOfRightOperand.equals("CHAR")) {
            if (operation.equals("+")) {
                return "CHAR";
            } else {
                return "NIL";
            }
        } else {
            ResultHandler.append("Семантическая ошибка! Неверное выражение!");
            return null;
        }
    }

    private static String CheckTypeOfOperand(String operand) {
        if ((operand.length() >= 2 && !("#t").equals(operand.substring(0, 2))) || operand.length() < 3) {
            String value = Lexical.getTable().get(operand);

            if (value.equals("ID") || value.equals("INT") || value.equals("CHR")) {

                boolean isInteger = Lexical.getTable().get(operand).equals("INT");
                boolean isConstAndInteger = table.get(operand).split(" - ")[1].split(" \\| ")[0].equals("AtrConst")
                        && Lexical.getTable().get(table.get(operand).split(" - ")[1].split(" \\| ")[1]).equals("INT");
                boolean isVarAndInteger = table.get(operand).split(" - ")[1].split(" \\| ")[0].equals("AtrVar")
                        && table.get(operand).split(" - ")[1].split(" \\| ")[2].equals("INTEGER");

                if (isInteger || isConstAndInteger || isVarAndInteger) {
                    return "INTEGER";
                } else {
                    return "CHAR";
                }
            } else {
                ResultHandler.append("Семантическая ошибка! Неверное выражение!");
                return null;
            }
        } else {
            return table.get(operand).split(" \\| ")[2];
        }
    }

    private static boolean CheckAssignment(String id, String value) {
        if (NUMBERS.contains(id)) {
            ResultHandler.append("Семантическая ошибка! Неверное выражение!");
            return false;
        }

        String idType = table.get(id).split(" - ")[1].split(" \\| ")[2];
        if (id.length() >= 2 && id.substring(0, 2).equals("#t")) idType = table.get(id).split(" \\| ")[2];
        String valueType = table.get(value).split(" - ")[0];
        if (valueType.equals("ID")) valueType = table.get(id).split(" - ")[1].split(" \\| ")[2];
        else if (value.length() >= 2 && value.substring(0, 2).equals("#t")) valueType = table.get(value).split(" \\| ")[2];

        return ((idType.equals("INTEGER") && (valueType.equals("INT") || valueType.equals("INTEGER"))) || (idType.equals("CHAR") && (valueType.equals("CHR") || valueType.equals("CHAR"))));
    }

    public static void CompleteTable() {
        for (Map.Entry<String, String> lexeme : Lexical.getTable().entrySet()) {
            if (!table.containsKey(lexeme.getKey())) {
                if (lexeme.getValue().equals("ID") && !table.containsKey(lexeme.getKey())) continue;

                table.put(lexeme.getKey(), lexeme.getValue());

                if (lexeme.getValue().equals("INT")) {
                    table.put(lexeme.getKey(), lexeme.getValue() + " - " + "AtrInt | " + Integer.toBinaryString(Integer.parseInt(lexeme.getKey())));
                }

                if (lexeme.getValue().equals("CHR")) {
                    table.put(lexeme.getKey(), lexeme.getValue() + " - " + "AtrStrConst");
                }
            }
        }
    }

    private static void setInt() {
        if (!table.containsKey("INTEGER") && Lexical.getTable().containsKey("INTEGER")) {
            table.put("INTEGER", "Предопределенное слово INTEGER - AtrStType | 2 | -32768 | 32767");
            table.put("-32768", "INT - AtrInt | " + Integer.toBinaryString(-32768));
            table.put("32767", "INT - AtrInt | " + Integer.toBinaryString(32767));
        }
    }

    private static void setChar() {
        if (!table.containsKey("CHAR") && Lexical.getTable().containsKey("CHAR")) {
            table.put("CHAR", "Предопределенное слово CHAR - AtrStType | 1 | 0 | 255");
            table.put("0", "INT - AtrInt | " + Integer.toBinaryString(0));
            table.put("255", "INT - AtrInt | " + Integer.toBinaryString(255));
        }
    }

    private static void checkType(String typeOfResult) {
        if (typeOfResult.equals("INTEGER")) {
            if (!table.containsKey("INTEGER")) {
                table.put("INTEGER", "Предопределенное слово INTEGER - AtrStType | 2 | -32768 | 32767");
                table.put("-32768", "INT - AtrInt | " + Integer.toBinaryString(-32768));
                table.put("32767", "INT - AtrInt | " + Integer.toBinaryString(32767));
            }
        } else if (typeOfResult.equals("CHAR")) {
            if (!table.containsKey("CHAR")) {
                table.put("CHAR", "Предопределенное слово CHAR - AtrStType | 1 | 0 | 255");
                table.put("0", "INT - AtrInt | " + Integer.toBinaryString(0));
                table.put("255", "INT - AtrInt | " + Integer.toBinaryString(255));
            }
        }
    }

    public static Map<String, String> GetTable() {
        return table;
    }

    public static String GetCode() {
        return code.toString();
    }

    private static void addGaps() {
        for (int i = Syntax.getDepth() - 1; i >= 0; --i)
            code.append(" ");
    }

    public static void init() {
        labelIndex = 0;
        tetradIndex = 0;
        subRageIndex = 0;
        attrArrayIndex = 0;
        NUMBERS = "1234567890";
        ss1 = new Stack<String>();
        ss2 = new Stack<String>();
        ss3 = new Stack<String>();
        ss4 = new Stack<String>();
        code = new StringBuilder();
        table = new LinkedHashMap<String, String>();
    }
}