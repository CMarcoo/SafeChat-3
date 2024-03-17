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

package top.cmarco.safechat.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import studio.thevipershow.vtc.PluginConfigurationsData;
import top.cmarco.safechat.SafeChat;
import top.cmarco.safechat.api.checks.Check;
import top.cmarco.safechat.persistence.mappers.PlayerDataManager;
import top.cmarco.safechat.SafeChatUtils;
import top.cmarco.safechat.api.checks.ChecksContainer;
import top.cmarco.safechat.config.Configurations;
import top.cmarco.safechat.config.localization.Localization;
import top.cmarco.safechat.persistence.types.PlayerData;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static top.cmarco.safechat.SafeChat.getLocale;

public class SafeChatCommand extends Command {

    private final static List<String> BASE_ARGS = Arrays.asList("help", "reload", "flags", "version");
    private final SafeChat safeChat;

    public SafeChatCommand(@NotNull SafeChat safeChat) {
        super("safechat");
        this.safeChat = safeChat;
    }

    public static void onHelp(@NotNull CommandSender sender) {
        if (SafeChatUtils.permissionCheck("safechat.commands.help", sender)) {
            sender.sendMessage(SafeChatUtils.color(getLocale().getString("help_command")));
        }
    }

    public static void onVersion(@NotNull CommandSender sender) {
        if (SafeChatUtils.permissionCheck("safechat.commands.version", sender)) {
            sender.sendMessage(SafeChatUtils.color(getLocale().getString("version_command")).replaceAll("(?i)\\{prefix}", getLocale().getString("prefix")).replaceAll("(?i)\\{version}", SafeChat.getPlugin(SafeChat.class).getDescription().getVersion()).replaceAll("(?i)\\{server_version}", Bukkit.getServer().getVersion()));
        }
    }

    public static void unknownCommand(@NotNull CommandSender sender) {
        sender.sendMessage(SafeChatUtils.color(getLocale().getString("unknown_command").replaceAll("(?i)\\{prefix}", getLocale().getString("prefix"))));
    }

    public static void unknownAmountOfArgs(@NotNull CommandSender sender) {
        sender.sendMessage(SafeChatUtils.color(getLocale().getString("too_many_arguments").replaceAll("(?i)\\{prefix}", getLocale().getString("prefix"))));
    }

    public static void tooLittleArgs(@NotNull CommandSender sender) {
        sender.sendMessage(SafeChatUtils.color(getLocale().getString("too_little_arguments").replaceAll("(?i)\\{prefix}", getLocale().getString("prefix"))));
    }

    public final void reloadCommand(@NotNull CommandSender sender) {
        if (SafeChatUtils.permissionCheck("safechat.commands.reload", sender)) {
            long operationStartTime = System.nanoTime();
            sender.sendMessage(SafeChatUtils.color(getLocale().getString("reload_begin").replaceAll("(?i)\\{prefix}", getLocale().getString("prefix"))));

            PluginConfigurationsData<SafeChat> data = safeChat.getConfigData();
            data.exportAndLoadAllLoadedConfigs(false); // storing new values.

            ResourceBundle.clearCache();
            Localization localization = new Localization();
            localization.loadTranslation(Objects.requireNonNull(safeChat.getConfigData().getConfig(Configurations.MESSAGES)));
            SafeChat.localization = localization;

            float timeTaken = (System.nanoTime() - operationStartTime) / 1E6F;
            sender.sendMessage(SafeChatUtils.color(String.format(getLocale().getString("reload_finish").replaceAll("(?i)\\{prefix}", getLocale().getString("prefix")), timeTaken)));
        }
    }

    public void flagsTypeSearchCommand(@NotNull CommandSender commandSender, @NotNull String flagType, @NotNull String playerName) {
        if (getAvailableCheckNamesList().contains(flagType)) {
            PlayerDataManager dataManager = safeChat.getSafeChatHibernate().getPlayerDataManager();

            if (dataManager == null) {
                return;
            }

            dataManager.getPlayerData(playerName).whenComplete((playerData, err) -> {

                if (err != null) {
                    safeChat.getLogger().warning(err.getLocalizedMessage());
                    return;
                }

                if (playerData != null) {
                    int flagAmount;
                    Map<String, Integer> playerFlags = playerData.getFlagsMap();
                    flagAmount = playerFlags.getOrDefault(flagType, 0);
                    commandSender.sendMessage(SafeChatUtils.color(String.format(getLocale().getString("flag_information").replaceAll("(?i)\\{prefix}", getLocale().getString("prefix")), playerName, flagAmount, flagType)));
                } else {
                    commandSender.sendMessage(SafeChatUtils.color(getLocale().getString("not_found_in_database").replaceAll("(?i)\\{prefix}", getLocale().getString("prefix"))));
                }
            });
        } else {
            commandSender.sendMessage(SafeChatUtils.color(String.format(getLocale().getString("check_does_not_exist").replaceAll("(?i)\\{prefix}", getLocale().getString("prefix")), flagType)));
        }
    }

    public void withThreeArgs(@NotNull CommandSender commandSender, @NotNull final String[] args) {
        if ("flags".equals(args[0].toLowerCase(Locale.ROOT))) {
            if (SafeChatUtils.permissionCheck("safechat.commands.flags", commandSender)) {
                flagsTypeSearchCommand(commandSender, args[1], args[2]);
            }
        } else {
            unknownCommand(commandSender);
        }
    }

    public void allFlagsSearchCommand(@NotNull CommandSender commandSender, @NotNull String playerName) {
        PlayerDataManager dataManager = safeChat.getSafeChatHibernate().getPlayerDataManager();
        if (dataManager == null) {
            return;
        }

        dataManager.getPlayerData(playerName).whenComplete((playerData, err) -> {

            if (err != null) {
                safeChat.getLogger().warning(err.getLocalizedMessage());
                return;
            }

            if (playerData != null) {
                Map<String, Integer> playerFlags = playerData.getFlagsMap();
                playerFlags.forEach((k, v) -> commandSender.sendMessage(SafeChatUtils.color(String.format(getLocale().getString("flag_information").replaceAll("(?i)\\{prefix}", getLocale().getString("prefix")), playerName, v, k))));
            } else {
                commandSender.sendMessage(SafeChatUtils.color(getLocale().getString("not_found_in_database").replaceAll("(?i)\\{prefix}", getLocale().getString("prefix"))));
            }
        });
    }

    public void withTwoArgs(@NotNull CommandSender commandSender, @NotNull final String[] args) {
        if (args[0].equalsIgnoreCase("flags")) {
            if (SafeChatUtils.permissionCheck("safechat.commands.flags", commandSender)) {
                allFlagsSearchCommand(commandSender, args[1]);
            }
        } else {
            unknownCommand(commandSender);
        }
    }

    public void withOneArgs(@NotNull String firstArg, @NotNull CommandSender sender) {
        switch (firstArg.toLowerCase(Locale.ROOT)) {
            case "reload":
                reloadCommand(sender);
                break;
            case "help":
                onHelp(sender);
                break;
            case "version":
                onVersion(sender);
                break;
            case "flags":
                tooLittleArgs(sender);
                break;
            default:
                unknownCommand(sender);
                break;
        }
    }

    @Override
    public boolean execute(final @NotNull CommandSender sender, final @NotNull String label, final String[] args) {
        final int arguments = args.length;
        switch (arguments) {
            case 0:
                onHelp(sender);
                break;
            case 1:
                withOneArgs(args[0], sender);
                break;
            case 2:
                withTwoArgs(sender, args);
                break;
            case 3:
                withThreeArgs(sender, args);
                break;
            default:
                unknownAmountOfArgs(sender);
                break;
        }

        return true;
    }

    private List<String> getAvailableCheckNamesList() {
        ChecksContainer checksContainer = ChecksContainer.getInstance(safeChat);
        return checksContainer.getActiveChecks().stream().map(Check::getName).collect(Collectors.toList());
    }

    @Override
    public @NotNull List<String> tabComplete(final @NotNull CommandSender sender, final @NotNull String alias, final String[] args) throws IllegalArgumentException {
        final int length = args.length;
        switch (length) {
            case 1:
                return BASE_ARGS.stream().filter(string -> sender.hasPermission("safechat.commands." + string)).collect(Collectors.toList());
            case 2: {
                if (args[0].equalsIgnoreCase("flags") && SafeChatUtils.permissionCheck("safechat.commands.flags", sender)) {
                    return getAvailableCheckNamesList();
                }
            }
            break;
            case 3: {
                if (args[0].equalsIgnoreCase("flags") && SafeChatUtils.permissionCheck("safechat.commands.flags", sender)) {
                    return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
                }
            }
            break;
            default:
                break;
        }
        return Collections.emptyList();
    }
}
