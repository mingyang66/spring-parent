package com.emily.sample.graphql.config;

import java.util.Arrays;
import java.util.List;

/**
 * @author :  Emily
 * @since :  2025/9/5 上午11:10
 */
public record Book (String id, String name, int pageCount, String authorId) {

    private static final List<Book> books = Arrays.asList(
            new Book("book-1", "Effective Java", 416, "author-1"),
            new Book("book-2", "Hitchhiker's Guide to the Galaxy", 208, "author-2"),
            new Book("book-3", "Down Under", 436, "author-3")
    );

    public static Book getById(String id) {
        return books.stream()
                .filter(book -> book.id().equals(id))
                .findFirst()
                .orElse(null);
    }
}
