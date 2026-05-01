package com.example;

import com.hedera.agentkit.HederaAgentKit;
import com.hedera.agentkit.HederaTool;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        HederaAgentKit agent = new HederaAgentKit(HederaTool.ACCOUNT_QUERY, HederaTool.TRANSFER_HBAR);

        System.out.println("Agent ready. Type your question (or 'exit' to quit):");
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("exit")) break;
            if (input.isBlank()) continue;
            System.out.println("User:  " + input);
            System.out.println("Agent: " + agent.chat(input));
            System.out.println();
        }
    }
}
