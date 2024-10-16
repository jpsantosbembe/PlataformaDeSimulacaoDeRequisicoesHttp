package com.joaobembe;

import io.github.cdimascio.dotenv.Dotenv;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        final String regex = dotenv.get("REGEX");
        System.out.println(regex);
    }
}