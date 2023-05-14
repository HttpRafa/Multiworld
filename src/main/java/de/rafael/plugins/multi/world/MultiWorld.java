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

package de.rafael.plugins.multi.world;

import de.rafael.plugins.multi.world.command.WorldCommand;
import de.rafael.plugins.multi.world.manager.WorldManager;
import lombok.Getter;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Rafael K.
 * @created 23:57, 13.05.23
 * @project multiworld
 */

public class MultiWorld extends JavaPlugin {

    public static final String PREFIX = "§8➜ §3W§borldManager §8● §7";

    @Getter
    private static MultiWorld instance;

    @Getter
    private WorldManager worldManager;

    @Override
    public void onLoad() {
        instance = this;

        this.worldManager = new WorldManager();
    }

    @Override
    public void onEnable() {
        {
            PluginCommand command = getCommand("world");
            if(command != null) {
                WorldCommand worldCommand = new WorldCommand();
                command.setExecutor(worldCommand);
                command.setTabCompleter(worldCommand);
                command.setPermission("multiworld.command.world");
            }
        }

        // Load all worlds
        this.worldManager.loadWorlds();
    }

}
