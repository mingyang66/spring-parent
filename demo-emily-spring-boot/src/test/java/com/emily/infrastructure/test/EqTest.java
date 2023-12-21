package com.emily.infrastructure.test;

import com.emily.infrastructure.test.po.eq.Book;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

/**
 * @author :  Emily
 * @since :  2023/12/21 7:53 PM
 */
public class EqTest {
    @Test
    public void eqTest() {
        Book book = new Book();
        book.setName("java");
        book.setAuthor("Emily");
        book.setPrice(10);
        book.setAge(10);
        book.setMoney(BigDecimal.TEN);
        book.setWeight(12.3f);

        Book book1 = new Book();
        book1.setName("java");
        book1.setAuthor("Emily");
        book1.setPrice(10);
        book1.setAge(10);
        book1.setMoney(BigDecimal.TEN);
        book1.setWeight(12.3f);

        Assertions.assertTrue(book.equals(book));
        Assertions.assertTrue(book.equals(book1));
        Assertions.assertEquals(book.hashCode(), book1.hashCode());
    }
}
