package com.minebirds.games.bonvoyage;

import com.minebirds.Database;
import com.minebirds.events.InteractionCheck;
import java.util.function.Consumer;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Events {

  static final Integer STEP_DURATION = 1;

  public static void closeGame(String key, String status, String message) {
    Document doc = Database.findGame(key);
    Tools.countdown(
      STEP_DURATION,
      c -> {
        Bulk.action(
          doc.getList("players", String.class),
          player -> {
            String title = message;
            String desc = "Going to lobby in " + c + "s";
            Messages.wide(player, title, desc);
          }
        );
      },
      c -> {
        Bulk.action(
          doc.getList("players", String.class),
          player -> {
            Messages.clear(player);
            player.teleport(Bukkit.getWorld("lobby").getSpawnLocation());
          }
        );
        Database.updateGameProp(key, "status", status);
      }
    );
  }

  public static void victory(String key) {
    closeGame(key, "VICTORY", "Quest completed");
  }

  public static void lost(String key) {
    closeGame(key, "LOST", "Quest failed");
  }

  public static void clock(String key) {
    World world = Alpha.getWorld(key);
    long serverTick = world.getTime();
    double hour = Tools.calcTime(serverTick);
    double day = hour / 24;
    Database.updateGameProp(key, "game_time_hour", String.valueOf(hour));
    Database.updateGameProp(key, "game_time_day", String.valueOf(day));
  }

  public static void die(String key, Player player) {
    Database.removeFromGameList(key, "survivors", player.getName());
    Document game = Database.findGame(key);
    java.util.List<String> survivors = game.getList("survivors", String.class);
    if (survivors.size() == 0) {
      Events.lost(key);
    } else {
      if (player.getName().equals(game.get("captain"))) {
        Bukkit.getLogger().info("Captain die");
      }
      player.getInventory().clear();
      Messages.atLobby(player, "Rest in Peace", "You become history");
      Tools.countdown(
        3,
        x -> {},
        x -> {
          Messages.clear(player);
        }
      );
    }
    // remove from survivors
    // it's captain?
    // call captainDie
    // survivors.length === 0
    // call lost
  }

  public static void captainDie(String key) {
    // set new captain
    // place the book on the boat
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
        pickCaptain(game);
        String captainName = Database.getGameProp(key, "captain").toString();
        Player captain = Bukkit.getPlayer(captainName);
        // give book the captain
        captain.getInventory().addItem(Book.create(resources, captainName));
        // save requirements at database
        Database.updateGameProp(key, "requirements", resources.toJson());
        TestHelper.fillPlayerWithResources(captain, resources);
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
            Double hour = Double.parseDouble(
              Database.getGameProp(key, "game_time_hour").toString()
            );
            Double day = Double.parseDouble(
              Database.getGameProp(key, "game_time_day").toString()
            );
            if (day >= 3 & hour > 1) {
              Events.lost(key);
            }
          },
          null
        );
      }
    );
  }

  public static void pickCaptain(Document game) {
    String key = game.get("key").toString();
    Alpha.choseCaptain(game, key);
  }
}
