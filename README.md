# CIS*4150 — Assignment 1

**Date:** September 23, 2026
**Authors:** Alex Daniel, Carsten S., & Mukul V.

Tested on a Windows device (Windows + WSL/Ubuntu, JDK 21, JUnit 4.13.2).

---

## Requirements

- A Java JDK (tested with OpenJDK 21). On Ubuntu/WSL:
  ```bash
  sudo apt install -y openjdk-21-jdk-headless
  ```
- Junit dependencies; `junit-4.13.2.jar`, `hamcrest-core-1.3.jar` (already included in this folder).

## How to run (WSL / Ubuntu)

From inside this folder in a WSL/Ubuntu terminal:

```bash
# 1. Compile the source and the tests
javac -cp .:junit-4.13.2.jar Union.java UnionTest.java

# 2. Run the tests
java -cp .:junit-4.13.2.jar:hamcrest-core-1.3.jar UnionTest
```

Notes for WSL:
- Use `:` (colon) as the classpath separator, as shown above — that is the
  Linux/WSL separator. (Native Windows PowerShell/cmd uses `;` instead.)
- Run the project from inside the WSL/Ubuntu filesystem (e.g.
  `~/projects/cis4150_A1`), not from a `/mnt/c/...` Windows path, so `javac`
  and `java` behave normally.

Alternatively, to use JUnit's built-in runner (terse dots + `OK (10 tests)`
instead of per-test output):

```bash
java -cp .:junit-4.13.2.jar:hamcrest-core-1.3.jar org.junit.runner.JUnitCore UnionTest
```