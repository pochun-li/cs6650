package com.album;

import com.google.gson.Gson;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class RabbitmqMain {

    public static void main(String[] args) throws IOException, InterruptedException {
        String jdbcUrl = "jdbc:postgresql://localhost:5432/album?serverTimezone=Asia/Shanghai&amp;useUnicode=true&amp;characterEncoding=utf8";
        String username = "postgres";
        String password = "postgres";
        PgPool pgPool = PgPool.getInstance(jdbcUrl, username, password);
        final Gson gson = new Gson();
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        String QUEUE_NAME = "album";
        try (
                Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()
        ) {
            while (true){
                channel.basicConsume(QUEUE_NAME, true, (tag, delivery) -> {
                    try {
                        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                        System.out.println(message);
                        AlbumCollection albumCollection = gson.fromJson(message, AlbumCollection.class);
                        pgPool.save(albumCollection);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }, callback -> { });
                Thread.sleep(1000);
            }
//            channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        }catch (Exception ignored){

        }

    }
}
