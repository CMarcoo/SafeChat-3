package top.cmarco.safechat.chat.check.types;

import info.debatty.java.stringsimilarity.RatcliffObershelp;
import org.jetbrains.annotations.NotNull;
import org.tomlj.TomlArray;
import top.cmarco.safechat.SafeChat;
import top.cmarco.safechat.api.checks.*;
import top.cmarco.safechat.config.blacklist.BlacklistConfig;
import top.cmarco.safechat.config.checks.CheckConfig;
import top.cmarco.safechat.config.messages.MessagesConfig;
import top.cmarco.safechat.config.messages.MessagesSection;
import top.cmarco.safechat.SafeChatUtils;
import top.cmarco.safechat.config.blacklist.BlacklistSection;
import top.cmarco.safechat.config.checks.CheckSections;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Checks if a string is banned using a blacklist.
 */
@CheckName(name = "Blacklist")
@CheckPermission(permission = "safechat.bypass.blacklist")
@CheckPriority(priority = CheckPriority.Priority.LOW)
public final class WordsBlacklistCheck extends ChatCheck {

    private final RatcliffObershelp algo = new RatcliffObershelp();
    private final BlacklistConfig blacklistConfig;
    private final CheckConfig checkConfig;
    private final MessagesConfig messagesConfig;

    public WordsBlacklistCheck(@NotNull BlacklistConfig blacklistConfig, @NotNull CheckConfig checkConfig, @NotNull MessagesConfig messagesConfig) {
        this.checkConfig = Objects.requireNonNull(checkConfig);
        this.messagesConfig = Objects.requireNonNull(messagesConfig);
        this.blacklistConfig = Objects.requireNonNull(blacklistConfig);
    }

    /**
     * Perform a check on ChatData.
     * The check can consist in anything, but it must follow these criteria:
     * The check must return true if the player failed the check.
     * The check must return false if the player passed the check.
     *
     * @param data The chat data.
     * @return True if failed, false otherwise.
     */
    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean check(@NotNull ChatData data) {
        boolean enabled = Objects.requireNonNull(checkConfig.getConfigValue(CheckSections.ENABLE_BLACKLIST_CHECK));

        if (!enabled) {
            return false;
        }

        boolean checkSimilar = checkConfig.getConfigValue(CheckSections.BLACKLIST_ALLOW_SIMILARITY, Boolean.class);
        boolean regexFallback = checkConfig.getConfigValue(CheckSections.ENABLE_BLACKLIST_FALLBACK, Boolean.class);
        boolean stripAbnormal = checkConfig.getConfigValue(CheckSections.ENABLE_BLACKLIST_STRIPPING, Boolean.class);


        TomlArray words = blacklistConfig.getConfigValue(BlacklistSection.WORDS);

        int wordsSize = words.size();

        if (wordsSize == 0) {
            return false;
        }

        String s = data.getMessage();

        if (s.isEmpty()) {
            return false;
        }

        if (stripAbnormal) {
            s = ABNORMAL_CHARACTERS.matcher(s).replaceAll("");
        }

        String[] ss = SPLIT_SPACE.split(s);

        for (int k = 0; k < wordsSize; k++) {
            String str = words.getString(k);
            if (checkSimilar) {
                double factor = ((Number) Objects.requireNonNull(checkConfig.getConfigValue(CheckSections.BLACKLIST_MAXIMUM_SIMILARITY))).doubleValue();
                for (final String value : ss) {
                    if (algo.similarity(value, str) >= factor || algo.similarity(value.toLowerCase(Locale.ROOT), str) >= factor) {
                        return true;
                    }
                }
            }

            if (str.equalsIgnoreCase(s)) {
                return true;
            } else {
                for (final String value : ss) {
                    if (value.equalsIgnoreCase(str)) {
                        return true;
                    }
                }
            }
            if (regexFallback) {
                StringBuilder stringBuilder = new StringBuilder();
                String quote = Pattern.quote("!@#$%^&*()_+-.'?;:");
                int length = str.length();
                for (String piece :
                        str.split("")) {
                    --length;
                    if (piece.trim().isEmpty()) {
                        continue;
                    }
                    if (length <= 0) {
                        stringBuilder.append("(").append(piece).append("+|([").append(quote).append("]|((§|&)[0-9A-FK-OR]|(§|&)))+\\s*+").append(piece).append(")");
                    } else if (length == str.length() - 1) {
                        stringBuilder.append("(").append(piece).append("+\\s*+|").append(piece).append("+\\s*+([").append(quote).append("]+\\s*+|((§|&)[0-9A-FK-OR]|(§|&)))+\\s*+)");
                    } else {
                        stringBuilder.append("(").append(piece).append("+\\s*+|([").append(quote).append("]+\\s*+|((§|&)[0-9A-FK-OR]|(§|&)))+\\s*+").append(piece).append("+\\s*+)");
                    }
                }
                Pattern pattern = Pattern.compile(stringBuilder.toString(), Pattern.MULTILINE | Pattern.COMMENTS | Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Get the warning messages status.
     *
     * @return True if a warning message should be sent
     * upon the player failing a check.
     */
    @Override
    public boolean hasWarningEnabled() {
        return Objects.requireNonNull(checkConfig.getConfigValue(CheckSections.ENABLE_BLACKLIST_WARNING));
    }

    /**
     * Get the warning messages that will be displayed when
     * the player fails a check.
     *
     * @return The warning messages.
     */
    @Override
    public @NotNull List<String> getWarningMessages() {
        TomlArray array = Objects.requireNonNull(messagesConfig.getConfigValue(MessagesSection.BLACKLIST_WARNING));
        return SafeChatUtils.getStrings(array);
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
        return message.replace(PLAYER_PLACEHOLDER, data.getPlayer().getName())
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
        return Objects.requireNonNull(checkConfig.getConfigValue(CheckSections.BLACKLIST_PUNISH_AFTER));
    }

    /**
     * Get the command to execute when a punishment is required.
     * Placeholders may be used.
     *
     * @return The command to execute.
     */
    @Override
    public @NotNull String getPunishmentCommand() {
        return Objects.requireNonNull(checkConfig.getConfigValue(CheckSections.BLACKLIST_PUNISH_COMMAND));
    }

    /**
     * Gets the status of logging for this check from
     * the config
     *
     * @return if logging is enabled for this check
     */
    @Override
    public boolean getLoggingEnabled() {
        return Objects.requireNonNull(checkConfig.getConfigValue(CheckSections.ENABLE_BLACKLIST_LOGGING, Boolean.class));
    }
}
