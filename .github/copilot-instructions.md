### MAKE NO CHANGES TO THE APPLICATION.PROPERTIES OR DOCKERFILE AND NO CHANGES WITH THE SECRETS

## 📌 Project Overview

**ClubCI** is a technical club management system with an Android app frontend
and a Spring Boot backend. It allows users to:

- Register/login to the app
- Fill event participation forms
- Complete recruitment forms
- Make log in database of payments via Google Pay the data send through the api
- Store all data securely in MongoDB Atlas

The backend is deployed and running; these instructions will help you get the
project set up locally for development or testing.

---

## 🛠 Features

- User authentication (login/register)
- Event registration and form submission
- Recruitment form management
- Payment log in mongo db
- MongoDB Atlas as the core database

---

## 🧰 Tech Stack

- **Android App:** Java
- **Backend:** Spring Boot (Java)
- **Database:** MongoDB Atlas (NoSQL)
- **Deployment:** Dockerized Spring Boot backend hosted on Render

---

## 🗂 Repository Structure

```
git-instructions/
├── src/                  # Backend source code
├── pom.xml               # Maven configuration
├── Dockerfile            # Docker deployment
├── README.md             # Instructions and documentation
└── (Other backend files)
```

> Note: The backend is fully contained within the `git-instructions/` folder.

The Android app code may be in a separate repository or folder depending on your
setup.

---

## ⚙️ Backend Setup (Local Development)

1. **Clone the repository:**

```bash
git clone <your-repo-url>
cd git-instructions
```

2. **Install Java (17+)** Verify Java installation:

```bash
java -version
```

3. **Install Maven** Verify Maven installation:

```bash
mvn -version
```

4. **Build the backend**

```bash
mvn clean package -DskipTests
```

This generates the backend `.jar` file in the `target/` directory.

5. **Run the backend locally**

```bash
java -jar target/<your-backend-jar>.jar
```

> The backend uses the MongoDB URI from `application.properties`. Ensure your
> network allows connections to your MongoDB Atlas cluster.

6. **Test the backend**

- Health check endpoint:

```
http://localhost:<port-from-properties>/actuator/health
```

Should return:

```json
{ "status": "UP" }
```

---

## 📱 Android App Setup

1. Open the Android project in **Android Studio**.
2. Ensure **Java SDK 17+** is installed.
3. Add the backend API URL to the Retrofit or network client configuration:

```java
private static final String BASE_URL = "<your-deployed-backend-url>/api/";
```

4. Sync Gradle and build the project.
5. Run the app on an emulator or physical device.

---

## 🌐 Deployed Backend Access

Your backend is already hosted on Render and accessible at:

```
<your-render-backend-url>
```

Use this URL in your Android app to make API requests. Endpoints include
(example):

- `POST /api/users/login`
- `POST /api/users/register`
- `POST /api/events/register`
- `GET /api/events`

---

## 📝 Environment Variables

- **MONGO_URI** – already configured in the deployed backend
- **PORT** – managed automatically by Render
