# Simple REST Java Library
![SimpleRest Logo](https://raw.githubusercontent.com/it-open/SimpleREST/master/logo/Logo-1400w.png)
## Howto Start - Examples
Look into the Wiki!
https://github.com/it-open/SimpleREST/wiki
## Maven
```xml
<dependency>
  <groupId>at.itopen</groupId>
  <artifactId>SimpleRest</artifactId>
  <version>0.4.5</version>
</dependency>
```
##Updateing

Updateing to 4.5 can break compiling because of Static Code Analysis some Names have been changed (either Case or there were '_' in Names)
All functions are the same or better/faster


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
## Added Microservice Avaibility
Loadbalancing was never so easy
- Discovery Service (Automatically Check and discover other Services od other or same Computer)
- Health Checks (Every System is checked)
- Client Side Loadbalancer 
- Long Lasting Messages (Messages will work even if initiater service oder initial target services dies)
- REST Client + REST Client with Loadbalancing. (Make loadbalaned rest calls in just one Line of Code, GET, PUT, POST, DELETE)


