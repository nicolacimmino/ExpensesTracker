
API
============


**HTTP GET /users/:username**

**username**: the username of the user.
**Returns**: the specified user information.
**Error**: 404 if the specified user doesn't exist.
 

    curl -i -X POST 127.0.0.1:3000/users/nicola/auth_token --data "{\"username\":\"nicola\", \"password\":\"bla\"}" -H "Content-Type: application/json"
