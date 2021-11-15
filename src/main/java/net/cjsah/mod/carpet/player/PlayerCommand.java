package net.cjsah.mod.carpet.player;

import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.cjsah.mod.carpet.fake.ServerPlayerEntityInterface;
import net.cjsah.mod.carpet.patch.EntityPlayerMPFake;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.DimensionArgument;
import net.minecraft.command.arguments.RotationArgument;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static net.minecraft.command.Commands.literal;
import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.ISuggestionProvider.suggest;


@Mod.EventBusSubscriber
public class PlayerCommand {
    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();
        final String[] gamemodes = Arrays.stream(GameType.values())
                .map(GameType::getName)
                .collect(Collectors.toList())
                .toArray(new String[]{});
        LiteralArgumentBuilder<CommandSource> builder = literal("player")
                .then(argument("player", StringArgumentType.word())
                        .suggests( (c, b) -> suggest(getPlayers(c.getSource()), b))
                        .then(literal("stop").executes(PlayerCommand::stop))
                        .then(makeActionCommand("use", EntityPlayerActionPack.ActionType.USE))
                        .then(makeActionCommand("jump", EntityPlayerActionPack.ActionType.JUMP))
                        .then(makeActionCommand("attack", EntityPlayerActionPack.ActionType.ATTACK))
                        .then(makeActionCommand("drop", EntityPlayerActionPack.ActionType.DROP_ITEM))
                        .then(makeDropCommand("drop", false))
                        .then(makeActionCommand("dropStack", EntityPlayerActionPack.ActionType.DROP_STACK))
                        .then(makeDropCommand("dropStack", true))
                        .then(makeActionCommand("swapHands", EntityPlayerActionPack.ActionType.SWAP_HANDS))
                        .then(literal("hotbar")
                                .then(argument("slot", IntegerArgumentType.integer(1, 9))
                                        .executes(c -> manipulate(c, ap -> ap.setSlot(IntegerArgumentType.getInteger(c, "slot"))))))
                        .then(literal("kill").executes(PlayerCommand::kill))
                        .then(literal("shadow"). executes(PlayerCommand::shadow))
                        .then(literal("mount").executes(manipulation(ap -> ap.mount(true)))
                                .then(literal("anything").executes(manipulation(ap -> ap.mount(false)))))
                        .then(literal("dismount").executes(manipulation(EntityPlayerActionPack::dismount)))
                        .then(literal("sneak").executes(manipulation(ap -> ap.setSneaking(true))))
                        .then(literal("unsneak").executes(manipulation(ap -> ap.setSneaking(false))))
                        .then(literal("sprint").executes(manipulation(ap -> ap.setSprinting(true))))
                        .then(literal("unsprint").executes(manipulation(ap -> ap.setSprinting(false))))
                        .then(literal("look")
                                .then(literal("north").executes(manipulation(ap -> ap.look(Direction.NORTH))))
                                .then(literal("south").executes(manipulation(ap -> ap.look(Direction.SOUTH))))
                                .then(literal("east").executes(manipulation(ap -> ap.look(Direction.EAST))))
                                .then(literal("west").executes(manipulation(ap -> ap.look(Direction.WEST))))
                                .then(literal("up").executes(manipulation(ap -> ap.look(Direction.UP))))
                                .then(literal("down").executes(manipulation(ap -> ap.look(Direction.DOWN))))
                                .then(literal("at").then(argument("position", Vec3Argument.vec3()).executes(PlayerCommand::lookAt)))
                                .then(argument("direction", RotationArgument.rotation())
                                        .executes(c -> manipulate(c, ap -> ap.look(RotationArgument.getRotation(c, "direction").getRotation(c.getSource())))))
                        ).then(literal("turn")
                                .then(literal("left").executes(c -> manipulate(c, ap -> ap.turn(-90, 0))))
                                .then(literal("right").executes(c -> manipulate(c, ap -> ap.turn(90, 0))))
                                .then(literal("back").executes(c -> manipulate(c, ap -> ap.turn(180, 0))))
                                .then(argument("rotation", RotationArgument.rotation())
                                        .executes(c -> manipulate(c, ap -> ap.turn(RotationArgument.getRotation(c, "rotation").getRotation(c.getSource())))))
                        ).then(literal("move").executes(c -> manipulate(c, EntityPlayerActionPack::stopMovement))
                                .then(literal("forward").executes(c -> manipulate(c, ap -> ap.setForward(1))))
                                .then(literal("backward").executes(c -> manipulate(c, ap -> ap.setForward(-1))))
                                .then(literal("left").executes(c -> manipulate(c, ap -> ap.setStrafing(1))))
                                .then(literal("right").executes(c -> manipulate(c, ap -> ap.setStrafing(-1))))
                        ).then(literal("spawn").executes(PlayerCommand::spawn)
                                .then(literal("in").requires((player) -> player.hasPermissionLevel(2))
                                        .then(argument("gamemode", StringArgumentType.word())
                                                .suggests( (c, b) -> suggest(gamemodes, b))
                                                .executes(PlayerCommand::spawn)))
                                .then(literal("at").then(argument("position", Vec3Argument.vec3()).executes(PlayerCommand::spawn)
                                        .then(literal("facing").then(argument("direction", RotationArgument.rotation()).executes(PlayerCommand::spawn)
                                                .then(literal("in").then(argument("dimension", DimensionArgument.getDimension()).executes(PlayerCommand::spawn)
                                                        .then(literal("in").requires((player) -> player.hasPermissionLevel(2))
                                                                .then(argument("gamemode", StringArgumentType.word()).suggests( (c, b) -> suggest(gamemodes, b))
                                                                        .executes(PlayerCommand::spawn)
                                                                )))
                                                )))
                                ))
                        )
                );
        dispatcher.register(builder);
    }

    private static LiteralArgumentBuilder<CommandSource> makeActionCommand(String actionName, EntityPlayerActionPack.ActionType type)
    {
        return literal(actionName)
                .executes(c -> action(c, type, EntityPlayerActionPack.Action.once()))
                .then(literal("once").executes(c -> action(c, type, EntityPlayerActionPack.Action.once())))
                .then(literal("continuous").executes(c -> action(c, type, EntityPlayerActionPack.Action.continuous())))
                .then(literal("interval").then(argument("ticks", IntegerArgumentType.integer(1))
                        .executes(c -> action(c, type, EntityPlayerActionPack.Action.interval(IntegerArgumentType.getInteger(c, "ticks"))))));
    }

    private static LiteralArgumentBuilder<CommandSource> makeDropCommand(String actionName, boolean dropAll)
    {
        return literal(actionName)
                .then(literal("all").executes(c ->manipulate(c, ap -> ap.drop(-2,dropAll))))
                .then(literal("mainhand").executes(c ->manipulate(c, ap -> ap.drop(-1,dropAll))))
                .then(literal("offhand").executes(c ->manipulate(c, ap -> ap.drop(40,dropAll))))
                .then(argument("slot", IntegerArgumentType.integer(0, 40)).
                        executes(c ->manipulate(c, ap -> ap.drop(
                                IntegerArgumentType.getInteger(c,"slot"),
                                dropAll
                        ))));
    }

    private static Collection<String> getPlayers(CommandSource source)
    {
        Set<String> players = Sets.newLinkedHashSet(Arrays.asList("Steve", "Alex"));
        players.addAll(source.getPlayerNames());
        return players;
    }

    private static ServerPlayerEntity getPlayer(CommandContext<CommandSource> context)
    {
        String playerName = StringArgumentType.getString(context, "player");
        MinecraftServer server = context.getSource().getServer();
        return server.getPlayerList().getPlayerByUsername(playerName);
    }

    private static boolean cantManipulate(CommandContext<CommandSource> context)
    {
        PlayerEntity player = getPlayer(context);
        if (player == null)
        {
            sendFeedback(context.getSource(), new StringTextComponent("Can only manipulate existing players").mergeStyle(TextFormatting.RED));
            return true;
        }
        PlayerEntity sendingPlayer;
        try
        {
            sendingPlayer = context.getSource().asPlayer();
        }
        catch (CommandSyntaxException e)
        {
            return false;
        }

        if (!context.getSource().getServer().getPlayerList().canSendCommands(sendingPlayer.getGameProfile()))
        {
            if (sendingPlayer != player && !(player instanceof EntityPlayerMPFake))
            {
                sendFeedback(context.getSource(), new StringTextComponent("Non OP players can't control other real players").mergeStyle(TextFormatting.RED));
                return true;
            }
        }
        return false;
    }

    private static boolean cantReMove(CommandContext<CommandSource> context)
    {
        if (cantManipulate(context)) return true;
        PlayerEntity player = getPlayer(context);
        if (player instanceof EntityPlayerMPFake) return false;
        sendFeedback(context.getSource(), new StringTextComponent("Only fake players can be moved or killed").mergeStyle(TextFormatting.RED));
        return true;
    }

    private static boolean cantSpawn(CommandContext<CommandSource> context)
    {
        String playerName = StringArgumentType.getString(context, "player");
        MinecraftServer server = context.getSource().getServer();
        PlayerList manager = server.getPlayerList();
        PlayerEntity player = manager.getPlayerByUsername(playerName);
        if (player != null)
        {
            sendFeedback(context.getSource(), new StringTextComponent("Player ").mergeStyle(TextFormatting.RED)
                    .appendSibling(new StringTextComponent(playerName).mergeStyle(TextFormatting.RED))
                    .appendSibling(new StringTextComponent(" is already logged on").mergeStyle(TextFormatting.RED).mergeStyle(TextFormatting.BOLD))
            );
            return true;
        }
        GameProfile profile = server.getPlayerProfileCache().getGameProfileForUsername(playerName);
        if (profile == null)
        {
            profile = new GameProfile(PlayerEntity.getOfflineUUID(playerName), playerName);
        }
        if (manager.getBannedPlayers().isBanned(profile))
        {
            sendFeedback(context.getSource(), new StringTextComponent("Player ").mergeStyle(TextFormatting.RED)
                            .appendSibling(new StringTextComponent(playerName).mergeStyle(TextFormatting.RED))
                            .appendSibling(new StringTextComponent(" is banned on this server").mergeStyle(TextFormatting.RED).mergeStyle(TextFormatting.BOLD))
            );
            return true;
        }
        if (manager.isWhiteListEnabled() && manager.canJoin(profile) && !context.getSource().hasPermissionLevel(2))
        {
            sendFeedback(context.getSource(), new StringTextComponent("Whitelisted players can only be spawned by operators").mergeStyle(TextFormatting.RED));
            return true;
        }
        return false;
    }

    private static int kill(CommandContext<CommandSource> context)
    {
        if (cantReMove(context)) return 0;
        getPlayer(context).onKillCommand();
        return 1;
    }

    private static int lookAt(CommandContext<CommandSource> context)
    {
        return manipulate(context, ap -> {
            try {
                ap.lookAt(Vec3Argument.getVec3(context, "position"));
            } catch (CommandSyntaxException ignore) {}
        });
    }

    @FunctionalInterface
    interface SupplierWithCommandSyntaxException<T>
    {
        T get() throws CommandSyntaxException;
    }

    private static <T> T tryGetArg(SupplierWithCommandSyntaxException<T> a, SupplierWithCommandSyntaxException<T> b) throws CommandSyntaxException
    {
        try
        {
            return a.get();
        }
        catch (IllegalArgumentException e)
        {
            return b.get();
        }
    }

    private static int spawn(CommandContext<CommandSource> context) throws CommandSyntaxException
    {
        if (cantSpawn(context)) return 0;
        CommandSource source = context.getSource();
        Vector3d pos = tryGetArg(() -> Vec3Argument.getVec3(context, "position"), source::getPos);
        Vector2f facing = tryGetArg(() -> RotationArgument.getRotation(context, "direction").getRotation(context.getSource()), source::getRotation);
        RegistryKey<World> dimType = tryGetArg(() -> DimensionArgument.getDimensionArgument(context, "dimension").getDimensionKey(), () -> source.getWorld().getDimensionKey());
        GameType mode = GameType.CREATIVE;
        boolean flying = false;
        try
        {
            ServerPlayerEntity player = context.getSource().asPlayer();
            mode = player.interactionManager.getGameType();
            flying = player.abilities.isFlying;
        }
        catch (CommandSyntaxException ignored) {}
        try {
            String opGameMode = StringArgumentType.getString(context, "gamemode");
            mode = GameType.parseGameTypeWithDefault(opGameMode, null);
            if(mode == null)
            {
                sendFeedback(context.getSource(), new StringTextComponent("Invalid game mode: "+opGameMode+".").mergeStyle(TextFormatting.RED).mergeStyle(TextFormatting.BOLD));
                return 0;
            }
        } catch (IllegalArgumentException ignored) {}
        if(mode == GameType.SPECTATOR)
        {
            // Force override flying to true for spectator players, or they will fell out of the world.
            flying = true;
        }
        String playerName = StringArgumentType.getString(context, "player");
        if (playerName.length()>maxPlayerLength(source.getServer()))
        {
            sendFeedback(context.getSource(), new StringTextComponent("Player name: "+playerName+" is too long").mergeStyle(TextFormatting.RED).mergeStyle(TextFormatting.BOLD));
            return 0;
        }

        MinecraftServer server = source.getServer();
        if (!World.isValid(new BlockPos(pos.x, pos.y, pos.z)))
        {
            sendFeedback(context.getSource(), new StringTextComponent("Player "+playerName+" cannot be placed outside of the world").mergeStyle(TextFormatting.RED).mergeStyle(TextFormatting.BOLD));
            return 0;
        }
        PlayerEntity player = EntityPlayerMPFake.createFake(playerName, server, pos.x, pos.y, pos.z, facing.y, facing.x, dimType, mode, flying);
        if (player == null)
        {
            sendFeedback(context.getSource(), new StringTextComponent("Player " + StringArgumentType.getString(context, "player") + " doesn't exist " +
                    "and cannot spawn in online mode. Turn the server offline to spawn non-existing players").mergeStyle(TextFormatting.RED).mergeStyle(TextFormatting.BOLD));
            return 0;
        }
        return 1;
    }

    private static int maxPlayerLength(MinecraftServer server)
    {
        return server.getServerPort() >= 0 ? 16 : 40;
    }

    private static int stop(CommandContext<CommandSource> context)
    {
        if (cantManipulate(context)) return 0;
        ServerPlayerEntity player = getPlayer(context);
        ((ServerPlayerEntityInterface) player).getActionPack().stopAll();
        return 1;
    }

    private static int manipulate(CommandContext<CommandSource> context, Consumer<EntityPlayerActionPack> action)
    {
        if (cantManipulate(context)) return 0;
        ServerPlayerEntity player = getPlayer(context);
        action.accept(((ServerPlayerEntityInterface) player).getActionPack());
        return 1;
    }

    private static Command<CommandSource> manipulation(Consumer<EntityPlayerActionPack> action)
    {
        return c -> manipulate(c, action);
    }

    private static int action(CommandContext<CommandSource> context, EntityPlayerActionPack.ActionType type, EntityPlayerActionPack.Action action)
    {
        return manipulate(context, ap -> ap.start(type, action));
    }

    private static int shadow(CommandContext<CommandSource> context)
    {
        ServerPlayerEntity player = getPlayer(context);
        if (player instanceof EntityPlayerMPFake)
        {
            sendFeedback(context.getSource(), new StringTextComponent("Cannot shadow fake players").mergeStyle(TextFormatting.RED));
            return 0;
        }
        ServerPlayerEntity sendingPlayer = null;
        try
        {
            sendingPlayer = context.getSource().asPlayer();
        }
        catch (CommandSyntaxException ignored) { }

        if (sendingPlayer!=player && cantManipulate(context)) return 0;
        EntityPlayerMPFake.createShadow(player.server, player);
        return 1;
    }

    private static void sendFeedback(CommandSource source, ITextComponent message) {
        source.sendFeedback(message, source.getServer().getWorld(World.OVERWORLD) != null);
    }

}