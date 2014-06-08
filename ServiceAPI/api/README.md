
API
============


**HTTP GET /users/:username/auth_token**

**username**: the username.

**Data**: posted data is a JSON document containing user credentials.

**Returns**: an authentication token.

**Error**: 401 if the supplied credentials are invalid.

Creates an autentication token that can be used to later access other parts of the API. 

`curl -i -X POST 127.0.0.1:3000/users/guest/auth_token --data "{\"password\":\"guest\"}" -H "Content-Type: application/json"`



**HTTP GET /users/:username/auth_token**

**username**: the username of the user.

**Returns**: an authentication token.

**Error**: 401 if the supplied credentials are invalid.

    curl -i -X POST 127.0.0.1:3000/expenses/nicola?auth_token=6c527ed6fd191b9d70f53c832dc797e2b8c556c4fc99e1486fbdf60aa37fc0301da147cdddffb6e6a86c05cfdc0e8848 --data "{\"from\":\"cash\", \"to\":\"home\" , \"amount\":\"99\"}" -H "Content-Type: application/json"


    curl -i -X GET 127.0.0.1:3000/expenses/nicola?auth_token=6c527ed6fd191b9d70f53c832dc797e2b8c556c4fc99e1486fbdf60aa37fc0301da147cdddffb6e6a86c05cfdc0e8848

