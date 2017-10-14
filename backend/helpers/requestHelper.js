var regex = require('../api/universal_regex');
var strings = require('../api/universal_strings');

function validateAndCleanRegisterRequest(data){
	validate(data.username, regex.usernameRegex, strings.invalidUsername);
	validate(data.password, regex.passwordRegex, strings.invalidPassword);
	validate(data.fname, regex.nameRegex, strings.invalidName);
	validate(data.lname, regex.nameRegex, strings.invalidName);
	validate(data.gender, regex.genderRegex, strings.invalidGender);
	validate(data.dob, regex.dateRegex, strings.invalidDob);
	validate(data.email, regex.emailRegex, strings.invalidEmail);
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
	validate(data.total_players_required, regex.gameTotalPlayersRegex, strings.invalidGameTotalPlayers);
	data.total_players_required = data.total_players_required - 0; // quick convert to int
	validate(data.gender, regex.gameGenderRegex, strings.invalidGameGenderPreference);
	validateAgeRange(data.age_range);
	validateLocation(data.location);
	validate(data.location_notes, regex.gameLocationNotesRegex, strings.invalidLocationNotes);
	validate(data.description, regex.gameDescriptionRegex, strings.invalidGameDescription);
	validateEnforcedParamsList(data.enforced_params);
    return data;
}

function validateAndCleanJoinRequest(data){
    validate(data.params.game_id, regex.idRegex);
    return data;
}

function validateAndCleanSearchRequest(data){
	validate(data.search_object, regex.searchObjectRegex, strings.invalidSearchObject);
	data.results_max = validateMaxResults(data.results_max);
	if(data.search_object == 'game'){
		// Game param validation
		validate(data.game_id, regex.idRegex, strings.invalidGameId);
		validate(data.game_name, regex.gameNameRegex, strings.invalidGameName);
		validate(data.game_type, regex.gameTypeRegex, strings.invalidGameType);
		data.game_skill_min = game_skill_min - 0; // quick convert to int
		data.game_skill_max = game_skill_max - 0; // quick convert to int
		validateSkillLevel(data.game_skill_min);
		validateSkillLevel(data.game_skill_max);
		data.game_total_players = data.game_total_players - 0; // quick convert to int
		validate(data.game_total_players, regex.gameTotalPlayersRegex, strings.invalidGameTotalPlayers);
		data.game_start_time = data.game_start_time - 0; // quick convert to int
		validateStartTime(data.game_start_time);
		data.game_duration = data.game_duration - 0; // quick convert to int
		validate(data.game_duration, regex.gameDurationRegex, strings.invalidGameDuration);
		validateLocation(data.game_location);
		data.game_location_range = data.game_location_range - 0;
		validateLocationRange(data.game_location_range);
	}
	else if(data.search_object == 'user'){
		// User param validation
		validate(data.username, regex.usernameRegex, strings.invalidUsername);
	}
	return data;
}

function jsonError(Error){
    return {'error': Error.toString().substring(7)};
}

module.exports = {
    validateAndCleanRegisterRequest,
    validateAndCleanUpdateRequest,
    validateAndCleanLoginRequest,
    validateAndCleanCreateGameRequest,
    validateAndCleanJoinRequest,
    validateAndCleanSearchRequest,
    jsonError,
}

//////////////// Helpers ////////////////

function validate(param, regexPattern, errorMessage){
	if(!regexPattern){
		throw new Error("Regex not found for input " + param);
	}
	if(!(param && (param = param.trim()) && regexPattern.test(param.trim()))){
		throw new Error(errorMessage);
	}
}

function validateStartTime(startTime){
	if (startTime == null || startTime < (Date.now / 1000)){
		throw new Error(strings.invalidGameStartTime)
	}
}

function validateAgeRange(ageRange){
	if (ageRange == null || ageRange.length != 2 || 
		ageRange[0] > ageRange[1]){
		throw new Error(strings.invalidGameAgeRange);
	}
}

function validateLocation(location){
	if (location == null || location.lng == null || location.lat == null){
		throw new Error(strings.invalidGameLocation);
	}
}

function validateEnforcedParamsList(enforcedList){
	if (enforcedList != null){
		for (let param of enforcedList){
			if (!(regex.gameEnforcedParamRegex.test(param))){
				throw new Error(strings.invalidEnforcedParamList);
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

function validateMaxResults(maxResult){
	if(maxResult && isInt(maxResult) && maxResult > 0 && maxResult < 100){
		return maxResult;
	}
	return 20;
}

function validateLocationRange(locationRange){
	if(!(locationRange && isInt(locationRange) && locationRange > 0 && location < 100)){
		throw new Error(strings.invalidGameLocationRange);
	}
}

function isInt(number){
	number = number - 0;
	return (typeof number==='number' && (number%1)===0);
}