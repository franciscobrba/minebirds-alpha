package com.minebirds.games.bonvoyage;

import com.minebirds.utils.BrazilianTokens;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.persistence.PersistentDataType;

public class Book {

  public static ItemStack create(Document resources, String playerName) {
    ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
    BookMeta bookMeta = (BookMeta) book.getItemMeta();
    bookMeta.setTitle("Bon Voyage");
    bookMeta.setAuthor("Cabral Emo");
    String page1 =
      """
                Olá, @Player, você é o novo capitão,

                Escrevi esse pergaminho no meu
                leito de morte para que você
                consiga liderar essa missão.

                Enquanto você explorava
                nossa embarcação foi atacada,
                levaram quase tudo...
                """;
    page1 = page1.replace("@Player", playerName);
    String page2 =
      """
                Para completar a missão
                você precisa lidear a tripulação
                para coletar alguns recursos,
                você tem apenas 3 dias
                """;
    String page3 = bookResourcesPage(resources);
    String page4 =
      """
        Se você conseguir a façanha de coletar esses items,
        você deve colocar os items no baú do navio.
      """;
    bookMeta.addPage(page1, page2, page3, page4);
    NamespacedKey key = new NamespacedKey("minebirds", "unique_id");
    bookMeta
      .getPersistentDataContainer()
      .set(key, PersistentDataType.STRING, "bv-book");
    book.setItemMeta(bookMeta);
    return book;
  }

  private static String bookResourcesPage(Document resources) {
    List<String> all = new ArrayList<>();
    all.add("Itens requeridos: \n");
    resources
      .keySet()
      .forEach(key -> {
        all.add(BrazilianTokens.translate(key) + " = " + resources.get(key));
      });
    String page = String.join("\n", all);
    return page;
  }
}
