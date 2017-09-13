
# Curl Tests

## Happy-day Scenarios

```
curl -H 'Accept: application/json' 127.0.0.1:8080/api/diagnostics/status -s  | python -mjson.tool
```

```
curl -u testonly:test -X POST -H 'Accept: */*' http://127.0.0.1:8080/api/health
```

## Test Custom 404 Response

Test controller advice: ``curl -H 'Accept: application/json' 127.0.0.1:8080/api/diagnostics/status2 -v`` - this should
produce custom 404 response from ErrorControllerAdvice.

## Test 401

```
curl -X POST -H 'Accept: */*' http://127.0.0.1:8080/api/health
curl -u wronguser:wrongpwd -X POST -H 'Accept: */*' http://127.0.0.1:8080/api/health
```

## Get Full Diagnostics

```
curl -H 'Accept: application/json' 127.0.0.1:8080/api/diagnostics/status?mode=full -v
```

## Test for custom "illegal argument" error

```
curl -H 'Accept: application/json' 127.0.0.1:8080/api/diagnostics/status?mode=UnknownMode -v
```

# Generate BCRYPT'ed passwords
