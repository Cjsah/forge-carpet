package net.cjsah.mod.carpet.script.exception;

import net.cjsah.mod.carpet.script.Context;
import net.cjsah.mod.carpet.script.Expression;
import net.cjsah.mod.carpet.script.Tokenizer;
import net.cjsah.mod.carpet.script.value.FunctionValue;

import java.util.ArrayList;
import java.util.List;

/* The internal expression evaluators exception class. */
public class InternalExpressionException extends RuntimeException {
    public List<FunctionValue> stack = new ArrayList<>();
    public InternalExpressionException(String message) {
        super(message);
    }
    
    /**
     * <p>Promotes this simple exception into one with context and extra information.
     * 
     * <p>Provides a cleaner way of handling similar exceptions, in this case
     * {@link InternalExpressionException} and {@link ThrowStatement} 
     * @param c Context
     * @param e Expression
     * @param token Token
     * @return The new {@link ExpressionException} (or {@link ProcessedThrowStatement}),
     *         depending on the implementation.
     */
    public ExpressionException promote(Context c, Expression e, Tokenizer.Token token) {
        return new ExpressionException(c, e, token, getMessage(), stack);
    }
}
