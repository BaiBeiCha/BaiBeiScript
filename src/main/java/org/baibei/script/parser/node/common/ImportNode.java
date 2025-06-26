package org.baibei.script.parser.node.common;

import org.baibei.script.interpreter.Context;
import org.baibei.script.interpreter.ScriptException;
import org.baibei.script.parser.node.ASTNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImportNode extends ASTNode {
    private final String rawPath;

    public ImportNode(String rawPath) {
        this.rawPath = rawPath;
    }

    @Override
    public Object execute(Context context) {
        try {
            String p = rawPath.replaceAll("^\"|\"$", "");

            if (!p.contains(".")) {
                p = p + ".bbs";
            }

            Path candidate = Paths.get(p);
            if (!candidate.isAbsolute()) {
                candidate = context.getCurrentDir().resolve(p);
                System.out.println("Absolute path: " + candidate);
            }

            if (!Files.exists(candidate)) {
                Path interpreterRoot = Paths.get("").toAbsolutePath();
                candidate = interpreterRoot.resolve("lib").resolve(p);
            }

            candidate = candidate.normalize();

            Path currentDir = context.getCurrentDir();
            context.getEngine().executeFile(candidate.toString());
            context.setCurrentDir(currentDir);
        } catch (IOException | ScriptException ex) {
            throw new RuntimeException("Import failed: " + rawPath, ex);
        }
        return null;
    }
}
