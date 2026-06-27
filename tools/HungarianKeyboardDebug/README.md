# Hungarian Keyboard Debug

Minimal Windows keyboard overlay for testing the mod while Minecraft has focus.
It uses a global low-level keyboard hook and physical scan codes.

Run:

```powershell
dotnet run --project tools/HungarianKeyboardDebug
```

Controls:

- `F12`: hide or show the overlay.
- `Ctrl+Shift+F12`: exit.

The overlay observes input but does not block or modify it.
