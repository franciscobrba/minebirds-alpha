package com.minebirds.games.bonvoyage;

import com.minebirds.Database;
import java.util.function.Consumer;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Tools {

  public static void timeOutMessage(
    String key,
    Integer duration,
    String message
  ) {
    Document doc = Database.findGame(key);
    java.util.List<String> survivors = doc.getList("survivors", String.class);
    Tools.countdown(
      duration,
      c -> {
        Bulk.action(
          survivors,
          player -> {
            String title = message;
            String desc = "Going to lobby in " + c + "s";
            Messages.wide(player, title, desc);
          }
        );
      },
      c -> {
        Bulk.action(
          survivors,
          player -> {
            Messages.clear(player);
            player.getInventory().clear();
            player.teleport(Bukkit.getWorld("lobby").getSpawnLocation());
          }
        );
      }
    );
  }

  public static void countdown(
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
          this.cancel();
        }
      }
    }
      .runTaskTimer(getPlugin(), 0L, 20L);
  }

  public static Plugin getPlugin() {
    return Bukkit.getPluginManager().getPlugin("Minebirds");
  }

  public static int calcTime(long tick) {
    // 1 day = 1m
    int base = 100;
    // 1 day = 20m
    // int base = 1000;

    // Garante que o tick esteja no intervalo de 0 a 23999
    long normalizedTick = tick % 24000;
    // Converte ticks para horas (24 horas em um dia Minecraft)
    int hours = (int) (normalizedTick / base);
    // Não é necessário arredondar para baixo, pois a conversão para int faz isso
    return hours;
  }
}
