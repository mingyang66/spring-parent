package com.emily.infrastructure.test.po.eq;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigDecimal;

/**
 * 图书
 *
 * @author :  Emily
 * @since :  2023/12/21 7:20 PM
 */
public class Book {
    private String name;
    private String author;
    private int age;
    private double price;
    private float weight;
    private BigDecimal money;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        Book book = (Book) obj;
        return new EqualsBuilder()
                .append(name, book.name)
                .append(author, book.author)
                .append(age, book.age)
                .append(price, book.price)
                .append(weight, book.weight)
                .append(money, book.money)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.name)
                .append(this.author)
                .append(this.age)
                .append(this.price)
                .append(this.weight)
                .append(this.money)
                .toHashCode();
    }
}
