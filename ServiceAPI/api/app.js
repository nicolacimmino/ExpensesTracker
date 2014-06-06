
// Mongo database connection
// We make use of monk as it provides usability
// improvemnts over mongodb.
// See https://www.npmjs.org/package/monk for 
// info and documentation.
var mongo = require('mongodb');
var monk = require('monk');
var db = monk('localhost:27017/expenses');

// We use path to build some static referneces to
// local folders below.
var path = require('path');

// We use express as web server framework.
// See https://www.npmjs.org/package/express for
// info and documentation.
var express = require('express');
var app = express();

// We use jade as a view engine. This is not central
// to the API as we only serve JSON data but is used
// for couple of pages giving human readable information
// about the API. This is mainly experimental, content
// is so static that this is really not necessary.
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');

// From here we start injecting dependencies into the 
// express application. Each module needed by expess
// is to be injected here with the "app.use()".
// NOTE: order matters, injected middleware is executed
// in the order it is provided.

// Serves the favourite icon.
var favicon = require('static-favicon');
app.use(favicon());

// Morgan is middleware that pretty prints a log
// of each HTTP request along with response status code.
// https://www.npmjs.org/package/morgan
var morgan = require('morgan');
app.use(morgan('dev'));

// Bodyparser is middleware that takes care to parse
// request bodies and make them readily available in
// the request object. We make use of "JSON".json" parser
// which will populate request.body if the request
// body MIME type is "application/json". We also use
// ".urlencoded" which will populate request.body if
// the body MIME type is "*/x-www-form-urlencoded".
// https://www.npmjs.org/package/body-parser
var bodyParser = require('body-parser');
app.use(bodyParser.json());
app.use(bodyParser.urlencoded());

// Serve static ontent such as pictures from the "public"
// folder.
app.use(express.static(path.join(__dirname, 'public')));

// cors is middleware that takes care to set the needed
// response headers to implement CORS (Cross Origin Resource Sharing).
// We need this as our API will be served by a certain domain but
// could be invoked by applications loaded from a different domain.
// https://www.npmjs.org/package/cors
var cors = require('cors')
app.use(cors());


// Since we want to share the datagbase connection instance
// with all the request handlers we add this middleware function
// that takes care to inject the db dependency into the request
// so that it's passed on to all following handlers.
app.use(function(req,res,next){
    req.db = db;
    next();
});

// We finally inject our modules.
// Note that in this case when invoking
// app.use() we pass a partial URL as first
// parameter so we tell Express which modules
// to use according to the reuested URL.
var routes = require('./routes/index');
var users = require('./routes/users');
var expenses = require('./routes/expenses');
app.use('/', routes);
app.use('/users', users);
app.use('/expenses', expenses);


// As mentioned above the middleware injected
// is executed in the order in which it's injected
// so this goes at the end of the chain and will
// set the response code to 404 (not found) as none
// of the above modules has found something to do
// with the requested URL. Note that since the
// function we inject here has 3 parameters it will
// not match errors, errors will be caught by the
// next function that has 4 parameters (Express convention).
app.use(function(req, res, next) {
    var err = new Error('Not Found');
    err.status = 404;
    // We pass (err) here instead o just using next()
    // In this way we cause the error handler to be invoked.
    next(err);
});


// And last in the chain we inject the error handler.
// This has a four parameter signature. We give a genric
// 500 (internal server error) unless a specific error
// was already set.
app.use(function(err, req, res, next) {
    res.status(err.status || 500);
    res.render('error', {
        message: err.message,
        error: {}
    });
});


module.exports = app;
