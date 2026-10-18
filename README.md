# MultiThreaded Java Web Server

A backend-focused Java project that demonstrates multithreading, socket communication, and basic HTTP request handling. The server accepts multiple client connections, serves static HTML pages, and processes each client in a separate thread.

## Features

- Multithreaded Java server using `ServerSocket`
- Individual thread per client connection
- HTTP request parsing for simple routes
- Static page serving from the `web/` directory
- Easy local execution with `make`

## Project structure

```text
.
├── README.md
├── Makefile
├── .gitignore
├── src/
│   └── main/java/com/multithreaded/socket/
│       ├── Server.java
│       └── Client.java
├── web/
│   ├── index.html
│   └── about.html
└── .readme
```

## Run the project

1. Compile:

```bash
make compile
```

2. Start the server:

```bash
make server
```

3. Open in browser:

```text
http://localhost:8080/
http://localhost:8080/about
```

4. Simulate multiple clients:

```bash
make client CLIENT_NAME=ClientA CLIENT_MESSAGE="Hello from Client A" PAGE=/
make client CLIENT_NAME=ClientB CLIENT_MESSAGE="Hello from Client B" PAGE=/about
```

## Skills demonstrated

- Java networking
- Multithreading and concurrency
- Backend server fundamentals
- HTTP request/response handling
- Portfolio-ready project structure
