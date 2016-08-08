package app

import (
	"fmt"
	"net/http"

	"google.golang.org/appengine"
	"google.golang.org/appengine/log"

	"time"

	jwt "github.com/dgrijalva/jwt-go"

	"encoding/json"
	"strings"
)

type CustomToken struct {
	TokenString string `json:"access_token"`
	ExpireIn    int64  `json:"expire_in"`
}

//Generate a new token
func generateCustomToken(r *http.Request, user *User) (customToken *CustomToken) {
	customToken = new(CustomToken)
	ctx := appengine.NewContext(r)

	for _, tokenString := range user.Tokens {
		token, err := jwt.Parse(tokenString, func(jwtToken *jwt.Token) (interface{}, error) {
			// since we only use the one private key to sign the tokens,
			// we also only use its public counter part to verify

			return VerifyKey, nil
		})
		log.Infof(ctx, fmt.Sprintf("tokenString = %s", tokenString))

		if err == nil {
			log.Infof(ctx, fmt.Sprintf("err = %s", err))

			if token.Valid == true {
				customToken.TokenString = tokenString
				customToken.ExpireIn = 3600
				return
			} else {
				//TODO delete when invalid
			}
		}

	}
	// create a signer for rsa 256
	t := jwt.New(jwt.GetSigningMethod("RS256"))

	// set our claims
	expireTime := time.Now().Add(time.Second * 3600).Unix()
	t.Claims = jwt.MapClaims{
		"CustomUserInfo": user,
		"exp":            expireTime,
	}
	// t.Claims["CustomUserInfo"] = user

	// set the expire time
	// see http://tools.ietf.org/html/draft-ietf-oauth-json-web-token-20#section-4.1.4
	// t.Claims["exp"] = expireTime
	tokenString, err := t.SignedString(SignKey)
	if err != nil {
		panic(err)
		return
	}
	customToken.TokenString = tokenString
	customToken.ExpireIn = 3600

	log.Infof(ctx, fmt.Sprintf("Generating token done = %s", tokenString))
	user.AddTokenToUser(r, customToken)

	return

}

type ApiErrors struct {
	Code    string
	Message string
}

func isAuthenticated(w http.ResponseWriter, r *http.Request) bool {
	//Authorization: Bearer 0b79bab50daca910b000d4f1a2b675d604257e42
	tokenArray := r.Header["Authorization"]

	if tokenArray == nil || len(tokenArray) != 1 {
		w.Header().Set("Content-Type", "application/json; charset=UTF-8")
		w.WriteHeader(http.StatusUnauthorized)
		json.NewEncoder(w).Encode(ApiErrors{"401", "No Token"})
		return false
	}

	tokenString := tokenArray[0]

	tokenArray = strings.Split(tokenString, "Bearer ")

	if tokenArray == nil || len(tokenArray) != 2 {
		json.NewEncoder(w).Encode(ApiErrors{"401", "Token malformed"})
		return false
	}

	tokenString = tokenArray[1]

	token, err := jwt.Parse(tokenString, func(jwtToken *jwt.Token) (interface{}, error) {
		// since we only use the one private key to sign the tokens,
		// we also only use its public counter part to verify

		return VerifyKey, nil
	})

	switch err.(type) {
	case nil: // no error
		if !token.Valid {
			// but may still be invalid
			w.WriteHeader(http.StatusUnauthorized)
			json.NewEncoder(w).Encode(ApiErrors{"401", "Token invalid"})
			return false
		}

		//assertion doesn't work so I have to convert to json then to struct
		mapB, _ := json.Marshal(token.Claims)
		result := &User{}
		err = json.Unmarshal(mapB, result)
		return true

	case *jwt.ValidationError: // something was wrong during the validation
		vErr := err.(*jwt.ValidationError)

		switch vErr.Errors {
		case jwt.ValidationErrorExpired:
			w.WriteHeader(http.StatusUnauthorized)
			json.NewEncoder(w).Encode(ApiErrors{"401", "Token Expired"})
			return false
		default:
			w.WriteHeader(http.StatusInternalServerError)
			json.NewEncoder(w).Encode(ApiErrors{"401", "Token malformed"})
			return false
		}

	default: // something else went wrong
		w.WriteHeader(http.StatusInternalServerError)
		json.NewEncoder(w).Encode(ApiErrors{"401", "Token malformed"})
		return false
	}
}
