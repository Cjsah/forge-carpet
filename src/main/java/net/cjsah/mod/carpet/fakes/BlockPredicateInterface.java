package net.cjsah.mod.carpet.fakes;

import net.cjsah.mod.carpet.script.value.Value;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public interface BlockPredicateInterface
{
    BlockState getCMBlockState();
    TagKey<Block> getCMBlockTagKey();
    Map<Value, Value> getCMProperties();
    CompoundTag getCMDataTag();
}
