# Amazon CloudWatch -> Rollbar

This project uses the [Serverless Framework](https://www.serverless.com/) to deploy an AWS Lambda function which
subscribes to Amazon CloudWatch log groups, consumes their log events, and continuously pushes them into a Rollbar
project.

## Get Started

You will need to add two configuration files to run this project: a _dotenv_, and a _YAML_ file.

### Environment config

First, create a _dotenv_ environment file such as `.env.{stage}` (i.e. `.env.live`) in the project directory.
Use the following configuration as a template to get you started, adding your own values where appropriate:

```dotenv
STAGE=live
LOG_LEVEL=DEBUG

ROLLBAR_ACCESS_TOKEN={POST_SERVER_ITEM_ACCESS_TOKEN}
ROLLBAR_ENVIRONMENT=production
ROLLBAR_LANGUAGE=Java
ROLLBAR_FILTER_THRESHOLD=WARNING
```

The `ROLLBAR_FILTER_THRESHOLD` variable will set a threshold to limit the number of log messages sent to Rollbar.
The threshold will send higher priority messages and is inclusive of the value. e.g. `WARNING` will send WARNING
and ERROR logs. `INFO` will send INFO, WARNING, and ERROR logs.

### Serverless config

You will then need to add a `.env-config.yml` file, to specify the CloudWatch log subscriptions to subscribe our AWS
Lambda to in the project's `serverless.yml` file.

```yaml
cloudwatchLogs:
  - cloudwatchLog: /aws/lambda/my-project-${self:provider.stage}-execute
```

## Deploy to AWS

```shell script
$ serverless deploy -v --env=live
```

### How it works

#### Determine Log Levels

Amazon CloudWatch logs are provided to AWS Lambda as strings, and so we need to parse the message to determine the
logging level, to better inform Rollbar. We analyse the first `200` characters of the log message, and look for
standalone keywords, such as `CRITICAL`, `SEVERE`, `FATAL`, and `WARN` etc. It's typical your log messages would have
these log level keywords. If we are unable to determine the log level, we default to `INFO`.