package net.cjsah.mod.carpet.patches;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.EventLoop;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.EventExecutor;
import net.cjsah.mod.carpet.CarpetSettings;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.cjsah.mod.carpet.fakes.ServerPlayerEntityInterface;
import net.cjsah.mod.carpet.utils.Messenger;
import net.minecraftforge.network.MCRegisterPacketHandler;
import org.jetbrains.annotations.NotNull;

import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings({"ConstantConditions", "UnusedReturnValue", "NullableProblems"})
public class EntityPlayerMPFake extends ServerPlayer {
    public Runnable fixStartingPosition = () -> {};
    public boolean isAShadow;

    public static EntityPlayerMPFake createFake(String username, MinecraftServer server, double d0, double d1, double d2, double yaw, double pitch, ResourceKey<Level> dimensionId, GameType gamemode, boolean flying) {
        ServerLevel worldIn = server.getLevel(dimensionId);
        GameProfileCache.setUsesAuthentication(false);
        GameProfile gameprofile;
        try {
            gameprofile = server.getProfileCache().get(username).orElse(null);
        } finally {
            GameProfileCache.setUsesAuthentication(server.isDedicatedServer() && server.usesAuthentication());
        }
        if (gameprofile == null) {
            if (!CarpetSettings.allowSpawningOfflinePlayers) return null;
            else gameprofile = new GameProfile(Player.createPlayerUUID(username), username);
        }
        if (gameprofile.getProperties().containsKey("textures")) {
            AtomicReference<GameProfile> result = new AtomicReference<>();
            SkullBlockEntity.updateGameprofile(gameprofile, result::set);
            gameprofile = result.get();
        }
        EntityPlayerMPFake instance = new EntityPlayerMPFake(server, worldIn, gameprofile, false);
        instance.fixStartingPosition = () -> instance.moveTo(d0, d1, d2, (float) yaw, (float) pitch);
        NetworkManagerFake manager = new NetworkManagerFake(PacketFlow.SERVERBOUND);
        try {
            manager.channelActive(new ChannelHandlerContextImpl());
        } catch (Exception e) {
            e.printStackTrace();
        }
        server.getPlayerList().placeNewPlayer(manager, instance);
        instance.teleportTo(worldIn, d0, d1, d2, (float)yaw, (float)pitch);
        instance.setHealth(20.0F);
        instance.unsetRemoved();
        instance.maxUpStep = 0.6F;
        instance.gameMode.changeGameModeForPlayer(gamemode);
        server.getPlayerList().broadcastAll(new ClientboundRotateHeadPacket(instance, (byte) (instance.yHeadRot * 256 / 360)), dimensionId);//instance.dimension);
        server.getPlayerList().broadcastAll(new ClientboundTeleportEntityPacket(instance), dimensionId);//instance.dimension);
        instance.getLevel().getChunkSource().move(instance);
        instance.entityData.set(DATA_PLAYER_MODE_CUSTOMISATION, (byte) 0x7f); // show all model layers (incl. capes)
        instance.getAbilities().flying = flying;
        return instance;
    }

    public static EntityPlayerMPFake createShadow(MinecraftServer server, ServerPlayer player) {
        player.getServer().getPlayerList().remove(player);
        player.connection.disconnect(new TranslatableComponent("multiplayer.disconnect.duplicate_login"));
        ServerLevel worldIn = player.getLevel();//.getWorld(player.dimension);
        GameProfile gameprofile = player.getGameProfile();
        EntityPlayerMPFake playerShadow = new EntityPlayerMPFake(server, worldIn, gameprofile, true);
        server.getPlayerList().placeNewPlayer(new NetworkManagerFake(PacketFlow.SERVERBOUND), playerShadow);

        playerShadow.setHealth(player.getHealth());
        playerShadow.connection.teleport(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
        playerShadow.gameMode.changeGameModeForPlayer(player.gameMode.getGameModeForPlayer());
        ((ServerPlayerEntityInterface) playerShadow).getActionPack().copyFrom(((ServerPlayerEntityInterface) player).getActionPack());
        playerShadow.maxUpStep = 0.6F;
        playerShadow.entityData.set(DATA_PLAYER_MODE_CUSTOMISATION, player.getEntityData().get(DATA_PLAYER_MODE_CUSTOMISATION));

        server.getPlayerList().broadcastAll(new ClientboundRotateHeadPacket(playerShadow, (byte) (player.yHeadRot * 256 / 360)), playerShadow.level.dimension());
        server.getPlayerList().broadcastAll(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, playerShadow));
        player.getLevel().getChunkSource().move(playerShadow);
        playerShadow.getAbilities().flying = player.getAbilities().flying;
        return playerShadow;
    }

    private EntityPlayerMPFake(MinecraftServer server, ServerLevel worldIn, GameProfile profile, boolean shadow) {
        super(server, worldIn, profile);
        isAShadow = shadow;
    }

    @Override
    protected void equipEventAndSound(ItemStack stack) {
        if (!isUsingItem()) super.equipEventAndSound(stack);
    }

    @Override
    public void kill() {
        kill(Messenger.s("Killed"));
    }

    public void kill(Component reason) {
        shakeOff();
        this.server.tell(new TickTask(this.server.getTickCount(), () -> this.connection.onDisconnect(reason)));
    }

    @Override
    public void tick() {
        if (this.getServer().getTickCount() % 10 == 0) {
            this.connection.resetPosition();
            this.getLevel().getChunkSource().move(this);
            hasChangedDimension(); //<- causes hard crash but would need to be done to enable portals // not as of 1.17
        }
        try {
            super.tick();
            this.doTick();
        } catch (NullPointerException ignored) { }


    }

    private void shakeOff() {
        if (getVehicle() instanceof Player) stopRiding();
        for (Entity passenger : getIndirectPassengers()) {
            if (passenger instanceof Player) passenger.stopRiding();
        }
    }

    @Override
    public void die(DamageSource cause) {
        shakeOff();
        super.die(cause);
        setHealth(20);
        this.foodData = new FoodData();
        kill(this.getCombatTracker().getDeathMessage());
    }

    @Override
    public String getIpAddress() {
        return "127.0.0.1";
    }

    private static class ChannelHandlerContextImpl implements ChannelHandlerContext {
        @Override
        public Channel channel() {
            return new ChannelImpl();
        }

        @Override public EventExecutor executor() { return null; }
        @Override public String name() { return null; }
        @Override public ChannelHandler handler() { return null; }
        @Override public boolean isRemoved() { return false; }
        @Override public ChannelHandlerContext fireChannelRegistered() { return null; }
        @Override public ChannelHandlerContext fireChannelUnregistered() { return null; }
        @Override public ChannelHandlerContext fireChannelActive() { return null; }
        @Override public ChannelHandlerContext fireChannelInactive() { return null; }
        @Override public ChannelHandlerContext fireExceptionCaught(Throwable cause) { return null; }
        @Override public ChannelHandlerContext fireUserEventTriggered(Object evt) { return null; }
        @Override public ChannelHandlerContext fireChannelRead(Object msg) { return null; }
        @Override public ChannelHandlerContext fireChannelReadComplete() { return null; }
        @Override public ChannelHandlerContext fireChannelWritabilityChanged() { return null; }
        @Override public ChannelFuture bind(SocketAddress localAddress) { return null; }
        @Override public ChannelFuture connect(SocketAddress remoteAddress) { return null; }
        @Override public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) { return null; }
        @Override public ChannelFuture disconnect() { return null; }
        @Override public ChannelFuture close() { return null; }
        @Override public ChannelFuture deregister() { return null; }
        @Override public ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) { return null; }
        @Override public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) { return null; }
        @Override public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) { return null; }
        @Override public ChannelFuture disconnect(ChannelPromise promise) { return null; }
        @Override public ChannelFuture close(ChannelPromise promise) { return null; }
        @Override public ChannelFuture deregister(ChannelPromise promise) { return null; }
        @Override public ChannelHandlerContext read() { return null; }
        @Override public ChannelFuture write(Object msg) { return null; }
        @Override public ChannelFuture write(Object msg, ChannelPromise promise) { return null; }
        @Override public ChannelHandlerContext flush() { return null; }
        @Override public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) { return null; }
        @Override public ChannelFuture writeAndFlush(Object msg) { return null; }
        @Override public ChannelPromise newPromise() { return null; }
        @Override public ChannelProgressivePromise newProgressivePromise() { return null; }
        @Override public ChannelFuture newSucceededFuture() { return null; }
        @Override public ChannelFuture newFailedFuture(Throwable cause) { return null; }
        @Override public ChannelPromise voidPromise() { return null; }
        @Override public ChannelPipeline pipeline() { return null; }
        @Override public ByteBufAllocator alloc() { return null; }
        @Override public <T> Attribute<T> attr(AttributeKey<T> key) { return null; }
        @Override public <T> boolean hasAttr(AttributeKey<T> key) { return false; }
    }

    private static class ChannelImpl implements Channel {
        @Override
        public ChannelConfig config() {
            return new DefaultChannelConfig(this);
        }

        @Override
        public ChannelMetadata metadata() {
            return new ChannelMetadata(false);
        }

        @Override
        public <T> Attribute<T> attr(AttributeKey<T> key) {
            return new AttributeImpl<>(key);
        }

        @Override public ChannelId id() { return null; }
        @Override public EventLoop eventLoop() { return null; }
        @Override public Channel parent() { return null; }
        @Override public boolean isOpen() { return false; }
        @Override public boolean isRegistered() { return false; }
        @Override public boolean isActive() { return false; }
        @Override public SocketAddress localAddress() { return null; }
        @Override public SocketAddress remoteAddress() { return null; }
        @Override public ChannelFuture closeFuture() { return null; }
        @Override public boolean isWritable() { return false; }
        @Override public long bytesBeforeUnwritable() { return 0; }
        @Override public long bytesBeforeWritable() { return 0; }
        @Override public Unsafe unsafe() { return null; }
        @Override public ChannelPipeline pipeline() { return null; }
        @Override public ByteBufAllocator alloc() { return null; }
        @Override public ChannelFuture bind(SocketAddress localAddress) { return null; }
        @Override public ChannelFuture connect(SocketAddress remoteAddress) { return null; }
        @Override public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) { return null; }
        @Override public ChannelFuture disconnect() { return null; }
        @Override public ChannelFuture close() { return null; }
        @Override public ChannelFuture deregister() { return null; }
        @Override public ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) { return null; }
        @Override public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) { return null; }
        @Override public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) { return null; }
        @Override public ChannelFuture disconnect(ChannelPromise promise) { return null; }
        @Override public ChannelFuture close(ChannelPromise promise) { return null; }
        @Override public ChannelFuture deregister(ChannelPromise promise) { return null; }
        @Override public Channel read() { return null; }
        @Override public ChannelFuture write(Object msg) { return null; }
        @Override public ChannelFuture write(Object msg, ChannelPromise promise) { return null; }
        @Override public Channel flush() { return null; }
        @Override public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) { return null; }
        @Override public ChannelFuture writeAndFlush(Object msg) { return null; }
        @Override public ChannelPromise newPromise() { return null; }
        @Override public ChannelProgressivePromise newProgressivePromise() { return null; }
        @Override public ChannelFuture newSucceededFuture() { return null; }
        @Override public ChannelFuture newFailedFuture(Throwable cause) { return null; }
        @Override public ChannelPromise voidPromise() { return null; }
        @Override public <T> boolean hasAttr(AttributeKey<T> key) { return false; }
        @Override public int compareTo(@NotNull Channel o) { return 0; }
    }

    @SuppressWarnings("unchecked")
    private record AttributeImpl<T>(AttributeKey<T> key) implements Attribute<T> {
        @Override
        public T get() {
            String name = this.key.name();
            if ("fml:netversion".equals(name)) return (T) "FML***";
            else if ("minecraft:netregistry".equals(name)) return (T) new MCRegisterPacketHandler.ChannelList();
            else return null;
        }

        @Override public AttributeKey<T> key() { return null; }
        @Override public void set(T value) { }
        @Override public T getAndSet(T value) { return null; }
        @Override public T setIfAbsent(T value) { return null; }
        @Override public T getAndRemove() { return null; }
        @Override public boolean compareAndSet(T oldValue, T newValue) { return false; }
        @Override public void remove() { }
    }
}
