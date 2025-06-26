package org.baibei.script.interpreter;

import org.baibei.script.commands.Command;
import org.baibei.script.commands.Function;

import java.nio.file.Path;
import java.util.*;

public class Context {
    // Стек для управления областями видимости
    private final Deque<Map<String, Object>> scopeStack = new ArrayDeque<>();

    // Хранилище функций
    private final Map<String, Function> functions = new HashMap<>();

    // Свойства программы
    private final Map<String, String> properties = new HashMap<>();

    // Регистрация команд
    private final Map<String, Command> commands = new HashMap<>();

    private final ScriptEngine engine;
    private Path currentDir;

    public Context(ScriptEngine engine, Path currentDir) {
        this.engine = engine;
        this.currentDir = currentDir;
        initializeDefaultScope();
        initializeMainFunctions();
    }

    public ScriptEngine getEngine() {
        return engine;
    }

    public Path getCurrentDir() {
        return currentDir;
    }

    public void setCurrentDir(Path currentDir) {
        this.currentDir = currentDir;
    }

    // === Управление областями видимости ===
    public void enterScope() {
        scopeStack.push(new HashMap<>());
    }

    public void exitScope() {
        if (!scopeStack.isEmpty()) {
            scopeStack.pop();
        }
    }

    private void initializeDefaultScope() {
        scopeStack.push(new HashMap<>());
    }

    // === Работа с переменными ===
    public void setVariable(String name, Object value) {
        for (Iterator<Map<String, Object>> it = scopeStack.descendingIterator(); it.hasNext();) {
            Map<String, Object> scope = it.next();
            if (scope.containsKey(name)) {
                scope.put(name, value);
                return;
            }
        }

        if (scopeStack.isEmpty()) {
            initializeDefaultScope();
        }

        assert scopeStack.peek() != null;
        scopeStack.peek().put(name, value);
    }

    public Object getVariable(String name) {
        for (Map<String, Object> scope : scopeStack) {
            if (scope.containsKey(name)) {
                return scope.get(name);
            }
        }
        throw new RuntimeException("Variable not found: " + name);
    }

    // === Работа с функциями ===
    public void addFunction(String name, Function function) {
        functions.put(name, function);
    }

    public Function getFunction(String name) {
        Function func = functions.get(name);
        if (func == null) {
            throw new RuntimeException("Function not found: " + name);
        }
        return func;
    }

    // === Работа со свойствами ===
    public void loadProperties(Properties props) {
        props.forEach((key, value) ->
                properties.put(key.toString(), value.toString()));
    }

    public String getProperty(String name) {
        String value = properties.get(name);
        if (value == null) {
            throw new RuntimeException("Property not found: " + name);
        }
        return value;
    }

    public void setProperty(String name, String value) {
        properties.put(name, value);
    }

    // === Работа с командами ===
    public void registerCommand(String name, Command command) {
        commands.put(name, command);
    }

    public Command getCommand(String name) {
        Command command = commands.get(name);
        if (command == null) {
            throw new RuntimeException("Unknown command: " + name);
        }
        return command;
    }

    public void importVariables(Map<String, Object> variables) {
        assert scopeStack.peek() != null;
        scopeStack.peek().putAll(variables);
    }

    public Map<String, Object> exportVariables() {
        Map<String, Object> allVariables = new HashMap<>();
        for (Map<String, Object> scope : scopeStack) {
            allVariables.putAll(scope);
        }
        return allVariables;
    }

    private void initializeMainFunctions() {
        registerCommand("println", args -> {
            System.out.println(String.join(" ", args));
            return null;
        });
        registerCommand("print", args -> {
            System.out.print(String.join(" ", args));
            return null;
        });
    }

    public Object call(String name, Object... args) {
        if (functions.containsKey(name)) {
            return functions.get(name).execute(args);
        } else if (commands.containsKey(name)) {
            String[] stringArgs = Arrays.stream(args)
                    .map(Object::toString)
                    .toArray(String[]::new);
            return commands.get(name).execute(stringArgs);
        }
        throw new RuntimeException("Function or command not found: " + name);
    }
}
