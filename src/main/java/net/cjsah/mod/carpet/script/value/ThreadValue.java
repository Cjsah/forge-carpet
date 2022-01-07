package net.cjsah.mod.carpet.script.value;

import net.cjsah.mod.carpet.script.Context;
import net.cjsah.mod.carpet.script.Expression;
import net.cjsah.mod.carpet.script.Tokenizer;
import net.cjsah.mod.carpet.script.exception.ExitStatement;
import net.cjsah.mod.carpet.script.exception.ExpressionException;
import net.cjsah.mod.carpet.script.exception.InternalExpressionException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import net.minecraft.nbt.Tag;

public class ThreadValue extends Value
{
    private final CompletableFuture<Value> taskFuture;
    private final long id;
    private static long sequence = 0L;

    public ThreadValue(Value pool, FunctionValue function, Expression expr, Tokenizer.Token token, Context ctx, List<Value> args)
    {
        id = sequence++;
        ExecutorService executor = ctx.host.getExecutor(pool);
        if (executor == null)
        {
            // app is shutting down - no more threads can be spawned.
            taskFuture = CompletableFuture.completedFuture(NULL);
        }
        else
        {
            taskFuture = CompletableFuture.supplyAsync(
                    () ->
                    {
                        try
                        {
                            return function.execute(ctx, Context.NONE, expr, token, args).evalValue(ctx);
                        }
                        catch (ExitStatement exit)
                        {
                            // app stopped
                            return exit.retval;
                        }
                        catch (ExpressionException exc)
                        {
                            ctx.host.handleExpressionException("Thread failed\n", exc);
                            return NULL;
                        }

                    },
                    ctx.host.getExecutor(pool)
            );
        }
        Thread.yield();
    }

    @Override
    public String getString()
    {
        return taskFuture.getNow(NULL).getString();
    }

    public Value getValue()
    {
        return taskFuture.getNow(NULL);
    }

    @Override
    public boolean getBoolean()
    {
        return taskFuture.getNow(NULL).getBoolean();
    }

    public Value join()
    {
        try
        {
            return taskFuture.get();
        }
        catch (ExitStatement exit)
        {
            taskFuture.complete(exit.retval);
            return exit.retval;
        }
        catch (InterruptedException | ExecutionException e)
        {
            return NULL;
        }
    }

    public boolean isFinished()
    {
        return taskFuture.isDone();
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof ThreadValue))
            return false;
        return ((ThreadValue) o).id == this.id;
    }

    @Override
    public int compareTo(Value o)
    {
        if (!(o instanceof ThreadValue))
            throw new InternalExpressionException("Cannot compare tasks to other types");
        return (int) (this.id - ((ThreadValue) o).id);
    }

    @Override
    public int hashCode()
    {
        return Long.hashCode(id);
    }

    @Override
    public Tag toTag(boolean force)
    {
        if (!force) throw new NBTSerializableValue.IncompatibleTypeException(this);
        return getValue().toTag(true);
    }

    @Override
    public String getTypeString()
    {
        return "task";
    }
}
