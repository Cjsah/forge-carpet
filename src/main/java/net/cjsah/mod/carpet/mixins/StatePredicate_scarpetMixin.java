package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.fakes.BlockPredicateInterface;
import net.cjsah.mod.carpet.script.value.StringValue;
import net.cjsah.mod.carpet.script.value.Value;
import net.cjsah.mod.carpet.script.value.ValueConversions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

@Mixin(targets = "net.minecraft.command.argument.BlockPredicateArgumentType$StatePredicate")
public class StatePredicate_scarpetMixin implements BlockPredicateInterface {

    @Shadow @Final private BlockState state;

    @Shadow @Final /*@Nullable*/ private CompoundTag nbt;

    @Shadow @Final private Set<Property<?>> properties;

    @Override
    public BlockState getCMBlockState() {
        return state;
    }

    @Override
    public Tag<Block> getCMBlockTag() {
        return null;
    }

    @Override
    public Map<Value, Value> getCMProperties() {
        return properties.stream().collect(Collectors.toMap(
                p -> StringValue.of(p.getName()),
                p -> ValueConversions.fromProperty(state, p),
                (a, b) -> b
        ));
    }

    @Override
    public CompoundTag getCMDataTag() {
        return nbt;
    }
}
