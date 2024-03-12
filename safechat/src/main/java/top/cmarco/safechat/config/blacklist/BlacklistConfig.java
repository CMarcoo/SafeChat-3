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

package top.cmarco.safechat.config.blacklist;

import org.jetbrains.annotations.NotNull;
import studio.thevipershow.vtc.TomlSectionConfiguration;
import top.cmarco.safechat.SafeChat;

/**
 * Configuration for the words blacklist.
 */
public final class BlacklistConfig extends TomlSectionConfiguration<SafeChat, BlacklistSection> {

    public BlacklistConfig(@NotNull SafeChat javaPlugin, @NotNull String configurationFilename, @NotNull Class<? extends BlacklistSection> enumTypeClass) {
        super(javaPlugin, configurationFilename, enumTypeClass);
    }
}
