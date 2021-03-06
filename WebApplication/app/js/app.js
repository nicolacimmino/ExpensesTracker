/* app.js is part of ExpensesWebClient SPA provides application basic config.
 *
 *   Copyright (C) 2014 Nicola Cimmino
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 */
'use strict';

// Create modules and inject needed dependencies.
angular.module('ExpensesWebClient.expensesAPIservice', ['LocalStorageModule']);
angular.module('ExpensesWebClient.controllers', []);

// Inject dependencies to controllers and services
//  into the ExpensesWebClient module.
angular.module('ExpensesWebClient', [
  'ExpensesWebClient.controllers',
  'ExpensesWebClient.expensesAPIservice',
  'ngRoute',
  'LocalStorageModule'
  ]).
config(['$routeProvider', function($routeProvider) {
  $routeProvider.
  when("/home", {templateUrl: "partials/home.html", controller: "loginController"}).
  when("/expenses", {templateUrl: "partials/expensesList.html", controller: "expensesController"}).
  when("/expenses/:id", {templateUrl: "partials/expenseEdit.html", controller: "expenseEditController"}).
  when("/mobiles", {templateUrl: "partials/mobilesList.html", controller: "mobilesListController"}).
  when("/accounts", {templateUrl: "partials/accountsList.html", controller: "accountsListController"}).
  when("/about", {templateUrl: "partials/about.html"}).
  when("/contact", {templateUrl: "partials/contact.html"}).
  otherwise({redirectTo: '/home'});
}]);
