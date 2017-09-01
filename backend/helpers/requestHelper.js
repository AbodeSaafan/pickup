var regex = require('../api/universal_regex');
var strings = require('../api/universal_strings');

function validateAndCleanRegisterRequest(data){
	validate(data.nickname, regex.nicknameRegex, strings.invalidNickname);
	validate(data.password, regex.passwordRegex, strings.invalidPassword);
	validate(data.fname, regex.nameRegex, strings.invalidName);
	validate(data.lname, regex.nameRegex, strings.invalidName);
	validate(data.gender, regex.genderRegex, strings.invalidGender);
	validate(data.dob, regex.dateRegex, strings.invalidDob);
	validate(data.email, regex.emailRegex, strings.invalidEmail);
	return data;
}

function validateAndCleanUpdateRequest(data){
	validate(data.nickname, regex.nicknameRegex, strings.invalidNickname);
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
	validate(data.skill, regex.gameSkillRegex, strings.invalidGameSkill);
	validateStartTime(data.start_time);
	validate(data.duration, regex.gameDurationRegex, strings.invalidGameDuration);
	validate(data.total_players_required, regex.gameTotalPlayersRegex, strings.invalidGameTotalPlayers);
	validate(data.gender, regex.gameGenderRegex, strings.invalidGameGenderPreference);
	validateAgeRange(data.age_range);
	validateLocation(data.location);
	validate(data.location_notes, regex.gameLocationRegex, strings.invalidLocationNotes);
	validate(data.description, regex.gameDescriptionRegex, strings.invalidGameDescription);
	validateEnforcedParamsList(data.enforced_params);
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
    jsonError,
}

//////////////// Helpers ////////////////

function validate(param, regexPattern, errorMessage){
	if(!regexPattern){
		throw new Error("Regex not found" + param);
	}
	if(!(param && (param = param.trim()) && regexPattern.test(param))){
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