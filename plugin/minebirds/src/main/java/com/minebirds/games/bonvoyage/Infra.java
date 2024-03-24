package com.minebirds.games.bonvoyage;

import com.minebirds.Schematics;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Infra {

  public static void buildShip(String key, Location givenLocation) {
    Schematics.build(
      givenLocation,
      "boat.v4",
      b -> {
        Bukkit.getLogger().info("Creating boat");
        if (b.getType().toString().equals("CHEST")) {
          Location chestLocation = b.getLocation();
          Alpha.saveLocation(key, "chest_location", chestLocation);
        }
      }
    );
  }
}
