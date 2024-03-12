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

package top.cmarco.safechat.config.database;

import org.jetbrains.annotations.NotNull;
import studio.thevipershow.vtc.SectionType;

public enum DatabaseSection implements SectionType {
    SQL_FLAVOR("database.sql-flavor", String.class),
    USERNAME("database.username", String.class),
    PASSWORD("database.password", String.class),
    DATABASE_NAME("database.db-name", String.class),
    PORT("database.port", Long.class),
    ADDRESS("database.address", String.class),
    TIMEOUT("database.timeout", Long.class),
    FILEPATH("database.filepath", String.class);

    private final String stringData;
    private final Class<?> classData;

    DatabaseSection(String stringData, Class<?> classData) {
        this.stringData = stringData;
        this.classData = classData;
    }

    @Override
    public @NotNull Class<?> getClassData() {
        return classData;
    }

    @Override
    public @NotNull String getStringData() {
        return stringData;
    }
}
