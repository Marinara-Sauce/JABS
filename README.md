# JABS (Just Another Gaming Backend Service)
## Overview
JABS is an open source solution for developers looking for an API for their game. JABS is designed to be deployable to a server and just... work. This program provides various functionality for games, such as a user system, matchmaking system, server browser, and can even deploy your game's dedicated servers. JABS is still in (very) early development.
## (Planned) Features
* User account system
* Allow users to log in via steam to minimize login credentials
* Matchmaking with a skill based system
* Dedicated server spawning, including servers spanning multiple machines.
* Server browser functionality
* Inventory/Shop/Currency
* News/Information hub
* Command Line interface for the server owner
## Requirements
The following programs are required to run the source code:
* A server running MySQL
* Maven >= 3.8.2
* Java >= 18

Required modules will be installed by Maven at runtime.

In addition, the MySQL database currently has to be built manually. Once the structure is finalized a script will be developed to generate one for you.
## Current Development Status
The current status for every feature is as follows:
* User account system - 10% Complete

Getting/Storing users is complete, but users cannot "login" and an authentication system is still needed.
* Matchmaking - 0% Complete
* Dedicated Server Support - 0% Complete
* A SQL Setup Script - Not Started
* Docker containerization - Not Started
# Commands
To compile/run the project: `mvn compile exec:java`

To test the project (and generate a JaCoCo report): `mvn clean test`

To package the project: `mvn build`
# License
Use of this program or code is licensed under the Attribution-ShareAlike 4.0 International license, which can be found here: https://creativecommons.org/licenses/by-sa/4.0/
In short, you are free to use or modify this code and it's cooresponding binaries for any purpose, including commercially, as long as the provided code or binaries is licensed under the same terms.

