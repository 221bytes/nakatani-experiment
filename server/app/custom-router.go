package app

import (
	"net/http"

	"github.com/gorilla/mux"
)

func NewRouter() *mux.Router {

	router := mux.NewRouter().StrictSlash(true)
	for _, route := range routes {
		var handler http.Handler

		handler = route.HandlerFunc

		router.
			Methods(route.Method).
			Path(route.Pattern).
			Name(route.Name).
			Handler(handler)

	}
	return router
}

type Route struct {
	Name        string
	Method      string
	Pattern     string
	HandlerFunc http.HandlerFunc
}

type Routes []Route

var routes = Routes{
	Route{
		"Index",
		"GET",
		"/",
		Index,
	},
	//Route{
	//	"Token",
	//	"POST",
	//	"/token",
	//	Token,
	//},
	Route{
		"MeetingIndex",
		"GET",
		"/events",
		EventIndex,
	},
	Route{
		"MeetingShow",
		"GET",
		"/events/{eventid}",
		EventShow,
	},
	Route{
		"MeetingCreate",
		"POST",
		"/events",
		EventCreate,
	},
	// Route{
	// 	"ImageUpdate",
	// 	"POST",
	// 	"/images",
	// 	ImageUpdate,
	// },
}
