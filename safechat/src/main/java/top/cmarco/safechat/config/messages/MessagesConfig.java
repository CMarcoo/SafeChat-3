package top.cmarco.safechat.config.messages;

import org.jetbrains.annotations.NotNull;
import studio.thevipershow.vtc.TomlSectionConfiguration;
import top.cmarco.safechat.SafeChat;

/**
 * Config for the messages.
 */
public final class MessagesConfig extends TomlSectionConfiguration<SafeChat, MessagesSection> {

    public MessagesConfig(@NotNull SafeChat javaPlugin, @NotNull String configurationFilename, @NotNull Class<? extends MessagesSection> enumTypeClass) {
        super(javaPlugin, configurationFilename, enumTypeClass);
    }
}
