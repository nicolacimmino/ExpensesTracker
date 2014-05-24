'use strict';

/* Services */


angular.module('ExpensesWebClient.services', []).
  factory('expensesAPIservice', function($http) {

    var expensesAPIservice = {};

    expensesAPIservice.getExpenses = function() {
      return $http({
        method: 'GET', 
        url: 'http://127.0.0.1:3000/expenses/nicola?auth_token=6c527ed6fd191b9d70f53c832dc797e2b8c556c4fc99e1486fbdf60aa37fc0301da147cdddffb6e6a86c05cfdc0e8848'
      });
    }

    return expensesAPIservice;
  });