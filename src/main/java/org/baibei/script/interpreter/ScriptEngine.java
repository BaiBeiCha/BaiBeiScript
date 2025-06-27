package org.baibei.script.interpreter;

import org.baibei.script.lexer.Lexer;
import org.baibei.script.lexer.Token;
import org.baibei.script.parser.Parser;
import org.baibei.script.parser.node.ASTNode;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class ScriptEngine {

    private final Context context;

    public ScriptEngine() {
        this.context = new Context(this, Paths.get("").toAbsolutePath());
        initializeBuiltins();
    }

    public ScriptEngine(Context context) {
        this.context = context;
        initializeBuiltins();
    }

    // Инициализация встроенных команд и функций
    private void initializeBuiltins() {
        context.addFunction("println", args -> {
            try {
                StringBuilder sb = new StringBuilder();
                for (Object arg : args) {
                    if (arg != null) {
                        if (arg instanceof Object[]) {
                            sb.append(Arrays.toString((Object[]) arg));
                        } else {
                            sb.append(arg.toString());
                        }
                    } else {
                        sb.append("null");
                    }
                    sb.append(" ");
                }
                System.out.println(sb.toString().trim());
                return null;
            } catch (Exception e) {
                throw new RuntimeException("println error: " + e.getMessage());
            }
        });

        context.addFunction("print", args -> {
            try {
                StringBuilder sb = new StringBuilder();
                for (Object arg : args) {
                    if (arg != null) {
                        if (arg instanceof Object[]) {
                            sb.append(Arrays.toString((Object[]) arg));
                        } else {
                            sb.append(arg.toString());
                        }
                    } else {
                        sb.append("null");
                    }
                    sb.append(" ");
                }
                System.out.print(sb.toString().trim());
                return null;
            } catch (Exception e) {
                throw new RuntimeException("print error: " + e.getMessage());
            }
        });

        context.addFunction("assert", (args) -> {
            if (args.length == 0) {
                throw new RuntimeException("Assert requires at least one argument");
            }

            boolean condition = Boolean.TRUE.equals(args[0]);
            if (!condition) {
                String message = args.length > 1 ? args[1].toString() : "Assertion failed";
                throw new RuntimeException(message);
            }
            return null;
        });

        context.addFunction("printArray", args -> {
            if (args.length == 0) {
                throw new RuntimeException("printArray requires an array");
            }
            Object array = args[0];
            if (!array.getClass().isArray()) {
                throw new RuntimeException("Argument must be an array");
            }

            int length = Array.getLength(array);
            for (int i = 0; i < length; i++) {
                Object element = Array.get(array, i);
                System.out.print(element + " ");
            }
            System.out.println();
            return null;
        });

        context.addFunction("arrayToString", args -> {
            if (args.length == 0) {
                throw new RuntimeException("arrayToString requires an array");
            }
            Object array = args[0];
            if (!array.getClass().isArray()) {
                throw new RuntimeException("Argument must be an array");
            }

            int length = Array.getLength(array);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("[");
            for (int i = 0; i < length; i++) {
                Object element = Array.get(array, i);
                stringBuilder.append(element).append(" ");
            }
            stringBuilder.append("]");
            return stringBuilder.toString();
        });

        context.addFunction("len", args -> {
            if (args.length != 1) {
                throw new RuntimeException("len() requires exactly one argument");
            }
            Object obj = args[0];

            if (obj == null) return 0;

            if (obj.getClass().isArray()) {
                return Array.getLength(obj);
            }

            if (obj instanceof List) {
                return ((List<?>) obj).size();
            }

            if (obj instanceof String) {
                return ((String) obj).length();
            }

            if (obj instanceof Object[]) {
                return ((Object[]) obj).length;
            }

            throw new RuntimeException("len() not supported for type: " + obj.getClass());
        });

        context.addFunction("range", args -> {
            if (args.length != 2) {
                throw new RuntimeException("range requires exactly two arguments");
            }

            Object start = args[0];
            Object end = args[1];
            int rangeStart = 0, rangeEnd = 0;

            if (start == null || end == null) {
                throw new RuntimeException("range requires at least two not null arguments");
            }

            if (start instanceof Object[] || end instanceof Object[] ||
                    start instanceof List || end instanceof List ||
                    start.getClass().isArray() || end.getClass().isArray()) {
                throw new RuntimeException("range arguments must not be arrays");
            }

            if (start instanceof String && end instanceof String) {
                throw new RuntimeException("range arguments must not be strings");
            }

            if (start instanceof Integer) {
                rangeStart = (Integer) start;
            } else if (start instanceof Long) {
                rangeStart = ((Long) start).intValue();
            } else if (start instanceof Double) {
                rangeStart = ((Double) start).intValue();
            }
            if (end instanceof Integer) {
                rangeEnd = (Integer) end;
            } else if (end instanceof Long) {
                rangeEnd = ((Long) end).intValue();
            } else if (end instanceof Double) {
                rangeEnd = ((Double) end).intValue();
            }

            int[] result = new int[(rangeEnd - rangeStart + 1)];
            for (int i = 0; i < result.length; i++) {
                result[i] = rangeStart + i;
            }

            return result;
        });
    }

    // Выполнение скрипта из строки
    public void execute(String script) throws ScriptException {
        try {
            Lexer lexer = new Lexer(script);
            List<Token> tokens = lexer.scanTokens();

            Parser parser = new Parser(tokens);
            ASTNode ast = parser.parse();

            ast.execute(context);
        } catch (Lexer.LexerException e) {
            throw new ScriptException("Lexing error: " + e.getMessage(), e);
        } catch (Throwable e) {
            throw new ScriptException("Parsing error: " + e.getMessage(), e);
        }
    }

    // Выполнение скрипта из файла
    public void executeFile(String path) throws IOException, ScriptException {
        Path file = Paths.get(path);
        Path dir  = file.getParent() != null
                ? file.getParent()
                : Paths.get("").toAbsolutePath();
        context.setCurrentDir(dir);

        String source = Files.readString(file, StandardCharsets.UTF_8);

        execute(source);
    }

    // Вычисление выражения и возврат результата
    public Object evaluateExpression(String expression) throws ScriptException {
        try {
            // Оборачиваем выражение во временный скрипт
            String script = "var __result = " + expression + ";";
            execute(script);
            return context.getVariable("__result");
        } catch (Exception e) {
            throw new ScriptException("Evaluation error: " + e.getMessage(), e);
        }
    }

    // Получение текущего контекста выполнения
    public Context getContext() {
        return context;
    }
}
