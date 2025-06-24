package org.baibei.scrypt.parser.node;

import org.baibei.scrypt.commands.Command;
import org.baibei.scrypt.interpreter.Context;

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
