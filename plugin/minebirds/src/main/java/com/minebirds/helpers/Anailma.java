package com.minebirds.helpers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;

// Professora de Geografia do Ensino médio
// Seu TCC na UEFS foi sobre o desenvolvimento e formação
// da região do Homero Figueredo, em Feira de Santana.
// Lecionou no Colégio Estadual Menino Jesus de Praga, no Sobradinho.
// Essa classe tem como objetivo auxiliar nas operações geográficas
public class Anailma {

  // TODO: Improve time and space complexity
  // TODO: Add fallback
  public static Location findOcean(Location target, World world) {
    int tx = target.getBlockX();
    int ty = target.getBlockY();
    int tz = target.getBlockZ();
    int attempts = 100;
    int skip = 300;
    int limit = tx + (skip * attempts);
    for (int x = tx; x < limit; x = x + skip) {
      Bukkit.getLogger().info("Looking for a ocean..");
      Location bl = new Location(world, x, ty, tz);
      Biome b = world.getBiome(bl);
      if (b == Biome.OCEAN) {
        Bukkit.getLogger().info("Ocean found!");
        return new Location(world, bl.getX(), world.getHighestBlockYAt(bl), tz);
      }
    }
    Bukkit.getLogger().info("Ocean not found");
    return target;
  }
}
