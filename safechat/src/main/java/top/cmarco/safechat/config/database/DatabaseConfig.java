package top.cmarco.safechat.config.database;

import org.jetbrains.annotations.NotNull;
import studio.thevipershow.vtc.TomlSectionConfiguration;
import top.cmarco.safechat.SafeChat;

public final class DatabaseConfig extends TomlSectionConfiguration<SafeChat, DatabaseSection> {

    public DatabaseConfig(@NotNull SafeChat javaPlugin, @NotNull String configurationFilename, @NotNull Class<? extends DatabaseSection> enumTypeClass) {
        super(javaPlugin, configurationFilename, enumTypeClass);
    }
}
