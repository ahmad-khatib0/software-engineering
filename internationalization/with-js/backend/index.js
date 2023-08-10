const express = require("express");
const cors = require("cors");

const app = express();

app.use(express.json());
app.use(cors());

const text = {
  en: `Udemy’s mission is to improve lives through learning. We enable anyone anywhere to create and share educational 
  content (instructors) and to access that educational content to learn (students).
   We consider our marketplace model the best way to `,
  de: `Udemys Mission ist es, das Leben durch Lernen zu verbessern. Wir
    ermöglichen es jedem, überall Bildungsinhalte zu erstellen und zu teilen (Lehrer) und auf diese
     Bildungsinhalte zuzugreifen, um zu lernen (Studenten). Wir betrachten unser Marktplatzmodell als 
     den besten Weg, um`,
};

app.get("/text", (req, res) => {
  const locale = req.header("Accept-Language").substr(0, 2);
  res.send({ text: text[locale] });
});

app.post("/like", (req, res) => {
  res.send({ message: "Thank you" });
});

app.listen(8000);
