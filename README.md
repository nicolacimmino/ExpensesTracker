Expenses Tracker
===============

Expenses Tracker is a demonstrative service that doesn't pretend to be a real production ready product. It is anyhow a showcase of how a Web Application and related companion mobile application can be developed in a contemporary technology stack.

Expenses Tracker allows to report expenses from an Android application and see them and edit them from a Web interface.

The diagram below depicts the main components of the system and the technologies used:

![Overall](https://raw.github.com/nicolacimmino/ExpensesTracker/master/documentation/overall.png)

The ServiceAPI is an HTTP ReSTful API and provides both user authentication and CRUD methods to interact with the expenses. Expenses data is stored in a Mongo database. 

A second server, that can be the same or even a different one, serves the Web Application pages to a browser. The Web Application accesses expense data trough the above API.

The Android application makes use of the same API to access and modify the expenses data.


