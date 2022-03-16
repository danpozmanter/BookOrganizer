# BookOrganizer

A **simple** reference example Kotlin GraphQL service, written for colleagues coming over from Python & FastAPI/Flask +
Ariadne/Graphene.

* Async GraphQL built on [Netflix DGS](https://netflix.github.io/dgs/) and [Spring](https://spring.io).
* Annotation based queries (crud) and dynamically generated execution spec based queries (custom) for comparison.
* Simple example mutation.
* A few example tests with and without mocking.
* Gradle test configuration with property based filtering and custom test logging.

## Notes

In practice, you'll be using the approach outlined under `crud` far more often.
I've included a more dynamic version under `custom`, using simple logic to construct a query
from variable inputs. (Primarily to provide a simple example for reference that
is written in Kotlin and is async).

There are multiple ways to handle mutations via Netflix DGS, this is just one simple example.

Gradle is a powerful tool. In the `build.gradle.kts` file, you'll find one way of many to separate tests,
and an optional approach in Gradle's Kotlin DSL to custom output. (In this case, showing the number of tests that
passed, failed, and were skipped).

I'm using Postgresql locally via Docker - this will need to be running in order to successfully run the example app, and to
run any integration tests.

This is based off a personal project to help someone search their library of 2000+ books.

## Guide

### Database

Local Postgresql instance via Docker:

The `sh` files in `database/` use the local environment variables:
* DEV_PG_USER
* DEV_PG_PASSWORD
* DEV_PG_PORT

and the `postgres` database (default).

```bash
# Initial setup:
./setup.sh
# Run container
./run.sh
# Stop
./stop.sh
```

### Run

`gradle bootRun`

### URLs

GraphiQL playground: http://localhost:8080/graphiql/index.html

GraphQL endpoint: http://localhost:8080/graphql/

### Testing

Run non integration tests only (default):

`gradle test`

Run integration tests only:

`gradle test -Pintegration`

Run all tests:

`gradle test -Pall`

To see more info, including standard output, pass in `-i` or `--info`.

### More info

Spring helpfully includes a `HELP.md` in new projects created via [Start.Spring](https://start.spring.io/)