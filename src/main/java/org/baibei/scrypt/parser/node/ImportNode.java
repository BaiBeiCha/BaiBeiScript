package org.baibei.scrypt.parser.node;

import org.baibei.scrypt.interpreter.Context;
import org.baibei.scrypt.interpreter.ScriptEngine;
import org.baibei.scrypt.interpreter.ScriptException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ImportNode extends ASTNode {

    private final String path;

    public ImportNode(String path) {
        this.path = path;
    }

    @Override
    public Object execute(Context context) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(path)));
            ScriptEngine engine = new ScriptEngine();
            engine.execute(content);
            return null;
        } catch (IOException e) {
            throw new RuntimeException("Error reading import file: " + path, e);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }
}
