# eltropytest

## Known Issues or Improvements:
1. Authorization failure msg is not proper.
2. Authorization require config changes.
3. Single transaction force other transactions to wait since the function synchronized.
4. Due to lack of time couldn't complete the interest calculation part.
5. For dev purpose hibernate.ddl-auto=create in application.properties. 

## example 
A pdf is added as a sample of the account statement report which application generates.
URI to generate pdf: http://localhost:8080/bank/api/v1/account/21/stmt [GET]
