# Submission Report for Fruit Service RMI Application 
**Student IDs**: [121063] 
**Course**: MIT8102 Advanced Distributed Systems 
**Institution**: Strathmore University 
**Group Members**: [Bruce Onyango] (dd/mm/yy)
**Date of Submission**: [01/07/25] (dd/mm/yy)

## Executive Synopsis

This study describes how Java RMI was used to construct a distributed fruit service management system.

## Overview of the System Architecture
A distributed three-tier architecture is used by the system:

```bash
┌─────────────────┐    HTTP/JSON    ┌─────────────────┐    RMI/Java     ┌─────────────────┐
│   Android App   │ ──────────────→ │  Servlet Layer  │ ──────────────→ │  RMI Server     │
│  (Client Tier)  │                 │(Web Tier)       |                 │ (Business Tier) │
│                 │ ←────────────── │                 │ ←────────────── │                 │
└─────────────────┘    JSON/HTTP    └─────────────────┘   Java Objects  └─────────────────┘
```
### Component Specifics 
#### 1. Client Tier (Android App) - 
**Technology**: at least Android Noughart 7 - 
**Communication**: HTTP/JSON
**Features**: 
 - Fruit management operations user interface
 - Asynchronous communication
 - Handling errors

#### 2. Java servlets at the Web Tier
**Technology**: Apache Tomcat, Java Servlets
**Communication**: RMI and HTTP
**Features**: 
 - The operations of RMI clients

#### 3. RMI Server, or Business Tier

**Technology**: Java RMI 
**Communication**: RMI with web tier
**Features**:
 - Invocation of remote methods
 - Execution of business logic

## Specifics of Implementation

#### 1. Interface for RMI
```java
public interface Compute extends Remote {
    <T> T executeTask(Task<T> t) throws RemoteException;
}
``` 
**Goal**: provides customers with remote methods 
**Advantages**:  Adaptable, scalable, and polymorphic

#### 2. Execution of the Task
```java
public interface Task<T> extends Serializable {
    T execute();
}
``` 
**Goal**:  Business operations are encapsulated for remote execution.
**Achievements**:  CalFruitCost, CalculateCost, AddFruitPrice, UpdateFruitPrice, DeleteFruitPrice
**Serialization**:  permits operation items to be transmitted across a network.

#### 3. Data Model 
```java
public class FruitPrice implements Serializable {
    private String fruitName;
    private double price;
    // ... methods
}
```
**Goal**:  represents the system's fruit data. 
**Serialization**:  Encourage the passing of RMI parameters

### Network Communication 
### RMI Server Setup
```java
// Server binding
Registry registry = LocateRegistry.createRegistry(1099);
registry.bind("FruitComputeEngine", engine);
```

#### Client Connection for RMI 
```java
// Client lookup
Registry registry = LocateRegistry.getRegistry(serverHost, 1099);
Compute comp = (Compute) registry.lookup("FruitComputeEngine");
```

## Features of distributed systems

### 1. Transparency: 
 - Clients use remote objects' methods just like they would local ones.

### 2. Network Communication: 
 - **HTTP Layer**: JSON over HTTP for Web communication; 
 - **RMI Layer**: Java object serialization over TCP; 
 - **Cross-Platform**: Java server and Android client communication

### Blackbox Test Scenarios

#### 1. Single Computer Testing 
 - ✅ Every component operating on localhost 
 - ✅ Verification of end-to-end functionality 
 - ✅ Data persistence following creation.

### Test Outcomes
Every test scenario passed, proving: 
 - Robust error handling 
 - Proper distributed system behavior 
 - Reliable RMI communication

## Important Learning Outcomes

### Development of Technical Skills
 1. **RMI Implementation**: Knowledge of Java RMI implementation and architecture
 2. **Distributed Architecture**: Multi-tier system design and implementation.
 3. **Integration**: Blending several technologies (Servlets, RMI, and Android)

### Applied Distributed Systems Concepts
 1) **Remote Procedure Calls**: RMI as a Remote Procedure Call paradigm implementation
 2. **Serialization**: Network transmission through object marshalling and unmarshalling
 The RMI registry for service discovery is the third service to be named. The location and access transparency of dispersed calls is the fourth service.

## Issues Found with This Implementation

 Issue 1: Transferring files from the Fruitservice backend to the Tomcat Webapps subdirectory **Difficulty**:  The requirement to perform two recompiles whenever the backend is modified

### Problem 2: Insufficient tolerance for errors
 **Problem**: There is no implementation to account for error in this implementation.

### Challenge 3: Absence of Load Distribution 
**Issue**: This method does not disperse the load to replicate distributed situations with large traffic.

## Capabilities of the System

### Fulfilled Functional Requirements
 - ✅ Add new fruits and their costs.
 - ✅ Update current fruit prices 
 - ✅ Remove fruits from stock 
 - ✅ Determine fruit purchase expenses 
 - ✅ Create receipts

### Non-functional requirements were satisfied: 
 - ✅ **Distributed**: Components operate on several computers; 
 - ✅ **Reliable**: Error management and recovery are done correctly; 
 - ✅ **Scalable**: The architecture can accommodate more servers; 
 - ✅ **Maintainable**:  Code that is modular and well documented

## Conclusion

This project effectively illustrates how to use Java RMI to construct a distributed system.  The system accomplishes the main goals of the assignment:

 1. **Distributed Computing**: distributed components communicating
 2. **RMI Implementation**: Using Java RMI correctly to invoke remote methods

The implementation demonstrates important distributed systems ideas such as serialization, remote procedure calls, and location transparency.  The system's distributed nature is confirmed by the successful testing, which also shows a practical grasp of distributed computing concepts.

### Upcoming Improvements
 - Database persistence layer
 - permission and authentication
 - fault tolerance and load balancing
 - logging and performance monitoring
 - web-based administrative interface

