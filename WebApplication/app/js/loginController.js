/* controllers.js is part of ExpensesTrackerWebApplication SPA contains the controllers.
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
  controller('loginController', function($scope, $location, expensesAPIservice, localStorageService) {
  
    // login(username, password)
    // Attempts authentication with the API and stores
    //  in the local storage the authentication token on success.
    $scope.login = function (username, password) {
      pageBusy();
      localStorageService.set('username', username);
      
      expensesAPIservice.getAuthToken(username, password).
        success(function (response,status, headers, config) {
          localStorageService.set('auth_token', response.auth_token);
          $location.path('/expenses');
          pageFree();
        }).
        error(function (response,status, headers, config) { 
          pageFree();
          $('#login_password_label').toggleClass("text-danger",true);
          $('#login_username_label').toggleClass("text-danger",true);
        });
    };
   
    // logout()
    // Invalidates the current user session.
    // NOTE: this only destroys the token locally, the API doesn't have at
    //  the moment a call to destroy the token on the server.
    $scope.logOut = function() {
      localStorageService.set('username', '');
      localStorageService.set('auth_token', '');
      $location.path('/');
    }
   
    // loginFormVisibility()
    // Returns css class suitable for the login form give the current application status
    // so that login forms are hidden when not needed.
    $scope.loginFormVisibility = function() {
      return (localStorageService.get('auth_token')) ? "hidden" : "show";
    };
     
  });
  
  
  

  
  
