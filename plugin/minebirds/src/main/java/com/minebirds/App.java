package com.minebirds;

import com.minebirds.events.InteractionCheck;
import com.minebirds.events.PlayerJoin;
import com.minebirds.events.PlayerQuit;
import com.minebirds.games.Bonvoyage;
import java.util.*;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class App extends JavaPlugin implements Listener {

  boolean dbConnection;

  @Override
  public void onEnable() {
    dbConnection = Database.connect();
    getServer().getPluginManager().registerEvents(new PlayerQuit(), this);
    getServer().getPluginManager().registerEvents(new PlayerJoin(), this);
  }

  public boolean onCommand(
    CommandSender sender,
    Command cmd,
    String label,
    String[] args
  ) {
    if (!dbConnection) return false;

    String hook = args[0];
    Player player = getServer().getPlayer(sender.getName());

    if (Objects.equals(hook, "quest")) {
      String action = args[1];
      if (Objects.equals(action, "create")) {
        String questId = args[2];
        if (Objects.equals(questId, "bv")) {
          Bonvoyage.create(player);
        }
      }
      if (Objects.equals(action, "join")) {
        String key = args[2];
        if (key != null) {
          Bonvoyage.join(player, key);
        }
      }
      if (Objects.equals(action, "start")) {
        String key = args[2];
        if (key != null) {
          Bonvoyage.start(key);
        }
      }
      return true;
    }
    return true;
  }

  @Override
  public void onDisable() {
    getLogger().info("onDisable is called!");
  }
}
