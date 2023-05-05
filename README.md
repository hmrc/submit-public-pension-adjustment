
# Submit Public Pension Adjustment Backend

This service provides the backend for the Submit Public Pension Adjustment Frontend.

## Frontend

[Submit Public Pension Adjustment Frontend](https://github.com/hmrc/submit-public-pension-adjustment-frontend)

## Persistence
This service uses mongodb to retrieve the details of a pension adjustment calculation, as created by [Calculate Public Pension Adjustment](https://github.com/hmrc/calculate-public-pension-adjustment).

## Requirements
This service is written in Scala using the Play framework, so needs at least a JRE to run.

JRE/JDK 11 is recommended.

The service also depends on mongodb.

## Running the service
Using service manager (sm or sm2)
Use the SUBMIT_PUBLIC_PENSION_ADJUSTMENT_ALL profile to bring up all services using the latest tagged releases
```
sm2 --start SUBMIT_PUBLIC_PENSION_ADJUSTMENT_ALL
```

Run `sm2 -s` to check what services are running.

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
