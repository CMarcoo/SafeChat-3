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

package top.cmarco.safechat.api.checks;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.regex.Pattern;

public abstract class ChatCheck implements Check {

    public static final Pattern DOMAIN_REGEX = Pattern.compile("([a-z0-9\\-]{3,}(\\.|\\[\\.])[a-z]{2,}|(\\.|_|-|\\[\\.])[a-z0-9\\-]{3,}\\3[a-z]{2,})", Pattern.CASE_INSENSITIVE);
    public static final Pattern IPV4_REGEX = Pattern.compile("[0-9]{1,3}[.,][0-9]{1,3}[.,][0-9]{1,3}[.,][0-9]{1,3}");
    public static final Pattern ABNORMAL_CHARACTERS = Pattern.compile("[^\\p{L}0-9§&]+");
    public static final Pattern SPLIT_SPACE = Pattern.compile("\\s+");
    public static final String PLAYER_PLACEHOLDER = "{PLAYER}";
    public static final String PREFIX_PLACEHOLDER = "{PREFIX}";

    private final String checkName;
    private final String permission;
    private final CheckPriority.Priority priority;

    public ChatCheck() {
        final Class<? extends ChatCheck> namedCheckClass = getClass();
        if (!namedCheckClass.isAnnotationPresent(CheckName.class)) {
            throw new UnsupportedOperationException("This check is not annotated with '@CheckName' annotation!");
        } else {
            this.checkName = Objects.requireNonNull(namedCheckClass.getAnnotation(CheckName.class).name());
        }
        if (namedCheckClass.isAnnotationPresent(CheckPriority.class)) {
            priority = namedCheckClass.getAnnotation(CheckPriority.class).priority();
        } else {
            priority = CheckPriority.Priority.NORMAL;
        }
        if (namedCheckClass.isAnnotationPresent(CheckPermission.class)) {
            permission = namedCheckClass.getAnnotation(CheckPermission.class).permission();
        } else {
            permission = "safechat.bypass." + checkName;
        }
    }

    @Override
    @NotNull
    public final String getName() {
        return checkName;
    }

    @Override
    @NotNull
    public final String getBypassPermission() {
        return permission;
    }

    @Override
    @NotNull
    public final CheckPriority.Priority getCheckPriority() {
        return priority;
    }
}
