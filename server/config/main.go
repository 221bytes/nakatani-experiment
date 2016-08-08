package hello

import (
	"fmt"
	"net/http"

	"github.com/221bytes/nakatani-experiment/server/app"
)

func init() {
	router := app.NewRouter()

	http.Handle("/", router)
}

func handler(w http.ResponseWriter, r *http.Request) {
	fmt.Fprint(w, "Hello, world!")
}
