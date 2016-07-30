package app

import (
	"fmt"
	"google.golang.org/appengine"
	"google.golang.org/appengine/datastore"
	"google.golang.org/appengine/log"
	"net/http"
	"time"

	"encoding/json"
)

type UserShort struct {
	ID       int64  `json:"id"`
	Provider string `json:"provider"`
	Name     string `json:"name"`
	IsActive bool   `json:"is_active"`
}
type User struct {
	ID              int64     `json:"id"`
	ProviderID      string    `json:"provider_id"`
	Provider        string    `json:"provider"`
	Name            string    `json:"name"`
	IsActive        bool      `json:"is_active"`
	LastConnection  time.Time `json:"last_connection"`
	FirstConnection time.Time `json:"first_connection"`
	Tokens          []string  `json:"access_token"`
}

func userKey(r *http.Request) *datastore.Key {
	ctx := appengine.NewContext(r)
	// The string "default_guestbook" here could be varied to have multiple userList.
	return datastore.NewKey(ctx, "UserList", "default_user_list", 0, nil)
}

func (user *User) GetUser(r *http.Request) bool {
	ctx := appengine.NewContext(r)

	// Ancestor queries, as shown here, are strongly consistent with the High
	// Replication Datastore. Queries that span entity groups are eventually
	// consistent. If we omitted the .Ancestor from this query there would be
	// a slight chance that Greeting that had just been written would not
	// show up in a query.
	q := datastore.NewQuery("User").Ancestor(userKey(r)).Filter("Provider =", user.Provider).Filter("ProviderID =", user.ProviderID)

	users := make([]User, 0, 1)
	if _, err := q.GetAll(ctx, &users); err != nil {
		log.Infof(ctx, fmt.Sprintf("error = %s", err))

		return false
	}

	if len(users) <= 0 {
		return false
	}
	*user = users[0]
	return true
}

func (user *User) CreateUser(r *http.Request) {
	ctx := appengine.NewContext(r)
	// We set the same parent key on every Greeting entity to ensure each Greeting
	// is in the same entity group. Queries across the single entity group
	// will be consistent. However, the write rate to a single entity group
	// should be limited to ~1/second.
	key := datastore.NewKey(ctx, "User", "", 0, userKey(r))
	key, err := datastore.Put(ctx, key, user)
	if err != nil {
		//TODO handleError
	}

	user.ID = key.IntID()

	log.Infof(ctx, fmt.Sprintf("User has been saved = %s", user.Name))

}

func (user *User) AddTokenToUser(r *http.Request, customToken *CustomToken) {
	ctx := appengine.NewContext(r)
	// We set the same parent key on every Greeting entity to ensure each Greeting
	// is in the same entity group. Queries across the single entity group
	// will be consistent. However, the write rate to a single entity group
	// should be limited to ~1/second.
	user.Tokens = append(user.Tokens, customToken.TokenString)
	key := datastore.NewKey(ctx, "User", "", user.ID, userKey(r))
	_, err := datastore.Put(ctx, key, user)
	if err != nil {
		//TODO handleError
	}
}

func (a *User) MarshalJSON() ([]byte, error) {
	return json.Marshal(map[string]interface{}{
		"id":   a.ID,
		"name": a.Name,
	})
}
