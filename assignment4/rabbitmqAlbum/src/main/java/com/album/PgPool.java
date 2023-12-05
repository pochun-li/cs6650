package com.album;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.rowset.serial.SerialBlob;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;

public class PgPool {

    private static PgPool instance = null;

    private final HikariDataSource ds;

    private PgPool(String url, String username, String password){
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setDriverClassName("org.postgresql.Driver");
        hikariConfig.setMaximumPoolSize(30);
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds = new HikariDataSource(hikariConfig);
    }

    public static PgPool getInstance(String jdbcUrl, String username, String password){
        if(instance == null){
            synchronized (PgPool.class) {
                if (instance == null) {
                    instance = new PgPool(jdbcUrl, username, password);
                }
            }
        }
        return instance;
    }

    public Connection getConnection() throws SQLException, ClassNotFoundException {
//        Class.forName("com.mysql.cj.jdbc.Driver");
        return ds.getConnection();
    }

    public void close(Connection connection) throws SQLException {
        if(connection != null){
            connection.close();
        }
    }

    /**
     * save data
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public long save(AlbumCollection collection) throws SQLException, ClassNotFoundException, IOException {
        Long id = collection.getId();
        try (Connection conn = getConnection()){
            PreparedStatement ps = conn.prepareStatement("select id from album_like where id = " + id);
            ResultSet rs = ps.executeQuery();
            if(rs.first()){
                ps = conn.prepareStatement("insert into album_like (id, like_count, dislike_count) values (?, ?, ?)");
                ps.setLong(1, id);
                ps.setLong(2, collection.getLikeCount());
                ps.setLong(3, collection.getDislikeCount());
            }else{
                ps = conn.prepareStatement("update album_like set like_count += ?, dislike_count += ? where id = ?");
                ps.setLong(1, collection.getLikeCount());
                ps.setLong(2, collection.getDislikeCount());
                ps.setLong(3, id);
            }
            ps.execute();
            return id;
        }
    }


    public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException {

    }
}
