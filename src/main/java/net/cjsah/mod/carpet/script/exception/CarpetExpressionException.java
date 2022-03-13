package net.cjsah.mod.carpet.script.exception;

import net.cjsah.mod.carpet.script.value.FunctionValue;
import net.cjsah.mod.carpet.utils.Messenger;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;

public class CarpetExpressionException extends RuntimeException implements ResolvedException {
    public final List<FunctionValue> stack;

    public CarpetExpressionException(String message, List<FunctionValue> stack) {
        super(message);
        this.stack = stack;
    }
    public void printStack(CommandSourceStack source) {
        if (stack != null && !stack.isEmpty()) {
            for (FunctionValue fun : stack) {
                Messenger.m(source, "e  ... in "+fun.fullName(), "e /"+(fun.getToken().lineno+1)+":"+(fun.getToken().linepos+1));
            }
        }
    }
}
