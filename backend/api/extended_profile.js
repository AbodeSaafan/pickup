var express = require("express");
var router = express.Router();
var tokenHelper = require("../helpers/tokenHelper");
var requestHelper = require("../helpers/requestHelper");
var databaseHelper = require("../helpers/databaseHelper");
var strings = require("./universal_strings");

/**
* @api {get} /extendedProfile Get User's extendedProfile
* @apiName getExtendedProfile
* @apiGroup Profiles
*
* @apiDescription API used for getting a user's extended Profile.
*
* @apiParam {String} jwt The JWT you have currently
*
* @apiSuccess {String} User's extended profile entry (user_id, skilllevel, age, gender, location, average_review, top_tag)
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
*		"user_id": 164
* 		"skilllevel": 7
* 		"age": 24
*		"gender": M
*		"location": {lat: 124.32 lng: -96.23}
*		"average_review": 3.76
*		"top_tag": 4
*		}
*
* @apiSampleRequest /api/extendedProfile
*/

router.get("/", function (req, res) {

	try {
		var tok = tokenHelper.verifyToken(req.query.jwt);
		var reqUserID = req.query.userID;

		databaseHelper.getExtendedProfile(reqUserID, (ext_profile) => {
			if(ext_profile) {
				var response = {
					user_id: ext_profile.user_id,
					username: ext_profile.username,
					age: ext_profile.age,
					gender: ext_profile.gender,
					skilllevel: ext_profile.skilllevel,
					location: ext_profile.location,
					average_review: ext_profile.average_review,
					top_tag: ext_profile.top_tag,
					top_tag_count: ext_profile.top_tag_count,
					games_created: parseInt(ext_profile.games_created),
					games_joined: parseInt(ext_profile.games_joined)
				};
				console.log(response);
				res.status(200).json(response); return;
			}else{
				res.status(400).json({"error": strings.userIdFail}); return;
			}
		});
	}

	catch(err){
		res.status(400).json(requestHelper.jsonError(err)); return;
	}
});

/**
* @api {put} /extendedProfile Update User's Extended Profile
* @apiName updateExtendedProfile
* @apiGroup Profiles
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
*		}
*
* @apiSampleRequest /api/extendedProfile
*/

router.put("/", function (req, res) {
	try {
		var details = requestHelper.validateAndCleanUpdateExtendedProfileRequest(req.body);

		var tok = tokenHelper.verifyToken(req.body.jwt);

		var userId = tok.user_id;
		var skill_level = details.skill_level;
		var location = details.location;

		databaseHelper.getExtendedProfile(userId, (user_id) => {
			if(user_id) {
				databaseHelper.updateExtendedUser(userId, skill_level, location, (update) => {
					if (update) {
						var details = {
							UserID: userId,
							Users_SkillLevel: skill_level,
							Users_Location: location
						};
						res.status(200).json(details); return;
					} else {
						res.status(400).json({"error": strings.UpdateFailed}); return;
					}
				});
			} else {
				res.status(400).json({"error": strings.userIdFail}); return;
			}
		});

	} catch (err) {
		res.status(400).json(requestHelper.jsonError(err)); return;
	}
});

module.exports = router;
