# RetCon

Note: RetCon is a work in progress and not yet complete. We are aiming for end of Q1 2024.

## About
RetCon is an open source runtime configuration provider for backend web services.

RetCon is written in Java and based on the Quarkus framework. It is licensed under the Apache license.

The term "RetCon" is short for retroactive continuity and is usually used to describe the act of changing a story or plot point after the publishing of some original media. The goal of this project is to allow for the changing of simple configuration parameters across an array of different applications, after the applications have been published to their respective runtime environment, without requiring redeployment or restarting.

## Project Goals
This project aims to provide an all-in-one solution for managing runtime feature flags as well as simple non-sensitive configuration parameters. It is designed to be self-contained, requiring only a small amount of persistent storage to hold an embedded H2 database, however it could be configured with an external SQL database instead.

Configuration changes are pushed to clients via websocket so that client polling is not needed. Clients should cache configuration data such that integrating RetCon should not slow the performance of critical tasks that expect configuration to be readily available in memory.

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

What can you do? You have a service that it's going to take some serious work to horizontally scale in an effective manner that's not just increasing burn for minimal gain.

Enter RetCon! Simply wrap your troublesome non-distributable code in an if statement and allow RetCon to provide a flag to tell the instance whether or not it should run a given job. In this way legacy code and code that causes problems with scaling, can easily be mitigated by a "single-instance" deployment. In a partial deployment denoted by quantity, RetCon will guarantee that exactly one instance receives your modified configuration.

### Canaries
Let's say you've implemented a new feature, and you're ready to roll it out to your 100 active service instances, and said feature relies on some configuration data. With RetCon's gradual deployments you could enable this feature for a subset of the whole, and even define the rate at which the deployment happens automatically. 
For instance, with RetCon you could instantly forward this configuration change to 10 instances of 100 active instances, or 10% of 100 active instances (you can specify by quantity or by percentage). You could then define a delay with which to wait before rolling out the change to a different percentage/quantity of service instances.
Lastly you could define a target percentage with which to stop this incrementing, and then opt to convert the deployment into a simple deployment (all new instances of your service get the configuration change immediately) or not.

### A/B Testing
Same as with canaries but with more targeting!

## Current Development Efforts
- Create a simple frontend for managing the configuration server (in progress)
- Support simple deployment based canary testing
- Add a priority system so users can define which deployments should overwrite previously cached values.
- Support deploying "single instance" features
- Figure out the best way to support multiple authentication schemes
- Generate default Flyway migration scripts for more DBs than just H2 (probably need to use Java based migrations)
- Log transactions made against the RetCon server
- Create client libraries for Go, Java, JavaScript, C#, and Python (different repositories)
