package org.baibei.script.parser.node.common;

import org.baibei.script.commands.Command;
import org.baibei.script.interpreter.Context;
import org.baibei.script.parser.node.ASTNode;

import java.util.List;

public class CommandNode extends ASTNode {

    private final String commandName;
    private final List<String> args;

    public CommandNode(String commandName, List<String> args) {
        this.commandName = commandName;
        this.args = args;
    }

    @Override
    public Object execute(Context context) {
        Command command = context.getCommand(commandName);
        if (command == null) {
            throw new RuntimeException("Unknown command: " + commandName);
        }
        return command.execute(args.toArray(new String[0]));
    }
}
