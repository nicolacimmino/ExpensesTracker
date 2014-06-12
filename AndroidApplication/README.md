Expenses Tracker
==========

The Android application allows the user to report new expenses while on the go and to visualize a list of recent expenses that is always kept in sync with the server. Whenever an expense is added, deleted or modified on the server the list in the application reflects this immediately. 


Software Architecture
===========

Below is an oversimplified view of the architecture of the main modules of the application. Data is stored locally in a SQLite database. This allows to use the application even when there is no internet connection. The Sync Service allows to leverage Android highly optimized Sync framework that allows to keep sync operations at a minimum and to have them scheduled oppurtunistically in order to reduce power consuption. On the server side is the ReSTful HTTP service that accepts the new data.

Should the data on the server change, for instance if a user modifiyes an expense trough the Web Application, the server will send a notification to the Android terminal through Google Cloud Messaging so the application knows it has to trigger a sync in order to keep the locally cached data up to date, this will happen even if the application is not running. Additionally making use of Google Cloud Messaging means the notification is not lost even if the mobile is offline when the change in the server happens.

![Screenshot](https://raw.github.com/nicolacimmino/ExpensesTracker/master/AndroidApplication/documentation/structure.png)


GUI
==========

The application has a simple input interface that I have kept to a minimum as it will be ofter used while on the move, for instance just after purchasing something in a shop. 

![Screenshot](https://raw.github.com/nicolacimmino/ExpensesTracker/master/AndroidApplication/documentation/screenshot.png)

User can also get a list of expenses, this is a very simple list for now with no filtering nor sorting.


![Screenshot](https://raw.github.com/nicolacimmino/ExpensesTracker/master/AndroidApplication/documentation/screenshot2.png)

The application keeps data in sync with the server by means of a sync adapter. The sync service creates a new Android account type in order to authenticate the user with the server. The account can be found under Settings as other accounts are usually found in Android.
Additionally at the first start the application asks the user to authenticate with the server and creates automatically a sync account.

![Screenshot](https://raw.github.com/nicolacimmino/ExpensesTracker/master/AndroidApplication/documentation/screenshot0.png)

