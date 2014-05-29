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

// Define the expensesController which depends on expensesAPIservice.
// This controller is responsible to provide expenses data to the scope.
angular.module('ExpensesWebClient.controllers', []).
 
  controller('loginController', function($scope, $location, expensesAPIservice, SharedData) {
       
  // Asyncronously fetch the auth token from the API and report it to the scope.
    $scope.login = function (username, password) {
    expensesAPIservice.getAuthToken(username, password).success(function (response,status, headers, config) {
        SharedData.authToken = response.auth_token;
    $location.path('/expenses');
    });
     };
    
  }).
  
  controller('expensesController', function($scope, $routeParams, $location, expensesAPIservice, SharedData) {
    
  $scope.expensesList = [];
   
  $scope.edit_expense = function(expense) {
    $location.path("/expenses/"+expense._id);
  };
  
  // Asyncronously fetch expenses from the API and report them to the scope.
  if(SharedData.authToken!="") {
    expensesAPIservice.getExpenses(SharedData.authToken).success(function (response,status, headers, config) {
      $scope.expensesList = response;
    });  
  } else {
    $location.path('/');
  }
  }).
  
  controller('expenseEditController', function($scope, $location, $routeParams, expensesAPIservice, SharedData) {
  
  if(SharedData.authToken!='' && $routeParams.id!="") {
    expensesAPIservice.getExpense(SharedData.authToken,$routeParams.id).success(function (response, status, headers, config) {
      $scope.expense = response[0];
      });
      
   $scope.updateExpense = function() {
    expensesAPIservice.updateExpense(SharedData.authToken, $scope.expense).success(function (response, status, headers, config) {
      $location.path('/expenses');
      })
   }
  }
  else
  {
    $location.path('/');
  }
  });
  
  