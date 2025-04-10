package handlers

import (
	"encoding/json"
	"net/http"

	"github.com/ahmad-khatib0/software-engineering/full-stack-testing/ecommerce-go/models"
	"github.com/ahmad-khatib0/software-engineering/full-stack-testing/ecommerce-go/pkg/storage"
)

type ItemHandler struct {
	storage *storage.MemoryStorage
}

func NewItemHandler() *ItemHandler {
	return &ItemHandler{
		storage: storage.NewMemoryStorage(),
	}
}

func (h *ItemHandler) GetAllItems(w http.ResponseWriter, r *http.Request) {
	items := h.storage.GetAllItems()

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(items)
}

func (h *ItemHandler) CreateItem(w http.ResponseWriter, r *http.Request) {
	var newItem models.Item
	err := json.NewDecoder(r.Body).Decode(&newItem)
	if err != nil {
		http.Error(w, err.Error(), http.StatusBadRequest)
		return
	}

	createdItem := h.storage.CreateItem(newItem)

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusCreated)
	json.NewEncoder(w).Encode(createdItem)
}
