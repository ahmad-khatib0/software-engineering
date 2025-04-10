package main

import (
	"log"
	"net/http"

	"github.com/ahmad-khatib0/software-engineering/full-stack-testing/ecommerce-go/handlers"
)

func main() {
	itemHandler := handlers.NewItemHandler()

	http.HandleFunc("/items", func(w http.ResponseWriter, r *http.Request) {
		switch r.Method {
		case http.MethodGet:
			itemHandler.GetAllItems(w, r)
		case http.MethodPost:
			itemHandler.CreateItem(w, r)
		default:
			w.WriteHeader(http.StatusMethodNotAllowed)
		}
	})

	log.Println("Server starting on :4000...")
	log.Fatal(http.ListenAndServe(":4000", nil))
}
