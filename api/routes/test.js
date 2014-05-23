var express = require('express');
var router = express.Router();

/* GET root document. 
   This is not technically part of the API but serves a page that
   can be used to test the API from a browser.
*/
router.get('/', function(req, res) {
  res.render('test', { title: 'API Test Page' });
});

module.exports = router;
