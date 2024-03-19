package com.minebirds.games;

import com.minebirds.App;
import com.minebirds.Database;
import com.minebirds.Schematics;
import com.minebirds.events.InteractionCheck;
import com.minebirds.events.PlayerQuit;
import com.minebirds.helpers.Anailma;
import com.minebirds.helpers.Caronte;
import com.minebirds.models.GameDoc;
import com.mongodb.lang.Nullable;
import java.util.HashMap;
import java.util.Map;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Bonvoyage {

  String key;
  Location spawnLocation;
  public Location chestLocation;
  public World world;
  public Player captain;
  public long gameDay = 0;
  public int gameHour = 0;
  public Document requiredResources;
  public Plugin ref;
  public HashMap<Biome, Integer> nearbyBiomes = new HashMap<>();

  private static final String ENV = "PROD";

  @SuppressWarnings("unused")
  public static int hoursPassed(long tick) {
    int base = 1000; // 1 day = 20m
    if (ENV == "TEST") {
      base = 100; // 1 day = 1m
    }
    // Garante que o tick esteja no intervalo de 0 a 23999
    long normalizedTick = tick % 24000;
    // Converte ticks para horas (24 horas em um dia Minecraft)
    int hours = (int) (normalizedTick / base);
    // Não é necessário arredondar para baixo, pois a conversão para int faz isso
    return hours;
  }

  public void questCompletedWithSuccess() {
    screenMessage("Success!!", "Quest completed!");
    Player c = this.captain;
    new BukkitRunnable() {
      int counter = 5;

      @Override
      public void run() {
        if (counter > 0) {
          c.sendMessage("Sending to the lobby in " + counter + "s...");
          counter--;
        } else {
          Caronte.travel(c, "lobby");
          // TODO: Delete the world
          this.cancel(); // Cancel the scheduled task
        }
      }
    }
      .runTaskTimer(Bukkit.getPluginManager().getPlugin("Minebirds"), 0L, 20L);
  }

  public void updateHour() {
    long serverTick = this.world.getTime();
    this.gameHour = hoursPassed(serverTick);
    this.gameDay = this.gameHour / 24;
    // this.captain.sendMessage("Game tick is " + serverTick);
    // this.captain.sendMessage("Game day is" + this.gameDay);
    // boolean won = BonvoyageRequirements.hasAllRequiredItems(
    //   this.captain,
    //   convertDocumentToMap(this.requiredResources)
    // );

    // if (won) {

    // }

    boolean captainIsOnShip = Bonvoyage.isPlayerNearLocation(
      this.captain,
      this.chestLocation,
      5
    );

    if (captainIsOnShip) {
      Bukkit.getLogger().info("Wellcome back to the ship");
    } else {
      Bukkit.getLogger().info("You're travelling nearby");
    }
  }

  public static boolean isPlayerNearLocation(
    Player player,
    Location targetLocation,
    double distance
  ) {
    if (player == null || targetLocation == null) {
      return false;
    }

    // Get the player's current location
    Location playerLocation = player.getLocation();

    // Check if the player's location is within the specified distance of the target location
    // Note: This method throws an IllegalArgumentException if the locations are not in the same world
    if (!playerLocation.getWorld().equals(targetLocation.getWorld())) {
      return false; // Player and targetLocation are in different worlds
    }

    return playerLocation.distance(targetLocation) <= distance;
  }

  public static Map<String, Integer> convertDocumentToMap(Document document) {
    Map<String, Integer> map = new HashMap<>();
    for (Map.Entry<String, Object> entry : document.entrySet()) {
      map.put(entry.getKey(), Integer.valueOf(entry.getValue().toString()));
    }
    return map;
  }

  public void checkSurroundingBiomes(Location currentLocation) {
    Bukkit.getLogger().info("Searching biomes...");
    World world = currentLocation.getWorld();
    if (world == null) return; // Always check if the world is not null
    this.captain.setGameMode(GameMode.CREATIVE);

    int[] xLine = { -1, -2, -3, -4, -5, -6, 1, 2, 3, 4, 5, 6 };
    int[] zLine = { -1, -2, -3, -4, -5, -6, 1, 2, 3, 4, 5, 6 };

    int slot = 40;
    for (int z : zLine) {
      for (int x : xLine) {
        int y = world.getHighestBlockYAt(x, z);
        int go_x = (x * slot);
        int go_z = (z * slot);
        Biome biome = currentLocation
          .clone()
          .add(go_x, y, go_z)
          .getBlock()
          .getBiome();
        Integer count = 0;
        if (this.nearbyBiomes.get(biome) != null) {
          count = this.nearbyBiomes.get(biome);
        }
        this.nearbyBiomes.put(biome, count + 1);
      }
    }
    Bukkit.getLogger().info("nearby biomes..." + this.nearbyBiomes.toString());
  }

  public Bonvoyage(String gameId) {
    this.key = gameId;
    Bukkit.getLogger().info("Creating World");
    createWorld(gameId);
    Bukkit.getLogger().info("World created!");
    createPerfectSpawnLocation();
    createShip();
    createGameDoc();
  }

  private void createWorld(String gameId) {
    String worldFolder = "world-" + gameId;
    Bukkit.getServer().createWorld(new WorldCreator(worldFolder));
    this.world = Bukkit.getServer().getWorld(worldFolder);
  }

  private void createPerfectSpawnLocation() {
    this.spawnLocation =
      Anailma.findOcean(this.world.getSpawnLocation(), this.world);
    this.world.setSpawnLocation(this.spawnLocation);
  }

  private void createShip() {
    Schematics.build(
      this.spawnLocation,
      "boat.v4",
      b -> {
        Bukkit.getLogger().info(b.getType().toString());
        if (b.getType().toString().equals("CHEST")) {
          Bukkit
            .getLogger()
            .info("Chest location is " + b.getLocation().toString());
          this.chestLocation = b.getLocation();
          this.spawnLocation = this.spawnLocation.clone().add(3, 0, 3);
          this.world.setSpawnLocation(this.spawnLocation);
        }
      }
    );
  }

  private void createGameDoc() {
    GameDoc game = new GameDoc();
    game.setCreator("franciscobrba");
    game.save(this.key);
  }

  public void createGameLobby() {
    Player currentPlayer = Bukkit.getPlayer("franciscobrba");
    currentPlayer.teleport(
      Bukkit.getServer().getWorld("lobby").getSpawnLocation()
    );
  }

  public void startGame() {
    Database.updateGameProp(this.key, "status", "STARTED");
    Bukkit
      .getServer()
      .getPluginManager()
      .registerEvents(
        new InteractionCheck(this),
        Bukkit.getPluginManager().getPlugin("Minebirds")
      );
  }

  public void spawnPlayers() {
    // TODO: spawn all players
    Player currentPlayer = Bukkit.getPlayer("franciscobrba");
    currentPlayer.closeInventory();
    currentPlayer.teleport(this.world.getSpawnLocation());
    // TODO: define a captain randomically
    this.captain = currentPlayer;
    // TODO: define a method to generate items
    clearScreenTitle();
    this.captain.getInventory().clear();
    this.captain.getInventory().addItem(createMissionBook());
    this.buildResourceList();
    this.checkSurroundingBiomes(chestLocation);
  }

  public void screenMessage(String title, String description) {
    this.captain.sendTitle(title, description, 1, 9999999, 1);
  }

  public void lobbyCountdown(@Nullable Integer counter) {
    Player currentPlayer = Bukkit.getPlayer("franciscobrba");
    if (counter != null) {
      currentPlayer.sendTitle(
        "Game is started!",
        "Teleporting in " + counter + "s...",
        1,
        9999999,
        1
      );
    } else {
      currentPlayer.getPlayer().sendTitle("", "", 0, 20, 0);
    }
  }

  public void clearScreenTitle() {
    Player currentPlayer = Bukkit.getPlayer("franciscobrba");
    currentPlayer.getPlayer().sendTitle("", "", 0, 20, 0);
  }

  public ItemStack createMissionBook() {
    ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
    BookMeta bookMeta = (BookMeta) book.getItemMeta();
    bookMeta.setTitle("Bon Voyage");
    bookMeta.setAuthor("Cabral Emo");
    String page1 =
      """
                Olá, @Player, você é o novo capitão,

                Escrevi esse pergaminho no meu
                leito de morte para que vocês
                consigam concluir a missão.

                Enquanto vocês exploravam
                nossa embarcação foi atacada,
                levaram quase tudo...
                """;
    String page2 =
      """
                Para completar a missão
                vocês precisam encontrar
                alguns recursos nas
                redondezas

                """;
    bookMeta.addPage(page1, page2);
    NamespacedKey key = new NamespacedKey("minebirds", "unique_id");
    bookMeta
      .getPersistentDataContainer()
      .set(key, PersistentDataType.STRING, "bv-book");
    book.setItemMeta(bookMeta);
    return book;
  }

  public void buildResourceList() {
    Document doc = new Document("SAND", 3);
    this.requiredResources = doc;
    Database.updateGameProp(this.key, "requiredResources", doc.toJson());
  }
}
