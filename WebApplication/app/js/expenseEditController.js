/* expenseEditController.js is part of ExpensesTrackerWebApplication.
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
  controller('expenseEditController', function($scope, $location, $routeParams, expensesAPIservice, localStorageService) {
  
    // Don't allow edit unless logged in and a expense Id has been set.
    if(!localStorageService.get('auth_token') || !$routeParams.id) {
      $location.path('/');
      return;
    }
    
    pageBusy();
    
    if($routeParams.id != 0) {
      // This is an existing expense, we load the expense data to allow edit.
      expensesAPIservice.getExpense(localStorageService.get('auth_token'),$routeParams.id).success(function (response, status, headers, config) {
        $scope.expense = response[0];
        $scope.expense.isnew = false;
        pageFree();
        });
    } else {
      // This is a new expense, we prepare the expense object and allow user to edit values.
      $scope.expense = {};
      $scope.expense.amount = "0";
      var today = new Date();
      $scope.expense.timestamp = today.toISOString().replace('T',' ').replace('Z','');
      $scope.expense.timestamp = $scope.expense.timestamp.substr(0, $scope.expense.timestamp.indexOf('.'));
      $scope.expense.isnew = true;
      $('#edit_form_action_button').val("Add");
      $('#edit_form_delete_button').hide();
      pageFree();
    }  

    
    // updateExpense()
    // Sends the current expense data to the API for update or new expense creation.
    $scope.updateExpense = function() {
      pageBusy();
      if($scope.expense.isnew === true) {
        expensesAPIservice.createExpense(localStorageService.get('auth_token'), $scope.expense).success(function (response, status, headers, config) {
          $location.path('/expenses');
          pageFree();
          })
      } else {
        expensesAPIservice.updateExpense(localStorageService.get('auth_token'), $scope.expense).success(function (response, status, headers, config) {
          $location.path('/expenses');
          pageFree();
          })    
      }
     };
     
     // deleteExpense()
     // Deletes the current expense from the server.
     $scope.deleteExpense = function() {
      pageBusy();
      expensesAPIservice.deleteExpense(localStorageService.get('auth_token'), $scope.expense).success(function (response, status, headers, config) {
        $location.path('/expenses');
        pageFree();
        })
     }
      
  });
  