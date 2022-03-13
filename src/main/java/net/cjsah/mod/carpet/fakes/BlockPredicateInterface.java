package net.cjsah.mod.carpet.fakes;

import net.cjsah.mod.carpet.script.value.Value;
import java.util.Map;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockPredicateInterface {
    BlockState getCMBlockState();
    Tag<Block> getCMBlockTag();
    Map<Value, Value> getCMProperties();
    CompoundTag getCMDataTag();
}
