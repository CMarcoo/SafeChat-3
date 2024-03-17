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

package top.cmarco.safechat.persistence.types;

import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Entity(name = "PlayerData")
@Table(name = "player_data")
public class PlayerData implements Cloneable {

    @Id
    @Column(name = "uuid")
    private String uuid;

    @Column(name = "name")
    private String name;

    @ElementCollection
    @CollectionTable(
            name = "flag_mapping",
            joinColumns = {@JoinColumn(name = "flag_id", referencedColumnName = "uuid")})
    @MapKeyColumn(name = "flag_name")
    @Column(name = "flags")
    private Map<String, Integer> flagsMap = new HashMap<>();

    @NotNull
    public Map<String, Integer> getFlagsMap() {
        return flagsMap;
    }

    public void setFlagsMap(@NotNull Map<String, Integer> flagsMap) {
        this.flagsMap = Objects.requireNonNull(flagsMap);
    }

    @NotNull
    public String getUuid() {
        return uuid;
    }

    public void setUuid(@NotNull String uuid) {
        this.uuid = Objects.requireNonNull(uuid);
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = Objects.requireNonNull(name);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
