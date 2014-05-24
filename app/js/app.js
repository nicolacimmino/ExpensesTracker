'use strict';

angular.module('ExpensesWebClient', []).config(['$httpProvider', function($httpProvider) {
        $httpProvider.defaults.useXDomain = true;
        delete $httpProvider.defaults.headers.common['X-Requested-With'];
    }
]);

angular.module('ExpensesWebClient', [
  'ExpensesWebClient.controllers',
  'ExpensesWebClient.services'
]);