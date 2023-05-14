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

package de.rafael.plugins.multi.world.location;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author Rafael K.
 * @created 00:20, 14.05.23
 * @project multiworld
 */

@Getter
@Setter
@ToString
public class WorldLocation {

    private String world;

    private double x, y, z;
    private float yaw, pitch;

    public WorldLocation(String world, double x, double y, double z, float yaw, float pitch) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public WorldLocation(@NotNull Location location) {
        this.world = Objects.requireNonNull(location.getWorld()).getName();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
    }

    public boolean isAt(@NotNull WorldLocation worldLocation) {
        return isAt(worldLocation.x, worldLocation.y, worldLocation.z, worldLocation.yaw, worldLocation.pitch);
    }

    public boolean isAt(@NotNull Location location) {
        return isAt(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    public boolean isAt(double x, double y, double z, float yaw, float pitch) {
        return this.x == x && this.y == y && this.z == z && this.yaw == yaw && this.pitch == pitch;
    }

    public Location bukkit() {
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }

    public String toShortString() {
        return world + " " + x + " " + y + " " + z + " " + yaw + " " + pitch;
    }

}