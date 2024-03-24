package com.minebirds.games.bonvoyage;

import com.minebirds.utils.Lists;
import com.minebirds.utils.Numbers;
import java.util.Map;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

public class Requirements {

  /**
   * Verifica se o jogador tem todos os itens necessários.
   *
   * @param player O jogador a ser verificado.
   * @param requiredItems Os itens necessários, mapeados do nome para a quantidade.
   * @return Verdadeiro se o jogador tem todos os itens, falso caso contrário.
   */
  public static boolean hasAllRequiredItems(
    Chest chest,
    Map<String, Integer> requiredItems
  ) {
    // Contador de itens no inventário do jogador
    Map<String, Integer> chestItemsCount = new java.util.HashMap<>();

    // Itera sobre o inventário do jogador
    for (ItemStack item : chest.getInventory().getContents()) {
      if (item != null) {
        // Assume que você tem uma função que converte o ItemStack para o nome do item como uma string.
        // Isso pode ser algo simples como item.getType().toString() ou algo mais complexo,
        // dependendo de como você deseja identificar os itens.
        String itemName = getItemName(item);

        // Atualiza a contagem no mapa
        chestItemsCount.put(
          itemName,
          chestItemsCount.getOrDefault(itemName, 0) + item.getAmount()
        );
      }
    }

    // Verifica se o jogador tem todos os itens necessários
    for (Map.Entry<String, Integer> entry : requiredItems.entrySet()) {
      String requiredItem = entry.getKey();
      Integer requiredAmount = entry.getValue();

      if (chestItemsCount.getOrDefault(requiredItem, 0) < requiredAmount) {
        // O jogador não tem itens suficientes
        return false;
      }
    }

    // O jogador tem todos os itens
    return true;
  }

  /**
   * Converte um ItemStack para o nome do item.
   * Esta implementação é apenas um placeholder, ajuste conforme necessário.
   *
   * @param item O ItemStack a ser convertido.
   * @return O nome do item como uma String.
   */
  private static String getItemName(ItemStack item) {
    // Aqui você pode adicionar sua lógica para converter o ItemStack em um nome de item.
    // Isso pode depender de como você está tratando os nomes dos itens em seu plugin.
    return item.getType().toString();
  }

  public static Document craftRequirementsList() {
    Document list = new Document();
    Lists
      .getRandomItems(requirementGroup_FOOD_T1(), 1)
      .forEach(s -> list.put(s, Numbers.randomBetween(12, 75)));
    Lists
      .getRandomItems(requirementGroup_FOOD_T2(), 1)
      .forEach(s -> list.put(s, Numbers.randomBetween(8, 32)));
    Lists
      .getRandomItems(requirementGroup_MONSTER_T1(), 2)
      .forEach(s -> list.put(s, Numbers.randomBetween(12, 24)));
    Lists
      .getRandomItems(requirementGroup_MONSTER_T2(), 1)
      .forEach(s -> list.put(s, Numbers.randomBetween(6, 12)));
    Lists
      .getRandomItems(requirementGroup_ORES_T1(), 1)
      .forEach(s -> list.put(s, Numbers.randomBetween(24, 82)));
    Lists
      .getRandomItems(requirementGroup_ORES_T2(), 2)
      .forEach(s -> list.put(s, Numbers.randomBetween(8, 24)));
    Lists
      .getRandomItems(requirementGroup_ORES_T3(), 1)
      .forEach(s -> list.put(s, Numbers.randomBetween(1, 3)));
    return list;
  }

  public static final java.util.List<String> requirementGroup_FOOD() {
    java.util.List<String> arr = new java.util.ArrayList<String>();
    arr.add(Material.BREAD.toString());
    arr.add(Material.APPLE.toString());
    arr.add(Material.CARROT.toString());
    arr.add(Material.MUTTON.toString());
    arr.add(Material.PORKCHOP.toString());
    arr.add(Material.RABBIT.toString());
    arr.add(Material.CHICKEN.toString());
    arr.add(Material.BEEF.toString());
    return arr;
  }

  public static final java.util.List<String> requirementGroup_FOOD_T1() {
    java.util.List<String> arr = new java.util.ArrayList<String>();
    arr.add(Material.BREAD.toString());
    arr.add(Material.APPLE.toString());
    arr.add(Material.CARROT.toString());
    return arr;
  }

  public static final java.util.List<String> requirementGroup_FOOD_T2() {
    java.util.List<String> arr = new java.util.ArrayList<String>();
    arr.add(Material.MUTTON.toString());
    arr.add(Material.PORKCHOP.toString());
    arr.add(Material.RABBIT.toString());
    arr.add(Material.CHICKEN.toString());
    arr.add(Material.BEEF.toString());
    return arr;
  }

  public static final java.util.List<String> requirementGroup_MONSTER_T1() {
    java.util.List<String> arr = new java.util.ArrayList<String>();
    arr.add(Material.SPIDER_EYE.toString());
    arr.add(Material.STRING.toString());
    arr.add(Material.BONE.toString());
    // arr.add(Material.ARROW.toString());
    return arr;
  }

  public static final java.util.List<String> requirementGroup_MONSTER_T2() {
    java.util.List<String> arr = new java.util.ArrayList<String>();
    arr.add(Material.BOW.toString());
    arr.add(Material.GUNPOWDER.toString());
    return arr;
  }

  public static final java.util.List<String> requirementGroup_ORES_T1() {
    java.util.List<String> arr = new java.util.ArrayList<String>();
    arr.add(Material.COAL.toString());
    arr.add(Material.COPPER_INGOT.toString());
    return arr;
  }

  public static final java.util.List<String> requirementGroup_ORES_T2() {
    java.util.List<String> arr = new java.util.ArrayList<String>();
    arr.add(Material.IRON_INGOT.toString());
    arr.add(Material.GOLD_INGOT.toString());
    return arr;
  }

  public static final java.util.List<String> requirementGroup_ORES_T3() {
    java.util.List<String> arr = new java.util.ArrayList<String>();
    arr.add(Material.LAVA_BUCKET.toString());
    arr.add(Material.EMERALD.toString());
    arr.add(Material.REDSTONE.toString());
    return arr;
  }

  public final java.util.List<String> requirementGroup_BLOCKS_T3() {
    java.util.List<String> arr = new java.util.ArrayList<String>();
    arr.add(Material.STONE.toString());
    arr.add(Material.SAND.toString());
    return arr;
  }
}
