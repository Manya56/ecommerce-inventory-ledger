# High-Concurrency Inventory System with Race Condition Mitigation

A Spring Boot and MySQL-backed inventory management showcase designed to simulate, detect, and resolve data integrity bugs under high-concurrency (Flash Sale) stress testing.

## 🚀 The Problem (The Vulnerability)
When 100 simultaneous users attempt to purchase an item at the exact same millisecond, a classic **Race Condition** occurs. Without concurrency control, multiple threads read the same initial inventory level, overwrite each other's database writes, and cause severe data corruption (e.g., selling items that are out of stock).

## 🛠️ The Solution (Pessimistic Locking)
I implemented a multi-layered synchronization and locking strategy to enforce data consistency:
1. **JUnit Concurrency Simulation:** Configured a dual-latch barrier (`CountDownLatch`) to force 100 threads to stampede the database simultaneously.
2. **Database Row-Level Isolation:** Applied Spring Data JPA `@Lock(LockModeType.PESSIMISTIC_WRITE)` to intercept database read operations.
3. **Transactional Guarantees:** Bound operations within Spring's `@Transactional` boundaries to issue a native `SELECT ... FOR UPDATE` command in MySQL, ensuring incoming purchases are queued sequentially.

## 📊 Performance & Testing Verification

### Before Concurrency Control (Race Condition Detected)
* **Initial Stock:** 100
* **Simultaneous Requests:** 100
* **Actual Final Stock:** ~84 (Data corruption: 16 missing inventory updates)
* **JUnit Status:** 🔴 FAILED

### After Concurrency Control (Thread-Safe)
* **Initial Stock:** 100
* **Simultaneous Requests:** 100
* **Actual Final Stock:** 0 (Perfect inventory deduction)
* **JUnit Status:** 🟢 PASSED
