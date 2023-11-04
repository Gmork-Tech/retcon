# RetCon

## About
RetCon is an open source runtime configuration provider for backend web services.

RetCon is written in Java and based on the Quarkus framework. It is licensed under the Apache license.

The term "RetCon" is short for retroactive continuity and is usually used to describe the act of changing a story or plot point after the publishing of some original media. The goal of this project is to allow for the changing of simple configuration parameters across an array of different applications, after the applications have been published to their respective runtime environment, without requiring redeployment or restarting.

## Project Goals
This project aims to provide an all-in-one solution for managing runtime feature flags as well as simple non-sensitive configuration parameters. It is designed to be self-contained, requiring only a small amount of persistent storage to hold a local SQLite database, however it could be configured with an external SQL database instead.

Configuration changes are pushed to clients via websocket so that client polling is not needed.

## Cool Things You Can Do!
Here's a quick list of cool things you can do with RetCon!

### Simple Config Changes
Let's say you have a couple services that distribute emails or text messages to predefined lists of users. You don't feel like adding a new SQL table for this. You don't want to deal with the migrations. You also don't want to redeploy your service in production every single time this list needs to change, and you just know as soon as you're positive the list is complete, there will be a change request. 

Or for instance, as your project has grown, your data models have grown with it, and one day you find that all of those nice paging properties you put in place need to change. It's simply no longer the best option to serve 1000 records per page.

Or in the simplest use case, what if you just want to have something handle feature flags for you? 

RetCon has you covered!

### Single Instance Configuration
Let's say you have a service that needs to scale horizontally. There is simply too much work for one instance to handle the load at this point. But... there is a data import job defined in said service that shouldn't be run by more than a single instance.

We don't want duplicate data, do we?

You could use a centralized ACID compliant database to store your job and provide locks, but this doesn't guarantee that the same instance will always perform said job. If the job relies on local caching, having each instance take turns executing the job is going to be memory inefficient. 

Enter RetCon! Simply tell RetCon you want the config

### Canaries
Let's say you've implemented

## Current Development Efforts
- Create the base server and database structure
- Create a simple frontend for managing the configuration server
- Support simple deployment based canary testing
- Support deploying single instance features
- Log transactions made against the RetCon server
- Create client libraries for Go, Java, JavaScript, C#, and Python (different repositories)
