/* controllers.js is part of ExpensesWebClient SPA contains the controllers.
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

// Add a factory to encapsulate the Expenses API.
// Note that the API has CORS enabled so we don't need to use
//  JSOND on the client side to circumvent the same origin policy. 
//
angular.module('ExpensesWebClient.services', []).
  factory('expensesAPIservice', function($http) {

    var expensesAPIservice = {};

    // Get all expenses.
    // Currently there is no pagination in the API, this will change.
      expensesAPIservice.getExpenses = function(authToken) {
        return $http({
          method: 'GET', 
          url: 'http://expensesapi.nicolacimmino.com/expenses/nicola?auth_token=' + authToken
        });
      }

    // Get an authentication token.
    expensesAPIservice.getAuthToken = function(username, password) {
        return $http({
          method: 'POST', 
          url: 'http://expensesapi.nicolacimmino.com/users/'+username+'/auth_token',
      data: {
        password:password
      }
        });
      }

    expensesAPIservice.getExpense = function(authToken, id) {
        return $http({
          method: 'GET', 
          url: 'http://expensesapi.nicolacimmino.com/expenses/nicola/'+id+'?auth_token=' + authToken
        });
    }
    
    // Update an expense.
    expensesAPIservice.updateExpense = function(authToken,expense) {
        return $http({
          method: 'PUT', 
          url: 'http://expensesapi.nicolacimmino.com/expenses/nicola/'+expense._id+'?auth_token=' + authToken,
          data: expense
        });
      }
      
     // Careate an expense.
    expensesAPIservice.createExpense = function(authToken,expense) {
        return $http({
          method: 'POST', 
          url: 'http://expensesapi.nicolacimmino.com/expenses/nicola/?auth_token=' + authToken,
          data: expense
        });
      }
      
    // Delete an expense.
    expensesAPIservice.deleteExpense = function(authToken,expense) {
        return $http({
          method: 'DELETE', 
          url: 'http://expensesapi.nicolacimmino.com/expenses/nicola/'+expense._id+'?auth_token=' + authToken
        });
      }  
    return expensesAPIservice;
  });