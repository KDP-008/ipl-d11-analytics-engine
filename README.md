# IPL D11 Analytics Engine

A high-performance, offline data engineering, cricket stream processing, and combinatorial optimization playground built on **Spring Boot** and modern **Java (Java 21 LTS / Java 25 ready)**.

This engine serves as an independent analytics microservice and evaluation benchmark for testing autonomous AI coding agents on multi-file engineering problems.

---

## Architecture Overview

```
┌────────────────────────────────────────────────────────────────────────────────────────┐
│                        IPL D11 ANALYTICS ENGINE ARCHITECTURE                           │
├────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                        │
│   [Historical NDJSON Telemetry] (246 events, match_284_csk_vs_mi.ndjson)               │
│                                  │                                                     │
│                                  ▼                                                     │
│   ┌────────────────────────────────────────────────────────────────────────────────┐   │
│   │                      STREAM INGESTION & CRICKET STATE MACHINE                  │   │
│   │  - MatchReplaySimulator: Configurable playback (1x, 100x, BURST)               │   │
│   │  - DeliveryStreamProcessor: Invariants, strike rotation, over limits, free-hit │   │
│   └──────────────────────┬───────────────────────────────────┬─────────────────────┘   │
│                          │                                   │                         │
│                          ▼                                   ▼                         │
│   ┌──────────────────────────────┐   ┌─────────────────────────────────────────────┐   │
│   │  REAL-TIME FPS ENGINE        │   │     COLUMNAR OLAP STORAGE LAYER             │   │
│   │  - Incremental delta scoring │   │  - DuckDB / Parquet time-series tables      │   │
│   │  - Milestones (30/50/100, 3/5│   │  - Partitioned: season/match/innings        │   │
│   │    wickets, maidens, economy)│   │  - Phase analytics (Powerplay, Middle, Death│   │
│   └──────────────┬───────────────┘   └─────────────────────────────────────────────┘   │
│                  │                                                                     │
│                  ▼                                                                     │
│   ┌──────────────────────────────┐   ┌─────────────────────────────────────────────┐   │
│   │  LIVE CONTEST LEADERBOARD    │   │       COMBINATORIAL ROSTER OPTIMIZER        │   │
│   │  - O(log N) updates & rank   │   │  - Constrained 0-1 Knapsack / MILP Solver   │   │
│   │  - Standard "1224" ranking   │   │  - 100-credit budget, role bounds, max 7    │   │
│   └──────────────────────────────┘   │  - Optimal fantasy team selection in <50ms  │   │
│                                      └─────────────────────────────────────────────┘   │
└────────────────────────────────────────────────────────────────────────────────────────┘
```

---

## Benchmark Design & Evaluation Criteria

For the complete senior-engineer specification, grading rubrics, and the 18-file touchpoint matrix, refer to:
[IPL_D11_Analytics_Engine_Specification.md](../IPL_D11_Analytics_Engine_Specification.md)

### Verification Fixtures (100% Offline):
* `src/main/resources/telemetry/match_284_csk_vs_mi.ndjson`: 246 deliveries from a full CSK vs MI IPL match.
* `src/main/resources/telemetry/ground_truth_player_points.json`: Exact expected point totals for all 22 players.
* `src/main/resources/telemetry/ground_truth_optimal_roster.json`: Mathematically verified optimal 11-player lineup under 100 credits (Optimal score: 627.5 pts, 97.0 credits).

---

## Quick Start & Verification

### Running Tests Locally
```bash
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home
./mvnw clean test
```

### Running the Application
```bash
./mvnw spring-boot:run
```
Service runs on port `8081`.

### Key REST Endpoints
* **Health & Telemetry Status**: `GET http://localhost:8081/api/analytics/health`
* **Trigger Match Replay**: `POST http://localhost:8081/api/analytics/replay/run`
* **Get Player Live Scores**: `GET http://localhost:8081/api/analytics/replay/scores`
* **Solve Optimal Roster**: `GET http://localhost:8081/api/analytics/optimize/roster?maxBudget=100.0`
* **Contest Leaderboard**: `GET http://localhost:8081/api/analytics/leaderboard?limit=10`
