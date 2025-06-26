package org.baibei.script.interpreter;

import org.baibei.script.commands.Command;
import org.baibei.script.commands.Function;

import java.lang.reflect.Array;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Context {

    public static class Variable {
        final String type;
        Object value;
        final boolean isFinal;
        final boolean isStatic;
        final int dimensions; // 0 = не массив, 1 = одномерный, 2 = двумерный и т.д.

        Variable(String type, Object value, boolean isFinal, boolean isStatic, int dimensions) {
            this.type = type;
            this.value = value;
            this.isFinal = isFinal;
            this.isStatic = isStatic;
            this.dimensions = dimensions;
        }
    }

    private final Deque<Map<String, Variable>> scopeStack = new ArrayDeque<>();
    private final Map<String, Function> functions = new HashMap<>();
    private final Map<String, String> properties = new HashMap<>();
    private final Map<String, Command> commands = new HashMap<>();

    private final ScriptEngine engine;

    private Path currentDir;

    public Context(ScriptEngine engine, Path currentDir) {
        this.engine = engine;
        this.currentDir = currentDir;
        enterScope();
        initializeMainFunctions();
    }

    // === Управление областями видимости ===
    public void enterScope() {
        scopeStack.push(new HashMap<>());
    }

    public void exitScope() {
        if (scopeStack.size() > 1) {
            scopeStack.pop();
        }
    }

    // === Работа с переменными ===
    public void declareVariable(String name, String type, Object value,
                                boolean isFinal, boolean isStatic, int dimensions) {
        Map<String, Variable> currentScope = scopeStack.peek();

        if (currentScope.containsKey(name)) {
            throw new RuntimeException("Variable already declared: " + name);
        }

        if ("var".equals(type)) {
            type = inferType(value);
        }

        if (value == null) {
            value = getDefaultValue(type, dimensions);
        }

        if (!isValidType(type, value, dimensions > 0)) {
            throw new RuntimeException("Type mismatch for variable: " + name);
        }

        currentScope.put(name, new Variable(type, value, isFinal, isStatic, dimensions));
    }

    public void setVariable(String name, Object value) {
        Variable var = findVariable(name);
        if (var == null) {
            declareVariable(name, inferType(value), value, false, false, 0);
            return;
        }

        if (var.isFinal) {
            throw new RuntimeException("Cannot modify final variable: " + name);
        }

        if (!isValidType(var.type, value, var.dimensions > 0)) {
            throw new RuntimeException("Type mismatch for variable: " + name);
        }

        var.value = value;
    }

    public Object getVariable(String name) {
        Variable var = findVariable(name);
        if (var == null) {
            throw new RuntimeException("Variable not found: " + name);
        }
        return var.value;
    }

    private Variable findVariable(String name) {
        for (Map<String, Variable> scope : scopeStack) {
            if (scope.containsKey(name)) {
                return scope.get(name);
            }
        }
        return null;
    }

    public String inferType(Object value) {
        if (value == null) return "object";
        if (value instanceof Integer) return "int";
        if (value instanceof Long) return "long";
        if (value instanceof Double) return "double";
        if (value instanceof String) return "string";
        if (value.getClass().isArray()) return "array";
        return "object";
    }

    private Object getDefaultValue(String type, int dimensions) {
        if (dimensions > 0) {
            return createArray(type, dimensions, 0);
        }
        return switch (type) {
            case "int" -> 0;
            case "long" -> 0L;
            case "double" -> 0.0;
            case "string" -> "";
            default -> null;
        };
    }

    private Object createArray(String type, int dimensions, int size) {
        if (dimensions == 1) {
            return switch (type) {
                case "int" -> new int[size];
                case "long" -> new long[size];
                case "double" -> new double[size];
                case "string" -> new String[size];
                default -> new Object[size];
            };
        }

        Object[] array = new Object[size];
        for (int i = 0; i < size; i++) {
            array[i] = createArray(type, dimensions - 1, size);
        }
        return array;
    }

    private boolean isValidType(String type, Object value, boolean isArray) {
        if (value == null) return true;

        if (isArray) {
            if (!value.getClass().isArray()) return false;

            int length = Array.getLength(value);
            for (int i = 0; i < length; i++) {
                Object element = Array.get(value, i);
                if (element != null) {
                    if (element.getClass().isArray()) {
                        if (!isValidType(type, element, true)) return false;
                    } else {
                        if (!isBasicTypeMatch(type, element)) return false;
                    }
                }
            }
            return true;
        }

        return isBasicTypeMatch(type, value);
    }

    private boolean isBasicTypeMatch(String type, Object value) {
        return switch (type) {
            case "int" -> value instanceof Integer;
            case "long" -> value instanceof Long;
            case "double" -> value instanceof Double;
            case "string" -> value instanceof String;
            default -> true;
        };
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

    public void importVariables(Map<String, ? extends Variable> variables) {
        assert scopeStack.peek() != null;
        scopeStack.peek().putAll(variables);
    }

    public Map<String, Object> exportVariables() {
        Map<String, Object> allVariables = new HashMap<>();
        for (Map<String, Variable> scope : scopeStack) {
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

    public ScriptEngine getEngine() {
        return engine;
    }

    public Path getCurrentDir() {
        return currentDir;
    }

    public void setCurrentDir(Path currentDir) {
        this.currentDir = currentDir;
    }
}
