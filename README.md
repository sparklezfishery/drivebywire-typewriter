# Drive By Wire — Typewriter

A NeoForge addon for Minecraft 1.21.1 that adds a keyboard-driven wire source to the [Drive By Wire](https://modrinth.com/mod/drive-by-wire) network, using [Simulated](https://modrinth.com/mod/simulated)'s typewriter mechanics.

## What it does

Adds a **Typewriter Hub** block. Right-click it with an empty hand to start typing — your keypresses are broadcast as redstone signals on the Drive By Wire network, one channel per key. Right-click again to stop.

## Blocks

| Block | Description |
|-------|-------------|
| Typewriter Hub | A typewriter and multi-channel wire source in one block. |

## Key channels

The hub exposes a channel for every key:

- Letters `A`–`Z`
- Digits `0`–`9`
- Symbols: Space, `'`, `,`, `-`, `.`, `/`, `;`, `=`, `[`, `]`, `\`
- Control: Enter, Tab, Backspace, Delete, Caps Lock
- Navigation: Arrow keys, Page Up/Down, Home, End
- Modifiers: Shift, Ctrl, Alt, Super, Menu

Each channel is active (`1`) while the key is held and inactive (`0`) when released. Only one player can use a hub at a time; disconnecting or losing window focus resets all channels.

## Requirements

| Mod | Version |
|-----|---------|
| NeoForge | 21.1.228+ |
| Simulated (Create: Simulated) | 1.1.3+ |
| Drive By Wire | 0.2.9+ |
| Create | 6.0.10+ |

## Usage

1. Place a **Typewriter Hub** in the world.
2. Connect wire from it to whatever you want to control.
3. Right-click the hub with an empty hand — your keypresses now drive the network.
4. Right-click again to disconnect.

![Wire Connect](./docs/wire.png)
![Turn A](./docs/turnA.png)
![Turn D](./docs/turnD.png)

## About This Project

This project was developed **almost entirely** by **ClaudeCode** (Anthropic's AI assistant). It was built iteratively on
top of the official [NeoForge mod project template(mod-generator)](https://neoforged.net/mod-generator/).

Beyond the initial template setup, all code contributions, feature implementations, refactors, and bug fixes were
autonomously generated and refined by ClaudeCode in a collaborative loop.

## License

This project is licensed under the **MIT License** – see the [LICENSE](LICENSE.txt) file for details.
