package com.minebirds.games.bonvoyage;

import java.util.*;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Bulk {

  public static void action(
    List<String> givenPlayers,
    Consumer<Player> givenAction
  ) {
    List<Player> players = onlinePlayers(givenPlayers);
    for (Player player : players) {
      givenAction.accept(player);
    }
  }

  public static List<Player> onlinePlayers(List<String> givenUsernames) {
    List<Player> output = new ArrayList<>();
    givenUsernames.forEach(username -> {
      Player p = Bukkit.getPlayer(username);
      if (p != null && p.isOnline()) {
        output.add(p);
      }
    });
    return output;
  }

  public static void teleport(List<String> players, Location location) {
    action(
      players,
      player -> {
        player.teleport(location);
      }
    );
  }

  public static void message(List<String> players, String message) {
    action(
      players,
      player -> {
        player.sendMessage(message);
      }
    );
  }

  public static void clear(List<String> players) {
    action(
      players,
      player -> {
        Messages.clear(player);
        player.closeInventory();
        player.getInventory().clear();
      }
    );
  }
}
