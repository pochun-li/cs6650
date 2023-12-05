package main

import (
	"github.com/gin-gonic/gin"
	"goServlet/router"
	"log"
)

func main() {
	server := gin.Default()
	server.GET("/album/:id", router.GetHandler)
	server.POST("/album", router.PostHandler)
	server.Run("0.0.0.0:8081") // listen and serve on 0.0.0.0:8080 (for windows "localhost:8080")
	log.Fatal("go servlet success")
}
