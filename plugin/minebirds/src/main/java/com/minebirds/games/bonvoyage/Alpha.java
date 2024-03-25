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
import org.bukkit.inventory.ItemStack;

public class Alpha {

  public static String createDatabaseEntry(Player player) {
    List<String> players = new ArrayList<>();
    players.add(player.getName());

    String key = String.valueOf(Numbers.gameKey());
    Database.createGame(
      new Document("key", key)
        .append("creator", player.getName())
        .append("status", "OPENED")
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
    Bulk.message(playersFromGame(game), choosen + " é o novo capitão...");
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

  public static Boolean gameCompleted(String key) {
    String status = Database.getGameProp(key, "status").toString();
    return status.equals("VICTORY") || status.equals("LOST");
  }

  public static void sendBookToNewCaptain(String key) {
    Document game = Database.findGame(key);
    Player player = Bukkit.getPlayer(game.get("captain").toString());
    ItemStack book = Book.create(
      Document.parse(game.get("requirements").toString()),
      player.getName()
    );
    if (player.getInventory().firstEmpty() == -1) {
      getWorld(key).dropItem(player.getLocation(), book);
    } else {
      player.getInventory().addItem(book);
    }
  }

  public static Map<String, Integer> convertDocumentToMap(Document document) {
    Map<String, Integer> map = new HashMap<>();
    for (Map.Entry<String, Object> entry : document.entrySet()) {
      map.put(entry.getKey(), Integer.valueOf(entry.getValue().toString()));
    }
    return map;
  }

  public static World getLobby() {
    return Bukkit.getWorld("lobby");
  }
}
