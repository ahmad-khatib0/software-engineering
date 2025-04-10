package consumer_test

import (
	"bytes"
	"encoding/json"
	"fmt"
	"net/http"
	"testing"

	"github.com/pact-foundation/pact-go/v2/consumer"
	"github.com/pact-foundation/pact-go/v2/matchers"
	"github.com/stretchr/testify/assert"
)

func TestECommerceServiceConsumer(t *testing.T) {
	mockProvider, err := consumer.NewV2Pact(consumer.MockHTTPProviderConfig{
		Consumer: "ECommerceFrontend",
		Provider: "ECommerceService",
		PactDir:  "../pacts",
	})
	assert.NoError(t, err)

	t.Run("GET /items", func(t *testing.T) {
		err = mockProvider.
			AddInteraction().
			Given("items exist").
			UponReceiving("GET request for all items").
			WithRequest("GET", "/items").
			WillRespondWith(200, func(builder *consumer.V2ResponseBuilder) {
				builder.
					Header("Content-Type", matchers.String("application/json")).
					JSONBody(matchers.EachLike(map[string]any{
						"SKU":   matchers.Like("984058981"),
						"color": matchers.Like("Green"),
						"size":  matchers.Like("M"),
					}, 1))
			}).
			ExecuteTest(t, func(config consumer.MockServerConfig) error {
				url := fmt.Sprintf("http://%s:%d/items", config.Host, config.Port)
				resp, err := http.Get(url)
				if err != nil {
					return err
				}
				defer resp.Body.Close()
				if resp.StatusCode != 200 {
					return fmt.Errorf("expected 200, got %d", resp.StatusCode)
				}
				return nil
			})
		assert.NoError(t, err)
	})

	t.Run("POST /items", func(t *testing.T) {
		err = mockProvider.
			AddInteraction().
			UponReceiving("POST request to create item").
			WithRequest("POST", "/items", func(builder *consumer.V2RequestBuilder) {
				builder.
					Header("Content-Type", matchers.String("application/json")).
					JSONBody(map[string]any{
						"SKU":   matchers.Like("111222333"),
						"color": matchers.Like("Purple"),
						"size":  matchers.Like("XXL"),
					})
			}).
			WillRespondWith(201, func(builder *consumer.V2ResponseBuilder) {
				builder.
					Header("Content-Type", matchers.String("application/json")).
					JSONBody(map[string]any{
						"SKU":   matchers.Like("111222333"),
						"color": matchers.Like("Purple"),
						"size":  matchers.Like("XXL"),
					})
			}).
			ExecuteTest(t, func(config consumer.MockServerConfig) error {
				url := fmt.Sprintf("http://%s:%d/items", config.Host, config.Port)
				payload := map[string]string{
					"SKU":   "111222333",
					"color": "Purple",
					"size":  "XXL",
				}
				body, _ := json.Marshal(payload)
				resp, err := http.Post(url, "application/json", bytes.NewReader(body))
				if err != nil {
					return err
				}
				defer resp.Body.Close()
				if resp.StatusCode != 201 {
					return fmt.Errorf("expected 201, got %d", resp.StatusCode)
				}
				return nil
			})
		assert.NoError(t, err)
	})
}
