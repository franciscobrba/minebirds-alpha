package com.minebirds.events;

import com.minebirds.Database;
import com.minebirds.games.Bonvoyage;
import com.minebirds.games.bonvoyage.Alpha;
import com.minebirds.games.bonvoyage.Events;
import com.minebirds.games.bonvoyage.Requirements;
import java.util.*;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InteractionCheck implements Listener {

  public String key;

  public InteractionCheck(String givenKey) {
    this.key = givenKey;
  }

  @EventHandler
  public void onPlayerDie(PlayerDeathEvent event) {
    Events.die(this.key, event.getEntity().getPlayer());
  }

  @EventHandler
  public void onInventoryClose(InventoryCloseEvent event) {
    Document game = Database.findGame(this.key);
    if (game == null) return;
    if (!(event.getInventory().getHolder() instanceof Chest)) return;
    Chest chest = (Chest) event.getInventory().getHolder();
    Location loc = chest.getLocation();
    Location chestLocation = Alpha.locationFromGame(game, "chest_location");
    if (
      loc.getBlockX() == chestLocation.getX() &&
      loc.getBlockY() == chestLocation.getY() &&
      loc.getBlockZ() == chestLocation.getZ()
    ) {
      boolean won = Requirements.hasAllRequiredItems(
        chest,
        convertDocumentToMap(
          Document.parse(game.get("requirements").toString())
        )
      );
      if (won) {
        Events.victory(this.key);
      }
    }
  }

  public static Map<String, Integer> convertDocumentToMap(Document document) {
    Map<String, Integer> map = new HashMap<>();
    for (Map.Entry<String, Object> entry : document.entrySet()) {
      map.put(entry.getKey(), Integer.valueOf(entry.getValue().toString()));
    }
    return map;
  }
}
