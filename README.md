# Clinscribe v1.1 - AI-Driven Medical Transcription Application

[![GitHub Repository](https://img.shields.io/badge/GitHub-Repository-blue?style=flat-square&logo=github)](https://github.com/QMQMWANG/Clinscribe-1.1)
[![Prerequisite Setup Video](https://img.shields.io/badge/Setup-Video-red?style=flat-square&logo=youtube)](https://youtu.be/k7C0ZB1e5yY)
[![Demo Video](https://img.shields.io/badge/Demo-Video-green?style=flat-square&logo=youtube)](https://youtu.be/iKyS6HUqGzc)

## Table of Contents
1. [Introduction](#introduction)
2. [Features](#features)
3. [Prerequisite Setup](#prerequisite-setup)
   - [Downloading Required Software](#downloading-required-software)
   - [Setting Up Ollama Application](#setting-up-ollama-application)
   - [Setting Up the Ollama Server](#setting-up-the-ollama-server)
4. [Cloning and Setting Up the Android Application](#cloning-and-setting-up-the-android-application)
   - [Cloning the Repository](#cloning-the-repository)
   - [Building the Application](#building-the-application)
5. [Using the Application](#using-the-application)
   - [Logging In](#logging-in)
   - [Workflow for Recording, Processing, and Managing Clinical Data](#workflow-for-recording-processing-and-managing-clinical-data)
6. [Exporting Data](#exporting-data)
7. [Appendix](#appendix)
8. [Contact Information](#contact-information)

## Introduction
**Clinscribe v1.1** is an AI-driven medical transcription application designed to assist healthcare professionals in efficiently recording, processing, and managing clinical data. This application leverages state-of-the-art AI models to transcribe conversations into structured FHIR records and generate clinical summaries.

## Features
- **Real-time Transcription:** Record and transcribe clinical conversations on the go.
- **FHIR Integration:** Automatically convert transcriptions into FHIR JSON records.
- **Clinical Summaries:** Generate clinical summaries from FHIR records.
- **Data Management:** Edit, store, and manage FHIR records within the app.
- **Export Options:** Export transcriptions, summaries, and FHIR records in various formats.

## Prerequisite Setup
### Downloading Required Software
Before starting, ensure that you have the following software installed:

- **[Android Studio](https://developer.android.com/studio):** IDE for building the Android application.
- **[Ollama Application](https://ollama.com/download):** Required to run the language model.
- **[Python 3.11 or higher](https://www.python.org/downloads/):** For running the server script.
- **[Visual Studio Code (VSCode)](https://code.visualstudio.com/):** For editing and running the Python server script.

### Setting Up Ollama Application
1. Open the Windows Terminal.
2. Download the `phi3` model by running the following command:
   ```bash
   ollama run phi3
