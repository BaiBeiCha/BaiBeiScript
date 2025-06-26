package org.baibei;

import org.baibei.script.interpreter.ScriptEngine;
import org.baibei.script.interpreter.ScriptException;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws ScriptException, IOException {
        ScriptEngine engine = new ScriptEngine();
        engine.executeFile("scripts\\main.bbs");
    }
}
