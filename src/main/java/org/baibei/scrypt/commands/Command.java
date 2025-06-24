package org.baibei.scrypt.commands;

@FunctionalInterface
public interface Command {
    Object execute(String... args);
}
