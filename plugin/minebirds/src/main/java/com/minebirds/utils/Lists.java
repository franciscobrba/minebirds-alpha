package com.minebirds.utils;

import java.util.*;

public class Lists {

  public static String getRandomItem(List<String> originalList) {
    return getRandomItems(originalList, 1).get(0);
  }

  public static List<String> getRandomItems(
    List<String> originalList,
    int numberOfItems
  ) {
    // Verifica se a lista é nula ou se o número de itens é maior que o tamanho da lista
    if (
      originalList == null ||
      originalList.size() == 0 ||
      numberOfItems <= 0 ||
      numberOfItems > originalList.size()
    ) {
      throw new IllegalArgumentException(
        "A lista é nula, vazia, ou o número de itens solicitados é inválido."
      );
    }

    // Embaralha a lista original
    java.util.Collections.shuffle(originalList);

    // Retorna uma sublista contendo os primeiros X itens
    return new java.util.ArrayList<>(originalList.subList(0, numberOfItems));
  }
}
