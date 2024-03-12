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
import org.tomlj.TomlArray;
import studio.thevipershow.vtc.SectionType;

/**
 * The sections of the blacklist config.
 */
public enum BlacklistSection implements SectionType {
    /**
     * Words list.
     */
    WORDS("words", TomlArray.class);

    private final String stringData;
    private final Class<?> classData;

    BlacklistSection(String stringData, Class<?> classData) {
        this.stringData = stringData;
        this.classData = classData;
    }

    /**
     * Get the class.
     *
     * @return the class.
     */
    @Override
    public @NotNull Class<?> getClassData() {
        return classData;
    }

    /**
     * This method will get the stored data.
     * The data will always be of the same type
     * annotated by this interface and should
     * never be null.
     *
     * @return The data.
     */
    @Override
    public @NotNull String getStringData() {
        return stringData;
    }
}
