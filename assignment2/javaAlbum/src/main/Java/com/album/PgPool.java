package com.album;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.rowset.serial.SerialBlob;
import java.io.BufferedInputStream;
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
     * find album data by id
     * @param id
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public AlbumInfo selectById(Long id) throws SQLException, ClassNotFoundException, IOException {
        try (Connection conn = getConnection()){
            AlbumInfo albumInfo = new AlbumInfo();
            PreparedStatement ps = conn.prepareStatement("select id, image, json from album where id = " + id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                albumInfo.setId(rs.getLong("id"));
                InputStream is = rs.getBinaryStream("image");
                if(is != null){
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    byte[] buff = new byte[100];
                    int rc = 0;
                    while ((rc = is.read(buff, 0, 100)) > 0) {
                        byteArrayOutputStream.write(buff, 0, rc);
                    }
                    albumInfo.setImage(new SerialBlob(byteArrayOutputStream.toByteArray()));
                }
                albumInfo.setJson(rs.getString("json"));
                return albumInfo;
            }
        }
        return null;
    }

    /**
     * save data
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public long save() throws SQLException, ClassNotFoundException, IOException {
        try (Connection conn = getConnection()){
            PreparedStatement ps = conn.prepareStatement("insert into album (image, json) values (?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setBinaryStream(1, PgPool.class.getResourceAsStream("/nmtb.png"));
            ps.setString(2, "{'artists': 'Sex Pistols', 'year': '1977', 'title': 'Sex Pistols'}");
            ps.execute();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()){
                return rs.getLong(1);
            }
            return 0;
        }
    }


    public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException {
        String url = "jdbc:postgresql://172.16.163.7:5432/album?serverTimezone=Asia/Shanghai&amp;useUnicode=true&amp;characterEncoding=utf8";
        PgPool pgPool = PgPool.getInstance(url, "postgres", "");
//        pgPool.save();
        System.out.println(pgPool.selectById(1L));
    }
}
