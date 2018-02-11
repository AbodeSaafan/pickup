var regex = require("../api/universal_regex");
var strings = require("../api/universal_strings");
var databaseHelper = require("../helpers/databaseHelper");
var async = require("async");

function validateAndCleanRegisterRequest(data){
	validate(data.username, regex.usernameRegex, strings.invalidUsername);
	validate(data.password, regex.passwordRegex, strings.invalidPassword);
	validate(data.fname, regex.nameRegex, strings.invalidFirstName);
	validate(data.lname, regex.nameRegex, strings.invalidLastName);
	validate(data.gender, regex.genderRegex, strings.invalidGender);
	validate(data.dob, regex.dateRegex, strings.invalidDob + " Given: " + data);
	validate(data.email, regex.emailRegex, strings.invalidEmail);
	return data;
}

function validateAndCleanUpdateAdminRequest(user_id, data){
	var user_details = {
		user_id: parseInt(user_id),
		username: null,
		password: null,
		fname: null,
		lname: null,
		gender: null,
		dob: null,
		email: null
	}
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
		validate(data.dob, regex.dateRegex, strings.invalidDob + " Given: " + data);
		user_details.dob = data.dob;
	}
	if (data.email) {
		validate(data.email, regex.emailRegex, strings.invalidEmail);
		user_details.email = data.email;
	}
	return data;
}

function validateAndCleanChangePasswordRequest(data){
	validate(data, regex.passwordRegex, strings.invalidPassword);
	return data;
}

function validateAndCleanUpdateRequest(data){
	validate(data.fname, regex.nameRegex, strings.invalidName);
	validate(data.lname, regex.nameRegex, strings.invalidName);
	validate(data.dob, regex.dateRegex, strings.invalidDob);
	return data;
}

function validateAndCleanLoginRequest(data){
	validate(data.password, regex.passwordRegex, strings.invalidPassword);
	validate(data.email, regex.emailRegex, strings.invalidEmail);
	return data;
}

function validateAndCleanCreateGameRequest(data){
	validate(data.name, regex.gameNameRegex, strings.invalidGameName);
	validate(data.type, regex.gameTypeRegex, strings.invalidGameType);
	validateSkillOffset(data.skill_offset);
	data.skill_offset = data.skill_offset - 0; // quick convert to int
	validateStartTime(data.start_time);
	data.start_time = data.start_time - 0; // quick convert to int
	validate(data.duration, regex.gameDurationRegex, strings.invalidGameDuration);
	data.duration = data.duration - 0; // quick convert to int
	validateTotalPlayersRequired(data.total_players_required);
	validate(data.gender, regex.gameGenderRegex, strings.invalidGameGenderPreference);
	data.age_range = validateAgeRange(data.age_range);
	data.location = validateLocation(data.location);
	validate(data.location_notes, regex.gameLocationNotesRegex, strings.invalidLocationNotes);
	validate(data.description, regex.gameDescriptionRegex, strings.invalidGameDescription);
	data.enforced_params = validateEnforcedParamsList(data.enforced_params);
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
	validate(data.userId, regex.idRegex, strings.invalidUserId);
	validate(data.gameId, regex.idRegex, strings.invalidGameId);
	validate(data.rating, regex.ratingRegex, strings.invalidRating);
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

function validateAndCleanUpdateExtendedProfileRequest (data) {
	data.location = validateLocation(data.location);
	validateSkill(data.skill_level);
	return data;
}

function validateAndCleanExtendedProfileRequest(data) {
	validate(data.username);
	return data;
}

function validateAndCleanFriendId (data) {
	validate(data.userId, regex.idRegex, strings.invalidUserId);
	return data;
}

function getIfReviewed(users, reviewerId, finished){
	var final_results = [];
	async.forEachOf(users, function(user, i, callback){
		databaseHelper.getIfReviewed(reviewerId, user.user_id, (reviewed)=>{
			final_results.push({"user_id": users[i].user_id, "reviewed" : reviewed});
			callback();
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
		databaseHelper.ensureGameIsJoinableByPlayer(game.game_id, user_id, (playable) => {
			if(playable){
				final_results.push(game);
			}
			callback();
		});

	}, function () {
		finished(final_results);
	});
}

function validateAndCleanDeleteAccountRequest(data){
	validate(data.password, regex.passwordRegex, strings.invalidPassword);
	return data;
}

function jsonError(Error){
	return {"error": Error.toString().substring(7)};
}

module.exports = {
	validateAndCleanRegisterRequest,
	validateAndCleanUpdateRequest,
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
	validateAndCleanChangePasswordRequest
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

function validateAgeRange(ageRange){
	ageRange = JSON.parse(ageRange);
	if (ageRange == null || ageRange.length != 2 ||
		ageRange[0] > ageRange[1]){
		throw new Error(strings.invalidGameAgeRange);
	}
	return ageRange;
}

function validateLocation(location){
	try{
		location = JSON.parse(location);
		if (location == null || location.lng == null || location.lat == null){
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
		return validateLocation(location);

	}
}

function validateEnforcedParamsList(enforcedList){
	enforcedList = JSON.parse(enforcedList);
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

	if(!(isInt(locationRange) && locationRange > 0 && locationRange < 100)){
		throw new Error(strings.invalidGameLocationRange);
	}
}

function isInt(number){
	number = number - 0;
	return (typeof number==="number" && (number%1)===0);
}

function validateTotalPlayersRequired(players){
	validate(players, regex.gameTotalPlayersRegex, strings.invalidGameTotalPlayers);
	players = players - 0; // quick convert to int
	if(players < 2 || players > 100){
		throw new Error(strings.invalidGameTotalPlayers);
	}
}
