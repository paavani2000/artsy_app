# 🎨 Artsy Project Suite – Artist Discovery Platform

An end-to-end, cross-platform solution for discovering and bookmarking artists using the [Artsy API](https://developers.artsy.net/). Built with both web and mobile experiences in mind, this project enables users to explore visual art, search and categorize artists, and save favorites across devices with a unified backend.

## 🧩 Overview

This project contains two independent yet connected applications:

- 🌐 **[Artsy Web App](./Web App)**: A full-stack application built with Angular, Node.js, and MongoDB  
- 📱 **[Artsy Android App](./Android App)**: A native Android app built using Kotlin and Jetpack Compose

Both apps use a shared backend and offer consistent features like search, category exploration, artist details, and secure favorite management.

---

## 🔍 Core Features (Web + Android)

- 🔎 **Search Artists**: Query the Artsy API by artist name or keyword  
- 🧠 **Category Browsing**: Discover artists based on genre or classification  
- 💾 **Favorites Management**: Save favorite artists after login  
- 🔐 **Authentication**: JWT-based login with persistent session support (cookies or mobile session store)  
- 🔗 **Artist Detail View**: Bio, links, and similar artists displayed cleanly  
- 🌐 **Open on Artsy**: Link out to official Artsy artist pages

---

## 🛠 Tech Stack

| Platform       | Tech Details                                                                 |
|----------------|------------------------------------------------------------------------------|
| 🌐 Web App      | Angular, TypeScript, Node.js, Express, MongoDB Atlas, JWT, Cookies   |
| 📱 Android App  | Kotlin, Jetpack Compose, Retrofit, Coroutines, PersistentCookieJar           |
| 🔗 Shared Backend | Node.js, Express.js, MongoDB Atlas, JWT, Cookie-parser, Google Cloud Hosting |
| 📡 External API | [Artsy Public API](https://developers.artsy.net/)                            |
