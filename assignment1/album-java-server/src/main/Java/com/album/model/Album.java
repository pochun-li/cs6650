package com.album.model;

import java.io.Serializable;

public class Album implements Serializable {
  private String artists;
  private String title;
  private String year;

  public Album(String artists, String title, String year) {
    this.artists = artists;
    this.title = title;
    this.year = year;
  }

  public Album() {
  }

  public String getArtists() {
    return artists;
  }

  public String getTitle() {
    return title;
  }

  public String getYear() {
    return year;
  }
}
