# Case Management API

API used to expose a consistent and open API for creating cases in a 
backing case management system.

## Case Management System
The current Case Management System this API integrates to is CASEBOOK.

## Design
Request processing is mediated by a message queue to mitigate downstream
errors, for example availability, of Case Management System.

Current implementation is using AWS SQS directly. If ever  

