package org.baibei.scrypt.interpreter;

import org.baibei.scrypt.lexer.Lexer;
import org.baibei.scrypt.lexer.Token;
import org.baibei.scrypt.parser.Parser;
import org.baibei.scrypt.parser.node.ASTNode;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ScriptEngine {

    private final Context context;

    public ScriptEngine() {
        this.context = new Context();
        initializeBuiltins();
    }

    // Инициализация встроенных команд и функций
    private void initializeBuiltins() {
        // Регистрация встроенной команды print
        context.registerCommand("print", args -> {
            System.out.println(String.join(" ", args));
            return null;
        });

        // Регистрация встроенной функции assert
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
        } catch (Parser.ParserException e) {
            throw new ScriptException("Parsing error: " + e.getMessage(), e);
        }
    }

    // Выполнение скрипта из файла
    public void executeFile(String filePath) throws ScriptException {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            execute(content);
        } catch (Exception e) {
            throw new ScriptException("Error reading file: " + filePath, e);
        }
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
