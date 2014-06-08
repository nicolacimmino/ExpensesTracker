/* accessControl.js is part of ExpensesWebInterface and is responsible to
 ~      provide access control for API requests.
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

var bcrypt = require('bcrypt');
var crypto = require('crypto');


var db;

module.exports.use = function(database) {
  db=database;
}

module.exports.getAuthToken  = function (username, password, onAllowed, onDenied) { 

  db.collection('users').find({ username:username },{}, 
      function(e,docs){
        try {
          if(docs.length == 1) {
            if(bcrypt.compareSync(password, docs[0].password)) {
              crypto.randomBytes(48, function(ex, buf) {
                db.collection('auth_tokens').insert(
                    {
                    'username':username,
                    'auth_token': buf.toString('hex') 
                    },
                    function(err, doc) {
                      if(err) {
                        onDenied();
                      } else {
                        onAllowed({auth_token: buf.toString('hex')});
                      }
                    });
                });
            } else {
              onDenied();
            }
          }
        } catch (Exception) {
              onDenied();
        }
      });
};  



module.exports.authorizeCreate = function (username, auth_token, onAllowed, onDenied) {
    db.collection('auth_tokens').find({auth_token:auth_token, username:username} , function(e, docs) {
      if(docs.length === 1) {
        onAllowed();
      } else {
        onDenied();
      }
    }); 
};

module.exports.authorizeRead = function (username, auth_token, onAllowed, onDenied) {
    db.collection('auth_tokens').find({auth_token:auth_token, username:username} , function(e, docs) {
      if(docs.length === 1) {
        onAllowed();
      } else {
        onDenied();
      }
    }); 
};


module.exports.authorizeUpdate = function (username, auth_token, onAllowed, onDenied) {
    db.collection('auth_tokens').find({auth_token:auth_token, username:username} , function(e, docs) {
      if(docs.length === 1) {
        onAllowed();
      } else {
        onDenied();
      }
    }); 
};

module.exports.authorizeDelete = function (username, auth_token, onAllowed, onDenied) {
    db.collection('auth_tokens').find({auth_token:auth_token, username:username} , function(e, docs) {
      if(docs.length === 1) {
        onAllowed();
      } else {
        onDenied();
      }
    }); 
};
