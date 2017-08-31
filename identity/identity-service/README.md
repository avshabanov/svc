
# Curl Tests

## Happy-day Scenarios

```
curl -H 'Accept: application/json' 127.0.0.1:8080/api/diagnostics/status -s  | python -mjson.tool
```


```
curl -X POST http://127.0.0.1:8080/api/health
```

## Test Custom 404 Response

Test controller advice: ``curl -H 'Accept: application/json' 127.0.0.1:8080/api/diagnostics/status2 -v`` - this should
produce custom 404 response from ErrorControllerAdvice.

## Get Full Diagnostics

```
curl -H 'Accept: application/json' 127.0.0.1:8080/api/diagnostics/status?mode=full -v
```

## Test for custom "illegal argument" error

```
curl -H 'Accept: application/json' 127.0.0.1:8080/api/diagnostics/status?mode=UnknownMode -v
```
