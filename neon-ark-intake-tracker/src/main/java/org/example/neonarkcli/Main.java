package org.example.neonarkcli;

import org.example.neonarkcli.api.HttpWardenApiClient;
import org.example.neonarkcli.api.MockWardenApiClient;
import org.example.neonarkcli.api.WardenGateway;
import org.example.neonarkcli.menu.WardenMenu;
import org.example.neonarkcli.service.WardenService;

import java.util.Scanner;

// Composition root — wires all dependencies together and starts the program.

public class Main {

    public static void main(String[] args) {

        // ── Deployment decision: flip this to false when the real server is live ──
        boolean useMock = true;

        // ── Build the gateway (adapter) ───────────────────────────────────────────
        WardenGateway gateway = useMock
                ? new MockWardenApiClient()
                : new HttpWardenApiClient("http://localhost:8080");

        // ── Wire the layers (Menu -> Service -> Gateway) ──────────────────────────
        WardenService service = new WardenService(gateway);
        Scanner       scanner = new Scanner(System.in);
        WardenMenu    menu    = new WardenMenu(service, scanner);

        // ── Print startup banner ──────────────────────────────────────────────────
        printBanner(useMock);

        // ── Hand control to the menu ──────────────────────────────────────────────
        menu.run();

        scanner.close();
    }

    private static void printBanner(boolean useMock) {
        System.out.println();
        System.out.println("  ============================================================");
        System.out.println("          NEON ARK SYSTEMS DIVISION                           ");
        System.out.println("          Boundary Console Verification Initiative            ");
        System.out.println("          Admin Warden Onboarding Console  v1.0               ");
        System.out.println("  ============================================================");
        System.out.println("  NOTICE: This console operates against a read-only           ");
        System.out.println("          database snapshot. Mutation actions are simulated.  ");
        System.out.println("          The database owns the truth. The client reflects it.");
        System.out.println("  ============================================================");
        System.out.println();
        System.out.println("  Mode    : " + (useMock ? "MOCK (seed data, no HTTP)" : "HTTP (live server)"));
        System.out.println("  Initializing seed data...");
        System.out.println("  Database snapshot loaded. Ready.");
    }
}
