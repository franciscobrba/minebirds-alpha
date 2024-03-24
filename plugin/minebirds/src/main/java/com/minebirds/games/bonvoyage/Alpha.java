package com.minebirds.games.bonvoyage;

import com.minebirds.Database;
import com.minebirds.helpers.Anailma;
import com.minebirds.utils.Lists;
import com.minebirds.utils.Numbers;
import java.util.*;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

public class Alpha {

  public static String createDatabaseEntry(Player player) {
    List<String> players = new ArrayList<>();
    players.add(player.getName());

    String key = String.valueOf(Numbers.gameKey());
    Database.createGame(
      new Document("key", key)
        .append("creator", player.getName())
        .append("players", players)
        .append("survivors", players)
    );
    return key;
  }

  public static World buildWorld(String gameKey) {
    String worldFolder = "w-" + gameKey;
    Bukkit.getServer().createWorld(new WorldCreator(worldFolder));
    return Bukkit.getServer().getWorld(worldFolder);
  }

  public static Location setSpawn(World givenWorld) {
    Location location = Anailma.findOcean(
      givenWorld.getSpawnLocation(),
      givenWorld
    );
    givenWorld.setSpawnLocation(location);
    return location;
  }

  public static Player choseCaptain(Document game, String key) {
    String choosen = Lists.getRandomItem(
      game.getList("survivors", String.class)
    );
    Database.updateGameProp(key, "captain", choosen);
    return Bukkit.getPlayer(choosen);
  }

  public static World getWorld(String key) {
    return Bukkit.getServer().getWorld("w-" + key);
  }

  public static void saveLocation(String key, String prop, Location location) {
    Database.updateGameProp(key, prop + "_x", String.valueOf(location.getX()));
    Database.updateGameProp(key, prop + "_y", String.valueOf(location.getY()));
    Database.updateGameProp(key, prop + "_z", String.valueOf(location.getZ()));
  }

  public static Location locationFromGame(Document game, String prop) {
    return new Location(
      getWorld(game.get("key").toString()),
      Double.parseDouble(game.get(prop + "_x").toString()),
      Double.parseDouble(game.get(prop + "_y").toString()),
      Double.parseDouble(game.get(prop + "_z").toString())
    );
  }

  public static List<String> playersFromGame(Document game) {
    return game.getList("players", String.class);
  }
}
