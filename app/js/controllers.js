'use strict';

/* Controllers */

  
angular.module('ExpensesWebClient.controllers', []).
  controller('expensesController', function($scope, expensesAPIservice) {
    $scope.nameFilter = null;
    $scope.expensesList = [];

    expensesAPIservice.getExpenses().success(function (response,status, headers, config) {
        $scope.expensesList = response;
    });
  });