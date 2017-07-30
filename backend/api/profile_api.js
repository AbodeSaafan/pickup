var express = require('express');
var router = express.Router();

router.get('/:username', function(req, res){
    // Access to parameters is done through req.query (GET and PUT)
    // Access to parameters is done through req.body (POST and DELETE)

    // res.status(X); // Response code
    // res.json(X); // Response data if any
    var temptestuser = {"username":req.params.username};
    console.log("GET /profile/"+req.params.username+" has been processed"); // Helpful log message
    res.status(200).json(temptestuser); return;
});


module.exports = router;