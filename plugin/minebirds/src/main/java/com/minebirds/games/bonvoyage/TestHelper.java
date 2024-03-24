package com.minebirds.games.bonvoyage;

import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TestHelper {

  public static void fillPlayerWithResources(
    Player player,
    Document resources
  ) {
    resources
      .keySet()
      .forEach(k -> {
        Bukkit.getLogger().info("Parse item" + k);
        int count = (int) resources.get(k);
        ItemStack item = new ItemStack(Material.matchMaterial(k), count);
        player.getInventory().addItem(item);
      });
  }

  public static Document craftRequirementsList() {
    return new Document("SAND", 1);
  }
}
