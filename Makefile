# Simple build system for Minesweeper (no package lines)
JAVAC = javac
JAVA  = java
SRC   = $(wildcard *.java)
OUT   = out

.PHONY: all gui legacy clean purge fmt rebuild compile

all: gui

$(OUT):
	mkdir -p $(OUT)

compile: $(OUT)
	$(JAVAC) -d $(OUT) $(SRC)

gui: compile
	$(JAVA) -cp $(OUT) MinesweeperApp

legacy: compile
	$(JAVA) -cp $(OUT) Main

# Regular clean — removes build output directory
clean:
	rm -rf $(OUT)

# Purge — removes ALL compiled .class files anywhere + out/
purge:
	find . -type f -name "*.class" -delete
	rm -rf $(OUT)
	@echo "All compiled files removed!"

# Rebuild — purge then compile
rebuild: purge compile
	@echo "Rebuilt successfully."

fmt:
	@echo "Optional: format with google-java-format if installed:"
	@echo "  find . -name '*.java' -maxdepth 1 -print0 | xargs -0 google-java-format -i"


