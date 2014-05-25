ExpensesWebService
==================

Web service for the Expense Reporter application and for the WEB Client.

This is a ReSTful API to access expense data. The service is built in node.js using Express, Jade, Monk and backed by a MongoDB instance.




API
============


**HTTP GET /users/:username**

**username**: the username of the user.

**Returns**: the specified user information.

**Error**: 404 if the specified user doesn't exist.
 

    curl -i -X POST 127.0.0.1:3000/users/nicola/auth_token --data "{\"password\":\"bla\"}" -H "Content-Type: application/json"

    
    curl -i -X POST 127.0.0.1:3000/expenses/nicola?auth_token=6c527ed6fd191b9d70f53c832dc797e2b8c556c4fc99e1486fbdf60aa37fc0301da147cdddffb6e6a86c05cfdc0e8848 --data "{\"from\":\"cash\", \"to\":\"home\" , \"amount\":\"99\"}" -H "Content-Type: application/json"

    curl -i -X GET 127.0.0.1:3000/expenses/nicola?auth_token=6c527ed6fd191b9d70f53c832dc797e2b8c556c4fc99e1486fbdf60aa37fc0301da147cdddffb6e6a86c05cfdc0e8848
