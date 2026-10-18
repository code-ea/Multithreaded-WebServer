.PHONY: all compile server client clean

JAVA_SRC := src/main/java/com/multithreaded/socket
OUT_DIR := target/classes
PKG := com.multithreaded.socket
CLIENT_NAME ?= Client-1
CLIENT_MESSAGE ?= Hello from default client
PAGE ?= /

all: compile

compile:
	mkdir -p $(OUT_DIR)
	javac -d $(OUT_DIR) $(JAVA_SRC)/*.java

server:
	java -cp $(OUT_DIR) $(PKG).Server

client:
	java -cp $(OUT_DIR) $(PKG).Client "$(CLIENT_NAME)" "$(CLIENT_MESSAGE)" "$(PAGE)"

clean:
	rm -rf $(OUT_DIR)
