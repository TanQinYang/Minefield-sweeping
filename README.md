# 💣 Java Minesweeper

A fully-featured **Minesweeper game built in pure Java**, complete with a modern **Swing GUI** and a **legacy terminal mode** for nostalgia lovers.  
Simple, portable, and 100% dependency-free.

---

## 🎮 Features

✅ Modern Swing UI  
✅ Classic number colors (blue/green/red, etc.)  
✅ Face button (🙂 → 😎 → 😵)  
✅ Left/right click & keyboard **chording**  
✅ Debug mode (reveal mines)  
✅ Adjustable difficulties (Beginner, Intermediate, Expert, Custom)  
✅ Timer that can start on first click or on New Game  
✅ Legacy terminal version preserved for CLI play  

---

## 🧱 Project Overview

| File / Folder | Description |
|----------------|-------------|
| `MinesweeperApp.java` | Main GUI entry point (Swing) |
| `MinesweeperPanel.java` | Handles grid UI, clicks, and chording |
| `UIEngine.java` | Adapter that connects the UI to `Minefield` logic |
| `Minefield.java` | Core game logic — mine generation, adjacency, flood-fill |
| `Cell.java` | Represents a single cell’s state |
| `Main.java` | **Legacy terminal** interface |
| `StackGen.java`, `QGen.java`, etc. | Generic data structures used internally |
| `GameTimer.java`, `CustomDialog.java` | GUI helper utilities |
| `Makefile` | Easy build & run commands |
| `README.md` | This file :) |

---

## 🖥️ How to Run

### Option 1 — GUI (Recommended)
```bash
# Compile everything
make gui
# or manually:
javac *.java
java MinesweeperApp
```

### Option 2 — Terminal (Legacy)
```bash
make legacy
# or manually:
java Main
```
You’ll see:
```
[LEGACY] You are running the terminal version. For the GUI, run: java MinesweeperApp
```

---

## ⚙️ Makefile Commands

| Command | Action |
|----------|---------|
| `make gui` | Compile + launch GUI |
| `make legacy` | Compile + run terminal mode |
| `make clean` | Remove `out/` build folder |
| `make purge` | Delete *all* `.class` files + `out/` |
| `make rebuild` | Purge + recompile (fresh build) |
| `make fmt` | Format code (if `google-java-format` installed) |

---

## ⌨️ Controls (GUI)

| Action | Mouse / Keyboard |
|---------|------------------|
| Reveal cell | Left click |
| Flag / unflag | Right click or Ctrl + Left click |
| Chord (open neighbors) | Left + Right click together / Middle click |
| Keyboard chord | Space / Enter on a number |
| Keyboard flag | F |
| New game | F2 or click 🙂 |
| Debug overlay | Game → Debug |
| Timer setting | Settings → “Start timer on first click” |

---

## 🎨 Visual Design

| Tile | Style |
|------|-------|
| Hidden | Dark gray, raised bevel |
| Revealed | Light gray, sunken bevel |
| Flag | Amber background 🚩 |
| Mine | 💣 red-tinted background on loss |

Classic number colors:
| Number | Color |
|---------|--------|
| 1 | Blue |
| 2 | Green |
| 3 | Red |
| 4 | Navy |
| 5 | Maroon |
| 6 | Teal |
| 7 | Black |
| 8 | Gray |

---

## 🧠 How It Works

1. **First click** — safely creates mines (`createMines`), computes adjacency (`evaluateField`), and flood-fills starting zeros.  
2. **Subsequent clicks** — call `guess(x, y, flag)` to reveal or flag cells.  
3. The UI observes cell state via `UIEngine.ViewCell` and updates the board dynamically.  
4. When all safe cells are revealed → 😎 win; if a mine is hit → 😵 game over.

---

## 🧰 Build Options

### Gradle (Alternative)
A full Gradle version is also included for developers:

```bash
cd minesweeper-gradle
./gradlew run          # GUI
./gradlew runLegacy    # terminal
./gradlew build        # compile + test
```

---

## 🧑‍💻 Development Notes

- Code adheres to standard Java style conventions (PEP-8 equivalent).  
- No external libraries required.  
- Compatible with **JDK 8+**.  
- Portable — works on Windows, macOS, and Linux.  
- Designed for clarity, separation of logic (game) vs presentation (UI).

---

## 🏗️ Future Enhancements

- 🏆 Leaderboard / high scores  
- 🎨 Custom color themes & accessibility mode  
- 💾 Persistent user preferences  
- 🔊 Sound effects  

---

## 📜 License

**MIT License** — you can freely use, modify, and share this project.  
Credit is appreciated if you fork or include it in your own portfolio.

---

## ❤️ Acknowledgments

Inspired by the classic Windows Minesweeper.  
Thanks to contributors who helped refine logic, add chording, and modernize the UI.

---

## 📬 Contributing

PRs are welcome!  
If you spot a bug or want to suggest a feature:
1. Open an Issue  
2. Describe your system + Java version  
3. Attach logs or screenshots if possible  
