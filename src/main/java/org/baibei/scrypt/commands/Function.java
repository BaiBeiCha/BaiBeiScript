package org.baibei.scrypt.commands;

@FunctionalInterface
public interface Function {
    Object execute(Object... args);
}
