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

package top.cmarco.safechat.chat.check.types;

import org.jetbrains.annotations.NotNull;
import top.cmarco.safechat.SafeChat;
import top.cmarco.safechat.api.checks.*;
import top.cmarco.safechat.config.messages.MessagesConfig;
import top.cmarco.safechat.SafeChatUtils;
import top.cmarco.safechat.config.checks.CheckConfig;
import top.cmarco.safechat.config.checks.CheckSections;
import top.cmarco.safechat.config.messages.MessagesSection;

import java.util.List;
import java.util.Objects;


@CheckName(name = "Caps")
@CheckPermission(permission = "safechat.bypass.caps")
@CheckPriority(priority = CheckPriority.Priority.LOW)
public final class CapsCheck extends ChatCheck {

    private final CheckConfig checkConfig;
    private final MessagesConfig messagesConfig;

    public CapsCheck(@NotNull CheckConfig checkConfig, @NotNull MessagesConfig messagesConfig) {
        this.checkConfig = checkConfig;
        this.messagesConfig = messagesConfig;
    }

    @Override
    public boolean check(@NotNull ChatData data) {
        boolean enabled = Objects.requireNonNull(checkConfig.getConfigValue(CheckSections.ENABLE_CAPS_CHECK));

        if (!enabled) {
            return false;
        }


        Long capsLimit = checkConfig.getConfigValue(CheckSections.CAPS_UPPERCASE_CHARACTERS_LIMIT);

        if (capsLimit == null) {
            capsLimit = 8L;
        }

        short uppercaseCounter = 0;
        for (final char c : data.getMessage().toCharArray()) {
            if (Character.isUpperCase(c)) {
                uppercaseCounter++;
            }
        }


        return uppercaseCounter >= capsLimit;
    }

    /**
     * Get the warning messages status.
     *
     * @return True if a warning message should be sent
     * upon the player failing a check.
     */
    @Override
    public boolean hasWarningEnabled() {
        return Objects.requireNonNull(checkConfig.getConfigValue(CheckSections.ENABLE_CAPS_WARNING));
    }

    /**
     * Get the warning messages that will be displayed when
     * the player fails a check.
     *
     * @return The warning messages.
     */
    @Override
    public @NotNull List<String> getWarningMessages() {
        return SafeChatUtils.getStrings(Objects.requireNonNull(messagesConfig.getConfigValue(MessagesSection.CAPS_WARNING)));
    }

    /**
     * Provide placeholders for your own check.
     * Replace any placeholder with your data.
     *
     * @param message The message that may contain placeholders.
     * @param data    The data (used for placeholders).
     * @return The message, modified if it had placeholders support.
     */
    @Override
    public @NotNull String replacePlaceholders(@NotNull String message, @NotNull ChatData data) {
        return message
                .replace(PLAYER_PLACEHOLDER, data.getPlayer().getName())
                .replace(PREFIX_PLACEHOLDER, SafeChat.getLocale().getString("prefix"));
    }

    /**
     * Get after how often should a player trigger a punish.
     * For example 2 will mean each 2 failed checks,
     * will trigger the punishment.
     *
     * @return The interval value.
     */
    @Override
    public long getPunishmentRequiredValue() {
        return Objects.requireNonNull(checkConfig.getConfigValue(CheckSections.CAPS_PUNISH_AFTER));
    }


    /**
     * Get the command to execute when a punishment is required.
     * Placeholders may be used.
     *
     * @return The command to execute.
     */
    @Override
    public @NotNull String getPunishmentCommand() {
        return Objects.requireNonNull(checkConfig.getConfigValue(CheckSections.CAPS_PUNISH_COMMAND));
    }


    /**
     * Gets the status of logging for this check from
     * the config
     *
     * @return if logging is enabled for this check
     */
    @Override
    public boolean getLoggingEnabled() {
        return checkConfig.getConfigValue(CheckSections.ENABLE_CAPS_LOGGING, Boolean.class);
    }
}
