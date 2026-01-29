package com.Ali.Store.App;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

public class TestForCreateSlug {
    @Test
    void create_slug() {
        String productName = "Iphone Pro Max 16";

        final String[] split = productName.split("/");

        List<String> stringList = List.of("Iphone", "Pro", "Max", "16");
        BinaryOperator<String> formatName = (slug, name) -> slug + "-" + name.split(" ")[0];
        final String reduce = stringList.stream()
                .map(String::toLowerCase)
                .peek(s -> System.out.println(s + " after low"))
                .map(s -> s.replaceAll("[^a-zA-Z0-9]", ""))
                .peek(s -> System.out.println(s + " after replace"))
                .reduce("", formatName);

        System.out.println(reduce);
    }

    @Test
    void name2() {
        String productName = "Iphone Pro Max 16";
        final String[] split = productName.split(" ");

        final List<String> list = Arrays.stream(split)
                .toList();

        BinaryOperator<String> formatSlug = (slug, name) -> slug + "-" + name;
        final Function<String, String> deleteSpecificCharacters = s -> s.replaceAll("[^a-zA-Z0-9]", "");
        final String reduce = list.stream()
                .map(String::toLowerCase)
                .map(deleteSpecificCharacters)
                .reduce("", formatSlug);

        System.out.println(reduce.substring(1));

        System.out.println(reduce);
    }

    @Test
    void name() {
        String productName = "Iphone Pro Max 16";

        StringBuilder result = new StringBuilder();
        for (int i=0; i < productName.split(" ").length; i++) {
            final String[] split = productName.split(" ");
            final String lowerCase = split[i].toLowerCase();

            result.append("-").append(lowerCase);
        }

           result = new StringBuilder(result.toString().replaceFirst("-", ""));
            assertThat(result.toString()).isEqualTo("iphone-pro-max-16");

    }
}
