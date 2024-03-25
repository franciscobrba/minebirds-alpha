package com.minebirds.games;

import com.minebirds.Database;
import com.minebirds.games.bonvoyage.Alpha;
import com.minebirds.games.bonvoyage.Events;
import com.minebirds.games.bonvoyage.Infra;
import com.minebirds.games.bonvoyage.Messages;
import com.minebirds.models.GameDoc;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Bonvoyage {

  public static void create(Player player) {
    String key = Alpha.createDatabaseEntry(player);
    World world = Alpha.buildWorld(key);
    Location ocean = Alpha.getOcean(world);
    Infra.buildShip(key, ocean);
    Alpha.setSpawn(world, ocean.clone().add(6, 2, 13));
    sendCreatorToLobby(player, key);
  }

  public static boolean join(Player player, String key) {
    Document game = Database.findGame(key);
    String playerName = player.getName();
    if (game != null) {
      if (!GameDoc.playerIsInGame(playerName, key)) {
        Database.addToGameList(key, "players", playerName);
        Database.addToGameList(key, "survivors", playerName);
        sendPlayerToLobby(player);
      } else {
        player.sendMessage("You already joined this game!");
      }
    } else {
      player.sendMessage("Game not found!");
    }
    return true;
  }

  public static void start(String key) {
    Document game = Database.findGame(key);
    if (game != null) {
      Events.start(key);
    } else {
      Bukkit.getLogger().info("Inexistent game!");
    }
  }

  public static void sendPlayerToLobby(Player player) {
    Messages.atLobby(player, "Joined Quest...", "Wait the quest creator");
  }

  public static void sendCreatorToLobby(Player player, String key) {
    String title = "Quest key: " + key;
    String desc = "Let the quest begin when all players are ready";
    Messages.atLobby(player, title, desc);
  }

  public static World getWorldByKey(String key) {
    return Bukkit.getServer().getWorld("w-" + key);
  }
}
