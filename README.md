# 🧠 Smart Research Assistant – Backend API

This is the backend service for the **Smart Research Assistant Chrome Extension** — an AI-powered research productivity tool that allows users to select any text from a webpage and instantly generate concise summaries using **Google Gemini API**.

---

## 🚀 Features

* Accepts raw selected text from Chrome Extension
* Generates AI-powered summaries using Gemini
* Clean REST API design using Spring Boot
* Deployed on Render Cloud
* Environment-based secure API key handling
* Demonstrates real-world usage and can be scaled in future

---

## 🛠️ Tech Stack

| Layer       | Technology           |
| ----------- | -------------------- |
| Backend     | Spring Boot 3        |
| Language    | Java 17              |
| HTTP Client | WebClient (Reactive) |
| AI Engine   | Google Gemini API    |
| Build Tool  | Maven                |
| Deployment  | Render (Dockerized)  |

---

## 📡 API Endpoint

### POST `/api/research/process`

#### Request Body

```json
{
  "content": "Text to summarize",
  "operation": "summarize"
}
```

#### Response

```text
AI generated summarized content
```

---

## ⚙️ Environment Configuration

Create environment variable:

```
GEMINI_KEY=your_api_key_here
```

`application.properties`

```properties
spring.application.name=research-assistant
server.port=8080

gemini.api.url=https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=
gemini.api.key=${GEMINI_KEY}
```

---

## 🐳 Docker Support

The application is containerized using Docker for smooth cloud deployment.

### Build locally

```bash
mvn clean package -DskipTests
docker build -t research-assistant-backend .
docker run -p 8080:8080 research-assistant-backend
```

---

## 🌍 Live Deployment

Backend API is deployed on **Render Cloud**
Handles real-time summarization requests from the Chrome Extension frontend.

---

## 🔐 Security

* Gemini API key is **never hardcoded**
* Uses environment variables for secrets
* Prevents accidental exposure in GitHub

---

## 🧩 Project Architecture

```
controller
 └── ResearchController.java

service
 └── ResearchService.java

dto
 └── ResearchRequest.java

config
 └── WebClientConfig.java
```
