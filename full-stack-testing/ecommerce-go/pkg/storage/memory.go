package storage

import "github.com/ahmad-khatib0/software-engineering/full-stack-testing/ecommerce-go/models"

type MemoryStorage struct {
	items []models.Item
}

func NewMemoryStorage() *MemoryStorage {
	return &MemoryStorage{
		items: []models.Item{
			{SKU: "984058981", Color: "Green", Size: "M"},
			{SKU: "984058982", Color: "Blue", Size: "L"},
			{SKU: "984058983", Color: "Red", Size: "S"},
		},
	}
}

func (s *MemoryStorage) GetAllItems() []models.Item {
	return s.items
}

func (s *MemoryStorage) CreateItem(item models.Item) models.Item {
	s.items = append(s.items, item)
	return item
}
