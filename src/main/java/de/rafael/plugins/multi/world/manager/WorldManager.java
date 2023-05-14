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

package de.rafael.plugins.multi.world.manager;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import de.rafael.plugins.multi.world.MultiWorld;
import de.rafael.plugins.multi.world.location.WorldLocation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Rafael K.
 * @created 00:17, 14.05.23
 * @project multiworld
 */

public class WorldManager {

    public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().serializeNulls().setPrettyPrinting().create();

    public static final File WORLDS_FILE = new File("worlds.json");

    @Getter
    private List<LoadedWorld> loadedWorlds;

    public WorldManager() {
        if(WORLDS_FILE.exists()) {
            try {
                JsonArray jsonWorlds = JsonParser.parseReader(new InputStreamReader(new FileInputStream(WORLDS_FILE), StandardCharsets.UTF_8)).getAsJsonArray();
                Type type = new TypeToken<List<LoadedWorld>>() {}.getType();
                this.loadedWorlds = GSON.fromJson(jsonWorlds, type);
                return;
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                Bukkit.getConsoleSender().sendMessage(MultiWorld.PREFIX + "§cFailed to load worlds§8.");
            }
        }
        this.loadedWorlds = new ArrayList<>();
    }

    public ActionResult unloadWorld(String name, boolean save) {
        if(this.loadedWorlds.stream().anyMatch(loadedWorld -> loadedWorld.name().equalsIgnoreCase(name))) {
            if(Bukkit.unloadWorld(name, save)) {
                this.loadedWorlds.removeIf(loadedWorld -> loadedWorld.name().equalsIgnoreCase(name));
                save();
                return ActionResult.WORLD_UNLOADED;
            } else {
                return ActionResult.WORLD_NOT_EMPTY;
            }
        } else {
            return ActionResult.WORLD_NOT_FOUND;
        }
    }

    public ActionResult loadWorld(String name) {
        File file = new File(Bukkit.getWorldContainer(), name);
        Path folderPath = new File(Bukkit.getWorldContainer(), name).toPath();
        Path serverFolder = Bukkit.getWorldContainer().toPath();
        if(folderPath.startsWith(serverFolder)) {
            if(checkFolder(file)) {
                return createWorld(WorldCreator.name(name));
            } else return ActionResult.WORLD_NOT_FOUND;
        } else {
            return ActionResult.PROCESS_FAILED;
        }
    }

    public ActionResult createWorld(@NotNull WorldCreator worldCreator) {
        if(Bukkit.getWorlds().stream().anyMatch(item -> item.getName().equalsIgnoreCase(worldCreator.name()))) {
            return ActionResult.WORLD_ALREADY_LOADED;
        }

        World world = worldCreator.createWorld();
        if(world != null) {
            LoadedWorld loadedWorld = new LoadedWorld(worldCreator.name());
            this.loadedWorlds.add(loadedWorld);
            save();
            return ActionResult.WORLD_CREATED;
        } else {
            return ActionResult.PROCESS_FAILED;
        }
    }

    public ActionResult deleteWorld(String name) {
        try {
            ActionResult actionResult = unloadWorld(name, true);
            if(actionResult == ActionResult.WORLD_UNLOADED) {
                Path folderPath = new File(Bukkit.getWorldContainer(), name).toPath();
                Path serverFolder = Bukkit.getWorldContainer().toPath();
                if(folderPath.startsWith(serverFolder)) {
                    FileUtils.deleteDirectory(folderPath.toFile());
                    return ActionResult.WORLD_DELETED;
                } else {
                    return ActionResult.PROCESS_FAILED;
                }
            } else {
                return actionResult;
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(MultiWorld.PREFIX + "§cFailed to delete world§8.");
            return ActionResult.PROCESS_FAILED;
        }
    }

    public Optional<LoadedWorld> world(String name) {
        return this.loadedWorlds.stream().filter(item -> item.name().equalsIgnoreCase(name)).findFirst();
    }

    public void save() {
        try(FileWriter fileWriter = new FileWriter(WORLDS_FILE, StandardCharsets.UTF_8)) {
            fileWriter.write(GSON.toJson(this.loadedWorlds, new TypeToken<List<LoadedWorld>>() {}.getType()));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(MultiWorld.PREFIX + "§cFailed to save worlds§8.");
        }
    }

    public void loadWorlds() {
        this.loadedWorlds.forEach(loadedWorld -> WorldCreator.name(loadedWorld.name()).createWorld());
    }

    public static boolean checkFolder(File folder) {
        return new File(folder, "level.dat").exists();
    }

    public enum ActionResult {

        WORLD_CREATED,
        WORLD_UNLOADED,
        WORLD_DELETED,
        WORLD_NOT_FOUND,
        WORLD_ALREADY_LOADED,
        WORLD_NOT_EMPTY,
        PROCESS_FAILED

    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class LoadedWorld {

        private final String name;
        private WorldLocation spawnLocation;

        public LoadedWorld(String name) {
            this.name = name;
        }

    }

}
