# Proof of Concept: Experiment using Hedera Java SDK + Langchain4j

An AI agent that lets you interact with the Hedera testnet in plain English — check account balances and transfer HBAR using natural language.

## Prerequisites

| Tool | Version |
|------|---------|
| Java | 25+ |
| Maven | 3.6+ |

Verify your versions:
```bash
java -version
mvn -version
```

## Setup

1. Create a `.env` file in the project root:

```
ANTHROPIC_API_KEY=your_anthropic_api_key
HEDERA_ACCOUNT_ID=0.0.xxxxxx
HEDERA_PRIVATE_KEY=your_hedera_ecdsa_private_key
```

- **ANTHROPIC_API_KEY** — get one at https://console.anthropic.com
- **HEDERA_ACCOUNT_ID** and **HEDERA_PRIVATE_KEY** — create a free testnet account at https://portal.hedera.com

## Run

```bash
mvn compile exec:java -Dexec.mainClass="com.example.Main"
```

The agent starts an interactive prompt. Type your question and press Enter:

```
Agent ready. Type your question (or 'exit' to quit):
What is my balance?
Please send 2 HBARs to 0.0.7279544
exit
```

Type `exit` to quit.
