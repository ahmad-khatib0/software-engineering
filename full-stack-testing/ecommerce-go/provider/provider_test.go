package provider_test

import (
	"os"
	"os/exec"
	"syscall"
	"testing"
	"time"

	"github.com/pact-foundation/pact-go/v2/provider"
	"github.com/stretchr/testify/assert"
)

func TestECommerceServiceProvider(t *testing.T) {
	// Start the service
	cmd := exec.Command("go", "run", "../main.go")
	cmd.Stdout = os.Stdout
	cmd.Stderr = os.Stderr
	err := cmd.Start()
	assert.NoError(t, err)

	// Wait for service to start
	time.Sleep(2 * time.Second)

	// Configure verifier
	verifier := provider.NewVerifier()

	// Verify provider
	err = verifier.VerifyProvider(t, provider.VerifyRequest{
		ProviderBaseURL:            "http://localhost:4000",
		PactFiles:                  []string{"../pacts/ECommerceFrontend-ECommerceService.json"},
		ProviderVersion:            "1.0.0",
		PublishVerificationResults: false,
	})

	// Kill the process
	_ = cmd.Process.Signal(syscall.SIGTERM)
	_ = cmd.Wait()

	assert.NoError(t, err)
}
