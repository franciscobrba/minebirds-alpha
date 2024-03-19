package com.minebirds.games;

import java.util.Map;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

public class BonvoyageRequirements {

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
}
