package com.minebirds.events;

import com.minebirds.games.Bonvoyage;
import com.minebirds.games.BonvoyageRequirements;
import com.minebirds.helpers.Caronte;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InteractionCheck implements Listener {

  public Bonvoyage ref;

  // public InteractionCheck(Class<? extends Bonvoyage> class1) {
  //   this.chestLocation = class1.chestLocation;
  // }

  public InteractionCheck(Bonvoyage r) {
    this.ref = r;
  }

  @EventHandler
  public void onPlayerDie(PlayerDeathEvent event) {
    Caronte.travel(event.getEntity().getPlayer(), "lobby");
  }

  @EventHandler
  public void onInventoryClose(InventoryCloseEvent event) {
    if (!(event.getInventory().getHolder() instanceof Chest)) return;

    Chest chest = (Chest) event.getInventory().getHolder();
    Location loc = chest.getLocation();

    // Check if this is the chest at your specified location
    if (
      loc.getBlockX() == this.ref.chestLocation.getX() &&
      loc.getBlockY() == this.ref.chestLocation.getY() &&
      loc.getBlockZ() == this.ref.chestLocation.getZ()
    ) {
      boolean won = BonvoyageRequirements.hasAllRequiredItems(
        chest,
        Bonvoyage.convertDocumentToMap(this.ref.requiredResources)
      );
      if (won) {
        this.ref.questCompletedWithSuccess();
      }
    }
  }
}
