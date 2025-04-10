.PHONY: test-consumer test-provider test-all clean

PACT_DIR=./pacts

test-consumer:
	go test ./consumer -v

test-provider:
	go test ./provider -v

test-all: test-consumer test-provider

clean:
	rm -rf $(PACT_DIR)
