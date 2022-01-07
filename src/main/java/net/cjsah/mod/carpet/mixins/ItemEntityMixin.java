package net.cjsah.mod.carpet.mixins;

import net.cjsah.mod.carpet.CarpetSettings;
import net.cjsah.mod.carpet.helpers.InventoryHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.cjsah.mod.carpet.fakes.ItemEntityInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity implements ItemEntityInterface
{
    private static final int SHULKERBOX_MAX_STACK_AMOUNT = 64;

    @Shadow private int itemAge;
    @Shadow private int pickupDelay;

    public ItemEntityMixin(EntityType<?> entityType_1, Level world_1) {
        super(entityType_1, world_1);
    }

    @Override
    public void thunderHit(ServerLevel world, LightningBolt lightning) {
        if (CarpetSettings.lightningKillsDropsFix) {
            if (this.itemAge > 8) { //Only kill item if its older then 8 ticks
                super.thunderHit(world, lightning);
            }
        } else {
            super.thunderHit(world, lightning);
        }
    }

    @Override
    public int getAgeCM() {
        return this.itemAge;
    }

    @Override
    public int getPickupDelayCM() {
        return this.pickupDelay;
    }

    @Inject(method="<init>(Lnet/minecraft/world/World;DDDLnet/minecraft/item/ItemStack;)V", at = @At("RETURN"))
    private void removeEmptyShulkerBoxTags(Level worldIn, double x, double y, double z, ItemStack stack, CallbackInfo ci)
    {
        if (CarpetSettings.stackableShulkerBoxes
                && stack.getItem() instanceof BlockItem
                && ((BlockItem)stack.getItem()).getBlock() instanceof ShulkerBoxBlock)
        {
            if (InventoryHelper.cleanUpShulkerBoxTag(stack)) {
                ((ItemEntity) (Object) this).setItem(stack);
            }
        }
    }

    @Redirect(
            method = "canMerge()Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;getMaxCount()I"
            )
    )
    private int getItemStackMaxAmount(ItemStack stack) {
        if (CarpetSettings.stackableShulkerBoxes && stack.getItem() instanceof BlockItem && ((BlockItem)stack.getItem()).getBlock() instanceof ShulkerBoxBlock)
            return SHULKERBOX_MAX_STACK_AMOUNT;

        return stack.getMaxStackSize();
    }

    @Inject(
            method = "tryMerge(Lnet/minecraft/entity/ItemEntity;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void tryStackShulkerBoxes(ItemEntity other, CallbackInfo ci)
    {
        ItemEntity self = (ItemEntity)(Object)this;
        ItemStack selfStack = self.getItem();
        if (!CarpetSettings.stackableShulkerBoxes || !(selfStack.getItem() instanceof BlockItem) || !(((BlockItem)selfStack.getItem()).getBlock() instanceof ShulkerBoxBlock)) {
            return;
        }

        ItemStack otherStack = other.getItem();
        if (selfStack.getItem() == otherStack.getItem()
                && !InventoryHelper.shulkerBoxHasItems(selfStack)
                && !InventoryHelper.shulkerBoxHasItems(otherStack)
                && selfStack.hasTag() == otherStack.hasTag()
                && selfStack.getCount() + otherStack.getCount() <= SHULKERBOX_MAX_STACK_AMOUNT)
        {
            int amount = Math.min(otherStack.getCount(), SHULKERBOX_MAX_STACK_AMOUNT - selfStack.getCount());

            selfStack.grow(amount);
            self.setItem(selfStack);

            this.pickupDelay = Math.max(((ItemEntityInterface)other).getPickupDelayCM(), this.pickupDelay);
            this.itemAge = Math.min(((ItemEntityInterface)other).getAgeCM(), this.itemAge);

            otherStack.shrink(amount);
            if (otherStack.isEmpty())
            {
                other.discard(); // discard remove();
            }
            else
            {
                other.setItem(otherStack);
            }
            ci.cancel();
        }
    }
}
