package org.baibei;

import org.baibei.scrypt.interpreter.ScriptEngine;
import org.baibei.scrypt.interpreter.ScriptException;

public class Main {
    public static void main(String[] args) throws ScriptException {
        ScriptEngine engine = new ScriptEngine();
        String script = """
                println("Starting for");
                for (var i = 0; i < 3; i = i + 1;) {
                    println(i);
                }
                println("Ending for");
                """;
        engine.execute(script);
    }
}