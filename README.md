# Fruit Service RMI Application

**Course**: MIT8102 Advanced Distributed Systems  
**Institution**: Strathmore University  
**Assignment**: #1 - Distributed Computing with RMI

## Overview

A distributed fruit service management system demonstrating Java RMI concepts through a 3-tier architecture:
- **Android Client** (Mobile UI)
- **Java Servlets** (Web Layer) 
- **RMI Server** (Business Logic)

## Quick Start

### Prerequisites
- Java JDK 11+
- Android Studio (for mobile client)
- Apache Tomcat 9+ (for servlets)

### Running the System

1. **Start RMI Server**:
   \`\`\`bash
   cd FruitServiceBackend
   java -cp build sun.rmi.registry.RegistryImpl 1099
   \`\`\`

2. **Deploy Servlets**:
   \`\`\`bash
   cd web
   ./build.sh
   ./deploy.sh
   \`\`\`

3. **Run Android Client**:
   - Open `android/` in Android Studio
   - Build and run on device/emulator

### Network Testing

To test across different computers:

1. **On Server Computer**:
   \`\`\`bash
   java -Djava.rmi.server.hostname=YOUR_IP -cp build FruitComputeEngine
   \`\`\`

2. **On Client Computer**:
   \`\`\`bash
   java -cp build NetworkTestClient
   # Enter server IP when prompted
   \`\`\`

## System Architecture

\`\`\`
Android App → HTTP/JSON → Servlets → RMI → Business Server
\`\`\`

## Features

- ✅ Add/Update/Delete fruits
- ✅ Calculate purchase costs
- ✅ Generate receipts
- ✅ Cross-network RMI communication
- ✅ Concurrent client support

## Documentation

- [Development Log](DEVELOPMENT_LOG.md) - Complete development diary
- [Submission Report](SUBMISSION_REPORT.md) - Detailed technical report
- [Network Setup](docs/network-setup.md) - Cross-computer testing guide

## Group Members

- [Student 1 Name] - [Student ID]
- [Student 2 Name] - [Student ID]

## Submission

This repository contains all required components for MIT8102 Assignment #1:
- ✅ Implementation files
- ✅ Client and server main programs  
- ✅ Development log/diary
- ✅ Network testing validation
- ✅ Comprehensive documentation
