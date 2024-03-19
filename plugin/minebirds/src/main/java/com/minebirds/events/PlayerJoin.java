package com.minebirds.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.SpawnCategory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerJoin implements Listener {

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    World world = event.getPlayer().getWorld();
    String worldName = world.getName();
    if (worldName == null) return;
    if (worldName.equals("lobby")) {
      world.setTime(7000);
      world.setStorm(false);
      world.setThundering(false);
      world.setWeatherDuration(0);
      world.setSpawnLimit(SpawnCategory.MONSTER, 0);
      removeNearbyAnimalsAndMobs(world.getSpawnLocation(), 200);
    }
  }

  public void removeNearbyAnimalsAndMobs(Location center, int radius) {
    World world = center.getWorld();
    if (world == null) return;
    for (Entity entity : world.getEntities()) {
      if (entity instanceof LivingEntity && !(entity instanceof Player)) {
        if (center.distance(entity.getLocation()) <= radius) {
          Bukkit.getLogger().info("killing mob " + entity.getType());
          entity.remove(); // Remove the entity
        }
      }
    }
  }

  @EventHandler
  public void onPlayerRespawn(PlayerRespawnEvent event) {}
}
