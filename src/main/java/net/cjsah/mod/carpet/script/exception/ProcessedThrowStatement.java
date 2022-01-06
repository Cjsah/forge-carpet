package net.cjsah.mod.carpet.script.exception;

import java.util.List;

import net.cjsah.mod.carpet.script.Context;
import net.cjsah.mod.carpet.script.Expression;
import net.cjsah.mod.carpet.script.Tokenizer.Token;
import net.cjsah.mod.carpet.script.value.FunctionValue;
import net.cjsah.mod.carpet.script.value.Value;

public class ProcessedThrowStatement extends ExpressionException {
    public final Throwables thrownExceptionType;
    public final Value data;
    
    public ProcessedThrowStatement(Context c, Expression e, Token token, List<FunctionValue> stack, Throwables thrownExceptionType, Value data) {
        super(c, e, token, ()  -> "Unhandled "+thrownExceptionType.getId()+" exception: "+data.getString(), stack);
        this.thrownExceptionType = thrownExceptionType;
        this.data = data;
    }
}
