/* expenses.js is part of ExpensesWebInterface and is responsible to
 *      provide routing for API requests to the expenses resource.
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

/* HTTP GET /expenses/:username?auth_token=auth_token
 * Param username: the username of the user.
 * Query param auth_token: a valid authoerization tolken
 * Returns: all expenses for the specified user.
 * Error: 401 if the auth_token doesn't authorize the operation.
 */
router.get('/:username', function(req, res) {
  
  var db = req.db;
  db.get('auth_tokens').find({auth_token:req.query.auth_token} , function(e, docs) {
            if(docs.length == 1 && docs[0].username==req.params.username) {
                db.get('expenses').find({ username: req.params.username },{}, function(e,docs){
                    res.json( docs );
                });
            } else {
                res.send(401);
            }
        });
});

/* HTTP POST /expenses/:username?auth_token=auth_token
 * Param username: the username of the user.
 * Query param auth_token: a valid authoerization tolken
 * POST data: a json describing the expense
 * Returns: all expenses for the specified user.
 * Error: 401 if the auth_token doesn't authorize the operation.
 */
router.post('/:username', function(req, res) {
    
    var db = req.db;
    expense = req.body;
    db.get('auth_tokens').find({auth_token:req.query.auth_token} , function(e, docs) {
            if(docs.length == 1 && docs[0].username==req.params.username) {
                expense.username = req.params.username;
                db.get('expenses').insert(expense,{}, function(e,docs){
                    res.send(200);
                });
            } else {
                res.send(401);
            }
        });
 
});

/* HTTP PUT /expenses/:username/:id?auth_token=auth_token
 * Param username: the username of the user.
 * Param id: the id of the expense.
 * Query param auth_token: a valid authoerization tolken
 * POST data: a json describing the expense
 * Returns: all expenses for the specified user.
 * Error: 401 if the auth_token doesn't authorize the operation.
 */
router.put('/:username/:id', function(req, res) {
    
    var db = req.db;
    expense = req.body;
    db.get('auth_tokens').find({auth_token:req.query.auth_token} , function(e, docs) {
            if(docs.length == 1 && docs[0].username==req.params.username) {
				expense.username = req.params.username;
				
				db.get('expenses').update({'_id':req.params.id}, expense, {safe:true}, function(err, result) {
						if (err) {
							console.log('Error updating expense: ' + err);
							res.send(500);
						} else {
							console.log('' + result + ' document(s) updated');
							res.send(expense);
						}
					});
            } else {
                res.send(401);
            }
        });
});

/* HTTP DELETE /expenses/:username/:id?auth_token=auth_token
 * Param username: the username of the user.
 * Param id: the id of the expense.
 * Query param auth_token: a valid authoerization tolken
 * POST data: a json describing the expense
 * Returns: all expenses for the specified user.
 * Error: 401 if the auth_token doesn't authorize the operation.
 */
router.delete('/:username/:id', function(req, res) {
    
    var db = req.db;
    expense = req.body;
    db.get('auth_tokens').find({auth_token:req.query.auth_token} , function(e, docs) {
            if(docs.length == 1 && docs[0].username==req.params.username) {
				expense.username = req.params.username;
				
				db.get('expenses').remove({'_id':req.params.id}, {safe:true}, function(err, result) {
						if (err) {
							console.log('Error deleting expense: ' + err);
							res.send(500);
						} else {
							console.log('' + result + ' document(s) deleted');
							res.send(200);
						}
					});
            } else {
                res.send(401);
            }
        });
});


module.exports = router;
