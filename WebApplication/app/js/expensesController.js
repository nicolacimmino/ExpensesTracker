/* expensesController.js is part of ExpensesTrackerWebApplication SPA.
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

angular.module('ExpensesWebClient.controllers').
  controller('expensesController', function($scope, $routeParams, $location, expensesAPIservice, localStorageService) {
    
    $scope.expensesList = [];

    // editExpense(expense)
    // Redirects to the editing page for the given expense.
    $scope.editExpense = function(expense) {
      $location.path("/expenses/"+expense._id);
    };
  
    // addExpense()
    // Redirects to the page to edit a new expense.
    $scope.addExpense = function() {
      pageBusy();
      $location.path("/expenses/0");
      pageFree();
     }
     
    // Don't even try expenses listing if not logged in.
    if(!localStorageService.get('auth_token')) {
      $location.path('/');
      return;
    }
    
    // Get all expenses from the server.
    pageBusy();
    expensesAPIservice.getExpenses(localStorageService.get('auth_token')).success(function (response,status, headers, config) {
      $scope.expensesList = response;
      pageFree();
    });  
  
  });
