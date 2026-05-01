package com.hedera.agentkit.plugins.core;

import com.hedera.hashgraph.sdk.AccountBalance;
import com.hedera.hashgraph.sdk.AccountBalanceQuery;
import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.PrivateKey;
import dev.langchain4j.agent.tool.Tool;

public class AccountQuery {

    private final String operatorAccountId;
    private final String operatorPrivateKey;

    public AccountQuery(String operatorAccountId, String operatorPrivateKey) {
        this.operatorAccountId = operatorAccountId;
        this.operatorPrivateKey = operatorPrivateKey;
    }

    @Tool("Get the HBAR balance for a Hedera account ID (format: shard.realm.num, e.g. 0.0.1234). Pass null to query the operator's own account.")
    public String getBalance(String accountId) {
        String targetAccount = (accountId == null || accountId.isBlank()) ? operatorAccountId : accountId;
        Client client = null;
        try {
            client = Client.forTestnet();
            client.setOperator(
                AccountId.fromString(operatorAccountId),
                PrivateKey.fromStringECDSA(operatorPrivateKey)
            );

            AccountBalance balance = new AccountBalanceQuery()
                    .setAccountId(AccountId.fromString(targetAccount))
                    .execute(client);

            return "Account " + targetAccount + " has a balance of " + balance.hbars + " HBARs.";
        } catch (Exception e) {
            return "Error fetching balance for account " + targetAccount + ": " + e.getMessage();
        } finally {
            if (client != null) {
                try { client.close(); } catch (Exception ignored) {}
            }
        }
    }
}
