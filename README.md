# Simple REST Java Library
A REST Library is the Base of most systems. This has to be fast and reliable. But for me as Developer it also has to be easy to use. 
Spring.io systems are to bloated and full of things you don't need or don't know how to use it.
SimpleREST tends to be a easy to use and fast to implement REST API. 

Features:
- Based on Netty to be fast and reliable
- Full JSON Support
- Works with Java POJOS
- Jackson JSON parser and converter
- JWT (JSON Web Token) and Basic HTTP Auth out of the box
- Default Wrapper for all responses so you can see HTTP status and answer in the JSON.
- Simple Default Functions for Index, Exceptions, Not Found
- CatchAll functions for every Path
- Access control for paths and endpoits
- HTML Endpoints (from Ressource or FileStructure)
- CRUD Helper for easy to use Create, Read, Update, Delete Operations on entities

## Simple REST Client
This system also needed a simple to use RESTClient. 

Features:
- GET, PUT, POST, DELETE
- Easy to use Paramaters
- File Uploads (Multiple)
- JSON Upload
- JSON Answer
- Object to JSON and JSON to Object

## Load Balancder
A fast Rest Server can also be used to make a Loadbalanced Microservice. Thats why i tried this. But it has to be easy to use.

Features:
- Service Discovery through the Network
- Multiple same Services on multiple Computers (loadbalanced and failover)
- Multiple different Services on the same Computer (sharing of resources)
- Work load according to system ressources (Client side Loadbalancer knows how much ressources each individual System has)
- Long lasting Messages whith automatic Fail detection and retry. Even if Target or Source Service dies.
- Easy and Simple to use.