
API
============


**HTTP GET /users/:username/auth_token**

**username**: the username.

**Data**: posted data is a JSON document containing user credentials.

**Returns**: an authentication token.

**Error**: 401 if the supplied credentials are invalid.

Creates an autentication token that can be used to later access other parts of the API. 

`
curl -i -X POST 127.0.0.1:3000/users/guest/auth_token --data "{\"password\":\"guest\"}" -H "Content-Type: application/json"
`

Response:

`
HTTP/1.1 200 OK
Date: Sun, 08 Jun 2014 18:39:01 GMT
X-Powered-By: Express
Access-Control-Allow-Origin: *
Content-Type: application/json
Content-Length: 113

{"auth_token":"0d6f66b1f132271ddad0d71218eb365e679ee8afa6e781f8c7a89dc1607c4d0db
021add5670b240aaa1c2eae9f22f18f"}
`

**HTTP GET /users/:username/auth_token**

**username**: the username of the user.

**Returns**: an authentication token.

**Error**: 401 if the supplied credentials are invalid.

    curl -i -X POST 127.0.0.1:3000/expenses/nicola?auth_token=6c527ed6fd191b9d70f53c832dc797e2b8c556c4fc99e1486fbdf60aa37fc0301da147cdddffb6e6a86c05cfdc0e8848 --data "{\"from\":\"cash\", \"to\":\"home\" , \"amount\":\"99\"}" -H "Content-Type: application/json"


    curl -i -X GET 127.0.0.1:3000/expenses/nicola?auth_token=6c527ed6fd191b9d70f53c832dc797e2b8c556c4fc99e1486fbdf60aa37fc0301da147cdddffb6e6a86c05cfdc0e8848

