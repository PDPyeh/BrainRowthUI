const express = require("express");
const cors = require("cors");

const app = express();

// Middleware
app.use(cors());
app.use(express.json());

// Route test (cek server hidup)
app.get("/", (req, res) => {
  res.send("BrainRowth backend is running");
});

// Route solver (nanti nyambung ke LM Studio)
const solverRoutes = require("./routes/solver");
app.use("/api", solverRoutes);

// Jalankan server
const PORT = 3000;
app.listen(3000, '0.0.0.0', () => {
    console.log('Server running on port 3000');
});
