using System.Diagnostics;
using System.Runtime.InteropServices;

namespace HungarianKeyboardDebug;

internal static class Program
{
    [STAThread]
    private static void Main()
    {
        ApplicationConfiguration.Initialize();
        Application.Run(new KeyboardOverlay());
    }
}

internal sealed class KeyboardOverlay : Form
{
    private const int WhKeyboardLl = 13;
    private const int WmKeyDown = 0x0100;
    private const int WmKeyUp = 0x0101;
    private const int WmSysKeyDown = 0x0104;
    private const int WmSysKeyUp = 0x0105;
    private const uint LlkhfExtended = 0x01;

    private readonly Dictionary<int, KeyBox> keys = [];
    private readonly LowLevelKeyboardProc hookCallback;
    private IntPtr hook;
    private bool leftControlDown;
    private bool leftShiftDown;

    public KeyboardOverlay()
    {
        hookCallback = HookKeyboard;
        Text = "Hungarian Keyboard Debug";
        FormBorderStyle = FormBorderStyle.FixedToolWindow;
        StartPosition = FormStartPosition.Manual;
        BackColor = Color.FromArgb(18, 21, 28);
        ForeColor = Color.White;
        TopMost = true;
        ShowInTaskbar = true;
        ClientSize = new Size(1085, 360);
        Location = new Point(20, 20);

        BuildKeyboard();
        hook = InstallHook(hookCallback);
        FormClosed += (_, _) => UnhookWindowsHookEx(hook);
    }

    protected override bool ShowWithoutActivation => true;

    protected override CreateParams CreateParams
    {
        get
        {
            const int WsExNoActivate = 0x08000000;
            var parameters = base.CreateParams;
            parameters.ExStyle |= WsExNoActivate;
            return parameters;
        }
    }

    private void BuildKeyboard()
    {
        var title = new Label
        {
            AutoSize = true,
            Font = new Font("Segoe UI", 15, FontStyle.Bold),
            Location = new Point(16, 12),
            Text = "Hungarian keyboard debug"
        };
        Controls.Add(title);

        var help = new Label
        {
            AutoSize = true,
            ForeColor = Color.FromArgb(170, 185, 208),
            Location = new Point(18, 43),
            Text = "Global input • F12: hide/show • Ctrl+Shift+F12: exit"
        };
        Controls.Add(help);

        KeySpec[][] rows =
        [
            [
                K(0x29, "0"), K(0x02, "1"), K(0x03, "2"), K(0x04, "3"), K(0x05, "4"),
                K(0x06, "5"), K(0x07, "6"), K(0x08, "7"), K(0x09, "8"), K(0x0A, "9"),
                K(0x0B, "Ö"), K(0x0C, "Ü"), K(0x0D, "Ó"), K(0x0E, "Backspace", 112)
            ],
            [
                K(0x0F, "Tab", 78), K(0x10, "Q"), K(0x11, "W"), K(0x12, "E"), K(0x13, "R"),
                K(0x14, "T"), K(0x15, "Z"), K(0x16, "U"), K(0x17, "I"), K(0x18, "O"),
                K(0x19, "P"), K(0x1A, "Ő"), K(0x1B, "Ú"), K(0x2B, "Ű", 76)
            ],
            [
                K(0x3A, "Caps Lock", 96), K(0x1E, "A"), K(0x1F, "S"), K(0x20, "D"),
                K(0x21, "F"), K(0x22, "G"), K(0x23, "H"), K(0x24, "J"), K(0x25, "K"),
                K(0x26, "L"), K(0x27, "É"), K(0x28, "Á"), K(0x1C, "Enter", 132)
            ],
            [
                K(0x2A, "Shift", 126), K(0x56, "Í"), K(0x2C, "Y"), K(0x2D, "X"),
                K(0x2E, "C"), K(0x2F, "V"), K(0x30, "B"), K(0x31, "N"), K(0x32, "M"),
                K(0x33, ","), K(0x34, "."), K(0x35, "-"), K(0x36, "Shift", 126)
            ],
            [
                K(0x1D, "Ctrl", 76), K(0x15B, "Win", 76), K(0x38, "Alt", 76),
                K(0x39, "Space", 424), K(0x138, "AltGr", 76), K(0x15C, "Win", 76),
                K(0x15D, "Menu", 76), K(0x11D, "Ctrl", 76)
            ]
        ];

        var y = 72;
        foreach (var row in rows)
        {
            var x = 16;
            foreach (var spec in row)
            {
                var key = new KeyBox(spec.Label, spec.Width)
                {
                    Location = new Point(x, y)
                };
                Controls.Add(key);
                keys[spec.Id] = key;
                x += spec.Width + 6;
            }
            y += 56;
        }
    }

    private static KeySpec K(int id, string label, int width = 60) => new(id, label, width);

    private IntPtr HookKeyboard(int code, IntPtr message, IntPtr data)
    {
        if (code >= 0)
        {
            var info = Marshal.PtrToStructure<KbdLlHookStruct>(data);
            var pressed = message == (IntPtr)WmKeyDown || message == (IntPtr)WmSysKeyDown;
            var released = message == (IntPtr)WmKeyUp || message == (IntPtr)WmSysKeyUp;

            if (pressed || released)
            {
                var extended = (info.Flags & LlkhfExtended) != 0;
                var id = (int)info.ScanCode | (extended ? 0x100 : 0);
                BeginInvoke(() => SetKeyState(id, pressed));

                leftControlDown = id == 0x1D ? pressed : leftControlDown;
                leftShiftDown = id == 0x2A ? pressed : leftShiftDown;

                if (pressed && info.VirtualKey == (uint)Keys.F12)
                {
                    BeginInvoke(() =>
                    {
                        if (leftControlDown && leftShiftDown)
                            Close();
                        else
                            Visible = !Visible;
                    });
                }
            }
        }

        return CallNextHookEx(IntPtr.Zero, code, message, data);
    }

    private void SetKeyState(int id, bool pressed)
    {
        if (keys.TryGetValue(id, out var key))
            key.Pressed = pressed;
    }

    private static IntPtr InstallHook(LowLevelKeyboardProc callback)
    {
        using var process = Process.GetCurrentProcess();
        using var module = process.MainModule!;
        return SetWindowsHookEx(WhKeyboardLl, callback, GetModuleHandle(module.ModuleName), 0);
    }

    private readonly record struct KeySpec(int Id, string Label, int Width);

    [StructLayout(LayoutKind.Sequential)]
    private struct KbdLlHookStruct
    {
        public uint VirtualKey;
        public uint ScanCode;
        public uint Flags;
        public uint Time;
        public UIntPtr ExtraInfo;
    }

    private delegate IntPtr LowLevelKeyboardProc(int code, IntPtr message, IntPtr data);

    [DllImport("user32.dll", SetLastError = true)]
    private static extern IntPtr SetWindowsHookEx(
        int hookId, LowLevelKeyboardProc callback, IntPtr module, uint threadId);

    [DllImport("user32.dll")]
    [return: MarshalAs(UnmanagedType.Bool)]
    private static extern bool UnhookWindowsHookEx(IntPtr hook);

    [DllImport("user32.dll")]
    private static extern IntPtr CallNextHookEx(
        IntPtr hook, int code, IntPtr message, IntPtr data);

    [DllImport("kernel32.dll", CharSet = CharSet.Unicode)]
    private static extern IntPtr GetModuleHandle(string? moduleName);
}

internal sealed class KeyBox : Control
{
    private bool pressed;

    [System.ComponentModel.Browsable(false)]
    [System.ComponentModel.DesignerSerializationVisibility(
        System.ComponentModel.DesignerSerializationVisibility.Hidden)]
    public bool Pressed
    {
        get => pressed;
        set
        {
            if (pressed == value) return;
            pressed = value;
            Invalidate();
        }
    }

    public KeyBox(string label, int width)
    {
        Text = label;
        Size = new Size(width, 50);
        Font = new Font("Segoe UI", label.Length > 6 ? 8.5f : 11f, FontStyle.Bold);
        SetStyle(ControlStyles.AllPaintingInWmPaint |
                 ControlStyles.OptimizedDoubleBuffer |
                 ControlStyles.UserPaint, true);
    }

    protected override void OnPaint(PaintEventArgs eventArgs)
    {
        var rectangle = new Rectangle(0, 0, Width - 1, Height - 4);
        var background = Pressed
            ? Color.FromArgb(242, 165, 38)
            : Color.FromArgb(45, 53, 68);
        var border = Pressed
            ? Color.FromArgb(255, 226, 139)
            : Color.FromArgb(91, 105, 128);
        var foreground = Pressed ? Color.FromArgb(36, 23, 0) : Color.White;

        using var path = new System.Drawing.Drawing2D.GraphicsPath();
        const int radius = 8;
        path.AddArc(rectangle.Left, rectangle.Top, radius, radius, 180, 90);
        path.AddArc(rectangle.Right - radius, rectangle.Top, radius, radius, 270, 90);
        path.AddArc(rectangle.Right - radius, rectangle.Bottom - radius, radius, radius, 0, 90);
        path.AddArc(rectangle.Left, rectangle.Bottom - radius, radius, radius, 90, 90);
        path.CloseFigure();

        using var brush = new SolidBrush(background);
        using var pen = new Pen(border);
        eventArgs.Graphics.SmoothingMode = System.Drawing.Drawing2D.SmoothingMode.AntiAlias;
        eventArgs.Graphics.FillPath(brush, path);
        eventArgs.Graphics.DrawPath(pen, path);
        TextRenderer.DrawText(
            eventArgs.Graphics,
            Text,
            Font,
            rectangle,
            foreground,
            TextFormatFlags.HorizontalCenter | TextFormatFlags.VerticalCenter);
    }
}
