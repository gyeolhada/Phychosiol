# Phychosiol

## Project Introduction
This project is a comprehensive hardware-software system named “Psychosiol: A Wearable-Based Depression Monitoring System”, focusing on the medical and healthcare domain. It aims to effectively address the challenges of monitoring patients with depression. Leveraging affective computing technologies, the system analyzes users’ multimodal physiological signals—including heart rate, blood oxygen, electrodermal activity (EDA), and body temperature—to accurately detect their emotional states. Physiological data is collected via a wearable hand-mounted device and transmitted to the mobile terminal via low-power Bluetooth. On the smartphone, the data is not only visualized but also used to support features such as depression episode detection.

## Project Features
### Software Design
The core software, Psychosiol App, is an Android-based mobile application designed for two user groups: patients and doctors.

(1) Patient-side features include depression episode monitoring and emergency alerts, physiological and emotional state tracking, real-time physiological charts, mood diaries, and psychological scale assessments.

(2) Doctor-side features include real-time access to patients’ physiological data and the ability to receive and respond to patient alerts.

The app is developed using Android Jetpack with an MVVM architecture, utilizes coroutines for enhanced concurrency, and employs both HTTP and UDP protocols for communication.

### Algorithm Design
At the algorithmic level, the core of the app includes signal processing, time-frequency transformation, and deep learning models. The system integrates LSTM and Transformer attention mechanisms to fuse multi-channel information for efficient and accurate emotion recognition. This multimodal physiological data fusion model enables timely analysis of physiological changes and immediate alerts during depressive episodes. Additionally, the system offers mood tracking and other supportive features, striving to deliver a one-stop mental health monitoring experience for users.

## Tech Stack
Java / Kotlin (Android)

Python (for audio signal processing)

Android Studio

Git / GitHub

## Project Structure
app/

├── src/

│   ├── main/

│   │   ├── java/         - Core Code

│   │   ├── python/       - Algorithm Execution *(not publicly available)*  

│   │   ├── res/          - Resource Files

│   │   └── AndroidManifest.xml

│   ├── test/

│   ├── androidTest/

├── build.gradle.kts

build.gradle.kts

README.md
