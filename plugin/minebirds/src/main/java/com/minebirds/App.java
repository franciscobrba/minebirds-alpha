package com.minebirds;

import com.minebirds.events.PlayerJoin;
import com.minebirds.events.PlayerQuit;
import com.minebirds.games.Bonvoyage;
import java.util.*;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

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
    String arg1 = args[1];
    Player player = Bukkit.getPlayer("franciscobrba");

    if (Objects.equals(hook, "gamepreview")) {
      if (Objects.equals(arg1, "bv")) {
        String key = String.valueOf(new Date().getTime());
        Bonvoyage game = new Bonvoyage(key);
        game.createGameLobby();
        game.startGame();
        startGameCountdown(
          3,
          c -> game.lobbyCountdown(c),
          c -> {
            game.lobbyCountdown(null);
            game.spawnPlayers();
            startGameLoop(x -> game.updateHour(), null);
          }
        );
        return true;
      } else {
        player.sendMessage("invalid game type");
        return true;
      }
    } else {
      player.sendMessage("invalid command");
      return true;
    }
  }

  public void startGameCountdown(
    int duration,
    Consumer<Integer> round,
    Consumer<Void> over
  ) {
    new BukkitRunnable() {
      int counter = duration;

      @Override
      public void run() {
        if (counter > 0) {
          round.accept(counter);
          counter--;
        } else {
          over.accept(null);
          // Caronte.travel(player, "world-" + key);
          // dayCheck(player, key);
          this.cancel(); // Cancel the scheduled task
        }
      }
    }
      .runTaskTimer(this, 0L, 20L);
  }

  public void startGameLoop(Consumer<Void> round, Consumer<Void> over) {
    new BukkitRunnable() {
      int counter = 2000;

      @Override
      public void run() {
        if (counter > 0) {
          round.accept(null);
          counter--;
        } else {
          over.accept(null);
          this.cancel();
        }
      }
    }
      .runTaskTimer(this, 0L, 40L);
  }

  @Override
  public void onDisable() {
    getLogger().info("onDisable is called!");
  }
}
