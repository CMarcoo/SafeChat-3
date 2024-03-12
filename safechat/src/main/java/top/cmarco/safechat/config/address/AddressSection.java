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

package top.cmarco.safechat.config.address;

import org.jetbrains.annotations.NotNull;
import org.tomlj.TomlArray;
import studio.thevipershow.vtc.SectionType;

public enum AddressSection implements SectionType {
    ALLOWED_DOMAINS("domains.allowed", TomlArray.class),
    ALLOWED_ADDRESSES("address.allowed", TomlArray.class);

    private final String stringData;
    private final Class<?> classData;

    AddressSection(String stringData, Class<?> classData) {
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
