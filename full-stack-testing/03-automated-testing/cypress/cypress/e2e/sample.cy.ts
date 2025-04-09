describe("Simple Page Test", () => {
  beforeEach(() => {
    cy.visit("http://localhost:3000"); // default Vite port
  });

  it("loads the page and verifies content", () => {
    cy.get("#title").should("contain.text", "Hello Cypress!");
  });

  it("clicks the button and checks the result", () => {
    cy.get("#click-me").click();
    cy.get("#output").should("have.text", "Button was clicked!");
  });
});
