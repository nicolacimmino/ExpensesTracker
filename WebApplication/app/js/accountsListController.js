/* accountsListController.js is part of ExpensesTrackerWebApplication SPA.
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
  controller('accountsListController', function($scope, $location, expensesAPIservice, localStorageService) {
       
    // Asyncronously fetch accounts from the API and report them to the scope.
    if(localStorageService.get('auth_token')) {
      pageBusy();
      expensesAPIservice.getAccounts(localStorageService.get('auth_token')).success(function (response,status, headers, config) {
        $scope.accountsList = response;
        pageFree();
      });  
    } else {
      $location.path('/');
    }
    
  });
  
