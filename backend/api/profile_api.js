var express = require('express');
var router = express.Router();
var body = require('body-parser');
var tokenHelper = require('../helpers/tokenHelper');
var requestHelper = require('../helpers/requestHelper');
var databaseHelper = require('../helpers/databaseHelper');
var md5 = require('md5');
var crypto = require('crypto');
var strings = require('./universal_strings');

/*router.get('/:username', function(req, res){
    // Access to parameters is done through req.query (GET and PUT)
    // Access to parameters is done through req.body (POST and DELETE)

    // res.status(X); // Response code
    // res.json(X); // Response data if any
    var temptestuser = {"username":req.params.username};
    console.log("GET /profile/"+req.params.username+" has been processed"); // Helpful log message
    res.status(200).json(temptestuser); return;
});*/

// endpoint should be -> /api/profile/:user_id
//api to get user profile
router.get('/:user_id', function (req, res) {
  var userID = req.params.user_id;
  try {
      console.log(req.headers.token);
      //verifyToken(req.headers.token);
      databaseHelper.getUserRowById(userID, (user_id) => {
          if(user_id) {
              console.log(user_id);
              res.status(200).json(user_id);
              return;
          }else{
              res.status(400).json({'error': strings.userIdFail});
              return;
          }
        })
    }catch(err){
       res.status(400).json({'error': strings.invalidJwt});
       return;
    }
});

router.post('/:user_id', function (req, res) {
    var user_id = req.params.user_id;

    try {
        var user = req.body;
        user['user_id'] = user_id;
        console.log(user);
        //verifyToken(req.headers.token);
        databaseHelper.updateUser(user, (user_id) => {
            if(user_id) {
                console.log(user_id);
                res.status(200).json(user_id);
                return;
            }else{
                res.status(400).json({'error': strings.userIdFail});
        return;
    }
        });
    }catch(err){
        res.status(400).json({'error': err});
        return;
    }
});




module.exports = router;
