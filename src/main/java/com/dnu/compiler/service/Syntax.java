package com.dnu.compiler.service;

public class Syntax {
    private static int depth;

    public static void Parse() {
        Program();
    }

    private static void Program() {
        String lexeme;
        if (!getLexeme().equals("PROGRAM")) {
            ResultHandler.append("Ошибка! Ключевое слово PROGRAM отсутствует!");
            return;
        }
        ResultHandler.defaultAppend(" PROGRAM ");

        lexeme = getLexeme();
        if (Lexical.getTable().get(lexeme) != null && !Lexical.getTable().get(lexeme).equals("ID")) {
            ResultHandler.append("Ошибка! Идентификатор программы не задан!");
            return;
        }
        ResultHandler.defaultAppend(" " + lexeme + " ");

        if (!getLexeme().equals(";")) {
            ResultHandler.append("Ошибка! Отсутсвует точка с запятой после идентифакатора программы!");
            return;
        }
        ResultHandler.defaultAppend(" ; ");

        Block();

        if (!getLexeme().equals(".")) {
            ResultHandler.append("Ошибка! Отсутсвует точка после блока программы!");
        }
        ResultHandler.defaultAppend(" . ");
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
        ResultHandler.defaultAppend("\n " + getLexeme() + " ");
        TypeDefinition();
        if (!getLexeme().equals(";")) {
            ResultHandler.append("Ошибка! Отсутсвует символ ; после определения типа!");
            return;
        }
        ResultHandler.defaultAppend(" ; ");

        String lexeme = getFirstLexeme();
        while (Lexical.getTable().get(lexeme) != null && Lexical.getTable().get(lexeme).equals("ID")) {
            TypeDefinition();
            if (!getLexeme().equals(";")) {
                ResultHandler.append("Ошибка! Отсутсвует символ ; после определения типа!");
                return;
            }
            ResultHandler.defaultAppend(" ; ");
            lexeme = getFirstLexeme();
        }
    }

    private static void DefiningConstants() {
        depth = 4;
        ResultHandler.defaultAppend("\n " + getLexeme() + " ");
        ConstDefinition();
        if (!getLexeme().equals(";")) {
            ResultHandler.append("Ошибка! Отсутсвует символ ; после определения константы!");
            return;
        }
        ResultHandler.defaultAppend(" ; ");
        Semantic.SecondPoint();

        String lexeme = getFirstLexeme();
        while (Lexical.getTable().get(lexeme) != null && Lexical.getTable().get(lexeme).equals("ID")) {
            ConstDefinition();
            if (!getLexeme().equals(";")) {
                ResultHandler.append("Ошибка! Отсутсвует символ ; после определения константы!");
                return;
            }
            ResultHandler.defaultAppend(" ; ");
            Semantic.SecondPoint();
            lexeme = getFirstLexeme();
        }
    }

    private static void DeclaringVariables() {
        depth = 4;
        ResultHandler.defaultAppend("\n " + getLexeme() + " ");
        VarDefinition();
        if (!getLexeme().equals(";")) {
            ResultHandler.append("Ошибка! Отсутсвует символ ; после определения переменной!");
            return;
        }
        ResultHandler.defaultAppend(" ; ");

        String lexeme = getFirstLexeme();
        while (Lexical.getTable().get(lexeme) != null && Lexical.getTable().get(lexeme).equals("ID")) {
            VarDefinition();
            if (!getLexeme().equals(";")) {
                ResultHandler.append("Ошибка! Отсутсвует символ ; после определения переменной!");
                return;
            }
            ResultHandler.defaultAppend(" ; ");
            lexeme = getFirstLexeme();
        }
    }

    private static void TypeDefinition() {
        String lexeme = getLexeme();
        if (Lexical.getTable().get(lexeme) != null && !Lexical.getTable().get(lexeme).equals("ID")) {
            ResultHandler.append("Ошибка! Идентификатор типа отсутсвует!");
            return;
        }
        ResultHandler.defaultAppend("\n");
        printSpaces(depth + 1);
        ResultHandler.defaultAppend(lexeme + " ");
        Semantic.FourthPoint(lexeme);

        if (!getLexeme().equals("=")) {
            ResultHandler.append("Ошибка! Символ = после идентификатора типа отсутствует!");
            return;
        }
        ResultHandler.defaultAppend(" = ");

        String nextLexeme = getFirstLexeme();
        Type();
        Semantic.EleventhPoint(nextLexeme);
    }

    private static void ConstDefinition() {
        String lexeme = getLexeme();
        if (Lexical.getTable().get(lexeme) != null && !Lexical.getTable().get(lexeme).equals("ID")) {
            ResultHandler.append("Ошибка! Идентификатор константы отсутсвует!");
            return;
        }
        ResultHandler.defaultAppend("\n");
        printSpaces(depth + 1);
        ResultHandler.defaultAppend(lexeme + " ");
        Semantic.FirstPoint(lexeme);

        if (!getLexeme().equals("=")) {
            ResultHandler.append("Ошибка! Символ = после идентификатора константы отсутствует!");
            return;
        }
        ResultHandler.defaultAppend(" = ");

        Const();
    }

    private static void Const() {
        String lexeme = getFirstLexeme();
        if (Lexical.getTable().get(lexeme) != null && Lexical.getTable().get(lexeme).equals("INT") || Lexical.getTable().get(lexeme).equals("CHR")) {
            ResultHandler.defaultAppend(" " + getLexeme() + " ");
            Semantic.ThirdPoint(lexeme);
        } else {
            ResultHandler.append("Ошибка! Указано неверное значение константы!");
            return;
        }
    }

    private static void VarDefinition() {
        ListOfId();

        if (!getLexeme().equals(":")) {
            ResultHandler.append("Ошибка! Отсутсвует символ : после объявления переменных!");
            return;
        }
        ResultHandler.defaultAppend(" : ");

        String lexeme = getFirstLexeme();
        Type();
        Semantic.FourteenthPoint(lexeme);
    }

    private static void CompositeAction() {
        if (!getLexeme().equals("BEGIN")) {
            ResultHandler.append("Ошибка! Отсутсвует ключевое слово BEGIN!");
            return;
        }
        ResultHandler.defaultAppend("\n");
        printSpaces(depth + 1);
        ResultHandler.defaultAppend("BEGIN \n");
        depth += 4;
        Semantic.CompleteTable();

        SequenceOfActions();

        if (!getLexeme().equals("END")) {
            ResultHandler.append("Ошибка! Отсутсвует ключевое слово END!");
            return;
        }
        depth -= 4;
        ResultHandler.defaultAppend("\n");
        printSpaces(depth);
        ResultHandler.defaultAppend(" END ");
    }

    private static void SequenceOfActions() {
        Action();

        String lexeme = getFirstLexeme();
        while (lexeme.equals(";")) {
            ResultHandler.defaultAppend(" " + getLexeme() + " \n");
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
            ResultHandler.append("Ошибка! Не указан оператор присваиния после имени переменной!");
            return;
        }
        ResultHandler.defaultAppend(" := ");

        Expression();
        Semantic.NineteenthPoint();
    }

    private static void ComplexAction() {
        String lexeme = getFirstLexeme();
        if (lexeme.equals("BEGIN")) {
            CompositeAction();
        } else if (lexeme.equals("IF")) {
            printSpaces(depth);
            ResultHandler.defaultAppend(" " + getLexeme() + " ");
            Semantic.Twentieth();

            Expression();
            Semantic.TwentyFirst();

            if (!getLexeme().equals("THEN")) {
                ResultHandler.append("Ошибка! Отсутсвует оператор THEN после логического выражения!");
                return;
            }
            ResultHandler.defaultAppend(" THEN ");

            Action();
            Semantic.TwentySecond();

            lexeme = getFirstLexeme();
            if (lexeme.equals("ELSE")) {
                ResultHandler.defaultAppend("\n");
                printSpaces(depth);
                ResultHandler.defaultAppend(" " + getLexeme() + " ");

                Action();
            }
            Semantic.TwentyThird();
        } else if (lexeme.equals("WHILE")) {
            printSpaces(depth);
            ResultHandler.defaultAppend(" " + getLexeme() + " ");
            Semantic.TwentyFourth();

            Expression();
            Semantic.TwentyFirst();

            if (!getLexeme().equals("DO")) {
                ResultHandler.append("Ошибка! Отсутсвует оператор DO после условия цикла WHILE!");
                return;
            }
            ResultHandler.defaultAppend(" DO ");

            Action();
            Semantic.TwentySecond();
        } else if (lexeme.equals("FOR")) {
            printSpaces(depth);
            ResultHandler.defaultAppend(" " + getLexeme() + " ");

            lexeme = getLexeme();
            if (Lexical.getTable().get(lexeme) != null && !Lexical.getTable().get(lexeme).equals("ID")) {
                ResultHandler.append("Ошибка! Отсутсвует переменная-счётчик для цикла FOR!");
                return;
            }
            ResultHandler.defaultAppend(" " + lexeme + " ");
            Semantic.TwentyFifth(lexeme);

            if (!getLexeme().equals(":=")) {
                ResultHandler.append("Ошибка! Отсутсвует оператор присваивания после переменной-счётчики цикла FOR!");
                return;
            }
            ResultHandler.defaultAppend(" := ");

            Expression();
            Semantic.TwentySixth();

            if (!getLexeme().equals("TO")) {
                ResultHandler.append("Ошибка! Отсутсвует оператор TO после переменной-счётчика цикла FOR!");
                return;
            }
            ResultHandler.defaultAppend(" TO ");

            Expression();
            Semantic.TwentySeventh();

            if (!getLexeme().equals("DO")) {
                ResultHandler.append("Ошибка! Отсутсвует оператор DO после выражения цикла FOR!");
                return;
            }
            ResultHandler.defaultAppend(" DO ");

            Action();
            Semantic.TwentyEighth();
        }
    }

    private static void Type() {
        String lexeme = getFirstLexeme();
        if (lexeme.equals("ARRAY")) {
            ResultHandler.defaultAppend(" " + getLexeme() + " ");
            if (!getLexeme().equals("[")) {
                ResultHandler.append("Ошибка! Отсутсвует символ [ после ключевого слова ARRAY!");
                return;
            }
            ResultHandler.defaultAppend(" [ ");

            RangeOfIndex();

            if (!getLexeme().equals("]")) {
                ResultHandler.append("Ошибка! Отсутствует сивол ] в указании диапазона массива!");
                return;
            }
            ResultHandler.defaultAppend(" ] ");

            if (!getLexeme().equals("OF")) {
                ResultHandler.append("Ошибка! Отсутсвует слово OF после указания диапазона массива!");
                return;
            }
            ResultHandler.defaultAppend(" OF ");

            String nextLexeme = getFirstLexeme();
            Type();
            Semantic.TenthPoint(nextLexeme);
        } else if (Lexical.getTable().get(lexeme) != null && Lexical.getTable().get(lexeme).equals("INT")) {
            ResultHandler.defaultAppend(" " + getLexeme() + " ");
            Semantic.SixthPoint(lexeme);
            if (!getLexeme().equals("..")) {
                ResultHandler.append("Ошибка! Не указан символ .. в указании типа!");
                return;
            }
            ResultHandler.defaultAppend(" .. ");

            lexeme = getLexeme();
            if (Lexical.getTable().get(lexeme) != null && !Lexical.getTable().get(lexeme).equals("INT")) {
                ResultHandler.append("Ошибка! Не указан диапазон в объявлении типа!");
                return;
            }
            ResultHandler.defaultAppend(" " + lexeme + " ");
            Semantic.SeventhPoint(lexeme);
        } else if (Lexical.getTable().get(lexeme) != null && Lexical.getTable().get(lexeme).equals("ID") || lexeme.equals("INTEGER") || lexeme.equals("CHAR")) {
            ResultHandler.defaultAppend(" " + getLexeme() + " ");
            if (Lexical.getTable().get(lexeme) != null && Lexical.getTable().get(lexeme).equals("ID")) {
                Semantic.FifthPoint(lexeme);
            }
        } else {
            ResultHandler.append("Ошибка! Ошибка в указании типа переменной!");
            return;
        }
    }

    private static void RangeOfIndex() {
        String lexeme = getFirstLexeme();
        if (Lexical.getTable().get(lexeme) != null && Lexical.getTable().get(lexeme).equals("INT")) {
            ResultHandler.defaultAppend(" " + getLexeme() + " ");
            Semantic.SixthPoint(lexeme);
            if (!getLexeme().equals("..")) {
                ResultHandler.append("Ошибка! Не указан символ .. в указании диапазона типа!");
                return;
            }
            ResultHandler.defaultAppend(" .. ");

            lexeme = getLexeme();
            if (Lexical.getTable().get(lexeme) != null && !Lexical.getTable().get(lexeme).equals("INT")) {
                ResultHandler.append("Ошибка! Не указан диапазон в объявлении типа!");
                return;
            }
            ResultHandler.defaultAppend(" " + lexeme + " ");
            Semantic.NinthPoint(lexeme);
        } else if (Lexical.getTable().get(lexeme) != null && Lexical.getTable().get(lexeme).equals("ID")) {
            ResultHandler.defaultAppend(" " + getLexeme() + " ");
            Semantic.EighthPoint(lexeme);
        } else {
            ResultHandler.append("Ошибка! Ошибка в объявлении диапазона индекса!");
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
            ResultHandler.defaultAppend(" " + getLexeme() + " ");
            Semantic.SeventeenthPoint(lexeme);
        } else {
            ResultHandler.append("Ошибка! Указан неверный знак отношения!");
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
            ResultHandler.defaultAppend(" " + getLexeme() + " ");
            Semantic.SeventeenthPoint(lexeme);
        } else {
            ResultHandler.append("Ошибка! Указана неверная аддитивная операция!");
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
            ResultHandler.defaultAppend(" " + getLexeme() + " ");
            Semantic.SeventeenthPoint(lexeme);
        } else {
            ResultHandler.append("Ошибка! Указана неверная мультипликативная операция!");
            return;
        }
    }

    private static void Factor() {
        String lexeme = getFirstLexeme();
        if (Lexical.getTable().get(lexeme) != null && (Lexical.getTable().get(lexeme).equals("INT") || Lexical.getTable().get(lexeme).equals("CHR"))) {
            ResultHandler.defaultAppend(" " + getLexeme() + " ");
            if (Lexical.getTable().get(lexeme).equals("INT") || Lexical.getTable().get(lexeme).equals("CHR")) {
                Semantic.FifteenthPoint(lexeme);
            }
        } else if (Lexical.getTable().get(lexeme) != null && Lexical.getTable().get(lexeme).equals("ID")) {
            Var();
        } else if (lexeme.equals("NOT")) {
            ResultHandler.defaultAppend(" " + getLexeme() + " ");
            Factor();
        } else if (lexeme.equals("(")) {
            ResultHandler.defaultAppend(" " + getLexeme() + " ");
            Expression();
            if (!getLexeme().equals(")")) {
                ResultHandler.append("Ошибка! Нет закрывающей скобки ) после выражения!");
                return;
            }
            ResultHandler.defaultAppend(" ) ");
        } else {
            ResultHandler.append("Ошибка! Неверный множитель!");
            return;
        }
    }

    private static void Var() {
        String lexeme = getLexeme();
        if (Lexical.getTable().get(lexeme) != null && !Lexical.getTable().get(lexeme).equals("ID")) {
            ResultHandler.append("Ошибка! Отсутсвует идентификатор переменной!");
            return;
        }
        ResultHandler.defaultAppend(" " + lexeme + " ");
        Semantic.SixteenthPoint(lexeme, getFirstLexeme());

        ComponentSelection();
    }

    private static void ComponentSelection() {
        if (getFirstLexeme().equals("[")) {
            ResultHandler.defaultAppend(" " + getLexeme() + " ");
            Expression();
            if (!getLexeme().equals("]")) {
                ResultHandler.append("Ошибка! Отсутсвует символ ]!");
                return;
            }
            ResultHandler.defaultAppend(" ] ");

            ComponentSelection();
        }
    }

    private static void ListOfId() {
        String lexeme = getLexeme();
        if (Lexical.getTable().get(lexeme) != null && !Lexical.getTable().get(lexeme).equals("ID")) {
            ResultHandler.append("Ошибка! Не указано имя переменной!");
            return;
        }
        ResultHandler.defaultAppend("\n");
        printSpaces(depth + 1);
        ResultHandler.defaultAppend(lexeme + " ");
        Semantic.TwelfthPoint(lexeme);

        lexeme = getFirstLexeme();
        while (lexeme.equals(",")) {
            ResultHandler.defaultAppend(" " + getLexeme() + " ");
            lexeme = getLexeme();
            if (Lexical.getTable().get(lexeme) != null && !Lexical.getTable().get(lexeme).equals("ID")) {
                ResultHandler.append("Ошибка! Не указано имя переменной после запятой!");
                return;
            }
            ResultHandler.defaultAppend(" " + lexeme + " ");
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