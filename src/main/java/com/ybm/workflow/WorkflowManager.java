/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.workflow;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class WorkflowManager {
/*
    List<Integer> transactionsIds =
            transactions.stream()
                    .filter(t -> t.getType() == Transaction.GROCERY)
                    .sorted(comparing(Transaction::getValue).reversed())
                    .map(Transaction::getId)
                    .collect(toList());

    List<Integer> transactionsIds =
            transactions.parallelStream()
                    .filter(t -> t.getType() == Transaction.GROCERY)
                    .sorted(comparing(Transaction::getValue).reversed())
                    .map(Transaction::getId)
                    .collect(toList());

    boolean expensive =
            transactions.stream()
                    .allMatch(t -> t.getValue() > 100);

    transactions.stream()
            .filter(t -> t.getType() == Transaction.GROCERY)
            .findAny()
              .ifPresent(System.out::println);

    int statement =
            transactions.stream()
                    .map(Transaction::getValue)
                    .sum(); // error since Stream has no sum method

    int statementSum =
            transactions.stream()
                    .mapToInt(Transaction::getValue)
                    .sum(); // works!

    IntStream oddNumbers =
            IntStream.rangeClosed(10, 30)
                    .filter(n -> n % 2 == 1);

    long numberOfLines =
            Files.lines(Paths.get(“yourFile.txt”), Charset.defaultCharset())
                    .count();

    List<String> words = Arrays.asList("Oracle", "Java", "Magazine");
    List<Integer> wordLengths =
            words.stream()
                    .map(String::length)
                    .collect(toList());

    */

    List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);
    List<Integer> twoEvenSquares =
            numbers.stream()
                    .filter(n -> {
                        System.out.println("filtering " + n);
                        return n % 2 == 0;
                    })
                    .map(n -> {
                        System.out.println("mapping " + n);
                        return n * n;
                    })
                    .limit(2)
                    .collect(toList());


}
