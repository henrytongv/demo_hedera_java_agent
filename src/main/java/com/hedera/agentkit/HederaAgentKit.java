package com.hedera.agentkit;

import com.hedera.agentkit.plugins.core.AccountQuery;
import com.hedera.agentkit.plugins.core.TransferHbar;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.UserMessage;
import io.github.cdimascio.dotenv.Dotenv;
import java.util.ArrayList;
import java.util.List;

public class HederaAgentKit {

    private final Agent agent;

    interface Agent {
        String chat(@UserMessage String userMessage);
    }

    public HederaAgentKit(HederaTool... tools) {
        Dotenv dotenv = Dotenv.configure()
                .directory(".")
                .filename(".env")
                .load();

        String apiKey = dotenv.get("ANTHROPIC_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("ANTHROPIC_API_KEY is not set in .env");
        }

        String accountId = dotenv.get("HEDERA_ACCOUNT_ID");
        String privateKey = dotenv.get("HEDERA_PRIVATE_KEY");

        AnthropicChatModel model = AnthropicChatModel.builder()
                .apiKey(apiKey)
                .modelName("claude-haiku-4-5")
                .maxTokens(1024)
                .build();

        List<Object> toolInstances = new ArrayList<>();
        for (HederaTool tool : tools) {
            switch (tool) {
                case ACCOUNT_QUERY -> toolInstances.add(new AccountQuery(accountId, privateKey));
                case TRANSFER_HBAR -> toolInstances.add(new TransferHbar(accountId, privateKey));
            }
        }

        String systemMessage = """
                You are a helpful Hedera blockchain assistant.
                The user's default Hedera account ID is: %s
                When no account ID is specified for a balance query, use that account ID.
                When no source account is specified for a transfer, use that account ID as the source.
                Use the appropriate tool when the user asks about account balances or transfers.
                """.formatted(accountId);

        this.agent = AiServices.builder(Agent.class)
                .chatModel(model)
                .systemMessageProvider(id -> systemMessage)
                .tools(toolInstances)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
    }

    public String chat(String message) {
        return agent.chat(message);
    }
}
