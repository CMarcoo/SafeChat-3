package top.cmarco.safechat.config.checks;

import org.jetbrains.annotations.NotNull;
import studio.thevipershow.vtc.TomlSectionConfiguration;
import top.cmarco.safechat.SafeChat;

public final class CheckConfig extends TomlSectionConfiguration<SafeChat, CheckSections> {

    public CheckConfig(@NotNull SafeChat javaPlugin, @NotNull String configurationFilename, @NotNull Class<? extends CheckSections> enumTypeClass) {
        super(javaPlugin, configurationFilename, enumTypeClass);
    }
}
