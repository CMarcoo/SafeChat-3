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

package top.cmarco.safechat.config;

import studio.thevipershow.vtc.ConfigurationData;
import studio.thevipershow.vtc.SectionType;
import studio.thevipershow.vtc.TomlSectionConfiguration;
import top.cmarco.safechat.SafeChat;
import top.cmarco.safechat.config.blacklist.BlacklistConfig;
import top.cmarco.safechat.config.checks.CheckConfig;
import top.cmarco.safechat.config.database.DatabaseSection;
import top.cmarco.safechat.config.messages.MessagesConfig;
import top.cmarco.safechat.config.messages.MessagesSection;
import top.cmarco.safechat.config.address.AddressConfig;
import top.cmarco.safechat.config.address.AddressSection;
import top.cmarco.safechat.config.blacklist.BlacklistSection;
import top.cmarco.safechat.config.checks.CheckSections;
import top.cmarco.safechat.config.database.DatabaseConfig;

/**
 * The available configurations enum.
 */
public enum Configurations implements ConfigurationData<SafeChat> {

    /**
     * The words blacklist configuration.
     */
    BLACKLIST("words-blacklist.toml", BlacklistConfig.class, BlacklistSection.class),
    /**
     * The address whitelist configuration.
     */
    ADDRESS("address-whitelist.toml", AddressConfig.class, AddressSection.class),
    /**
     * The messages configuration.
     */
    MESSAGES("messages.toml", MessagesConfig.class, MessagesSection.class),
    /**
     * The checks settings configuration.
     */
    CHECKS_SETTINGS("checks-settings.toml", CheckConfig.class, CheckSections.class),
    /**
     * The settings for SQL databases connections.
     */
    DATABASE_SETTINGS("database-settings.toml", DatabaseConfig.class, DatabaseSection.class);

    private final String stringData;
    private final Class<? extends TomlSectionConfiguration<SafeChat, ?>> classData;
    private final Class<? extends SectionType> sectionTypeClass;

    Configurations(String stringData, Class<? extends TomlSectionConfiguration<SafeChat, ?>> classData, Class<? extends SectionType> sectionTypeClass) {
        this.stringData = stringData;
        this.classData = classData;
        this.sectionTypeClass = sectionTypeClass;
    }

    @Override
    public Class<? extends TomlSectionConfiguration<SafeChat, ?>> getTomlSectionClass() {
        return classData;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Enum<T> & SectionType> Class<? extends T> getSectionClass() {
        return (Class<? extends T>) sectionTypeClass;
    }

    @Override
    public String getConfigurationName() {
        return stringData;
    }
}
