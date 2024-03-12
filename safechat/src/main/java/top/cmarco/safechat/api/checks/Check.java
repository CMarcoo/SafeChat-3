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

package top.cmarco.safechat.api.checks;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A check for chat events.
 */
public interface Check {

    /**
     * The permission that will make this check be skipped.
     * A player with this permission will never be checked.
     * This can be used {@link CheckPermission}
     *
     * @return The permission.
     */
    @NotNull
    String getBypassPermission();

    /**
     * Get the priority of the check.
     * The priority of a check will influence
     * the order of call of your check. Higher
     * priority checks will get called before lower
     * priority ones.
     * If no annotation of type {@link CheckPriority} is present
     * on the Check class, NORMAL priority will be used.
     *
     * @return The priority of the check.
     */
    @NotNull
    CheckPriority.Priority getCheckPriority();

    /**
     * Get the name of this check.
     * Note:
     * You can instead extend {@link ChatCheck}
     * and use annotation {@link CheckName} on your
     * check class and you won't need to define this method.
     *
     * @return The check's name.
     */
    @NotNull String getName();

    /**
     * Perform a check on ChatData.
     * The check can consist in anything, but it must follow these criteria:
     * The check must return true if the player failed the check.
     * The check must return false if the player passed the check.
     *
     * @param data The chat data.
     * @return True if failed, false otherwise.
     */
    boolean check(@NotNull ChatData data);

    /**
     * Get the warning messages status.
     *
     * @return True if a warning message should be sent
     * upon the player failing a check.
     */
    boolean hasWarningEnabled();

    /**
     * Get the warning messages that will be displayed when
     * the player fails a check.
     *
     * @return The warning messages.
     */
    @NotNull
    List<String> getWarningMessages();

    /**
     * Provide placeholders for your own check.
     * Replace any placeholder with your data.
     *
     * @param message The message that may contain placeholders.
     * @param data    The data (used for placeholders).
     * @return The message, modified if it had placeholders support.
     */
    @NotNull
    String replacePlaceholders(@NotNull String message, @NotNull ChatData data);

    /**
     * Get after how often should a player trigger a punish.
     * For example 2 will mean each 2 failed checks,
     * will trigger the punishment.
     *
     * @return The interval value.
     */
    long getPunishmentRequiredValue();

    /**
     * Get the command to execute when a punishment is required.
     * Placeholders may be used.
     *
     * @return The command to execute.
     */
    @NotNull
    String getPunishmentCommand();

    /**
     * Gets the logging status for this check
     *
     * @return True if logging is enabled for this check
     */
    boolean getLoggingEnabled();
}
