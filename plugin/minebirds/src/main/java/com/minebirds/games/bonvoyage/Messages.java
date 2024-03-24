package com.minebirds.games.bonvoyage;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Messages {

  public static void clear(Player player) {
    player.sendTitle("", "", 0, 20, 0);
  }

  public static void wide(Player player, String title, String description) {
    player.sendTitle(title, description, 1, 9999999, 1);
  }

  public static void atLobby(Player player, String title, String description) {
    player.teleport(Bukkit.getServer().getWorld("lobby").getSpawnLocation());
    wide(player, title, description);
  }
}
