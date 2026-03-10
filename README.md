<div align="center">
  <img src="src/main/resources/com/genes/icone.ico" width="120" alt="ForensiQ Logo"/>
  
  # ForensiQ
  
  **PDF Forensic Analyzer for Windows**
  
  ![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=java)
  ![JavaFX](https://img.shields.io/badge/JavaFX-21-blue?style=flat-square)
  ![PDFBox](https://img.shields.io/badge/PDFBox-2.0.30-red?style=flat-square)
  ![Release](https://img.shields.io/badge/Release-v1.0.0-brightgreen?style=flat-square)
</div>

---

## What is ForensiQ?

ForensiQ is a desktop tool for forensic analysis of PDF files on Windows.
It extracts metadata, detects threat vectors, and generates a risk score, all without sending your files anywhere.

---

## Features

| Module | Description |
|---|---|
| `[INF]` File Information | Name, size, path, pages, PDF version, encryption status |
| `[ID]` Identity & Metadata | Title, author, subject, keywords, creator, producer |
| `[TIME]` Timeline | Creation date, last modification, delta between both |
| `[PERM]` Permissions | Print, copy, modify, annotate, fill forms |
| `[THREAT]` Attack Vectors | JavaScript, embedded files, open actions, forms, signatures |
| `[RISK]` Risk Score | Forensic score from 0 to 100 with risk level classification |
| `[CONTENT]` Page Preview | Character, word and line count + text preview of page 1 |

---

## Installation

1. Go to [Releases](https://github.com/NygelNunes/ForensiQ/releases)
2. Download `ForensiQ_Setup_v1.0.exe`
3. Run as administrator
4. No Java installation required — JRE is bundled

**Requirements:** Windows 10 or later (64-bit)

---

## Building from Source
```bash
git clone https://github.com/NygelNunes/ForensiQ.git
cd ForensiQ
mvn clean package
```

The fat JAR will be generated at `target/forensiq-fat.jar`.

---

## Tech Stack

- **Java 17**
- **JavaFX 21** — UI framework
- **Apache PDFBox 2.0.30** — PDF parsing
- **Maven** — build tool
- **Inno Setup** — Windows installer

