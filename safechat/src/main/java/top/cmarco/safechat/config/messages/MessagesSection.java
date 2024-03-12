
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

package top.cmarco.safechat.config.messages;

import org.jetbrains.annotations.NotNull;
import org.tomlj.TomlArray;
import studio.thevipershow.vtc.SectionType;

/**
 * All the sections in the messages config.
 */
public enum MessagesSection implements SectionType {
    /**
     * The language to be used
     */
    LOCALE("messages.locale", String.class),
    /**
     * The messages sent when someone sends an address.
     */
    ADDRESS_WARNING("messages.address-warning", TomlArray.class),
    /**
     * The messages sent when someone used a blacklisted word.
     */
    BLACKLIST_WARNING("messages.blacklisted-word-warning", TomlArray.class),
    /**
     * The messages sent when someone repeats the same message.
     */
    REPETITION_WARNING("messages.text-repetition-warning", TomlArray.class),
    /**
     * The messages sent when someone writes too fast.
     */
    FLOOD_WARNING("messages.chat-flood-warning", TomlArray.class),
    /**
     * The messages sent when too many uppercase characters are used.
     */
    CAPS_WARNING("messages.caps-warning", TomlArray.class);

    private final String stringData;
    private final Class<?> classData;

    MessagesSection(String stringData, Class<?> classData) {
        this.stringData = stringData;
        this.classData = classData;
    }

    @Override
    public final @NotNull Class<?> getClassData() {
        return classData;
    }

    @Override
    public final @NotNull String getStringData() {
        return stringData;
    }
}
