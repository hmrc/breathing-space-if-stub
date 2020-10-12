# breathing-space-if-stub

This is a stub implementation of the EIS Integration Framework backend for the Breathing Space program.

## Sample Curl microservice commands

Get BS Periods

    'https://breathing-space-if-stub.protected.mdtp/breathing-space-stateless/NINO/BS000005A/periods' --header 'CorrelationId: 75e4cdc2-eecb-11ea-913a-0a9f09f70a70' --header 'OriginatorId: DS2_BS_UNATTENDED' --header 'UserId: 0000000'

Post Bs Periods

    --request POST 'https://breathing-space-if-stub.protected.mdtp/breathing-space-stateless/NINO/BS000502A/periods' --header 'CorrelationId: 75e4cdc2-eecb-11ea-913a-0a9f09f70a70' --header 'OriginatorId: DS2_BS_UNATTENDED' --header 'UserId: 0000000' --header 'Content-Type: application/json' --data-raw '{"periods":[{"startDate":"2020-05-25","pegaRequestTimestamp":"2020-12-22T14:19:03+01:00"},{"startDate":"2020-06-22","endDate":"2020-08-22","pegaRequestTimestamp":"2020-12-22T14:19:03+01:00"},{"startDate":"2020-06-22","endDate":"2020-08-22","pegaRequestTimestamp":"2020-12-22T14:19:03+01:00"}]}'

Put Bs Periods

    -request PUT 'http://localhost:9601/breathing-space-stateless/NINO/MS000001B/periods' --header 'CorrelationId: 24a8a24f-7126-4aa7-b690-259b2eaccaee' --header 'OriginatorId: DS2_BS_ATTENDED' --header 'UserId: 1234567' --header 'Content-Type: application/json' --data-raw '{"periods":[{"periodID": "4043d4b5-1f2a-4d10-8878-ef1ce9d97b32", "startDate":"2020-05-25","pegaRequestTimestamp":"2020-12-22T14:19:03+01:00"},{"periodID": "6aed4f02-f652-4bef-af14-49c79e968c2e", "startDate":"2020-06-22","endDate":"2020-08-22","pegaRequestTimestamp":"2020-12-22T14:19:03+01:00"},{"periodID": "a5a1e6f9-929b-442a-b3b1-96ce74f4372b", "startDate":"2020-06-22","endDate":"2020-08-22","pegaRequestTimestamp":"2020-12-22T14:19:03+01:00"}]}'

## Stateless Endpoints
The stateless endpoint always return the same response for the same request made.

### All Endpoint behaviour
Below is a list of special Nino values that, when passed to any of the stateless stub endpoints, will produce a special response from 
the stub (such as an error response).

Any Nino that begins with the characters "BS" and ends with the character "B" (for 'bad') will result
in the stub returning an unhappy Http response code. 

The last 3 digits of the Nino can be used to specify exactly what response code the stub should return. So
for instance:

    NINO           Http Response Code 
    ------------------------------------------------
    "BS000400B" => 400
    "BS000404B" => 404
    "BS000502B" => 502

If the last 3 digits is not within the standard range of Http error codes then a '500' code will be returned. 

### GET BS Periods Endpoint
In addition to the generic behaviour described above this particular endpoint will also    

    NINO           Response 
    ------------------------------------------------
    "BS000001A" => A single Bs Period fully populated
    "BS000002A" => A single Bs Period partially populated
    "BS000003A" => Multiple Bs Periods fully populated
    "BS000004A" => Multiple Bs Periods partially populated
    "BS000005A" => Multiple Bs Periods with mixed population
    All other Nino values will return an empty periods response

## Stateful Endpoints
TBC

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
