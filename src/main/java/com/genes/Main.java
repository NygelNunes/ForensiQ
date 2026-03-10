package com.genes;

/**
 * Entry point do fat JAR.
 *
 * IMPORTANTE: esta classe intermediária é obrigatória.
 * Fat JARs com JavaFX falham se o Main-Class do MANIFEST
 * apontar diretamente para uma classe que extends Application.
 * O classloader do JavaFX precisa ser inicializado antes,
 * e isso só acontece quando App.main() é chamado indiretamente.
 */
public class Main {
    public static void main(String[] args) {
        App.main(args);
    }
}