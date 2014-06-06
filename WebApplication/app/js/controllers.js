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
      pageBusy();
      expensesAPIservice.getAuthToken(username, password).
       success(function (response,status, headers, config) {
        SharedData.authToken = response.auth_token;
        $location.path('/expenses');
        pageFree();
        }).
       error(function (response,status, headers, config) { 
        pageFree();
        $('#login_password_label').toggleClass("text-danger",true);
        $('#login_username_label').toggleClass("text-danger",true);
        });
     };
     
    $scope.loginFormVisibility = function() {
    return (SharedData.authToken != "") ? "hidden" : "show";
    };
    
    $scope.logOut = function() {
      SharedData.authToken = "";
      $location.path('/');
    }
  }).
  
  controller('expensesController', function($scope, $routeParams, $location, expensesAPIservice, SharedData) {
    
  $scope.expensesList = [];
   
  $scope.edit_expense = function(expense) {
    $location.path("/expenses/"+expense._id);
  };
  
  $scope.add_expense = function() {
    pageBusy();
    $location.path("/expenses/0");
    pageFree();
   }
   
  // Asyncronously fetch expenses from the API and report them to the scope.
  if(SharedData.authToken!="") {
    pageBusy();
    expensesAPIservice.getExpenses(SharedData.authToken).success(function (response,status, headers, config) {
      $scope.expensesList = response;
      pageFree();
    });  
  } else {
    $location.path('/');
  }
  }).
  
  controller('expenseEditController', function($scope, $location, $routeParams, expensesAPIservice, SharedData) {
  
  if(SharedData.authToken!='' && $routeParams.id!="") {
    pageBusy();
    
    if($routeParams.id != 0) {
      expensesAPIservice.getExpense(SharedData.authToken,$routeParams.id).success(function (response, status, headers, config) {
        $scope.expense = response[0];
        $scope.expense.isnew = false;
        pageFree();
        });
    } else {
      $scope.expense = {};
      $scope.expense.amount = "0";
      var today = new Date();
      $scope.expense.timestamp = today.toISOString().replace('T',' ').replace('Z','');
      $scope.expense.timestamp = $scope.expense.timestamp.substr(0, $scope.expense.timestamp.indexOf('.'));
      $scope.expense.isnew = true;
      pageFree();
      
    }
    
   $scope.updateExpense = function() {
    pageBusy();
    if($scope.expense.isnew === true) {
      expensesAPIservice.createExpense(SharedData.authToken, $scope.expense).success(function (response, status, headers, config) {
        $location.path('/expenses');
        pageFree();
        })
    } else {
      expensesAPIservice.updateExpense(SharedData.authToken, $scope.expense).success(function (response, status, headers, config) {
        $location.path('/expenses');
        pageFree();
        })    
    }
   }
   
   $scope.deleteExpense = function() {
    pageBusy();
    expensesAPIservice.deleteExpense(SharedData.authToken, $scope.expense).success(function (response, status, headers, config) {
      $location.path('/expenses');
      pageFree();
      })
   }
   
   
  }
  else
  {
    $location.path('/');
  }
  }).
  
  controller("NavigationController", function($scope, $location) {
  $scope.menuClass = function(page) {
    var current = $location.path().substring(1);
    return page === current ? "active" : "";
  };
});
  
  