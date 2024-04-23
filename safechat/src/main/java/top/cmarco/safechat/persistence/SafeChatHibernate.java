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
package top.cmarco.safechat.persistence;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.registry.internal.BootstrapServiceRegistryImpl;
import org.hibernate.cfg.Configuration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.cmarco.safechat.SafeChat;
import top.cmarco.safechat.config.database.DatabaseConfig;
import top.cmarco.safechat.config.database.DatabaseSection;
import top.cmarco.safechat.persistence.mappers.PlayerDataManager;
import top.cmarco.safechat.persistence.types.PlayerData;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public final class SafeChatHibernate {

    private final DatabaseConfig dbConfig;
    private final SafeChat safeChat;
    private StandardServiceRegistry stdServiceRegistry;
    private SessionFactory sessionFactory;
    private PlayerDataManager playerDataManager;
    private HibernateSQLMapping hibernateSQLMapping;
    private final ClassLoader pluginClassLoader;

    public SafeChatHibernate(@NotNull DatabaseConfig dbConfig, @NotNull SafeChat safeChat, ClassLoader pluginClassLoader) {
        this.dbConfig = Objects.requireNonNull(dbConfig);
        this.safeChat = Objects.requireNonNull(safeChat);
        this.pluginClassLoader = pluginClassLoader;
    }

    /**
     * Setups and determines the hibernate sql mapping.
     * Must be called before anything else.
     */
    public void setupHibernateSQLMapping() throws RuntimeException {
        String sqlFlavour = Objects.requireNonNull(dbConfig.getConfigValue(DatabaseSection.SQL_FLAVOR));
        sqlFlavour = sqlFlavour.toLowerCase(Locale.ROOT);

        for (HibernateSQLMapping mapping : HibernateSQLMapping.values()) {
            if (mapping.getSqlFlavour().equals(sqlFlavour)) {
                this.hibernateSQLMapping = mapping;
                return;
            }
        }

        throw new RuntimeException(String.format("An unknown database type has been used (%s).", sqlFlavour));
    }

    public void shutdown() {
        if (stdServiceRegistry != null) {
            stdServiceRegistry.close();
            StandardServiceRegistryBuilder.destroy(stdServiceRegistry);
        }
    }

    /**
     * Setups a session factory if the hibernate mapping was valid.
     */
    public void setupSessionFactory() throws RuntimeException {
        if (this.hibernateSQLMapping == null) {
            throw new RuntimeException("Tried to setup session factory with an invalid database!");
        } else {
            try {
                final BootstrapServiceRegistryBuilder sigmaRegistryBuilder = new BootstrapServiceRegistryBuilder();
                sigmaRegistryBuilder.applyClassLoader(this.pluginClassLoader);

                final StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder(sigmaRegistryBuilder.build());
                final Map<String, Object> settings = hibernateSQLMapping.generateProperties(dbConfig);

                registryBuilder.applySettings(settings);
                stdServiceRegistry = registryBuilder.build();

                final MetadataSources metadataSources = new MetadataSources(stdServiceRegistry).addAnnotatedClass(PlayerData.class);
                final Metadata metadata = metadataSources.getMetadataBuilder().build();

                sessionFactory = metadata.getSessionFactoryBuilder().build();
            } catch (final HibernateException e) {
                safeChat.getLogger().warning(e.getLocalizedMessage());
            }

        }
    }

    public void setupPlayerDataManager() {
        playerDataManager = new PlayerDataManager(Objects.requireNonNull(sessionFactory), safeChat);
    }

    @Nullable
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    @NotNull
    public SafeChat getSafeChat() {
        return safeChat;
    }

    @Nullable
    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }
}
