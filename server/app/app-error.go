package app

import (
	"encoding/json"
	"fmt"
	"google.golang.org/appengine"
	"google.golang.org/appengine/log"
	"net/http"
)

type AppError struct {
	Message string
	Code    int
	Err     error
	Where   string
}

func (appError *AppError) Execute(w http.ResponseWriter, r *http.Request) {
	ctx := appengine.NewContext(r)
	w.WriteHeader(http.StatusInternalServerError)
	log.Errorf(ctx, fmt.Sprintf("%s:%s", appError.Where, appError.Err))
	if err := json.NewEncoder(w).Encode(appError); err != nil {
		log.Errorf(ctx, fmt.Sprintf("Serialization error:%s", err))
		panic(err)
	}
}

func (a *AppError) MarshalJSON() ([]byte, error) {
	return json.Marshal(map[string]interface{}{
		"message": a.Message,
		"code":    a.Code,
	})
}
