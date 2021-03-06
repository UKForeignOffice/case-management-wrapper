# Case Management API

API used to expose a consistent and open API for creating cases in a 
backing case management system.

## Case Management System
The current Case Management System this API integrates to is CASEBOOK.

## Design
Request processing is mediated by a message queue to mitigate downstream
errors, for example availability, of Case Management System.

## Configuration

Configuration for each component can be found in the relevant `application.yml`. Some of 
these properties can be overridden at deployment time using environment variables.  

Environment variable | Default | Description
--- | --- | ---
ROOT_LOGGING_LEVEL | `info` | Root logging level
MESSAGE_QUEUE_ENDPOINT | http://localhost:9324 | SQS endpoint
MESSAGE_QUEUE_URL | http://localhost:9324/queue/default | SQS message queue
CASEBOOK_URL | | URL of CASEBOOK service
CASEBOOK_KEY | | Secret key for CASEBOOK authentication
CASEBOOK_CLIENT_CERTIFICATE | | Client certificate for signing requests to CASEBOOK
CASEBOOK_CLIENT_KEY | | Client key for signing requests to CASEBOOK
