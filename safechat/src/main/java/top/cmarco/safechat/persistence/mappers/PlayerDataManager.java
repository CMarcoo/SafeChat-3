
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

package top.cmarco.safechat.persistence.mappers;

import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.bukkit.entity.Player;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.jetbrains.annotations.NotNull;
import top.cmarco.safechat.SafeChat;
import top.cmarco.safechat.persistence.types.PlayerData;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class PlayerDataManager {

    private final SessionFactory sessionFactory;
    private final SafeChat safeChat;

    public PlayerDataManager(@NotNull SessionFactory sessionFactory, @NotNull SafeChat safeChat) {
        this.sessionFactory = sessionFactory;
        this.safeChat = safeChat;
    }

    public static void increasePlayerFlag(@NotNull PlayerData playerData, @NotNull String checkName) {
        final Map<String, Integer> data = playerData.getFlagsMap();
        if (data.containsKey(checkName)) {
            data.compute(checkName, (k, v) -> Objects.requireNonNull(v) + 1);
        } else {
            data.put(checkName, 1);
        }
    }

    public CompletableFuture<Void> addPlayerData(@NotNull UUID uuid, @NotNull String username) {
        return CompletableFuture.runAsync(() -> {
            Transaction transaction = null;
            try (final Session session = sessionFactory.openSession()) {
                transaction = session.beginTransaction();
                final PlayerData playerData = new PlayerData();
                playerData.setUuid(uuid.toString());
                playerData.setName(username);
                session.save(playerData);
            } catch (HibernateException e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                safeChat.getLogger().warning(e.getLocalizedMessage());
            }
        });
    }

    public void addPlayerData(@NotNull Player player) {
        addPlayerData(player.getUniqueId(), player.getName());
    }

    public void addOrUpdatePlayerData(@NotNull Player player, @NotNull String checkName) {
        Transaction transaction = null;
        final UUID uuid = player.getUniqueId();
        try (final Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            PlayerData playerData = session.get(PlayerData.class, uuid.toString());

            if (playerData == null) {
                playerData = new PlayerData();
                playerData.setName(player.getName());
                playerData.setUuid(uuid.toString());
                increasePlayerFlag(playerData, checkName);
                session.save(playerData);
            } else {
                increasePlayerFlag(playerData, checkName);
                session.update(playerData);
            }

            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            safeChat.getLogger().warning(e.getLocalizedMessage());
        }
    }

    @NotNull
    public CompletableFuture<PlayerData> getPlayerData(@NotNull Player player) {
        CompletableFuture<PlayerData> future = new CompletableFuture<>();

        future.completeAsync(() -> {
            final UUID uuid = player.getUniqueId();
            Transaction transaction = null;

            try (final Session session = sessionFactory.openSession()) {
                transaction = session.beginTransaction();

                final TypedQuery<PlayerData> query = session.createQuery("SELECT a FROM PlayerData a LEFT JOIN FETCH a.flagsMap WHERE a.uuid= :uuid", PlayerData.class);
                query.setParameter("uuid", uuid.toString());

                final PlayerData resultData = query.getSingleResult();

                transaction.commit();

                return resultData;
            } catch (NoResultException ignored) {

            } catch (HibernateException e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                safeChat.getLogger().warning(e.getLocalizedMessage());
            }
            return null;
        });

        return future;
    }

    @NotNull
    public CompletableFuture<PlayerData> getPlayerData(@NotNull String username) {
        CompletableFuture<PlayerData> future = new CompletableFuture<>();

        future.completeAsync(() -> {
            Transaction transaction = null;
            try (final Session session = sessionFactory.openSession()) {
                transaction = session.beginTransaction();

                final TypedQuery<PlayerData> query = session.createQuery("SELECT a FROM PlayerData a LEFT JOIN FETCH a.flagsMap WHERE a.name= :name", PlayerData.class);
                query.setParameter("name", username);


                final PlayerData resultData = query.getSingleResult();

                transaction.commit();

                return resultData;
            } catch (NoResultException noResultException) {
                return null;
            } catch (HibernateException exception) {
                if (transaction != null) {
                    transaction.rollback();
                }
                safeChat.getLogger().warning(exception.getLocalizedMessage());
            }
            return null;
        });

        return future;
    }

    @NotNull
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    @NotNull
    public SafeChat getSafeChat() {
        return safeChat;
    }
}
