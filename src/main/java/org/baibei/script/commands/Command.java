package org.baibei.script.commands;

@FunctionalInterface
public interface Command {
    Object execute(String... args);
}
