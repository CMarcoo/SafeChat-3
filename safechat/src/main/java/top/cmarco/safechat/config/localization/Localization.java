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

package top.cmarco.safechat.config.localization;

import top.cmarco.safechat.SafeChat;
import top.cmarco.safechat.config.messages.MessagesConfig;
import top.cmarco.safechat.config.messages.MessagesSection;

import org.apache.commons.lang.LocaleUtils;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Localization {

    private final ResourceBundle defaultBundle = ResourceBundle.getBundle("messages", Locale.ENGLISH, new UTF8Control());
    private final List<Locale> localeList = Collections.singletonList(Locale.ENGLISH);
    private ResourceBundle customBundle;

    public void loadTranslation(MessagesConfig config) {
        String language;
        if ((language = config.getConfigValue(MessagesSection.LOCALE)) != null
                && LocaleUtils.isAvailableLocale(new Locale(language))
                && localeList.contains(new Locale(language))) {
            Locale locale = new Locale(language);
            try {
                customBundle = ResourceBundle.getBundle("messages", locale, new FileResClassLoader(this.getClass().getClassLoader(), SafeChat.getPlugin(SafeChat.class)
                        .getDataFolder()), new UTF8Control());
            } catch (MissingResourceException exception) {
                customBundle = defaultBundle;
            }
        } else {
            try {
                customBundle = ResourceBundle.getBundle("messages", Locale.ENGLISH, new FileResClassLoader(this.getClass().getClassLoader(), SafeChat.getPlugin(SafeChat.class)
                        .getDataFolder()), new UTF8Control());
            } catch (MissingResourceException exception) {
                customBundle = defaultBundle;
            }
        }
    }

    public String getString(String string) {
        try {
            try {
                return customBundle.getString(string);
            } catch (final MissingResourceException ex) {
                return defaultBundle.getString(string);
            }
        } catch (MissingResourceException exception) {
            Logger.getLogger("Minecraft").log(Level.WARNING, String.format("Missing translation key \"%s\" in translation file %s", exception.getKey(), get().getLocale().toString()), exception);
            return defaultBundle.getString(string);
        }
    }

    public ResourceBundle get() {
        return customBundle;
    }
}