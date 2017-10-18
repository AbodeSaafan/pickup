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

router.get('/', function (req, res) {

  var token = req.query.jwt;

  tokenHelper.verifyToken(token);

  try{
    var userId = tokenHelper.getUserFromToken(token).user_id;
  }
  catch(err){
    res.status(400).json(requestHelper.jsonError(err)); return;
  }

  databaseHelper.getExtendedProfile(userId, (user_id) => {
      if(user_id) {
          console.log(user_id);
          res.status(200).json(user_id);
          return;
      }else{
          res.status(400).json({'error': strings.userIdFail});
          return;
      }
    })
});

// endpoint should be -> /api/extendedProfile/:user_id?skill_level=&location=
//api to update extended profile (skill_level)
router.put('/', function (req, res) {

  var token = req.query.jwt;

  tokenHelper.verifyToken(token);

  try{
    var userId = tokenHelper.getUserFromToken(token).user_id;
  }
  catch(err){
    res.status(400).json(requestHelper.jsonError(err)); return;
  }

  var skill_level = req.query.skill_level;
  var location = req.query.location;

  databaseHelper.getExtendedProfile(userId, (user_id) => {
      if(user_id) {
          databaseHelper.updateExtendedUser(userId, skill_level, location, (update) => {
            if (update) {
              console.log(update)
              res.status(200).json(update);
            } else {
              res.status(400).json({'error': strings.userIdFail});
              return;
            }
          })
          return;
      }else{
          res.status(400).json({'error': strings.userIdFail});
          return;
      }
  })
});

module.exports = router;
