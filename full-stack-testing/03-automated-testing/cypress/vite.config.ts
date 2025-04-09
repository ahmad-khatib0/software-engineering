import { defineConfig } from "vite";

export default defineConfig({
  root: ".",
  server: {
    port: 3000,
    // Enable CORS for Cypress
    cors: true,
  },
});
