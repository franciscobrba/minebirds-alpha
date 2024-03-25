package com.minebirds.games.bonvoyage;

import com.minebirds.Database;
import com.minebirds.events.InteractionCheck;
import java.util.function.Consumer;
import javax.xml.crypto.Data;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Events {

  static final Integer STEP_DURATION = 4;
  static final Integer WIN_DURATION = 4;
  static final Integer LOST_DURATION = 4;

  public static void closeGame(String key, String status) {
    Database.updateGameProp(key, "status", status);
  }

  public static void victory(String key) {
    closeGame(key, "VICTORY");
    Tools.timeOutMessage(key, WIN_DURATION, "Quest completed");
  }

  public static void clock(String key) {
    Boolean gameCompleted = Alpha.gameCompleted(key);
    if (!gameCompleted) {
      World world = Alpha.getWorld(key);
      long serverTick = world.getTime();
      double hour = Tools.calcTime(serverTick);
      double day = hour / 24;
      Database.updateGameProp(key, "game_time_hour", String.valueOf(hour));
      Database.updateGameProp(key, "game_time_day", String.valueOf(day));
    }
  }

  public static void diedCaptain(String key, Player player) {
    Document game = Database.findGame(key);
    Alpha.choseCaptain(game, key);
    Alpha.sendBookToNewCaptain(key);
  }

  public static void diedPlayer(String key, Player player) {
    Database.removeFromGameList(key, "survivors", player.getName());
    Document game = Database.findGame(key);
    String captain = game.get("captain").toString();
    java.util.List<String> survivors = game.getList("survivors", String.class);
    player.getInventory().clear();
    player.setRespawnLocation(Alpha.getLobby().getSpawnLocation());

    if (survivors.size() == 0) {
      closeGame(key, "LOST");
      Database.updateGameProp(key, "resolution", "NO_SURVIVORS");
    } else if (player.getName().equals(captain)) {
      diedCaptain(key, player);
    }
  }

  public static void gameTimeout(String key) {
    closeGame(key, "LOST");
    Database.updateGameProp(key, "resolution", "TIMEOUT");
    Tools.timeOutMessage(key, LOST_DURATION, "3 days already, it's over");
  }

  public static void start(String key) {
    Document doc = Database.findGame(key);
    Tools.countdown(
      STEP_DURATION,
      c -> {
        Bulk.action(
          doc.getList("players", String.class),
          player -> {
            String title = "Game was started";
            String desc = "Teleporting in " + c + "s";
            Messages.wide(player, title, desc);
          }
        );
      },
      c -> {
        // find game
        Document game = Database.findGame(key);
        // teleport players
        Bulk.teleport(
          Alpha.playersFromGame(game),
          Alpha.locationFromGame(game, "chest_location")
        );
        // clear messages
        Bulk.clear(Alpha.playersFromGame(game));
        // generate resources
        Document resources = Requirements.craftRequirementsList();

        // pick a captain
        Alpha.choseCaptain(game, key);
        String captainName = Database.getGameProp(key, "captain").toString();
        Player captain = Bukkit.getPlayer(captainName);
        // give book the captain
        captain.getInventory().addItem(Book.create(resources, captainName));
        // save requirements at database
        Database.updateGameProp(key, "requirements", resources.toJson());

        // add boat to player
        //

        // send back to plugin
        // start event check
        Bukkit
          .getServer()
          .getPluginManager()
          .registerEvents(new InteractionCheck(key), Tools.getPlugin());
        // start clock event
        Events.clock(key);
        Tools.countdown(
          8000,
          x -> {
            Events.clock(key);
            Boolean gameCompleted = Alpha.gameCompleted(key);
            if (!gameCompleted) {
              Double hour = Double.parseDouble(
                Database.getGameProp(key, "game_time_hour").toString()
              );
              Double day = Double.parseDouble(
                Database.getGameProp(key, "game_time_day").toString()
              );
              if (day >= 3 & hour > 1) {
                gameTimeout(key);
              }
            }
          },
          null
        );
      }
    );
  }
}
