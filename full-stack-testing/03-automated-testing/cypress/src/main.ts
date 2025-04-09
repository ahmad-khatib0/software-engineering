const button = document.getElementById("click-me")!;
const output = document.getElementById("output")!;

button.addEventListener("click", () => {
  output.textContent = "Button was clicked!";
});
