package com.album;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.zaxxer.hikari.HikariDataSource;

@WebServlet("/album/*")
public class AlbumServlet extends HttpServlet {

  private final Gson gson = new Gson();

  private PgPool pgPool;

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    ServletContext context = this.getServletContext();
    String jdbcUrl = context.getInitParameter("jdbcUrl");
    String username = context.getInitParameter("username");
    String password = context.getInitParameter("password");
    pgPool = PgPool.getInstance(jdbcUrl, username, password);
  }

  /**
   *
   * @param request
   * @param response
   * @throws IOException
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    try {
      long id = pgPool.save();
      ImageMetaData metaData = new ImageMetaData(id,100L);
      String metaDataJString = this.gson.toJson(metaData);
      PrintWriter out = response.getWriter();
      response.setCharacterEncoding("UTF-8");
      out.print(metaDataJString);
      out.flush();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   *
   * @param request
   * @param response
   * @throws IOException
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (request.getPathInfo() != null && request.getPathInfo().length() > 1) {
      try {
        Long albumID = Long.valueOf(request.getPathInfo().split("/")[1]);
        AlbumInfo albumInfo = pgPool.selectById(albumID);
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        out.print(this.gson.toJson(albumInfo));
        out.flush();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }
}

