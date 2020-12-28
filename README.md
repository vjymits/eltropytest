# eltropytest

## Known Issues or Improvements:
1. Authorization faliure error msg is not proper.
2. Authorization require config changes.
3. Single transaction force other transaction to wait, since the function synchronized.
4. Due to lack of time couldn't complete intrest calculation part.
5. For dev purpose hibernate.ddl-auto=create in application.properties. 

## example 
A pdf added as a sample of account statemnet report application generates.
URI to generate pdf: http://localhost:8080/bank/api/v1/account/21/stmt [GET]
