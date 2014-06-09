API
==================

This API allows to access expense data on the Expense Tracker service. This is a ReSTful API to access expense data. The service is built in node.js using Express, Jade, MongoJS and backed by a MongoDB instance. Jade has really margin user here and has been thrown in mainly to experiment with it, it's only used to serve a human readable page should someone browse to the API root domain.

The table below is a summary of the API resources and operations, for full documentation of the API and curl test vectors see the wiki (<https://github.com/nicolacimmino/ExpensesTracker/wiki/API>).

|Resource|Method|Description|
|--------|------|-----------|
|users/:userid/auth_token| POST | Creates an authorization token for the supplied credentials.|
|expenses/:userid/| GET | Gets all the expenses for the supplied user.|
|expenses/:userid/| POST | Creates a new expense for the supplied user.|
|expenses/:userid/:expenseID| GET | Gets all the information for the supplied expense.|
|expenses/:userid/:expenseID| PUT | Updates the information for the supplied expense.|
|expenses/:userid/:expenseID| DELETE | Deletes the supplied expense.|
|accounts/:userid| GET | Gets information about the supplied user accouunts.|




