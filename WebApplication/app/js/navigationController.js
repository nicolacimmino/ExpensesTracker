/* navigationController.js is part of ExpensesTrackerWebApplication SPA.
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
  controller("NavigationController", function($scope, $location, localStorageService) {
  
    // menuClass(page)
    // Returns a css class suitable for a menu element representing the supplied page.
    // This is used to highlight a menu item for the current page.
    $scope.menuClass = function(page) {
      var current = $location.path().substring(1);
      
      // Until user is logged in we hide the all "My Expenses" menu.
      if(page=="expenses" && !localStorageService.get('auth_token'))
      {
        return "hidden";
      }
      
      return page === current ? "active" : "";
    };
    
  });
  