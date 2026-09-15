package org.sparklezfish.drivebywire.typewriter;

import java.util.function.IntSupplier;

/** One activation per hold; canceled interactions require a fresh press. */
final class HoldToOpen {
    private final IntSupplier delayMs;
    private long started = -1;
    private boolean consumed;

    HoldToOpen(IntSupplier delayMs) {
        this.delayMs = delayMs;
    }

    boolean update(boolean down, boolean eligible, long now) {
        if (!down) {
            started = -1;
            consumed = false;
        } else if (!eligible) {
            started = -1;
            consumed = true;
        } else if (!consumed) {
            if (started < 0) started = now;
            if (now - started >= delayMs.getAsInt()) {
                consumed = true;
                return true;
            }
        }
        return false;
    }
}
