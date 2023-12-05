package com.album;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.album.model.ImageMetaData;
import com.album.model.Album;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@WebServlet("/album/*")
public class AlbumServlet extends HttpServlet {

  private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    try {
      ImageMetaData metaData = new ImageMetaData(1L,100L);
      PrintWriter out = response.getWriter();
      response.setCharacterEncoding("UTF-8");
      out.print(gson.toJson(metaData));
      out.flush();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (request.getPathInfo() != null && request.getPathInfo().length() > 1) {
      Album album = new Album("Sex Pistols", "Never Mind The Bollocks", "1977");
      PrintWriter out = response.getWriter();
      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");
      out.print(gson.toJson(album));
      out.flush();
    }
  }
}

