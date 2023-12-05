package router

import (
	"github.com/gin-gonic/gin"
	"goServlet/model"
	"net/http"
)

func GetHandler(c *gin.Context) {
	albumInfo := model.AlbumInfo{
		Artist: "Sex Pistols",
		Title:  "Never Mind the Bollocks",
		Year:   "1997",
	}
	c.JSON(http.StatusOK, albumInfo)
}

func PostHandler(c *gin.Context) {
	data := model.ImageMetaData{
		AlbumID:   1,
		ImageSize: 99,
	}
	c.JSON(http.StatusOK, data)
}
