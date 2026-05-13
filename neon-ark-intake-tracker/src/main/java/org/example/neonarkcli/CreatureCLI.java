package org.example.neonarkcli;

import org.example.neonarkcli.api.HttpCreatureApiClient;
import org.example.neonarkcli.menu.CreatureMenu;

import java.util.Scanner;

// Composition root — wires dependencies and starts the CLI.
// The CLI connects to the Spring Boot server at localhost:8080.
// Make sure Docker + the server are running before starting.
public class CreatureCLI {

    public static void main(String[] args) {
        String baseUrl = "http://localhost:8080";

        HttpCreatureApiClient api    = new HttpCreatureApiClient(baseUrl);
        Scanner               scanner = new Scanner(System.in);
        CreatureMenu          menu   = new CreatureMenu(api, scanner);

        printBanner(baseUrl);
        menu.run();
        scanner.close();
    }

    private static void printBanner(String baseUrl) {
        System.out.println();
        System.out.println("  =====================================================");
        System.out.println("          NEON ARK CREATURE MANAGEMENT SYSTEM         ");
        System.out.println("          CLI Client v1.0                              ");
        System.out.println("  =====================================================");
        System.out.println("  Server : " + baseUrl);
        System.out.println("  Mode   : LIVE (connected to Spring Boot backend)");
        System.out.println("  Tip    : Start with option 1 to verify connectivity.");
        System.out.println("  =====================================================");
        System.out.println();
    }
}
