package com.album;

import com.google.gson.Gson;
import com.rabbitmq.client.*;
import java.nio.charset.StandardCharsets;

public class RabbitmqMain {

    public static void main(String[] args) {
        String jdbcUrl = "jdbc:postgresql://localhost:5432/album?serverTimezone=Asia/Shanghai&amp;useUnicode=true&amp;characterEncoding=utf8";
        String username = "postgres";
        String password = "123456";
        PgPool pgPool = PgPool.getInstance(jdbcUrl, username, password);
        final Gson gson = new Gson();
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        String QUEUE_NAME = "album";
        try (
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel()
        ) {
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            channel.basicConsume(QUEUE_NAME, true, (tag, delivery) -> {
                try {
                    String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                    AlbumCollection albumCollection = gson.fromJson(message, AlbumCollection.class);
                    pgPool.save(albumCollection);
                } catch (Exception e) {
                    e.getMessage();
                }
            }, callback -> { });
        }catch (Exception ignored){

        }
    }
}
