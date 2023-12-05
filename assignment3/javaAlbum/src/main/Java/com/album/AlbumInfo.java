package com.album;

import java.io.Serializable;
import java.sql.Blob;

public class AlbumInfo implements Serializable {
  private Long id;
  private Blob image;
  private String json;

  public AlbumInfo() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Blob getImage() {
    return image;
  }

  public void setImage(Blob image) {
    this.image = image;
  }

  public String getJson() {
    return json;
  }

  public void setJson(String json) {
    this.json = json;
  }

  @Override
  public String toString() {
    return "AlbumInfo{" +
            "id=" + id +
            ", image=" + image +
            ", json='" + json + '\'' +
            '}';
  }
}
