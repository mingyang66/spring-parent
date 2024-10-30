package com.emily.sample.validation.entity;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * @author :  Emily
 * @since :  2024/10/29 上午11:25
 */
public class Computer {
    @NotEmpty(message = "name不可为空")
    private String name;
    @NotEmpty(message = "board不可为空")
    private String board;
    @NotNull
    private Password password;

    public Computer(Password password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBoard() {
        return board;
    }

    public void setBoard(String board) {
        this.board = board;
    }

    public static class Password {
        @NotEmpty(message = "password不可为空")
        private String password;

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
