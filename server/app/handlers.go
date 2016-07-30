// Package sort provides primitives for sorting slices and user-defined
// collections.
package app

import (
	"encoding/json"
	"fmt"
	"net/http"

	"google.golang.org/appengine"

	"strconv"

	"github.com/gorilla/mux"
	"google.golang.org/appengine/log"
)

func Index(w http.ResponseWriter, r *http.Request) {
	// if isAuthenticated(w, r) == false {
	// 	return
	// }
	fmt.Fprint(w, "Welcome!\n")
}

func EventIndex(w http.ResponseWriter, r *http.Request) {
	ctx := appengine.NewContext(r)
	w.Header().Set("Content-Type", "application/json; charset=UTF-8")
	w.WriteHeader(http.StatusOK)
	event, err := getAllEvent(r)
	if err != nil {
		appError := &AppError{Message: "Internal error", Code: http.StatusInternalServerError, Where: "EventIndex", Err: err}
		appError.Execute(w, r)
		return
	}
	if event == nil {
		appError := &AppError{Message: "No Event", Code: http.StatusNoContent, Where: "EventIndex", Err: err}
		if err := json.NewEncoder(w).Encode(&appError); err != nil {
			log.Errorf(ctx, fmt.Sprintf("EventIndex:%s", err))
			panic(err)
		}
		return
	}
	if err := json.NewEncoder(w).Encode(&event); err != nil {
		log.Errorf(ctx, fmt.Sprintf("EventIndex:%s", err))
		panic(err)
	}
}

func EventShow(w http.ResponseWriter, r *http.Request) {
	ctx := appengine.NewContext(r)
	vars := mux.Vars(r)
	w.Header().Set("Content-Type", "application/json; charset=UTF-8")
	w.WriteHeader(http.StatusOK)
	eventId, err := strconv.Atoi(vars["eventid"])
	if err != nil {
		appError := &AppError{Message: "Bad Request", Code: http.StatusBadRequest, Where: "EventShow", Err: err}
		if err := json.NewEncoder(w).Encode(appError); err != nil {
			log.Errorf(ctx, fmt.Sprintf("EventShow:%s", err))
			panic(err)
		}
	}
	event, err := GetEvent(r, int64(eventId))
	if err != nil {
		w.WriteHeader(http.StatusNotFound)
		appError := &AppError{Message: "Not Found", Code: http.StatusNotFound, Where: "EventIndex", Err: err}
		appError.Execute(w, r)
		return

	}

	if err := json.NewEncoder(w).Encode(&event); err != nil {
		log.Errorf(ctx, fmt.Sprintf("EventShow:%s", err))
		panic(err)
	}
	return

}

func EventCreate(w http.ResponseWriter, r *http.Request) {
	ctx := appengine.NewContext(r)
	event, err := CreateEvent(r)
	if err != nil {
		w.WriteHeader(http.StatusNotFound)
		appError := &AppError{Message: "Not Found", Code: http.StatusNotFound, Where: "EventCreate", Err: err}
		appError.Execute(w, r)
		return

	}
	if err := json.NewEncoder(w).Encode(&event); err != nil {
		log.Errorf(ctx, fmt.Sprintf("EventShow:%s", err))
		panic(err)
	}
}

//func Token(w http.ResponseWriter, r *http.Request) {
//	user := new(User)
//	providerInfo := new(ProviderInfo)
//	decoder := json.NewDecoder(r.Body)
//	err := decoder.Decode(providerInfo)
//	if err != nil {
//		json.NewEncoder(w).Encode(ApiErrors{"401", "Incorrect Provider"})
//		return
//	}
//	if providerInfo.CheckProviderInfo(r, user) == false {
//		json.NewEncoder(w).Encode(ApiErrors{"401", "Incorrect Provider"})
//		return
//	}
//
//	if user.GetUser(r) == false {
//		user.CreateUser(r)
//	}
//
//	customToken := generateCustomToken(r, user)
//
//	json.NewEncoder(w).Encode(customToken)
//}

// func ImageUpdate(w http.ResponseWriter, r *http.Request) {
// 	img := CreateImage(r)
// 	if err := json.NewEncoder(w).Encode(img); err != nil {
// 		panic(err)
// 	}
// }
