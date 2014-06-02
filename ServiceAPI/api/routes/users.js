/* users.js is part of ExpensesWebInterface and is responsible to
 ~      provide routing for API requests to the users resource.
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
var bcrypt = require('bcrypt');
var crypto = require('crypto');

/* GET users listing. 
 * We do not allow users listing. Deny.
 */
router.get('/', function(req, res) {
  res.json({"error":"Not allowed"});
});

/* HTTP GET /users/:username
 * Param username: the username of the user.
 * Returns: the specified user information.
 * Error: 404 if the specified user doesn't exist.
 */
router.get('/:username', function(req, res) {
  
  var db = req.db;
    db.get('users').find({ username: req.params.username },{}, function(e,docs){
        if(docs.length == 1) {
            res.json( { "username": docs[0].username } );
        } else {
            res.send(404);
        }
    });
    
});


/* HTTP POST /users/:username/auth_token
 * POST data: username and password
 * Param username: the username id of the user.
 * Returns: an authentication token.
 * Error: 404 if the specified user doesn't exist.
 * Error: 401 if the suppliced username/password don't match.
 */
router.post('/:username/auth_token', function(req, res) {
  
    var db = req.db;
    db.get('users').find({ username: req.params.username },{}, function(e,docs){
        if(docs.length == 1) {
            // While we don't have the API to register a new user log the expected
            //    salted and hashed password so we can store it in db and login.
            //console.log(bcrypt.hashSync("bla", 10));
            if(bcrypt.compareSync(req.body.password, docs[0].password)) {
                    crypto.randomBytes(48, function(ex, buf) {
                    db.get('auth_tokens').insert({
                                'username':req.params.username,
                                'auth_token': buf.toString('hex') 
                                },
                                function(err, doc) {
                                    if(err) {
                                        res.send(500);
                                    } else {
                                        res.json( { "auth_token": buf.toString('hex') } );
                                    }
                                });
                });
            } else {
                res.send(401);
            }
        } else {
            res.send(404);
        }
    });
    
});

module.exports = router;
