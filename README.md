
# Submit Public Pension Adjustment Backend

This service provides the backend for the Submit Public Pension Adjustment Frontend.

## Frontend

[Submit Public Pension Adjustment Frontend](https://github.com/hmrc/submit-public-pension-adjustment-frontend)

## Requirements
This service is written in Scala using the Play framework, so needs at least a JRE to run.

JRE/JDK 11 is recommended.

The service also depends on mongodb.

## Running the service
Using service manager (sm or sm2)
Use the PUBLIC_PENSION_ADJUSTMENT_ALL profile to bring up all services using the latest tagged releases
```
sm2 --start PUBLIC_PENSION_ADJUSTMENT_ALL
```

Run `sm2 -s` to check what services are running

## Launching the service locally
To bring up the service on the configured port 12803, use

```
sbt run
```

## Testing the service
This service uses sbt-scoverage to provide test coverage reports.

## Scalafmt
To prevent formatting failures in a GitHub pull request,
run the command ``sbt scalafmtAll`` before pushing to the remote repository.

### License
This code is open source software licensed under the [Apache 2.0 License]("https://www.apache.org/licenses/LICENSE-2.0.html").
