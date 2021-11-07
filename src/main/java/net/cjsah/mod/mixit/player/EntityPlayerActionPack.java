package net.cjsah.mod.mixit.player;

import net.cjsah.mod.mixit.fake.ServerPlayerEntityInterface;
import net.cjsah.mod.mixit.helper.Tracer;
import net.minecraft.block.BlockState;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.server.SHeldItemChangePacket;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class EntityPlayerActionPack
{
    private final ServerPlayerEntity player;

    private final Map<ActionType, Action> actions = new TreeMap<>();

    private BlockPos currentBlock;
    private int blockHitDelay;
    private boolean isHittingBlock;
    private float curBlockDamageMP;

    private boolean sneaking;
    private boolean sprinting;
    private float forward;
    private float strafing;

    private int itemUseCooldown;

    public EntityPlayerActionPack(ServerPlayerEntity playerIn)
    {
        player = playerIn;
        stopAll();
    }
    public void copyFrom(EntityPlayerActionPack other)
    {
        actions.putAll(other.actions);
        currentBlock = other.currentBlock;
        blockHitDelay = other.blockHitDelay;
        isHittingBlock = other.isHittingBlock;
        curBlockDamageMP = other.curBlockDamageMP;

        sneaking = other.sneaking;
        sprinting = other.sprinting;
        forward = other.forward;
        strafing = other.strafing;

        itemUseCooldown = other.itemUseCooldown;
    }

    public EntityPlayerActionPack start(ActionType type, Action action)
    {
        Action previous = actions.remove(type);
        if (previous != null) type.stop(player, previous);
        if (action != null)
        {
            actions.put(type, action);
            type.start(player, action); // noop
        }
        return this;
    }

    public EntityPlayerActionPack setSneaking(boolean doSneak)
    {
        sneaking = doSneak;
        player.setSneaking(doSneak);
        if (sprinting && sneaking)
            setSprinting(false);
        return this;
    }
    public EntityPlayerActionPack setSprinting(boolean doSprint)
    {
        sprinting = doSprint;
        player.setSprinting(doSprint);
        if (sneaking && sprinting)
            setSneaking(false);
        return this;
    }

    public EntityPlayerActionPack setForward(float value)
    {
        forward = value;
        return this;
    }
    public EntityPlayerActionPack setStrafing(float value)
    {
        strafing = value;
        return this;
    }
    public EntityPlayerActionPack look(Direction direction)
    {
        switch (direction)
        {
            case NORTH: return look(180, 0);
            case SOUTH: return look(0, 0);
            case EAST: return look(-90, 0);
            case WEST: return look(90, 0);
            case UP: return look(player.getYaw(1.0F), -90);
            case DOWN: return look(player.getYaw(1.0F), 90);
        }
        return this;
    }
    public EntityPlayerActionPack look(Vector2f rotation)
    {
        return look(rotation.x, rotation.y);
    }

    public EntityPlayerActionPack look(float yaw, float pitch)
    {
        player.setPositionAndRotation(player.getPosX(), player.getPosY(), player.getPosZ(), yaw % 360, MathHelper.clamp(pitch, -90, 90));
        return this;
    }

    public EntityPlayerActionPack lookAt(Vector3d position)
    {
        player.lookAt(EntityAnchorArgument.Type.EYES, position);
        return this;
    }

    public EntityPlayerActionPack turn(float yaw, float pitch)
    {
        return look(player.getYaw(1.0F) + yaw, player.getPitch(1.0F) + pitch);
    }

    public EntityPlayerActionPack turn(Vector2f rotation)
    {
        return turn(rotation.x, rotation.y);
    }

    public EntityPlayerActionPack stopMovement()
    {
        setSneaking(false);
        setSprinting(false);
        forward = 0.0F;
        strafing = 0.0F;
        return this;
    }


    public EntityPlayerActionPack stopAll()
    {
        for (ActionType type : actions.keySet()) type.stop(player, actions.get(type));
        actions.clear();
        return stopMovement();
    }

    public EntityPlayerActionPack mount(boolean onlyRideables)
    {
        //test what happens
        List<Entity> entities;
        if (onlyRideables)
        {
            entities = player.world.getEntitiesInAABBexcluding(player, player.getBoundingBox().expand(3.0D, 1.0D, 3.0D),
                    e -> e instanceof MinecartEntity || e instanceof BoatEntity || e instanceof HorseEntity);
        }
            else
        {
            entities = player.world.getEntitiesInAABBexcluding(player, player.getBoundingBox().expand(3.0D, 1.0D, 3.0D), EntityPredicates.NOT_SPECTATING);
        }
        if (entities.size()==0)
            return this;
        Entity closest = null;
        double distance = Double.POSITIVE_INFINITY;
        Entity currentVehicle = player.getRidingEntity();
        for (Entity e: entities)
        {
            if (e == player || (currentVehicle == e))
                continue;
            double dd = player.getDistanceSq(e);
            if (dd<distance)
            {
                distance = dd;
                closest = e;
            }
        }
        if (closest == null) return this;
        if (closest instanceof AbstractHorseEntity && onlyRideables)
            ((AbstractHorseEntity) closest).getEntityInteractionResult(player, Hand.MAIN_HAND);
        else
            player.startRiding(closest,true);
        return this;
    }
    public EntityPlayerActionPack dismount()
    {
        player.stopRiding();
        return this;
    }

    public void onUpdate()
    {
        Map<ActionType, Boolean> actionAttempts = new HashMap<>();
        actions.entrySet().removeIf((e) -> e.getValue().done);
        for (Map.Entry<ActionType, Action> e : actions.entrySet())
        {
            Action action = e.getValue();
            // skipping attack if use was successful
            if (!(actionAttempts.getOrDefault(ActionType.USE, false) && e.getKey() == ActionType.ATTACK))
            {
                Boolean actionStatus = action.tick(this, e.getKey());
                if (actionStatus != null)
                    actionAttempts.put(e.getKey(), actionStatus);
            }
            // optionally retrying use after successful attack and unsuccessful use
            if ( e.getKey() == ActionType.ATTACK
                    && actionAttempts.getOrDefault(ActionType.ATTACK, false)
                    && !actionAttempts.getOrDefault(ActionType.USE, true) )
            {
                // according to MinecraftClient.handleInputEvents
                Action using = actions.get(ActionType.USE);
                if (using != null) // this is always true - we know use worked, but just in case
                {
                    using.retry(this, ActionType.USE);
                }
            }
        }
        if (forward != 0.0F)
        {
            player.moveForward = forward*(sneaking?0.3F:1.0F);
        }
        if (strafing != 0.0F)
        {
            player.moveStrafing = strafing*(sneaking?0.3F:1.0F);
        }
    }

    static RayTraceResult getTarget(ServerPlayerEntity player)
    {
        double reach = player.interactionManager.isCreative() ? 5 : 4.5f;
        return Tracer.rayTrace(player, 1, reach, false);
    }

    private void dropItemFromSlot(int slot, boolean dropAll)
    {
        PlayerInventory inv = player.inventory; // getInventory;
        if (!inv.getStackInSlot(slot).isEmpty())
            player.dropItem(inv.decrStackSize(slot,
                    dropAll ? inv.getStackInSlot(slot).getCount() : 1
            ), false, true); // scatter, keep owner
    }

    public void drop(int selectedSlot, boolean dropAll)
    {
        PlayerInventory inv = player.inventory; // getInventory;
        if (selectedSlot == -2) // all
        {
            for (int i = inv.getSizeInventory(); i >= 0; i--)
                dropItemFromSlot(i, dropAll);
        }
        else // one slot
        {
            if (selectedSlot == -1)
                selectedSlot = inv.currentItem;
            dropItemFromSlot(selectedSlot, dropAll);
        }
    }

    public void setSlot(int slot)
    {
        player.inventory.currentItem = slot-1;
        player.connection.sendPacket(new SHeldItemChangePacket(slot-1));
    }

    public enum ActionType
    {
        USE(true)
        {
            @Override
            boolean execute(ServerPlayerEntity player, Action action)
            {
                EntityPlayerActionPack ap = ((ServerPlayerEntityInterface) player).getActionPack();
                if (ap.itemUseCooldown > 0)
                {
                    ap.itemUseCooldown--;
                    return true;
                }
                if (player.isHandActive())
                {
                    return true;
                }
                RayTraceResult hit = getTarget(player);
                for (Hand hand : Hand.values())
                {
                    switch (hit.getType())
                    {
                        case BLOCK:
                        {
                            player.markPlayerActive();
                            ServerWorld world = player.getServerWorld();
                            BlockRayTraceResult blockHit = (BlockRayTraceResult) hit;
                            BlockPos pos = blockHit.getPos();
                            Direction side = blockHit.getFace();
                            if (pos.getY() < player.server.getBuildLimit() - (side == Direction.UP ? 1 : 0) && world.isBlockModifiable(player, pos))
                            {
                                ActionResultType result = player.interactionManager.func_219441_a(player, world, player.getHeldItem(hand), hand, blockHit);
                                if (result.isSuccessOrConsume())
                                {
                                    if (result.isSuccess()) player.swingArm(hand);
                                    ap.itemUseCooldown = 3;
                                    return true;
                                }
                            }
                            break;
                        }
                        case ENTITY:
                        {
                            player.markPlayerActive();
                            EntityRayTraceResult entityHit = (EntityRayTraceResult) hit;
                            Entity entity = entityHit.getEntity();
                            boolean handWasEmpty = player.getHeldItem(hand).isEmpty();
                            boolean itemFrameEmpty = (entity instanceof ItemFrameEntity) && ((ItemFrameEntity) entity).getDisplayedItem().isEmpty();
                            Vector3d relativeHitPos = entityHit.getHitVec().subtract(entity.getPosX(), entity.getPosY(), entity.getPosZ());
                            if (entity.applyPlayerInteraction(player, relativeHitPos, hand).isSuccessOrConsume())
                            {
                                ap.itemUseCooldown = 3;
                                return true;
                            }
                            // fix for SS itemframe always returns CONSUME even if no action is performed
                            if (player.interactOn(entity, hand).isSuccessOrConsume() && !(handWasEmpty && itemFrameEmpty))
                            {
                                ap.itemUseCooldown = 3;
                                return true;
                            }
                            break;
                        }
                    }
                    ItemStack handItem = player.getHeldItem(hand);
                    if (player.interactionManager.processRightClick(player, player.getServerWorld(), handItem, hand).isSuccessOrConsume())
                    {
                        ap.itemUseCooldown = 3;
                        return true;
                    }
                }
                return false;
            }

            @Override
            void inactiveTick(ServerPlayerEntity player, Action action)
            {
                EntityPlayerActionPack ap = ((ServerPlayerEntityInterface) player).getActionPack();
                ap.itemUseCooldown = 0;
                player.stopActiveHand();
            }
        },
        ATTACK(true) {
            @Override
            boolean execute(ServerPlayerEntity player, Action action) {
                RayTraceResult hit = getTarget(player);
                switch (hit.getType()) {
                    case ENTITY: {
                        EntityRayTraceResult entityHit = (EntityRayTraceResult) hit;
                        if (!action.isContinuous)
                        {
                            player.attackTargetEntityWithCurrentItem(entityHit.getEntity());
                            player.swingArm(Hand.MAIN_HAND);
                        }
                        player.resetCooldown();
                        player.markPlayerActive();
                        return true;
                    }
                    case BLOCK: {
                        EntityPlayerActionPack ap = ((ServerPlayerEntityInterface) player).getActionPack();
                        if (ap.blockHitDelay > 0)
                        {
                            ap.blockHitDelay--;
                            return false;
                        }
                        BlockRayTraceResult blockHit = (BlockRayTraceResult) hit;
                        BlockPos pos = blockHit.getPos();
                        Direction side = blockHit.getFace();
                        if (player.blockActionRestricted(player.world, pos, player.interactionManager.getGameType())) return false;
                        if (ap.currentBlock != null && player.world.getBlockState(ap.currentBlock).isAir())
                        {
                            ap.currentBlock = null;
                            return false;
                        }
                        BlockState state = player.world.getBlockState(pos);
                        boolean blockBroken = false;
                        if (player.interactionManager.getGameType().isCreative())
                        {
                            player.interactionManager.func_225416_a(pos, CPlayerDiggingPacket.Action.START_DESTROY_BLOCK, side, player.server.getBuildLimit());
                            ap.blockHitDelay = 5;
                            blockBroken = true;
                        }
                        else  if (ap.currentBlock == null || !ap.currentBlock.equals(pos))
                        {
                            if (ap.currentBlock != null)
                            {
                                player.interactionManager.func_225416_a(ap.currentBlock, CPlayerDiggingPacket.Action.ABORT_DESTROY_BLOCK, side, player.server.getBuildLimit());
                            }
                            player.interactionManager.func_225416_a(pos, CPlayerDiggingPacket.Action.START_DESTROY_BLOCK, side, player.server.getBuildLimit());
                            boolean notAir = !state.isAir();
                            if (notAir && ap.curBlockDamageMP == 0)
                            {
                                state.onBlockClicked(player.world, pos, player);
                            }
                            if (notAir && state.getPlayerRelativeBlockHardness(player, player.world, pos) >= 1)
                            {
                                ap.currentBlock = null;
                                //instamine??
                                blockBroken = true;
                            }
                            else
                            {
                                ap.currentBlock = pos;
                                ap.curBlockDamageMP = 0;
                            }
                        }
                        else
                        {
                            ap.curBlockDamageMP += state.getPlayerRelativeBlockHardness(player, player.world, pos);
                            if (ap.curBlockDamageMP >= 1)
                            {
                                player.interactionManager.func_225416_a(pos, CPlayerDiggingPacket.Action.STOP_DESTROY_BLOCK, side, player.server.getBuildLimit());
                                ap.currentBlock = null;
                                ap.blockHitDelay = 5;
                                blockBroken = true;
                            }
                            player.world.sendBlockBreakProgress(-1, pos, (int) (ap.curBlockDamageMP * 10));

                        }
                        player.markPlayerActive();
                        player.swingArm(Hand.MAIN_HAND);
                        return blockBroken;
                    }
                }
                return false;
            }

            @Override
            void inactiveTick(ServerPlayerEntity player, Action action)
            {
                EntityPlayerActionPack ap = ((ServerPlayerEntityInterface) player).getActionPack();
                if (ap.currentBlock == null) return;
                player.world.sendBlockBreakProgress(-1, ap.currentBlock, -1);
                player.interactionManager.func_225416_a(ap.currentBlock, CPlayerDiggingPacket.Action.ABORT_DESTROY_BLOCK, Direction.DOWN, player.server.getBuildLimit());
                ap.currentBlock = null;
            }
        },
        JUMP(true)
        {
            @Override
            boolean execute(ServerPlayerEntity player, Action action)
            {
                if (action.limit == 1)
                {
                    if (player.isOnGround()) player.jump(); // onGround
                }
                else
                {
                    player.setJumping(true);
                }
                return false;
            }

            @Override
            void inactiveTick(ServerPlayerEntity player, Action action)
            {
                player.setJumping(false);
            }
        },
        DROP_ITEM(true)
        {
            @Override
            boolean execute(ServerPlayerEntity player, Action action)
            {
                player.markPlayerActive();
                player.drop(false); // dropSelectedItem
                return false;
            }
        },
        DROP_STACK(true)
        {
            @Override
            boolean execute(ServerPlayerEntity player, Action action)
            {
                player.markPlayerActive();
                player.drop(true); // dropSelectedItem
                return false;
            }
        },
        SWAP_HANDS(true)
        {
            @Override
            boolean execute(ServerPlayerEntity player, Action action)
            {
                player.markPlayerActive();
                ItemStack itemStack_1 = player.getHeldItem(Hand.OFF_HAND);
                player.setHeldItem(Hand.OFF_HAND, player.getHeldItem(Hand.MAIN_HAND));
                player.setHeldItem(Hand.MAIN_HAND, itemStack_1);
                return false;
            }
        };

        public final boolean preventSpectator;

        ActionType(boolean preventSpectator)
        {
            this.preventSpectator = preventSpectator;
        }

        void start(ServerPlayerEntity player, Action action) {}
        abstract boolean execute(ServerPlayerEntity player, Action action);
        void inactiveTick(ServerPlayerEntity player, Action action) {}
        void stop(ServerPlayerEntity player, Action action)
        {
            inactiveTick(player, action);
        }
    }

    public static class Action
    {
        public boolean done = false;
        public final int limit;
        public final int interval;
        public final int offset;
        private int count;
        private int next;
        private final boolean isContinuous;

        private Action(int limit, int interval, int offset, boolean continuous)
        {
            this.limit = limit;
            this.interval = interval;
            this.offset = offset;
            next = interval + offset;
            isContinuous = continuous;
        }

        public static Action once()
        {
            return new Action(1, 1, 0, false);
        }

        public static Action continuous()
        {
            return new Action(-1, 1, 0, true);
        }

        public static Action interval(int interval)
        {
            return new Action(-1, interval, 0, false);
        }

        public static Action interval(int interval, int offset)
        {
            return new Action(-1, interval, offset, false);
        }

        Boolean tick(EntityPlayerActionPack actionPack, ActionType type)
        {
            next--;
            Boolean cancel = null;
            if (next <= 0)
            {
                if (interval == 1 && !isContinuous)
                {
                    // need to allow entity to tick, otherwise won't have effect (bow)
                    // actions are 20 tps, so need to clear status mid tick, allowing entities process it till next time
                    if (!type.preventSpectator || !actionPack.player.isSpectator())
                    {
                        type.inactiveTick(actionPack.player, this);
                    }
                }

                if (!type.preventSpectator || !actionPack.player.isSpectator())
                {
                    cancel = type.execute(actionPack.player, this);
                }
                count++;
                if (count == limit)
                {
                    type.stop(actionPack.player, null);
                    done = true;
                    return cancel;
                }
                next = interval;
            }
            else
            {
                if (!type.preventSpectator || !actionPack.player.isSpectator())
                {
                    type.inactiveTick(actionPack.player, this);
                }
            }
            return cancel;
        }

        void retry(EntityPlayerActionPack actionPack, ActionType type)
        {
            //assuming action run but was unsuccesful that tick, but opportunity emerged to retry it, lets retry it.
            if (!type.preventSpectator || !actionPack.player.isSpectator())
            {
                type.execute(actionPack.player, this);
            }
            count++;
            if (count == limit)
            {
                type.stop(actionPack.player, null);
                done = true;
            }
        }
    }
}
