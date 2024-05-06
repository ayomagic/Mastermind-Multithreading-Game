# Mastermind-Multithreading-Game
### Overview

This project implements the Mastermind game by utilizing networking capabilities using Java Socket Programming. The goal is to develop a game room application that includes both a server and multiple clients, utilizing the Java 8 standard library.

### Server Implementation

- **Functionality**: The server generates a secret code and manages game rounds by receiving guesses from clients and sending back feedback.
- **Concurrency**: It supports multiple clients, allowing them to simultaneously make guesses to figure out the same secret code.
- **Class and Method**: The server's main class should be named `ServerMain.java` with a `main()` method that initializes and runs the server.

### Client Implementation

- **Interaction**: Clients send guesses to the server and receive responses specific to their guesses.
- **Gameplay Rules**: Each client operates independently with a limited number of guesses. The first client to correctly guess the secret code wins the round.
- **Waiting Mechanism**: If a client exhausts its guesses, it waits until the current round ends.
- **Class and Method**: Clients are implemented in `ClientMain.java`, which also contains a `main()` method to run the client program.

### Networking and Game Flow

- **Instant Replay**: After a round ends, clients can immediately start a new game once the server announces the beginning of the next round. This setup ensures a continuous play experience without requiring acknowledgements from every client before proceeding.
- **Reliability Assumption**: It is assumed that clients will respond in a timely manner, and scenarios involving delayed or absent responses are not considered critical edge cases for this implementation.

This project not only enhances understanding of network programming and Java sockets but also reinforces OOP design principles through practical application in a familiar game context.
