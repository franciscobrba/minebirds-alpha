package com.minebirds;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BaseBlock;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;

public class Schematics {

  // static final String SERVER_FOLDER = "/Users/francisco/mb-env/server";
  static final String SERVER_FOLDER = "";

  @SuppressWarnings("null")
  public static void build(
    Location location,
    String fileName,
    Consumer<Block> callback
  ) {
    java.io.File file = new java.io.File(
      SERVER_FOLDER + "/plugins/schematics/" + fileName + ".schematic"
    );

    ClipboardFormat format = ClipboardFormats.findByFile(file);
    if (format != null) {
      try {
        ClipboardReader reader = format.getReader(
          new java.io.FileInputStream(file)
        );
        Clipboard clipboard = reader.read();
        Bukkit
          .getLogger()
          .info("Schematic Implementation Started: " + fileName);
        // Loop trough the schematic file to get the {x,y,z} of each block
        for (
          int x = clipboard.getMinimumPoint().getBlockX();
          x < clipboard.getMaximumPoint().getBlockX() + 1;
          x++
        ) {
          for (
            int y = clipboard.getMinimumPoint().getBlockY();
            y < clipboard.getMaximumPoint().getBlockY();
            y++
          ) {
            for (
              int z = clipboard.getMinimumPoint().getBlockZ();
              z < clipboard.getMaximumPoint().getBlockZ() + 1;
              z++
            ) {
              BaseBlock block = clipboard.getFullBlock(
                BlockVector3.at(x, y, z)
              );
              // Place the block using the given location as a start point
              location
                .clone()
                .add(x, y, z)
                .getBlock()
                .setType(BukkitAdapter.adapt(block).getMaterial());
              // get reference of the inserted block
              Block b = location.clone().add(x, y, z).getBlock();
              callback.accept(b);
            }
          }
        }
      } catch (java.io.IOException e) {}
    }
  }
}
