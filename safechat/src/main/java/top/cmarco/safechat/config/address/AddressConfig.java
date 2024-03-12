package top.cmarco.safechat.config.address;

import org.jetbrains.annotations.NotNull;
import studio.thevipershow.vtc.TomlSectionConfiguration;
import top.cmarco.safechat.SafeChat;

public final class AddressConfig extends TomlSectionConfiguration<SafeChat, AddressSection> {

    public AddressConfig(@NotNull SafeChat javaPlugin, @NotNull String configurationFilename, @NotNull Class<? extends AddressSection> enumTypeClass) {
        super(javaPlugin, configurationFilename, enumTypeClass);
    }
}
