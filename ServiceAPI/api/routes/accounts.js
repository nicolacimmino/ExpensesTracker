/* accounts.js is part of ExpensesWebInterface and is responsible to
 *      provide routing for API requests to the accounts resource.
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

var express = require('express');
var router = express.Router();

/* HTTP GET /accounts/:username?auth_token=auth_token
 * Param username: the username of the user.
 * Param filter: comma separated list of wanted accounts.
 * Query param auth_token: a valid authoerization tolken
 * Returns: all accounts for the specified user.
 * Error: 401 if the auth_token doesn't authorize the operation.
 */
router.get('/:username', function(req, res) {
  
  var db = req.db;
  var accountsFilter = (req.query.filter || '').split(',');
 
  db.collection('auth_tokens').find({auth_token:req.query.auth_token} , function(e, docs) {
      if(docs.length == 1 && docs[0].username==req.params.username) {
          try {
            if(docs.length == 1 && docs[0].username==req.params.username) {
                // Expenses transactions are booked as source,destination,amount. Each account balance is the
                //  sum of the amounts where that account was the destination minus the sum of
                //  the amounts where that account was the source. We make that calculation here using
                //  MongoDB map reduction. We:
                //  1) Define a mapping function that splits each transaction into two elements the first
                //     with the source as account and -amount as value, the second with destination as
                //     account and value as value. Here we also filter only the accounts requested
                //     by the caller.
                //  2) Define a reduction function where all elements with the same account have their
                //     amounts saved.
                db.collection('transactions').mapReduce(
                  function() {                                                                          // Mapping function
                    if(accountsFilter.indexOf(this.from.toLowerCase()) > -1) { 
                      emit(this.from, -this.amount) 
                    };
                    if(accountsFilter.indexOf(this.to.toLowerCase()) > -1) { 
                      emit(this.to, this.amount); 
                    }      
                  },    
                  function(account, amounts) {                                                          // Reduction function
                    return Array.sum(amounts); 
                  },                            
                  {                                                                                     // Options
                    out : { inline : 1}, 
                    scope: { accountsFilter:accountsFilter}, 
                    query: { username:req.params.username} 
                  },        
                  function(e,docs, stats){                                                              // Callback
                    try {
                      res.send( docs ); 
                    } catch (err) {
                      res.send(501);
                    }
                  }                                          
                );
            } else {
                res.send(401);
            }
         } catch (err) {
           res.send(401);
         }
     } else {
        res.send(401);
     }
   });
});

module.exports = router;