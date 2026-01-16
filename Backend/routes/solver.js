const express = require("express");
const router = express.Router();

const LMSTUDIO_API = "http://127.0.0.1:1234/v1/chat/completions";
const MODEL_ID = "qwen3-4b-instruct-2507";

router.post("/solve-text", async (req, res) => {
  const { question } = req.body;

  if (!question || question.trim() === "") {
    return res.status(400).json({ error: "Question is required" });
  }

  try {
    const response = await fetch(LMSTUDIO_API, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        model: MODEL_ID,
        messages: [
          {
            role: "system",
            content:
              "Kamu adalah asisten matematika berbahasa Indonesia. " +
              "Tidak Boleh menjawab soal pengetahuan umum dan lainnya selain yang berhubungan dengan perhitungan " +
              "Tugasmu menjelaskan langkah-langkah penyelesaian soal matematika dengan benar dan singkat. " +
              "Semua jawaban WAJIB dalam Bahasa Indonesia."
          },
          {
            role: "user",
            content:
              "Selesaikan soal berikut secara LANGKAH DEMI LANGKAH. " +
              "Jawab HANYA dalam format JSON valid TANPA teks lain, TANPA markdown, TANPA penjelasan tambahan.\n\n" +
              "Format wajib:\n" +
              "{\n" +
              '  "steps": ["Langkah 1 ...", "Langkah 2 ..."],\n' +
              '  "final_answer": "jawaban akhir singkat"\n' +
              "}\n\n" +
              "Jika kamu menjawab di luar format JSON ini, jawabanmu dianggap SALAH.\n\n" +
              `Soal: ${question}`
          }
        ],
        max_tokens: 512,
        temperature: 0.2
      })
    });

    const data = await response.json();
    const raw = data.choices?.[0]?.message?.content || "";

    let steps = [];
    let finalAnswer = "";
    let parseError = null;

    try {
      let cleaned = raw
        .replace(/```json/gi, "")
        .replace(/```/g, "")
        .replace(/<think>[\s\S]*?<\/think>/gi, "")
        .trim();

      const firstBrace = cleaned.indexOf("{");
      const lastBrace = cleaned.lastIndexOf("}");

      // kalau ga ketemu { }, berarti ga ada JSON → lempar error biar masuk fallback
      if (firstBrace === -1 || lastBrace === -1 || lastBrace <= firstBrace) {
        throw new Error("No JSON object found");
      }

      cleaned = cleaned.slice(firstBrace, lastBrace + 1);

      const parsed = JSON.parse(cleaned);

      steps = Array.isArray(parsed.steps)
        ? parsed.steps.map((s) => String(s))
        : [];

      finalAnswer =
        typeof parsed.final_answer === "string"
          ? parsed.final_answer
          : "";
    } catch (err) {
      console.error("JSON parse error:", err);
      parseError = err.message;

      // Fallback: anggap seluruh raw text sebagai jawaban narasi
      steps = [];
      finalAnswer = raw;
    }

    // 👉 DI SINI kita balikin response ke Android
    res.json({
      question,
      steps,
      final_answer: finalAnswer,
      raw_answer: raw,
      parse_error: parseError
    });

  } catch (err) {
    console.error("Error calling LM Studio:", err);
    res.status(500).json({ error: "Failed to contact LM Studio" });
  }
});

module.exports = router;
