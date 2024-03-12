
/*
 * {{ SafeChat }}
 * Copyright (C) 2024 CMarco
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package top.cmarco.safechat.debug;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Debugger {

    private static Debugger instance = null;
    private final Logger safechatLogger;
    private boolean enabled = false;

    private Debugger(@NotNull Logger safechatLogger) {
        this.safechatLogger = Objects.requireNonNull(safechatLogger);
    }

    public static synchronized Debugger getInstance(@NotNull Logger logger) {
        if (instance == null) {
            instance = new Debugger(logger);
        }
        return instance;
    }

    private void informateChannel(final Level level, final DebugChannel debugChannel, final String msg) {
        safechatLogger.log(level, debugChannel.getChannelName() + " -> " + msg);
    }

    @SuppressWarnings("ConstantConditions")
    public void infoChannel(@NotNull DebugChannel debugChannel, @NotNull String message) {
        if (debugChannel == null && message != null) {
            informateChannel(Level.WARNING, DebugChannel.DEBUGGER, "tried to log \"" + message + "\" into a null debug channel.");
        } else if (message == null) {
            informateChannel(Level.WARNING, DebugChannel.DEBUGGER, "tried to debug with a null message.");
        } else {
            informateChannel(Level.INFO, debugChannel, message);
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void warnChannel(@NotNull DebugChannel debugChannel, @NotNull String message) {
        if (debugChannel == null && message != null) {
            informateChannel(Level.WARNING, DebugChannel.DEBUGGER, "tried to log \"" + message + "\" into a null debug channel.");
        } else if (message == null) {
            informateChannel(Level.WARNING, DebugChannel.DEBUGGER, "tried to debug with a null message.");
        } else {
            informateChannel(Level.WARNING, debugChannel, message);
        }
    }

    @NotNull
    public Logger getSafechatLogger() {
        return safechatLogger;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
