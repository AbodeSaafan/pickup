var regex = require("../api/universal_regex");
var strings = require("../api/universal_strings");
var databaseHelper = require("../helpers/databaseHelper");
var async = require("async");
var crypto = require("crypto");

function validateAndCleanRegisterRequest(data){
	validate(data.username, regex.usernameRegex, strings.invalidUsername);
	validate(data.password, regex.passwordRegex, strings.invalidPassword);
	validate(data.fname, regex.nameRegex, strings.invalidFirstName);
	validate(data.lname, regex.nameRegex, strings.invalidLastName);
	validate(data.gender, regex.genderRegex, strings.invalidGender);
	validateAgeOverMinimum(data.dob);
	validate(data.email, regex.emailRegex, strings.invalidEmail);
	return data;
}

function validateAndCleanUpdateAdminRequest(user_id, data){
	
	var user_details = {user_id: parseInt(user_id)};

	if (data.username) {
		validate(data.username, regex.usernameRegex, strings.invalidUsername);
		user_details.username = data.username;
	}
	if (data.fname) {
		validate(data.fname, regex.nameRegex, strings.invalidFirstName);
		user_details.fname = data.fname;
	}
	if (data.lname) {
		validate(data.lname, regex.nameRegex, strings.invalidLastName);
		user_details.lname = data.lname;
	}
	if (data.gender) {
		validate(data.gender, regex.genderRegex, strings.invalidGender);
		user_details.gender = data.gender;
	}
	if (data.dob) {
		validateAgeOverMinimum(data.dob);
		user_details.dob = data.dob;
	}
	if (data.email && data.password) {
		validate(data.email, regex.emailRegex, strings.invalidEmail);
		validate(data.password, regex.passwordRegex, strings.invalidPassword);
		user_details.email = data.email;
		user_details.password = data.password;
	}

	return user_details;
}

function validateAndCleanChangePasswordRequest(data){
	validate(data.old_password, regex.passwordRegex, strings.invalidPassword);
	validate(data.new_password, regex.passwordRegex, strings.invalidPassword);
	return data;
}

function validateAndCleanLoginRequest(data){
	validate(data.password, regex.passwordRegex, strings.invalidPasswordSignIn);
	validate(data.email, regex.emailRegex, strings.invalidUsernameSignIn);
	return data;
}

function validateAndCleanCreateGameRequest(data){
	validate(data.name, regex.gameNameRegex, strings.invalidGameName);
	validate(data.type, regex.gameTypeRegex, strings.invalidGameType);
	if(data.skill_offset && data.type.toLowerCase == "serious"){
		validateSkillOffset(data.skill_offset);
		data.skill_offset = data.skill_offset - 0; // quick convert to int
	} else {
		data.skill_offset = 0;
	}
	validateStartTime(data.start_time);
	data.start_time = data.start_time - 0; // quick convert to int
	validateNumber(data.duration, strings.invalidGameDuration, 0, Number.MAX_SAFE_INTEGER);
	validateNumber(data.total_players_required, strings.invalidGameTotalPlayers, 2, 100);
	data.location = validateLocation(data.location);
	validate(data.location_notes, regex.gameLocationNotesRegex, strings.invalidLocationNotes);
	validate(data.description, regex.gameDescriptionRegex, strings.invalidGameDescription);
	data.enforced_params = data.enforced_params ? validateEnforcedParamsList(data.enforced_params) : [];
	return data;
}

function validateAndCleanJoinRequest(data){
	validate(data.game_id, regex.idRegex, strings.invalidGameId);
	return data;
}

function validateAndCleanLeaveRequest(data){
	validate(data.game_id, regex.idRegex);
	return data;
}

function validateAndCleanReviewRequest(data){
	validateNumber(data.userId, strings.invalidUserId, 1, Number.MAX_SAFE_INTEGER);
	validateNumber(data.gameId, strings.invalidGameId, 1, Number.MAX_SAFE_INTEGER);
	validateNumber(data.rating, strings.invalidRating, 1, 10);
	validateRatings(data.tags);
	data.reviewed = data.reviewed == "true";
	return data;
}


function validateAndCleanSearchRequest(data){
	validate(data.search_object, regex.searchObjectRegex, strings.invalidSearchObject);
	data.results_max = validateMaxResults(data.results_max);
	if(data.search_object == "game"){
		// Game param validation
		searchValidate(data.game_id, regex.idRegex, strings.invalidGameId, data, "game_id");
		data.game_id = data.game_id - 0; //quick convert to int
		searchValidate(data.game_name, regex.gameNameRegex, strings.invalidGameName, data, "game_name");
		searchValidate(data.game_type, regex.gameTypeRegex, strings.invalidGameType, data, "game_type");
		searchValidateSkillLevel(data.game_skill_min, data, "game_skill_min");
		searchValidateSkillLevel(data.game_skill_max, data, "game_skill_max");
		data.game_skill_min = data.game_skill_min - 0; // quick convert to int
		data.game_skill_max = data.game_skill_max - 0; // quick convert to int
		searchValidate(data.game_total_players, regex.gameTotalPlayersRegex, strings.invalidGameTotalPlayers, data, "game_total_players");
		data.game_total_players = data.game_total_players - 0; // quick convert to int
		searchValidateStartTime(data.game_start_time, data, "game_start_time");
		data.game_start_time = data.game_start_time - 0; // quick convert to int
		searchValidateEndTime(data.game_end_time, data, "game_end_time");
		data.game_end_time = data.game_end_time - 0; // quick convert to int
		searchValidate(data.game_duration, regex.gameDurationRegex, strings.invalidGameDuration, data, "game_duration");
		data.game_duration = data.game_duration - 0; // quick convert to int
		data.game_location = searchValidateLocation(data.game_location, data, "game_location");
		searchValidateLocationRange(data.game_location_range, data, "game_location_range");
		data.game_location_range = data.game_location_range - 0;
	}
	else if(data.search_object == "user"){
		// User param validation
		validate(data.username, regex.usernameRegex, strings.invalidUsername);
	}
	return data;
}

function validateAndCleanUpdateExtendedProfileRequest (user_id, data) {
	var user_details = {user_id: parseInt(user_id)};
	if (data.skill_level) {
		validateSkill(data.skill_level);
		user_details.skill_level = data.skill_level;
	}
	if (data.location) {
		var location = validateLocation(data.location);
		user_details.location = "(" + location.lat + "," + location.lng + ")";
	}
	return user_details;
}

function validateAndCleanExtendedProfileRequest(data) {
	validate(data.username);
	return data;
}

function validateAndCleanFriendId (data) {
	// Sometimes we get strings here from PUT calls, so we convert it to an int first
	data.userId = parseInt(data.userId);
	validateNumber(data.userId, strings.invalidUserId, 1, Number.MAX_SAFE_INTEGER);
	return data;
}

function getIfReviewed(users, reviewerId, finished){
	var final_results = [];
	async.forEachOf(users, function(user, i, callback){
		databaseHelper.getIfReviewed(reviewerId, user.user_id, (reviewed)=>{
			databaseHelper.getUsernameById(user.user_id, (user) =>{
				final_results.push({"user_id": users[i].user_id, "fname": user.fname ,"username": user.username, "reviewed" : reviewed});
				callback();
			});
		});
	}, function () {
		finished(final_results);
	});
}

function addTag(reviewId, tags, finished) {
	var final_results = [];
	async.forEachOf(tags, function (tag, i, callback) {
		databaseHelper.addTag(reviewId, tag, (tagAdded) => {
			if (!tagAdded) {
				final_results.push("1");
			}
			callback();
		});
	}, function () {
		finished(final_results.length > 0);
	});
}

function updateTag(reviewId, tags, finished) {
	databaseHelper.deleteTag(reviewId, (deleteComplete) => {
		if (deleteComplete) {
			var final_results = [];
			async.forEachOf(tags, function (tag, i, callback) {
				databaseHelper.addTag(reviewId, tag, (tagAdded) => {
					if (!tagAdded) {
						final_results.push("1");
					}
					callback();
				});
			}, function () {
				finished(final_results.length > 0);
			});
		}
		else {
			finished(true);
		}
	});
}

function filterGames(games, user_id, finished) {
	var final_results = [];
	async.forEachOf(games, function (game, i, callback) {
		game.game_id = parseInt(game.game_id);
		databaseHelper.ensureGameIsJoinableByPlayer(game.game_id, user_id, (playable) => {
			game.player_restricted = !playable;
			// Make sure enforced params is an array 
			if (game.enforced_params && game.enforced_params.trim() != "" && game.enforced_params.trim() != "{}"){
				game.enforced_params = convertEnforcedParam(game.enforced_params);
			} else {
				game.enforced_params = undefined;
			}
			final_results.push(game);
			// This changes x and y to lat and lng
			game.location.lat = game.location.x; 
			game.location.lng = game.location.y;
			game.location.x = undefined;
			game.location.y = undefined;
			callback();
		});

	}, function () {
		finished(final_results);
	});
}

function convertEnforcedParam(stringValue){
	return stringValue.replace("{", "").replace("}", "").split(",");
}

function validateAndCleanDeleteAccountRequest(data){
	validate(data.password, regex.passwordRegex, strings.invalidPassword);
	return data;
}

function jsonError(Error){
	return {"error": Error.toString().substring(7)};
}

function generateSalt(){
	return crypto.randomBytes(40).toString("hex");
}

module.exports = {
	validateAndCleanRegisterRequest,
	validateAndCleanLoginRequest,
	validateAndCleanCreateGameRequest,
	validateAndCleanJoinRequest,
	validateAndCleanSearchRequest,
	validateAndCleanReviewRequest,
	validateAndCleanLeaveRequest,
	validateAndCleanExtendedProfileRequest,
	validateAndCleanUpdateExtendedProfileRequest,
	validateAndCleanFriendId,
	filterGames,
	validateAndCleanDeleteAccountRequest,
	jsonError,
	getIfReviewed,
	addTag,
	updateTag,
	validateAndCleanUpdateAdminRequest,
	validateAndCleanChangePasswordRequest,
	generateSalt
};

//////////////// Helpers ////////////////

function validate(param, regexPattern, errorMessage){
	if(!regexPattern){
		throw new Error("Regex not found for input " + param);
	}
	if(!(param && (param = param.trim()) && regexPattern.test(param.trim()))){
		throw new Error(errorMessage);
	}
}

function validateNumber(param, errorMessage, lowerBound, upperBound){
	if(!(param && typeof param==="number" && param >= lowerBound && param <= upperBound)){
		throw new Error(errorMessage);
	}
}

function searchValidate(param, regexPattern, errorMessage, obj, objParamString){
	if(!(param && param.trim())){
		delete obj[objParamString]; return; // Clear non-applicable term
	} else {
		validate(param, regexPattern, errorMessage);
	}
}

function validateStartTime(startTime){
	if (startTime == null || startTime < (Date.now / 1000)){
		throw new Error(strings.invalidGameStartTime);
	}
}

function searchValidateStartTime(startTime, obj, objParamString){
	if(!(startTime && startTime.trim())){
		delete obj[objParamString]; return;
	} else {
		validateStartTime(startTime);
	}
}

function searchValidateEndTime(endTime, obj, objParamString){
	if(!(endTime && endTime.trim())){
		delete obj[objParamString]; return;
	} else {
		validateStartTime(endTime);
	}
}

function validateLocation(location){
	try{
		if (location == null || location.lng == null || location.lat == null
			|| typeof location.lat !== "number" || typeof location.lng !== "number"
			|| location.lat > 90 || location.lat < -90
			|| location.lng > 180 || location.lng < -180
		){
			throw new Error(strings.invalidGameLocation);
		}
		return location;
	}
	catch(Exception){
		throw new Error(strings.invalidGameLocation);
	}
}

function searchValidateLocation(location, obj, objParamString){
	if(!location || location == ""){
		delete obj[objParamString]; return;
	} else {
		return validateLocation(JSON.parse(location));

	}
}

function validateEnforcedParamsList(enforcedList){
	if (enforcedList != null){
		for (let param of enforcedList){
			if (!(regex.gameEnforcedParamRegex.test(param))){
				throw new Error(strings.invalidEnforcedParamList);
			}
		}
		return enforcedList;
	}
}

function validateRatings(ratings){
	if (ratings != null){
		for (let param of ratings){
			if (!(regex.ratingRegex.test(param))){
				throw new Error(strings.invalidRating);
			}
		}
	}
}

function validateSkillOffset(skill){
	if(!(skill && isInt(skill) && skill >= 0 && skill <=10)){
		throw new Error(strings.invalidGameSkillOffset);
	}
}

function validateSkill(skill){
	if(!(skill && isInt(skill) && skill >= 0 && skill <=10)){
		throw new Error(strings.invalidGameSkill);
	}
}

function searchValidateSkillLevel(skill, obj, objParamString){
	if(!(skill && skill.trim())){
		delete obj[objParamString]; return;
	} else {
		validateSkill(skill);
	}
}

function validateMaxResults(maxResult){
	if(maxResult && isInt(maxResult) && maxResult > 0 && maxResult < 100){
		return maxResult;
	}
	return 20;
}

function searchValidateLocationRange(locationRange, obj, objParamString){
	if(!locationRange){
		delete obj[objParamString]; return;
	}

	if(!(isInt(locationRange) && locationRange > 0 && locationRange <= 200)){
		throw new Error(strings.invalidGameLocationRange);
	}
}

function isInt(number){
	number = number - 0;
	return (typeof number==="number" && (number%1)===0);
}

// https://stackoverflow.com/questions/4060004/calculate-age-given-the-birth-date-in-the-format-yyyymmdd
function validateAgeOverMinimum(dateString){
	validate(dateString, regex.dateRegex, strings.invalidDob);	
	var birthday = new Date(dateString);
	var ageDate = new Date(Date.now() - birthday.getTime()); // miliseconds from epoch
	var age =  Math.abs(ageDate.getUTCFullYear() - 1970);
	if(age < 18){
		throw new Error(strings.ageIsNotAtMinimum);
	}
}
