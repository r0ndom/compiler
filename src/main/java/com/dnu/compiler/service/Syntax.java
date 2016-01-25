package com.dnu.compiler.service;

public class Syntax {
    private static int depth;

    public static void Parse() {
        Program();
    }

    private static void Program() {
        String lexeme;
        if (!getLexeme().equals("PROGRAM")) {
            ResultHandler.append("Ошибка! Ключевое слово PROGRAM отсутствует!" + " Программа \n");
            return;
        }
        ResultHandler.defaultAppend(" PROGRAM " + " Программа \n");

        lexeme = getLexeme();
        if (Lexical.getTable().get(lexeme) != null && !Lexical.getTable().get(lexeme).equals("ID")) {
            ResultHandler.append("Ошибка! Идентификатор программы не задан!" + " Программа \n");
            return;
        }
        ResultHandler.defaultAppend(" " + lexeme + " " + " Программа \n");

        if (!getLexeme().equals(";")) {
            ResultHandler.append("Ошибка! Отсутсвует точка с запятой после идентифакатора программы!" + " Программа \n");
            return;
        }
        ResultHandler.defaultAppend(" ; " + " Программа \n");

        Block();

        if (!getLexeme().equals(".")) {
            ResultHandler.append("Ошибка! Отсутсвует точка после блока программы!" + " Программа \n");
        }
        ResultHandler.defaultAppend(" . " + " Программа \n");
    }

    private static void Block() {
        String lexeme = getFirstLexeme();
        if (lexeme.equals("TYPE")) DefiningTypes();
        lexeme = getFirstLexeme();
        if (lexeme.equals("CONST")) DefiningConstants();
        lexeme = getFirstLexeme();
        if (lexeme.equals("VAR")) DeclaringVariables();
        depth = 0;
        CompositeAction();
    }

    private static void DefiningTypes() {
        depth = 4;
        ResultHandler.defaultAppend("\n " + getLexeme() + " " + " Определение типа \n");
        TypeDefinition();
        if (!getLexeme().equals(";")) {
            ResultHandler.append("Ошибка! Отсутсвует символ ; после определения типа!" + " Определение типа \n");
            return;
        }
        ResultHandler.defaultAppend(" ; " + " Определение типа \n");

        String lexeme = getFirstLexeme();
        while (Lexical.getTable().get(lexeme) != null && Lexical.getTable().get(lexeme).equals("ID")) {
            TypeDefinition();
            if (!getLexeme().equals(";")) {
                ResultHandler.append("Ошибка! Отсутсвует символ ; после определения типа!" + " Определение типа \n");
                return;
            }
            ResultHandler.defaultAppend(" ; " + " Определение типа \n");
            lexeme = getFirstLexeme();
        }
    }

    private static void DefiningConstants() {
        depth = 4;
        ResultHandler.defaultAppend("\n " + getLexeme() + " " + " Определение констант \n");
        ConstDefinition();
        if (!getLexeme().equals(";")) {
            ResultHandler.append("Ошибка! Отсутсвует символ ; после определения константы!" + " Определение констант \n");
            return;
        }
        ResultHandler.defaultAppend(" ; " + " Определение констант \n");
        Semantic.SecondPoint();

        String lexeme = getFirstLexeme();
        while (Lexical.getTable().get(lexeme) != null && Lexical.getTable().get(lexeme).equals("ID")) {
            ConstDefinition();
            if (!getLexeme().equals(";")) {
                ResultHandler.append("Ошибка! Отсутсвует символ ; после определения константы!" + " Определение констант \n");
                return;
            }
            ResultHandler.defaultAppend(" ; " + " Определение констант \n");
            Semantic.SecondPoint();
            lexeme = getFirstLexeme();
        }
    }

    private static void DeclaringVariables() {
        depth = 4;
        ResultHandler.defaultAppend("\n " + getLexeme() + " " + " Определение переменных \n");
        VarDefinition();
        if (!getLexeme().equals(";")) {
            ResultHandler.append("Ошибка! Отсутсвует символ ; после определения переменной!" + " Определение переменных \n");
            return;
        }
        ResultHandler.defaultAppend(" ; " + " Определение переменных \n");

        String lexeme = getFirstLexeme();
        while (Lexical.getTable().get(lexeme) != null && Lexical.getTable().get(lexeme).equals("ID")) {
            VarDefinition();
            if (!getLexeme().equals(";")) {
                ResultHandler.append("Ошибка! Отсутсвует символ ; после определения переменной!" + " Определение переменных \n");
                return;
            }
            ResultHandler.defaultAppend(" ; " + " Определение переменных \n");
            lexeme = getFirstLexeme();
        }
    }

    private static void TypeDefinition() {
        String lexeme = getLexeme();
        if (Lexical.getTable().get(lexeme) != null && !Lexical.getTable().get(lexeme).equals("ID")) {
            ResultHandler.append("Ошибка! Идентификатор типа отсутсвует!" + " Определение типа \n");
            return;
        }
        ResultHandler.defaultAppend("\n");
        printSpaces(depth + 1);
        ResultHandler.defaultAppend(lexeme + " " + " Определение типа \n");
        Semantic.FourthPoint(lexeme);

        if (!getLexeme().equals("=")) {
            ResultHandler.append("Ошибка! Символ = после идентификатора типа отсутствует!" + " Определение типа \n");
            return;
        }
        ResultHandler.defaultAppend(" = " + " Определение типа \n");

        String nextLexeme = getFirstLexeme();
        Type();
        Semantic.EleventhPoint(nextLexeme);
    }

    private static void ConstDefinition() {
        String lexeme = getLexeme();
        if (Lexical.getTable().get(lexeme) != null && !Lexical.getTable().get(lexeme).equals("ID")) {
            ResultHandler.append("Ошибка! Идентификатор константы отсутсвует!" + " Определение константы \n");
            return;
        }
        ResultHandler.defaultAppend("\n");
        printSpaces(depth + 1);
        ResultHandler.defaultAppend(lexeme + " " + " Определение константы \n");
        Semantic.FirstPoint(lexeme);

        if (!getLexeme().equals("=")) {
            ResultHandler.append("Ошибка! Символ = после идентификатора константы отсутствует!" + " Определение константы \n");
            return;
        }
        ResultHandler.defaultAppend(" = " + " Определение константы \n");

        Const();
    }

    private static void Const() {
        String lexeme = getFirstLexeme();
        if (Lexical.getTable().get(lexeme) != null && Lexical.getTable().get(lexeme).equals("INT") || Lexical.getTable().get(lexeme).equals("CHR")) {
            ResultHandler.defaultAppend(" " + getLexeme() + " " + " Константа \n");
            Semantic.ThirdPoint(lexeme);
        } else {
            ResultHandler.append("Ошибка! Указано неверное значение константы!" + " Константа \n");
            return;
        }
    }

    private static void VarDefinition() {
        ListOfId();

        if (!getLexeme().equals(":")) {
            ResultHandler.append("Ошибка! Отсутсвует символ : после объявления переменных!" + " Определение переменной \n");
            return;
        }
        ResultHandler.defaultAppend(" : " + " Определение переменной \n");

        String lexeme = getFirstLexeme();
        Type();
        Semantic.FourteenthPoint(lexeme);
    }

    private static void CompositeAction() {
        if (!getLexeme().equals("BEGIN")) {
            ResultHandler.append("Ошибка! Отсутсвует ключевое слово BEGIN!" + " Составное действие \n");
            return;
        }
        ResultHandler.defaultAppend("\n");
        printSpaces(depth + 1);
        ResultHandler.defaultAppend("BEGIN" + " Составное действие \n");
        depth += 4;
        Semantic.CompleteTable();

        SequenceOfActions();

        if (!getLexeme().equals("END")) {
            ResultHandler.append("Ошибка! Отсутсвует ключевое слово END!" + " Составное действие \n");
            return;
        }
        depth -= 4;
        ResultHandler.defaultAppend("\n");
        printSpaces(depth);
        ResultHandler.defaultAppend(" END " + " Составное действие \n");
    }

    private static void SequenceOfActions() {
        Action();

        String lexeme = getFirstLexeme();
        while (lexeme.equals(";")) {
            ResultHandler.defaultAppend(" " + getLexeme() + " Последовательность действий \n");
            Action();
            lexeme = getFirstLexeme();
        }
    }

    private static void Action() {
        String lexeme = getFirstLexeme();
        if (Lexical.getTable().get(lexeme) != null && Lexical.getTable().get(lexeme).equals("ID")){
            SimpleAction();
        } else {
            ComplexAction();
        }
    }

    private static void SimpleAction() {
        AssignmentAction();
    }

    private static void AssignmentAction() {
        printSpaces(depth);
        Var();

        if (!getLexeme().equals(":=")) {
            ResultHandler.append("Ошибка! Не указан оператор присваиния после имени переменной!" + " Действие присваивания \n");
            return;
        }
        ResultHandler.defaultAppend(" := " + " Действие присваивания \n");

        Expression();
        Semantic.NineteenthPoint();
    }

    private static void ComplexAction() {
        String lexeme = getFirstLexeme();
        if (lexeme.equals("BEGIN")) {
            CompositeAction();
        } else if (lexeme.equals("IF")) {
            printSpaces(depth);
            ResultHandler.defaultAppend(" " + getLexeme() + " " + "Сложное действие \n");
            Semantic.Twentieth();

            Expression();
            Semantic.TwentyFirst();

            if (!getLexeme().equals("THEN")) {
                ResultHandler.append("Ошибка! Отсутсвует оператор THEN после логического выражения!" + "Сложное действие \n");
                return;
            }
            ResultHandler.defaultAppend(" THEN " + "Сложное действие \n");

            Action();
            Semantic.TwentySecond();

            lexeme = getFirstLexeme();
            if (lexeme.equals("ELSE")) {
                ResultHandler.defaultAppend("\n");
                printSpaces(depth);
                ResultHandler.defaultAppend(" " + getLexeme() + " " + "Сложное действие \n");

                Action();
            }
            Semantic.TwentyThird();
        } else if (lexeme.equals("WHILE")) {
            printSpaces(depth);
            ResultHandler.defaultAppend(" " + getLexeme() + " " + "Сложное действие \n");
            Semantic.TwentyFourth();

            Expression();
            Semantic.TwentyFirst();

            if (!getLexeme().equals("DO")) {
                ResultHandler.append("Ошибка! Отсутсвует оператор DO после условия цикла WHILE!" + "Сложное действие \n");
                return;
            }
            ResultHandler.defaultAppend(" DO " + "Сложное действие \n");

            Action();
            Semantic.TwentySecond();
        } else if (lexeme.equals("FOR")) {
            printSpaces(depth);
            ResultHandler.defaultAppend(" " + getLexeme() + " " + "Сложное действие \n");

            lexeme = getLexeme();
            if (Lexical.getTable().get(lexeme) != null && !Lexical.getTable().get(lexeme).equals("ID")) {
                ResultHandler.append("Ошибка! Отсутсвует переменная-счётчик для цикла FOR!" + "Сложное действие \n");
                return;
            }
            ResultHandler.defaultAppend(" " + lexeme + " " + "Сложное действие \n");
            Semantic.TwentyFifth(lexeme);

            if (!getLexeme().equals(":=")) {
                ResultHandler.append("Ошибка! Отсутсвует оператор присваивания после переменной-счётчики цикла FOR!" + "Сложное действие \n");
                return;
            }
            ResultHandler.defaultAppend(" := " + "Сложное действие \n");

            Expression();
            Semantic.TwentySixth();

            if (!getLexeme().equals("TO")) {
                ResultHandler.append("Ошибка! Отсутсвует оператор TO после переменной-счётчика цикла FOR!" + "Сложное действие \n");
                return;
            }
            ResultHandler.defaultAppend(" TO " + "Сложное действие \n");

            Expression();
            Semantic.TwentySeventh();

            if (!getLexeme().equals("DO")) {
                ResultHandler.append("Ошибка! Отсутсвует оператор DO после выражения цикла FOR!" + "Сложное действие \n");
                return;
            }
            ResultHandler.defaultAppend(" DO " + "Сложное действие \n");

            Action();
            Semantic.TwentyEighth();
        }
    }

    private static void Type() {
        String lexeme = getFirstLexeme();
        if (lexeme.equals("ARRAY")) {
            ResultHandler.defaultAppend(" " + getLexeme() + " " + " Тип \n");
            if (!getLexeme().equals("[")) {
                ResultHandler.append("Ошибка! Отсутсвует символ [ после ключевого слова ARRAY!" + " Тип \n");
                return;
            }
            ResultHandler.defaultAppend(" [ " + " Тип \n");

            RangeOfIndex();

            if (!getLexeme().equals("]")) {
                ResultHandler.append("Ошибка! Отсутствует сивол ] в указании диапазона массива!" + " Тип \n");
                return;
            }
            ResultHandler.defaultAppend(" ] " + " Тип \n");

            if (!getLexeme().equals("OF")) {
                ResultHandler.append("Ошибка! Отсутсвует слово OF после указания диапазона массива!" + " Тип \n");
                return;
            }
            ResultHandler.defaultAppend(" OF " + " Тип \n");

            String nextLexeme = getFirstLexeme();
            Type();
            Semantic.TenthPoint(nextLexeme);
        } else if (Lexical.getTable().get(lexeme) != null && Lexical.getTable().get(lexeme).equals("INT")) {
            ResultHandler.defaultAppend(" " + getLexeme() + " " + " Тип \n");
            Semantic.SixthPoint(lexeme);
            if (!getLexeme().equals("..")) {
                ResultHandler.append("Ошибка! Не указан символ .. в указании типа!" + " Тип \n");
                return;
            }
            ResultHandler.defaultAppend(" .. " + " Тип \n");

            lexeme = getLexeme();
            if (Lexical.getTable().get(lexeme) != null && !Lexical.getTable().get(lexeme).equals("INT")) {
                ResultHandler.append("Ошибка! Не указан диапазон в объявлении типа!" + " Тип \n");
                return;
            }
            ResultHandler.defaultAppend(" " + lexeme + " " + " Тип \n");
            Semantic.SeventhPoint(lexeme);
        } else if (Lexical.getTable().get(lexeme) != null && Lexical.getTable().get(lexeme).equals("ID") || lexeme.equals("INTEGER") || lexeme.equals("CHAR")) {
            ResultHandler.defaultAppend(" " + getLexeme() + " " + " Тип \n");
            if (Lexical.getTable().get(lexeme) != null && Lexical.getTable().get(lexeme).equals("ID")) {
                Semantic.FifthPoint(lexeme);
            }
        } else {
            ResultHandler.append("Ошибка! Ошибка в указании типа переменной!" + " Тип \n");
            return;
        }
    }

    private static void RangeOfIndex() {
        String lexeme = getFirstLexeme();
        if (Lexical.getTable().get(lexeme) != null && Lexical.getTable().get(lexeme).equals("INT")) {
            ResultHandler.defaultAppend(" " + getLexeme() + " " + " Диапазон индекса \n");
            Semantic.SixthPoint(lexeme);
            if (!getLexeme().equals("..")) {
                ResultHandler.append("Ошибка! Не указан символ .. в указании диапазона типа!" + " Диапазон индекса \n");
                return;
            }
            ResultHandler.defaultAppend(" .. " + " Диапазон индекса \n");

            lexeme = getLexeme();
            if (Lexical.getTable().get(lexeme) != null && !Lexical.getTable().get(lexeme).equals("INT")) {
                ResultHandler.append("Ошибка! Не указан диапазон в объявлении типа!" + " Диапазон индекса \n");
                return;
            }
            ResultHandler.defaultAppend(" " + lexeme + " " + " Диапазон индекса \n");
            Semantic.NinthPoint(lexeme);
        } else if (Lexical.getTable().get(lexeme) != null && Lexical.getTable().get(lexeme).equals("ID")) {
            ResultHandler.defaultAppend(" " + getLexeme() + " " + " Диапазон индекса \n");
            Semantic.EighthPoint(lexeme);
        } else {
            ResultHandler.append("Ошибка! Ошибка в объявлении диапазона индекса!" + " Диапазон индекса \n");
            return;
        }
    }

    private static void Expression() {
        SimpleExpression();

        String lexeme = getFirstLexeme();
        if (lexeme.equals("<") || lexeme.equals("<=") || lexeme.equals(">") || lexeme.equals(">=") || lexeme.equals("<>") || lexeme.equals("=")) {
            Symbol();
            SimpleExpression();
        }
    }

    private static void Symbol() {
        String lexeme = getFirstLexeme();
        if (lexeme.equals("<") || lexeme.equals("<=") || lexeme.equals(">") || lexeme.equals(">=") || lexeme.equals("<>") || lexeme.equals("=")) {
            ResultHandler.defaultAppend(" " + getLexeme() + " " + " Знак отношения \n");
            Semantic.SeventeenthPoint(lexeme);
        } else {
            ResultHandler.append("Ошибка! Указан неверный знак отношения!" + " Знак отношения \n");
            return;
        }
    }

    private static void SimpleExpression() {
        Term();

        String lexeme = getFirstLexeme();
        while (lexeme.equals("+") || lexeme.equals("-") || lexeme.equals("OR")) {
            AdditiveOperation();
            Term();
            Semantic.EighteenPoint();
            lexeme = getFirstLexeme();
        }
    }

    private static void AdditiveOperation() {
        String lexeme = getFirstLexeme();
        if (lexeme.equals("+") || lexeme.equals("-") || lexeme.equals("OR")) {
            ResultHandler.defaultAppend(" " + getLexeme() + " " + " Аддитивная операция \n");
            Semantic.SeventeenthPoint(lexeme);
        } else {
            ResultHandler.append("Ошибка! Указана неверная аддитивная операция!" + " Аддитивная операция \n");
            return;
        }
    }

    private static void Term() {
        Factor();

        String lexeme = getFirstLexeme();
        while (lexeme.equals("*") || lexeme.equals("DIV") || lexeme.equals("MOD") || lexeme.equals("AND")) {
            MultiplicativeOperation();
            Factor();
            Semantic.EighteenPoint();
            lexeme = getFirstLexeme();
        }
    }

    private static void MultiplicativeOperation() {
        String lexeme = getFirstLexeme();
        if (lexeme.equals("*") || lexeme.equals("DIV") || lexeme.equals("MOD") || lexeme.equals("AND")) {
            ResultHandler.defaultAppend(" " + getLexeme() + " " + " Мультипликативная операция \n");
            Semantic.SeventeenthPoint(lexeme);
        } else {
            ResultHandler.append("Ошибка! Указана неверная мультипликативная операция!" + " Мультипликативная операция \n");
            return;
        }
    }

    private static void Factor() {
        String lexeme = getFirstLexeme();
        if (Lexical.getTable().get(lexeme) != null && (Lexical.getTable().get(lexeme).equals("INT") || Lexical.getTable().get(lexeme).equals("CHR"))) {
            ResultHandler.defaultAppend(" " + getLexeme() + " " + " Множитель \n");
            if (Lexical.getTable().get(lexeme).equals("INT") || Lexical.getTable().get(lexeme).equals("CHR")) {
                Semantic.FifteenthPoint(lexeme);
            }
        } else if (Lexical.getTable().get(lexeme) != null && Lexical.getTable().get(lexeme).equals("ID")) {
            Var();
        } else if (lexeme.equals("NOT")) {
            ResultHandler.defaultAppend(" " + getLexeme() + " " + " Множитель \n");
            Factor();
        } else if (lexeme.equals("(")) {
            ResultHandler.defaultAppend(" " + getLexeme() + " " + " Множитель \n");
            Expression();
            if (!getLexeme().equals(")")) {
                ResultHandler.append("Ошибка! Нет закрывающей скобки ) после выражения!" + " Множитель \n");
                return;
            }
            ResultHandler.defaultAppend(" ) " + " Множитель \n");
        } else {
            ResultHandler.append("Ошибка! Неверный множитель!" + " Множитель \n");
            return;
        }
    }

    private static void Var() {
        String lexeme = getLexeme();
        if (Lexical.getTable().get(lexeme) != null && !Lexical.getTable().get(lexeme).equals("ID")) {
            ResultHandler.append("Ошибка! Отсутсвует идентификатор переменной!"  + " Переменная \n");
            return;
        }
        ResultHandler.defaultAppend(" " + lexeme + " " + " Переменная \n");
        Semantic.SixteenthPoint(lexeme, getFirstLexeme());

        ComponentSelection();
    }

    private static void ComponentSelection() {
        if (getFirstLexeme().equals("[")) {
            ResultHandler.defaultAppend(" " + getLexeme() + " " + " Выбор компоненты \n");
            Expression();
            if (!getLexeme().equals("]")) {
                ResultHandler.append("Ошибка! Отсутсвует символ ]!" + " Выбор компоненты \n");
                return;
            }
            ResultHandler.defaultAppend(" ] " + " Выбор компоненты \n");

            ComponentSelection();
        }
    }

    private static void ListOfId() {
        String lexeme = getLexeme();
        if (Lexical.getTable().get(lexeme) != null && !Lexical.getTable().get(lexeme).equals("ID")) {
            ResultHandler.append("Ошибка! Не указано имя переменной!" + " Список идентификаторов \n");
            return;
        }
        ResultHandler.defaultAppend("\n");
        printSpaces(depth + 1);
        ResultHandler.defaultAppend(lexeme + " " + " Список идентификаторов \n");
        Semantic.TwelfthPoint(lexeme);

        lexeme = getFirstLexeme();
        while (lexeme.equals(",")) {
            ResultHandler.defaultAppend(" " + getLexeme() + " " + " Список идентификаторов \n");
            lexeme = getLexeme();
            if (Lexical.getTable().get(lexeme) != null && !Lexical.getTable().get(lexeme).equals("ID")) {
                ResultHandler.append("Ошибка! Не указано имя переменной после запятой!" + " Список идентификаторов \n");
                return;
            }
            ResultHandler.defaultAppend(" " + lexeme + " " + " Список идентификаторов \n");
            Semantic.ThirteenthPoint(lexeme);
            lexeme = getFirstLexeme();
        }
    }

    private static String getLexeme() {
        String lexeme = getFirstLexeme();
        Lexical.tokensString.delete(0, lexeme.length() + 1);
        return lexeme;
    }

    private static String getFirstLexeme() {
        return Lexical.tokensString.toString().split(" ")[0];
    }

    private static void printSpaces(int spaceCount) {
        for (int i = 0; i < spaceCount; i++) {
            ResultHandler.defaultAppend(" ");
        }
    }

    public static int getDepth() {
        return depth;
    }

    public static void init() {
        depth = 0;
    }
}