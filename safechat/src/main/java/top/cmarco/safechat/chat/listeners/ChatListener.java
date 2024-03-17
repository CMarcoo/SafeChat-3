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

package top.cmarco.safechat.chat.listeners;

import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;
import top.cmarco.safechat.SafeChat;
import top.cmarco.safechat.persistence.mappers.PlayerDataManager;
import top.cmarco.safechat.SafeChatUtils;
import top.cmarco.safechat.api.checks.ChatData;
import top.cmarco.safechat.api.checks.Check;
import top.cmarco.safechat.api.checks.ChecksContainer;
import top.cmarco.safechat.api.events.ChatPunishmentEvent;
import top.cmarco.safechat.api.events.PlayerFailCheckEvent;
import top.cmarco.safechat.persistence.SafeChatHibernate;
import top.cmarco.safechat.persistence.types.PlayerData;

import java.util.Collection;
import java.util.Objects;

@SuppressWarnings("unused")
public final class ChatListener implements Listener {
    private final static String DATA_MANAGER_ABSENT = "SafeChat's Hibernate PlayerDataManager wasn't configured yet!";
    private final SafeChat safeChat;
    private final SafeChatHibernate safeChatHibernate;
    private final PlayerDataManager playerDataManager;
    private final ChecksContainer checksContainer;

    public ChatListener(SafeChatHibernate safeChatHibernate, ChecksContainer checksContainer) {
        this.safeChatHibernate = safeChatHibernate;
        this.playerDataManager = Objects.requireNonNull(safeChatHibernate.getPlayerDataManager(), DATA_MANAGER_ABSENT);
        this.checksContainer = checksContainer;
        this.safeChat = safeChatHibernate.getSafeChat();
    }

    private static void sendWarning(@NotNull Check check, @NotNull ChatData data) {
        if (!check.hasWarningEnabled()) {
            return;
        }

        Player player = data.getPlayer();

        for (String msg : check.getWarningMessages()) {
            String message = check.replacePlaceholders(Objects.requireNonNull(msg), data);
            player.sendMessage(SafeChatUtils.color(message));
        }
    }

    private void checkFlagsAmount(@NotNull Check check, @NotNull ChatData chatData) {
        if (check.getPunishmentRequiredValue() == -1L) {
            return;
        }

        String checkName = check.getName();

        playerDataManager.getPlayerData(chatData.getPlayer()).whenComplete((playerData1, err) -> {

            if (err != null) {
                safeChat.getLogger().warning(err.getLocalizedMessage());
                return;
            }

            final int flagAmount;
            if (playerData1 == null) {
                flagAmount = 1;
            } else {

                Integer i = playerData1.getFlagsMap().get(checkName);
                flagAmount = Objects.requireNonNullElse(i, 1);
            }

            if (flagAmount % Math.abs(check.getPunishmentRequiredValue()) == 0) {

                BukkitScheduler scheduler = safeChat.getServer().getScheduler();

                scheduler.runTask(safeChat, () -> {

                    ChatPunishmentEvent punishmentEvent = new ChatPunishmentEvent(check);
                    safeChat.getServer().getPluginManager().callEvent(punishmentEvent);

                    if (!punishmentEvent.isCancelled()) {
                        dispatchCommands(check, chatData);
                    }
                });
            }
        });
    }

    private void dispatchCommands(@NotNull Check check, @NotNull ChatData chatData) {
        Server server = safeChat.getServer();
        ConsoleCommandSender console = server.getConsoleSender();
        String formattedCommand = check.replacePlaceholders(check.getPunishmentCommand(), chatData);
        server.dispatchCommand(console, formattedCommand);
    }

    private void updateData(@NotNull Player player, @NotNull String checkName) {
        playerDataManager.addOrUpdatePlayerData(player, checkName);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    private void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        ChatData data = new ChatData(event.getPlayer(), event.getMessage(), System.currentTimeMillis());

        Collection<Check> sortedPriorityChecks = checksContainer.getActiveChecks();

        for (Check check : sortedPriorityChecks) {

            if (event.getPlayer().hasPermission(check.getBypassPermission())) {
                continue;
            }

            if (check.check(data)) {
                PlayerFailCheckEvent playerFailCheckEvent = new PlayerFailCheckEvent(check, data);
                safeChat.getServer().getPluginManager().callEvent(playerFailCheckEvent);

                if (playerFailCheckEvent.isCancelled()) {
                    continue;
                }

                if (check.getLoggingEnabled()) {
                    SafeChatUtils.logMessage(check, data.getPlayer(), data.getMessage());
                }

                event.setCancelled(true);
                sendWarning(check, data);
                updateData(data.getPlayer(), check.getName());
                break;
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    private void onPlayerFailCheck(PlayerFailCheckEvent event) {
        BukkitScheduler scheduler = safeChat.getServer().getScheduler();
        scheduler.runTask(safeChat, () -> this.checkFlagsAmount(event.getCheck(), event.getChatData()));
    }

    @NotNull
    public SafeChatHibernate getSafeChatHibernate() {
        return safeChatHibernate;
    }

    @NotNull
    public SafeChat getSafeChat() {
        return safeChat;
    }

    @NotNull
    public ChecksContainer getChecksContainer() {
        return checksContainer;
    }
}
