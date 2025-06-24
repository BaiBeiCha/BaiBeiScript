package org.baibei.script.commands;

@FunctionalInterface
public interface Function {
    Object execute(Object... args);
}
