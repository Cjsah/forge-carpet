package net.cjsah.mod.carpet.script.utils;

import net.cjsah.mod.carpet.script.exception.InternalExpressionException;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;

import java.util.Locale;

public class InputValidator {
    public static String validateSimpleString(String input, boolean strict)
    {
        String simplified = input.toLowerCase(Locale.ROOT).replaceAll("[^A-Za-z0-9+_]", "");
        if (simplified.isEmpty() || (strict && !simplified.equals(input)))
            throw new InternalExpressionException("simple name can only contain numbers, letter and _");
        return simplified;
    }

    public static ResourceLocation identifierOf(String string)
    {
        try
        {
            return new ResourceLocation(string);
        }
        catch (ResourceLocationException iie)
        {
            throw new InternalExpressionException("Incorrect identifier format '"+string+"': "+iie.getMessage());
        }
    }

}
