package com.album;

public class ImageMetaData {

    private Long albumId;

    private Long imageSize;

    public ImageMetaData(Long albumId, Long imageSize) {
        this.albumId = albumId;
        this.imageSize = imageSize;
    }

    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }

    public Long getImageSize() {
        return imageSize;
    }

    public void setImageSize(Long imageSize) {
        this.imageSize = imageSize;
    }
}
