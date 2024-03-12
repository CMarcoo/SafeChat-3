package top.cmarco.safechat.persistence;

import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.*;
import org.hibernate.hikaricp.internal.HikariCPConnectionProvider;
import org.jetbrains.annotations.NotNull;
import org.sqlite.hibernate.dialect.SQLiteDialect;
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
    POSTGRESQL("postgresql", "top.cmarco.safechat.libs.org.postgresql.Driver", generateUrl("postgresql"), PostgreSQLDialect.class, false),
    /**
     * MariaDB Server type.
     */
    MARIADB("mariadb", "top.cmarco.safechat.libs.mariadb.jdbc.Driver", generateUrl("mariadb"), MariaDBDialect.class, false),
    /**
     * H2 Engine Type.
     */
    H2("h2", "top.cmarco.safechat.libs.org.h2.Driver", generateFileUrl("h2"), H2Dialect.class, true),
    /**
     * CockroachDB Server type.
     */
    COCKROACHDB("cockroachdb", "top.cmarco.safechat.libs.org.postgresql.Driver", generateUrl("postgresql"), CockroachDB201Dialect.class, false),
    /**
     * HyperSQL Engine type.
     */
    HYPERSQL("hsql", "top.cmarco.safechat.libs.hsqldb.jdbc.JDBCDriver", generateFileUrl("hsqldb"), HSQLDialect.class, true),
    /**
     * Microsoft's SQLServer type.
     */
    SQLSERVER("sqlserver", "top.cmarco.safechat.libs.microsoft.sqlserver.jdbc.SQLServerDriver", generateUrl("sqlserver"), SQLServerDialect.class, false),
    /**
     * IBM DB2 Server type.
     */
    DB2("db2", "top.cmarco.safechat.libs.com.ibm.db2.jcc.DB2Driver", generateFileUrl("db2"), DB2Dialect.class, false),
    /**
     * SQLite (TESTING)
     */
    SQLITE("sqlite", "org.sqlite.JDBC", generateFileUrl("sqlite"), SQLiteDialect.class, true);

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

    public Map<String, String> generateProperties(@NotNull DatabaseConfig dbConfig) {
        Map<String, String> properties = new HashMap<>();
        properties.put("hibernate.connection.dataSourceClassname", HikariCPConnectionProvider.class.getName());
        properties.put("hibernate.hikari.jdbcUrl", generateAppropriateUrl(this, dbConfig));
        if (!isFileBased()) {
            properties.put("hibernate.hikari.username", dbConfig.getConfigValue(DatabaseSection.USERNAME));
            properties.put("hibernate.hikari.password", dbConfig.getConfigValue(DatabaseSection.PASSWORD));
        }
        properties.put("hibernate.dataSourceClassName", HikariDataSource.class.getName());
        properties.put(Environment.DRIVER, driverClassName);
        properties.put("hibernate.hikari.dataSource.cachePrepStmts", "true");
        properties.put("hibernate.hikari.dataSource.prepStmtCacheSize", "256");
        properties.put("hibernate.hikari.dataSource.useServerPrepStmts", "true");
        properties.put("hibernate.hikari.dataSource.useLocalSessionState", "true");
        properties.put("hibernate.hikari.dataSource.cacheResultSetMetadata", "true");
        properties.put("hibernate.hikari.dataSource.cacheServerConfiguration", "true");
        properties.put(Environment.HBM2DDL_AUTO, "update");
        properties.put(Environment.SHOW_SQL, "false");
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
