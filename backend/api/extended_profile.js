var express = require('express');
var router = express.Router();
var body = require('body-parser');
var tokenHelper = require('../helpers/tokenHelper');
var requestHelper = require('../helpers/requestHelper');
var databaseHelper = require('../helpers/databaseHelper');
var md5 = require('md5');
var crypto = require('crypto');
var strings = require('./universal_strings');

// endpoint should be -> /api/extendedProfile
//api to get extended profile


/**
* @api {get} /extendedProfile Get User's extendedProfile
* @apiName getExtendedProfile
* @apiGroup ExtendedProfile
*
* @apiDescription API used for getting a user's extended Profile.
*
* @apiParam {String} jwt The JWT you have currently
*
* @apiSuccess {String} User's extendedProfile entry (userId, skill_level, age, gender, location)
*
* @apiError error An error is given for invalid user_ID
*
* @apiExample Example call:
*     {
*       "jwt": Encrypted_JWT_Token,
*     }
*
* @apiSuccessExample Success-Response:
*      HTTP/1.1 200 OK
*		{
*		  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImFkc3NhZGFAbWFpbC5jb20iLCJpYXQiOjE1MDUxNTc2NjQsImV4cCI6MTUwNTE1ODU2NH0.HmhW4y-AZ1D5rMHbQ8RY0eBIGfo-8Lb_sFL1FrruFoc"
*		}
*
* @apiSampleRequest /api/extendedProfile
*/

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

/**
* @api {put} /refresh Update User's Extended Profile
* @apiName updateExtendedUser
* @apiGroup ExtendedProfile
*
* @apiDescription API used for updating user's extended profile.
*
* @apiParam {String} jwt The JWT you have currently
* @apiParam {String} skill_level The skill level of the user
* @apiParam {String} location The location of the user
*
* @apiSuccess {String} token A JWT token that can be used to authenticate futher API calls
*
* @apiError error The error field has a string with an exact error
*
* @apiExample Example call:
*     {
*       "jwt": Encrypted_JWT_Token,
        "skill_level": 3,
        "location": Mississauga
*     }
*
* @apiSuccessExample Success-Response:
*      HTTP/1.1 200 OK
*		{
*		  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImFkc3NhZGFAbWFpbC5jb20iLCJpYXQiOjE1MDUxNTc2NjQsImV4cCI6MTUwNTE1ODU2NH0.HmhW4y-AZ1D5rMHbQ8RY0eBIGfo-8Lb_sFL1FrruFoc"
*		}
*
* @apiSampleRequest /api/extendedProfile
*/

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
              console.log()
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
