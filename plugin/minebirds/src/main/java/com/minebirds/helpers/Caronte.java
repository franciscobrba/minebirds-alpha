package com.minebirds.helpers;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

// Na mitologia grega, Caronte (em grego: Χάρων, transl.: Chárōn) é o barqueiro de Hades,
// que carrega as almas dos recém-mortos sobre as águas do rio Estige e Aqueronte,
// que dividiam o mundo dos vivos do mundo dos mortos.
public class Caronte {

  public static void travel(Player player, String worldName) {
    World world = Bukkit.getServer().getWorld(worldName);
    player.getPlayer().sendTitle("", "", 0, 20, 0);
    if (world != null) {
      player.teleport(world.getSpawnLocation(), TeleportCause.PLUGIN);
    } else {
      Bukkit.getLogger().info("World not found");
    }
  }
}
