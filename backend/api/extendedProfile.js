var express = require('express');
var router = express.Router();
var body = require('body-parser');
var tokenHelper = require('../helpers/tokenHelper');
var requestHelper = require('../helpers/requestHelper');
var databaseHelper = require('../helpers/databaseHelper');
var md5 = require('md5');
var crypto = require('crypto');
var strings = require('./universal_strings');

// endpoint should be -> /api/extendedProfile/:user_id
//api to get extended profile

router.get('/:user_id', function (req, res) {
  var userID = req.params.user_id;
  try {
      //console.log(req.headers.token);
      tokenHelper.verifyToken(req.headers.token);
      databaseHelper.getExtendedProfile(userID, (user_id) => {
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
