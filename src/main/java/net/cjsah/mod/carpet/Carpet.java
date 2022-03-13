package net.cjsah.mod.carpet;

import com.mojang.brigadier.CommandDispatcher;
import net.cjsah.mod.carpet.commands.CounterCommand;
import net.cjsah.mod.carpet.commands.DistanceCommand;
import net.cjsah.mod.carpet.commands.DrawCommand;
import net.cjsah.mod.carpet.commands.InfoCommand;
import net.cjsah.mod.carpet.commands.LogCommand;
import net.cjsah.mod.carpet.commands.MobAICommand;
import net.cjsah.mod.carpet.commands.PerimeterInfoCommand;
import net.cjsah.mod.carpet.commands.PlayerCommand;
import net.cjsah.mod.carpet.commands.ProfileCommand;
import net.cjsah.mod.carpet.commands.ScriptCommand;
import net.cjsah.mod.carpet.commands.SpawnCommand;
import net.cjsah.mod.carpet.commands.TickCommand;
import net.cjsah.mod.carpet.helpers.HopperCounter;
import net.cjsah.mod.carpet.helpers.TickSpeed;
import net.cjsah.mod.carpet.logging.HUDController;
import net.cjsah.mod.carpet.logging.LoggerRegistry;
import net.cjsah.mod.carpet.network.ServerNetworkHandler;
import net.cjsah.mod.carpet.script.CarpetScriptServer;
import net.cjsah.mod.carpet.settings.SettingsManager;
import net.cjsah.mod.carpet.utils.FabricAPIHooks;
import net.cjsah.mod.carpet.utils.MobAI;
import net.cjsah.mod.carpet.utils.SpawnReporter;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.PerfCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Mod(Carpet.ID)
public class Carpet {
    public static final String ID = "carpet";

    public static final Random rand = new Random();
    public static MinecraftServer minecraft_server;
    private static CommandDispatcher<CommandSourceStack> currentCommandDispatcher;
    public static CarpetScriptServer scriptServer;
    public static SettingsManager settingsManager;
    public static final List<CarpetExtension> extensions = new ArrayList<>();

    public Carpet() {
        settingsManager = new SettingsManager(CarpetSettings.carpetVersion, "carpet", "Carpet Mod");
        settingsManager.parseSettingsClass(CarpetSettings.class);
        extensions.forEach(CarpetExtension::onGameStarted);
        FabricAPIHooks.initialize();
        CarpetScriptServer.parseFunctionClasses();
    }

    // Separate from onServerLoaded, because a server can be loaded multiple times in singleplayer
    /**
     * Registers a {@link CarpetExtension} to be managed by Carpet.<br>
     * Should be called before Carpet's startup, like in Fabric Loader's
     * {@link net.minecraftforge.fml.common.Mod} entrypoint
     * @param extension The instance of a {@link CarpetExtension} to be registered
     */
    public static void manageExtension(CarpetExtension extension) {
        extensions.add(extension);
        // for extensions that come late to the party, after server is created / loaded
        // we will handle them now.
        // that would handle all extensions, even these that add themselves really late to the party
        if (currentCommandDispatcher != null) {
            extension.registerCommands(currentCommandDispatcher);
        }
    }

    public static void onServerLoaded(MinecraftServer server) {
        minecraft_server = server;
        // shoudl not be needed - that bit needs refactoring, but not now.
        SpawnReporter.reset_spawn_stats(server, true);

        settingsManager.attachServer(server);
        extensions.forEach(e -> {
            SettingsManager sm = e.customSettingsManager();
            if (sm != null) sm.attachServer(server);
            e.onServerLoaded(server);
        });
        scriptServer = new CarpetScriptServer(server);
        MobAI.resetTrackers();
        LoggerRegistry.initLoggers();
        //TickSpeed.reset();
    }

    public static void onServerLoadedWorlds(MinecraftServer minecraftServer) {
        HopperCounter.resetAll(minecraftServer, true);
        extensions.forEach(e -> e.onServerLoadedWorlds(minecraftServer));
        scriptServer.initializeForWorld();
    }

    public static void tick(MinecraftServer server) {
        TickSpeed.tick();
        HUDController.update_hud(server, null);
        if (scriptServer != null) scriptServer.tick();

        //in case something happens
        CarpetSettings.impendingFillSkipUpdates.set(false);

        extensions.forEach(e -> e.onTick(server));
    }

    public static void registerCarpetCommands(CommandDispatcher<CommandSourceStack> dispatcher, Commands.CommandSelection environment) {
        settingsManager.registerCommand(dispatcher);
        extensions.forEach(e -> {
            SettingsManager sm = e.customSettingsManager();
            if (sm != null) sm.registerCommand(dispatcher);
        });
        TickCommand.register(dispatcher);
        ProfileCommand.register(dispatcher);
        CounterCommand.register(dispatcher);
        LogCommand.register(dispatcher);
        SpawnCommand.register(dispatcher);
        PlayerCommand.register(dispatcher);
        InfoCommand.register(dispatcher);
        DistanceCommand.register(dispatcher);
        PerimeterInfoCommand.register(dispatcher);
        DrawCommand.register(dispatcher);
        ScriptCommand.register(dispatcher);
        MobAICommand.register(dispatcher);
        extensions.forEach(e -> e.registerCommands(dispatcher));
        currentCommandDispatcher = dispatcher;

        if (environment != Commands.CommandSelection.DEDICATED)
            PerfCommand.register(dispatcher);
    }

    public static void onPlayerLoggedIn(ServerPlayer player) {
        ServerNetworkHandler.onPlayerJoin(player);
        LoggerRegistry.playerConnected(player);
        scriptServer.onPlayerJoin(player);
        extensions.forEach(e -> e.onPlayerLoggedIn(player));

    }

    public static void onPlayerLoggedOut(ServerPlayer player) {
        ServerNetworkHandler.onPlayerLoggedOut(player);
        LoggerRegistry.playerDisconnected(player);
        extensions.forEach(e -> e.onPlayerLoggedOut(player));
    }

    public static void clientPreClosing() {
        if (scriptServer != null) scriptServer.onClose();
        scriptServer = null;
    }

    public static void onServerClosed(MinecraftServer server) {
        // this for whatever reason gets called multiple times even when joining on SP
        // so we allow to pass multiple times gating it only on existing server ref
        if (minecraft_server != null) {
            if (scriptServer != null) scriptServer.onClose();
            scriptServer = null;
            ServerNetworkHandler.close();
            currentCommandDispatcher = null;

            LoggerRegistry.stopLoggers();
            HUDController.resetScarpetHUDs();
            extensions.forEach(e -> e.onServerClosed(server));
            minecraft_server = null;
        }

        // this for whatever reason gets called multiple times even when joining;
        TickSpeed.reset();
    }
    public static void onServerDoneClosing(MinecraftServer server) {
        settingsManager.detachServer();
    }

    public static void registerExtensionLoggers() {
        extensions.forEach(CarpetExtension::registerLoggers);
    }

    public static void onReload(MinecraftServer server) {
        scriptServer.reload(server);
        extensions.forEach(e -> e.onReload(server));
    }

}
