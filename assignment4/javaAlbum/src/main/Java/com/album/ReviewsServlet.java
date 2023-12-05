package com.album;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;


import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@WebServlet(name = "ReviewsServlet", value = "/review/*")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 10,    // 10 MB
        maxFileSize = 1024 * 1024 * 50,        // 50 MB
        maxRequestSize = 1024 * 1024 * 100)
public class ReviewsServlet extends HttpServlet {

    private final Gson gson = new Gson();

    private static final ConnectionFactory factory = new ConnectionFactory();

    private final static String QUEUE_NAME = "album";

    private PgPool pgPool;

    @Override
    public void init() throws ServletException {
        super.init();
        factory.setHost("localhost");
        ServletContext context = this.getServletContext();
        String jdbcUrl = context.getInitParameter("jdbcUrl");
        String username = context.getInitParameter("username");
        String password = context.getInitParameter("password");
        pgPool = PgPool.getInstance(jdbcUrl, username, password);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        try {
            String urlPath = req.getPathInfo();
            Long albumId = Long.parseLong(urlPath.split("/")[1]);

            AlbumCollection collection = pgPool.countById(albumId);
            PrintWriter out = resp.getWriter();
            resp.setCharacterEncoding("UTF-8");
            out.print(gson.toJson(collection));
            out.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        try {
            String urlPath = request.getPathInfo();
            Long albumId = Long.parseLong(urlPath.split("/")[1]);
            String action = urlPath.split("/")[2];

            AlbumCollection collection = new AlbumCollection();
            collection.setId(albumId);
            if("like".equals(action)){
                collection.setLikeCount(1L);
                collection.setDislikeCount(0L);
            }else{
                collection.setDislikeCount(1L);
                collection.setLikeCount(0L);
            }
            String msg = gson.toJson(collection);
            try (Connection connection = factory.newConnection();
                 Channel channel = connection.createChannel()) {
                channel.queueDeclare(QUEUE_NAME, false, false, false, null);
                channel.basicPublish("", QUEUE_NAME, null, msg.getBytes(StandardCharsets.UTF_8));
            }catch (Exception ignored){

            }
            PrintWriter out = response.getWriter();
            response.setCharacterEncoding("UTF-8");
            out.print(gson.toJson(msg));
            out.flush();
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }
}
