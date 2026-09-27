# IPL D11 Analytics Engine

A high-performance, offline data engineering, cricket stream processing, and combinatorial optimization playground built on **Spring Boot** and modern **Java (Java 21 LTS / Java 25 ready)**.

---

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
