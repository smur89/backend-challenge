# Backend Challenge

We ask candidate for backend positions to do this challenge. It is intended to give us an impression of what the candidate is capable of, how they write and engineer code. 

Typically, after sending an application we have a phonecall with candidates after which we send them this repository. After completing the task candidates hand in their code. Our Backend team reviews the code and invites the candidate for a pairprogramming session during which we keep working on the code they handed in (refactoring, new functionality, debugging).

The challenge starts with a scala project using the play framework. It is meant to be also used by developers new to the language, we will not expect every candidate to write the most advanced scala. However a candidate should show, that they manage to deal with scala and an existing codebase.

If the candidate runs into problems or questions they can not resolve themself, we encourage them to ask us.

## How to run the project

This project is managed with [sbt](https://www.scala-sbt.org/). Once you have sbt already installed, just clone the repository, navigate to the root and run `sbt run`.

To run tests, run `sbt test`.

We recommend developing with IntelliJ IDEA, but that is left to the developers choice.

The project does not require any further infrastructure or setup and it should stay that way.

## What is in the project right now

The project contains the first REST-API-endpoints for a blog. The famous /posts example. The situation is, that a memeber of your team was supposed to built this API but got sick and you have to take over. The API shall only support JSON. The following requirements are given:

You can send requests for example with a tool like [httpie](https://httpie.org/):

- `http --verbose GET http://localhost:9000/api/v1/posts`
- `http --verbose POST http://localhost:9000/api/v1/posts title="hello" body="world" id:=1`

## The task

We need an API with which we can mange the posts for our blog. A post consist of an id (Integer), title (String) and body (String). We need CRUD operations on this resource with the following endpoints:

 - `GET     api/v1/posts      // Returns all blog posts`
 - `POST    api/v1/posts      // Create a new blog post`
 - `GET     api/v1/posts/:id  // Returns the blog post with the given id`
 - `PUT     api/v1/posts/:id  // Updates the blog post with the given id`
 - `DELETE  api/v1/posts/:id  // Deletes the blog post with the given id`

 When sending data to the api, it should be in the following format:

 ```
 {
   "id": 123,
   "title": "My Post-Title",
   "body": "My Post-Body"
 }
 ```

 When receiving data from the api, it is packed into a wrapper looking like this:
 - Returning a single entity:
 ```
 {
   "status": 200,          // Same as HTTP Status
   "data": {
     "id": 123,
     "title": "My Post-Title",
     "body": "My Post-Body"
   }
 }
 ```
 - Returning multiple entities:
 ```
 {
   "status": 200,          
   "data": [
     {"id": 123,"title":"Title 1","body":"Body1"},
     {"id": 124,"title":"Title 2","body":"Body2"},
     ...
     ]
 }
 ```
 - Returning an error:
 ```
 {
   "status": 400,
   "message": "Id is already in use"
 }
 ```

 Please implement the endpoints. The posts can be stored in the memory, so it is ok to loose them when the app goes down (no real persistence necessary).

 Once done, you may either open a PR or zip the whole project and sent it to us via email.
