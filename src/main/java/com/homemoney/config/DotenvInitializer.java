package com.homemoney.config;

import io.github.cdimascio.dotenv.Dotenv;

public class DotenvInitializer {
    public static void initialize() {
        Dotenv dotenv = Dotenv.configure().directory("./").load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
    }
}
