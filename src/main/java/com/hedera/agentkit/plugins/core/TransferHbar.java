package com.hedera.agentkit.plugins.core;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.Status;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.TransactionResponse;
import com.hedera.hashgraph.sdk.TransferTransaction;
import dev.langchain4j.agent.tool.Tool;

public class TransferHbar {

    private final String operatorAccountId;
    private final String operatorPrivateKey;

    public TransferHbar(String operatorAccountId, String operatorPrivateKey) {
        this.operatorAccountId = operatorAccountId;
        this.operatorPrivateKey = operatorPrivateKey;
    }

    @Tool("Transfer HBAR to a recipient Hedera account ID. Specify the recipient account ID and the integer amount of HBAR to send. The source is always the operator's own account.")
    public String transfer(String recipientAccountId, long amountHbar) {
        Client client = null;
        try {
            AccountId operatorId = AccountId.fromString(operatorAccountId);
            PrivateKey privateKey = PrivateKey.fromStringECDSA(operatorPrivateKey);

            client = Client.forTestnet();
            client.setOperator(operatorId, privateKey);

            TransferTransaction txTransfer = new TransferTransaction()
                    .addHbarTransfer(operatorId, new Hbar(-amountHbar))
                    .addHbarTransfer(AccountId.fromString(recipientAccountId), new Hbar(amountHbar));

            TransactionResponse txResponse = txTransfer.execute(client);
            TransactionReceipt receipt = txResponse.getReceipt(client);
            Status status = receipt.status;
            String txId = txResponse.transactionId.toString();

            return "Transfer of " + amountHbar + " HBAR to " + recipientAccountId + " completed.\n"
                    + "Status: " + status + "\n"
                    + "Transaction ID: " + txId + "\n"
                    + "Hashscan URL: https://hashscan.io/testnet/transaction/" + txId;
        } catch (Exception e) {
            return "Error transferring HBAR to " + recipientAccountId + ": " + e.getMessage();
        } finally {
            if (client != null) {
                try { client.close(); } catch (Exception ignored) {}
            }
        }
    }
}
