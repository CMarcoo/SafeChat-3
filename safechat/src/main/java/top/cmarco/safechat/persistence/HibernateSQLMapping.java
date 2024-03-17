
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

import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.*;
import org.hibernate.hikaricp.internal.HikariCPConnectionProvider;
import org.jetbrains.annotations.NotNull;
import top.cmarco.safechat.config.database.DatabaseSection;
import top.cmarco.safechat.config.database.DatabaseConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public enum HibernateSQLMapping {
    /**
     * MySQL Server type.
     */
    MYSQL("mysql", "org.mysql.jdbc.Driver", generateUrl("mysql"), MySQLDialect.class, false),
    /**
     * PostgreSQL Server type.
     */
    POSTGRESQL("postgresql", "org.postgresql.Driver", generateUrl("postgresql"), PostgreSQLDialect.class, false),
    /**
     * MariaDB Server type.
     */
    MARIADB("mariadb", "mariadb.jdbc.Driver", generateUrl("mariadb"), MariaDBDialect.class, false),
    /**
     * H2 Engine Type.
     */
    H2("h2", "org.h2.Driver", generateFileUrl("h2"), H2Dialect.class, true),
    /**
     * CockroachDB Server type.
     */
    COCKROACHDB("cockroachdb", "org.postgresql.Driver", generateUrl("postgresql"), CockroachDialect.class, false),
    /**
     * HyperSQL Engine type.
     */
    HYPERSQL("hsql", "hsqldb.jdbc.JDBCDriver", generateFileUrl("hsqldb"), HSQLDialect.class, true),
    /**
     * Microsoft's SQLServer type.
     */
    SQLSERVER("sqlserver", "microsoft.sqlserver.jdbc.SQLServerDriver", generateUrl("sqlserver"), SQLServerDialect.class, false),
    /**
     * IBM DB2 Server type.
     */
    DB2("db2", "com.ibm.db2.jcc.DB2Driver", generateFileUrl("db2"), DB2Dialect.class, false);

    public static final String DB_TYPE_PLACEHOLDER = "{TYPE}";
    public static final String STD_JDBC_URL = "jdbc:" + DB_TYPE_PLACEHOLDER + "://%s:%d/%s";
    public static final String STD_JDBC_FILE_URL = "jdbc:" + DB_TYPE_PLACEHOLDER + ":%s";
    private final String sqlFlavour;
    private final String driverClassName;
    private final String urlFormatter;
    private final Class<? extends Dialect> hibernateDialectClass;
    private final boolean fileBased;

    HibernateSQLMapping(@NotNull String sqlFlavour, @NotNull String driverClassName, @NotNull String urlFormatter, @NotNull Class<? extends Dialect> hibernateDialectClass, boolean fileBased) {
        this.sqlFlavour = sqlFlavour;
        this.driverClassName = driverClassName;
        this.urlFormatter = urlFormatter;
        this.hibernateDialectClass = hibernateDialectClass;
        this.fileBased = fileBased;
    }

    @NotNull
    private static String generateUrl(@NotNull String dbType) {
        return STD_JDBC_URL.replace(DB_TYPE_PLACEHOLDER, Objects.requireNonNull(dbType));
    }

    @NotNull
    private static String generateFileUrl(@NotNull String dbType) {
        return STD_JDBC_FILE_URL.replace(DB_TYPE_PLACEHOLDER, Objects.requireNonNull(dbType));
    }

    @NotNull
    public static String generateAppropriateUrl(@NotNull HibernateSQLMapping hibernateSQLMapping, @NotNull DatabaseConfig dbConfig) {
        final String urlFormatter = hibernateSQLMapping.getUrlFormatter();
        if (hibernateSQLMapping.isFileBased()) {
            return String.format(urlFormatter, (String) Objects.requireNonNull(dbConfig.getConfigValue(DatabaseSection.FILEPATH)));
        } else {
            String address = Objects.requireNonNull(dbConfig.getConfigValue(DatabaseSection.ADDRESS));
            Long port = Objects.requireNonNull(dbConfig.getConfigValue(DatabaseSection.PORT));
            String databaseName = Objects.requireNonNull(dbConfig.getConfigValue(DatabaseSection.DATABASE_NAME));

            return String.format(urlFormatter, address, port, databaseName);
        }
    }

    public Map<String, Object> generateProperties(@NotNull DatabaseConfig dbConfig) {
        final Map<String, Object> properties = new HashMap<>();

        properties.put("hibernate.connection.dataSourceClassname", HikariCPConnectionProvider.class.getName());
        properties.put("hibernate.hikari.jdbcUrl", generateAppropriateUrl(this, dbConfig));

        if (!isFileBased()) {
            properties.put("hibernate.hikari.username", dbConfig.getConfigValue(DatabaseSection.USERNAME));
            properties.put("hibernate.hikari.password", dbConfig.getConfigValue(DatabaseSection.PASSWORD));
        }

        properties.put("hibernate.dataSourceClassName", HikariDataSource.class.getName());
        properties.put(Environment.JAKARTA_JDBC_DRIVER, driverClassName);
        properties.put("hibernate.hikari.dataSource.cachePrepStmts", "true");
        properties.put("hibernate.hikari.dataSource.prepStmtCacheSize", "256");
        properties.put("hibernate.hikari.dataSource.useServerPrepStmts", "true");
        properties.put("hibernate.hikari.dataSource.useLocalSessionState", "true");
        properties.put("hibernate.hikari.dataSource.cacheResultSetMetadata", "true");
        properties.put("hibernate.hikari.dataSource.cacheServerConfiguration", "true");
        properties.put(Environment.HBM2DDL_AUTO, "update");
        properties.put(Environment.SHOW_SQL, "false");
        // properties.put("hibernate.")

        return properties;
    }

    @NotNull
    public String getSqlFlavour() {
        return sqlFlavour;
    }

    @NotNull
    public String getDriverClassName() {
        return driverClassName;
    }

    @NotNull
    public String getUrlFormatter() {
        return urlFormatter;
    }

    @NotNull
    public Class<? extends Dialect> getHibernateDialectClass() {
        return hibernateDialectClass;
    }

    public boolean isFileBased() {
        return fileBased;
    }
}
