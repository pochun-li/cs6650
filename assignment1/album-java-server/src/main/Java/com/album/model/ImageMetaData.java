package com.album.model;

import java.io.Serializable;

public class ImageMetaData implements Serializable {
  private Long albumID;
  private Long imageSize;

  public ImageMetaData(Long albumID, Long imageSize) {
    this.albumID = albumID;
    this.imageSize = imageSize;
  }

  public ImageMetaData() {
  }

  public Long getAlbumID() {
    return albumID;
  }

  public Long getImageSize() {
    return imageSize;
  }

  public void setAlbumID(Long albumID) {
    this.albumID = albumID;
  }

  public void setImageSize(Long imageSize) {
    this.imageSize = imageSize;
  }
}
