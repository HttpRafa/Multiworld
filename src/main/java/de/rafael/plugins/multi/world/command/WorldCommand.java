/*
 * MIT License
 *
 * Copyright (c) 2023 HttpRafa
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.rafael.plugins.multi.world.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.rafael.plugins.multi.world.MultiWorld;
import de.rafael.plugins.multi.world.location.WorldLocation;
import de.rafael.plugins.multi.world.manager.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author Rafael K.
 * @created 00:12, 14.05.23
 * @project worldmanager
 */

public class WorldCommand implements CommandExecutor, TabCompleter {

    public static final Gson GSON = new GsonBuilder().create();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if(args.length == 1 && args[0].equalsIgnoreCase("list")) {
            for (World world : Bukkit.getWorlds()) {
                sender.sendMessage(MultiWorld.PREFIX + "§8» §b" + world.getName());
            }
        } else if(args.length == 2 && args[0].equalsIgnoreCase("load")) {
            sender.sendMessage(MultiWorld.PREFIX + "§7Process was §astarted§8...");
            WorldManager.ActionResult actionResult = MultiWorld.instance().worldManager().loadWorld(args[1]);
            switch (actionResult) {
                case WORLD_CREATED -> {
                    sender.sendMessage(MultiWorld.PREFIX + "§7World §b" + args[1] + "§7 successfully §aloaded§8!");
                }
                case WORLD_NOT_FOUND -> {
                    sender.sendMessage(MultiWorld.PREFIX + "§7World §b" + args[1] + "§7 was §cnot found §7please recheck the §bspelling §7of the name§8!");
                }
                case WORLD_ALREADY_LOADED -> {
                    sender.sendMessage(MultiWorld.PREFIX + "§7World §b" + args[1] + "§7 is §balready loaded§8!");
                }
                case PROCESS_FAILED -> {
                    sender.sendMessage(MultiWorld.PREFIX + "§7Something went §cwrong§8!");
                }
            }
        } else if(args.length == 2 && args[0].equalsIgnoreCase("unload")) {
            sender.sendMessage(MultiWorld.PREFIX + "§7Process was §astarted§8...");
            WorldManager.ActionResult actionResult = MultiWorld.instance().worldManager().unloadWorld(args[1], true);
            switch (actionResult) {
                case WORLD_UNLOADED -> {
                    sender.sendMessage(MultiWorld.PREFIX + "§7World §b" + args[1] + "§7 successfully §aunloaded§8!");
                }
                case WORLD_NOT_EMPTY -> {
                    sender.sendMessage(MultiWorld.PREFIX + "§7The world unload process §cfailed §7please ensure that the world has §cno §bplayers §7on it§8!");
                }
                case WORLD_NOT_FOUND -> {
                    sender.sendMessage(MultiWorld.PREFIX + "§7World §b" + args[1] + "§7 was §cnot found §7please recheck the §bspelling §7of the name§8!");
                }
            }
        } else if(args.length == 2 && args[0].equalsIgnoreCase("delete")) {
            sender.sendMessage(MultiWorld.PREFIX + "§7Process was §astarted§8...");
            WorldManager.ActionResult actionResult = MultiWorld.instance().worldManager().deleteWorld(args[1]);
            switch (actionResult) {
                case WORLD_DELETED -> {
                    sender.sendMessage(MultiWorld.PREFIX + "§7World §b" + args[1] + "§7 successfully §adeleted§8!");
                }
                case WORLD_NOT_EMPTY -> {
                    sender.sendMessage(MultiWorld.PREFIX + "§7The world unload process §cfailed §7please ensure that the world has §cno §7players on it§8!");
                }
                case WORLD_NOT_FOUND -> {
                    sender.sendMessage(MultiWorld.PREFIX + "§7World §b" + args[1] + "§7 was §cnot found §7please recheck the §bspelling §7of the name§8!");
                }
                case PROCESS_FAILED -> {
                    sender.sendMessage(MultiWorld.PREFIX + "§7Something went §cwrong§8!");
                }
            }
        } else if(args.length == 2 && args[0].equalsIgnoreCase("teleport")) {
            if(sender instanceof Player player) {
                World world = Bukkit.getWorld(args[1]);
                if(world != null) {
                    MultiWorld.instance().worldManager().world(args[1]).ifPresentOrElse(loadedWorld -> {
                        sender.sendMessage(MultiWorld.PREFIX + "§7World §b" + args[1] + "§7 was §afound §bteleporting§8...");
                        player.teleport(loadedWorld.spawnLocation() == null ? world.getSpawnLocation() : loadedWorld.spawnLocation().bukkit(), PlayerTeleportEvent.TeleportCause.COMMAND);
                    }, () -> {
                        sender.sendMessage(MultiWorld.PREFIX + "§7World §b" + args[1] + "§7 was §afound §bteleporting§8...");
                        player.teleport(world.getSpawnLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
                    });
                } else {
                    sender.sendMessage(MultiWorld.PREFIX + "§7World §b" + args[1] + "§7 was §cnot found §7please recheck the §bspelling §7of the name§8!");
                }
            } else {
                sender.sendMessage(MultiWorld.PREFIX + "§7This action can only be done by a §bplayer§8!");
            }
        } else if(args.length == 2 && args[0].equalsIgnoreCase("spawn")) {
            if(sender instanceof Player player) {
                MultiWorld.instance().worldManager().world(args[1]).ifPresentOrElse(loadedWorld -> {
                    loadedWorld.spawnLocation(new WorldLocation(player.getLocation()));
                    MultiWorld.instance().worldManager().save();
                    sender.sendMessage(MultiWorld.PREFIX + "§7New spawn location for the world §b" + args[1] + "§7 was §aset§8.");
                }, () -> {
                    sender.sendMessage(MultiWorld.PREFIX + "§7World §b" + args[1] + "§7 was §cnot found §7please recheck the §bspelling §7of the name§8!");
                });
            } else {
                sender.sendMessage(MultiWorld.PREFIX + "§7This action can only be done by a §bplayer§8!");
            }
        } else if(args.length >= 2 && args[0].equalsIgnoreCase("create")) {
            WorldCreator worldCreator = WorldCreator.name(args[1]);
            if(args.length >= 3) {
                try {
                    World.Environment environment = World.Environment.valueOf(args[2].toUpperCase());
                    worldCreator.environment(environment);
                } catch (Throwable throwable) {
                    sender.sendMessage(MultiWorld.PREFIX + "§cFailed §7to parse world §benvironment §7please recheck the §bspelling §7of the name§8!");
                }
            }
            if (args.length >= 4) {
                try {
                    WorldType type = WorldType.valueOf(args[3].toUpperCase());
                    worldCreator.type(type);
                } catch (Throwable throwable) {
                    sender.sendMessage(MultiWorld.PREFIX + "§cFailed §7to parse world §btype §7please recheck the §bspelling §7of the name§8!");
                }
            }
            if (args.length >= 5) {
                try {
                    worldCreator.generateStructures(Boolean.parseBoolean(args[4]));
                } catch (Throwable throwable) {
                    sender.sendMessage(MultiWorld.PREFIX + "§cFailed §7to parse world §bboolean §7please recheck the §bspelling §7of the name§8!");
                }
            }
            if (args.length >= 6) {
                worldCreator.generator(args[5]);
            }
            sender.sendMessage(MultiWorld.PREFIX + "§7Process was §astarted§8...");
            WorldManager.ActionResult actionResult = MultiWorld.instance().worldManager().createWorld(worldCreator);
            switch (actionResult) {
                case WORLD_CREATED -> {
                    sender.sendMessage(MultiWorld.PREFIX + "§7World §b" + args[1] + "§7 successfully §acreated§8!");
                }
                case WORLD_ALREADY_LOADED -> {
                    sender.sendMessage(MultiWorld.PREFIX + "§7World §b" + args[1] + "§7 is §balready loaded§8!");
                }
                case PROCESS_FAILED -> {
                    sender.sendMessage(MultiWorld.PREFIX + "§7Something went §cwrong§8!");
                }
            }
        } else {
            sender.sendMessage(MultiWorld.PREFIX + "§7You used the command §cnot correctly §7please recheck your arguments§8.");
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if(args.length <= 1) {
            return Stream.of("unload", "load", "create", "delete", "teleport", "list", "spawn")
                    .filter(item -> item.toLowerCase().startsWith((args.length == 0 ? "" : args[0]).toLowerCase()))
                    .toList();
        } else if(args.length == 2 && args[0].equalsIgnoreCase("unload") || args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("spawn")) {
            return MultiWorld.instance().worldManager().loadedWorlds().stream().map(WorldManager.LoadedWorld::name)
                    .filter(item -> item.toLowerCase().startsWith(args[1].toLowerCase()))
                    .toList();
        } else if(args.length == 2 && args[0].equalsIgnoreCase("teleport")) {
            return Bukkit.getWorlds().stream().map(WorldInfo::getName)
                    .filter(item -> item.toLowerCase().startsWith(args[1].toLowerCase()))
                    .toList();
        } else if(args.length == 2 && args[0].equalsIgnoreCase("load")) {
            List<String> suggestions = new ArrayList<>();
            for (File file : Objects.requireNonNull(Bukkit.getWorldContainer().listFiles())) {
                if(file.isDirectory() && WorldManager.checkFolder(file)) {
                    suggestions.add(file.getName());
                }
            }
            return suggestions.stream()
                    .filter(item -> item.toLowerCase().startsWith(args[1].toLowerCase()))
                    .toList();
        } else if(args[0].equalsIgnoreCase("create")) {
            if(args.length == 2) {
                return Stream.of("<Name>")
                        .filter(item -> item.toLowerCase().startsWith(args[1].toLowerCase()))
                        .toList();
            } else if(args.length == 3) {
                return Arrays.stream(World.Environment.values()).map(item -> item.name().toLowerCase())
                        .filter(item -> item.toLowerCase().startsWith(args[2].toLowerCase()))
                        .toList();
            } else if(args.length == 4) {
                return Arrays.stream(WorldType.values()).map(item -> item.name().toLowerCase())
                        .filter(item -> item.toLowerCase().startsWith(args[3].toLowerCase()))
                        .toList();
            } else if(args.length == 5) {
                return Stream.of("<Structures>", "true", "false")
                        .filter(item -> item.toLowerCase().startsWith(args[4].toLowerCase()))
                        .toList();
            } else if(args.length == 6) {
                return Stream.of("<Generator>")
                        .filter(item -> item.toLowerCase().startsWith(args[5].toLowerCase()))
                        .toList();
            }
        }
        return new ArrayList<>();
    }

}
