package net.cjsah.mod.carpet.script.exception;

import net.cjsah.mod.carpet.script.value.Value;

/* Exception thrown to terminate execution mid expression (aka return statement) */
public class ExitStatement extends RuntimeException {
    public final Value retval;
    public ExitStatement(Value value) {
        retval = value;
    }
}
