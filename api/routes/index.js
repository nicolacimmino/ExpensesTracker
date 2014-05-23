var express = require('express');
var router = express.Router();

/* GET root document. 
   This is not technically part of the API but can be used as
   an info page with some information on the API meant for human
   readers.
*/
router.get('/', function(req, res) {
  res.render('index', { title: 'Expenses Web Service API' });
});

module.exports = router;
