package model

type AlbumInfo struct {
	Artist string `json:"artist,omitempty"`
	Title  string `json:"title,omitempty"`
	Year   string `json:"year,omitempty"`
}

type ImageMetaData struct {
	AlbumID   int64 `json:"albumID,omitempty"`
	ImageSize int64 `json:"imageSize,omitempty"`
}
