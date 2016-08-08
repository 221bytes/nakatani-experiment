package app

import (
	"fmt"
	"net/http"
	"time"

	"google.golang.org/appengine"
	"google.golang.org/appengine/datastore"
	"google.golang.org/appengine/log"

	"encoding/json"
)

type Coordinates struct {
	Lat float64 `json:"latitude"`
	Lng float64 `json:"longitude"`
}

type Event struct {
	ID           int64       `json:"id"`
	IdPictograme int64       `json:"idPictograme"`
	Name         string      `json:"name"`
	Coordinates  Coordinates `json:"coordinates"`
	Time         time.Time   `json:"time"`
}

func eventKey(r *http.Request) *datastore.Key {
	ctx := appengine.NewContext(r)
	// The string "default_guestbook" here could be varied to have multiple userList.
	return datastore.NewKey(ctx, "MeetingList", "default_meeting_list", 0, nil)
}

func getAllEvent(r *http.Request) (events []Event, err error) {
	ctx := appengine.NewContext(r)
	q := datastore.NewQuery("Event").Ancestor(eventKey(r))
	keys, err := q.GetAll(ctx, &events)
	if err != nil {
		log.Errorf(ctx, fmt.Sprintf("getAllEvent:%s", err))
		return
	}

	for i, _ := range events {
		events[i].ID = keys[i].IntID()
	}
	return
}

func GetEvent(r *http.Request, meetingId int64) (event Event, err error) {
	ctx := appengine.NewContext(r)

	key := datastore.NewKey(ctx, "Event", "", meetingId, eventKey(r))

	err = datastore.Get(ctx, key, &event)
	if err != nil {
		log.Infof(ctx, fmt.Sprintf("error = %s", err))
		return
	}

	event.ID = key.IntID()
	log.Infof(ctx, fmt.Sprintf("eventId = %d", event.ID))

	return
}

func CreateEvent(r *http.Request) (event Event, err error) {
	ctx := appengine.NewContext(r)

	decoder := json.NewDecoder(r.Body)
	err = decoder.Decode(&event)

	if err != nil {
		log.Errorf(ctx, fmt.Sprintf("CreateEvent json decode error %s", err))
		return
	}
	event.Time = time.Now()
	key := datastore.NewKey(ctx, "Event", "", 0, eventKey(r))
	key, err = datastore.Put(ctx, key, &event)
	if err != nil {
		log.Errorf(ctx, fmt.Sprintf("CreateMeeting datastore error %s", err))
		return
	}

	event.ID = key.IntID()

	log.Infof(ctx, fmt.Sprintf("Meeting has been saved = %d", event.ID))

	return
}

//func (a *Meeting) MarshalJSON() ([]byte, error) {
//	return json.Marshal(map[string]interface{}{
//		"id":   a.ID,
//		"name": a.Name,
//	})
//}
