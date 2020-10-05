# breathing-space-if-stub

This is a stub implementation of the EIS Integration Framework backend for the Breathing Space program.

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
