package com.minebirds.games.bonvoyage;

import com.minebirds.Schematics;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Infra {

  public static void buildShip(String key, Location givenLocation) {
    Bukkit.getLogger().info("Creating boat");
    Schematics.build(
      givenLocation,
      "boat.v7",
      b -> {
        if (b.getType().toString().equals("CHEST")) {
          Location chestLocation = b.getLocation();
          Alpha.saveLocation(key, "chest_location", chestLocation);
        }
      }
    );
    Bukkit.getLogger().info("Boat created!");
  }
}
