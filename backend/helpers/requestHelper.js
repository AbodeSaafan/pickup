var regex = require('../api/universal_regex');
var strings = require('../api/universal_strings');

function validate(param, regexPattern, errorMessage){
	if(!regexPattern){
		throw new Error("Regex not found" + param);
	}
	if(!(param && (param = param.trim()) && regexPattern.test(param))){
		throw new Error(errorMessage);
	}
}

module.exports = {
	validateAndCleanRegisterRequest(data){
		validate(data.nickname, regex.nicknameRegex, strings.invalidNickname);
		validate(data.password, regex.passwordRegex, strings.invalidPassword);
		validate(data.fname, regex.nameRegex, strings.invalidName);
		validate(data.lname, regex.nameRegex, strings.invalidName);
		validate(data.gender, regex.genderRegex, strings.invalidGender);
		validate(data.dob, regex.dateRegex, strings.invalidDob);
		validate(data.email, regex.emailRegex, strings.invalidEmail);
		return data;
	},validateAndCleanUpdateRequest(data){
		validate(data.nickname, regex.nicknameRegex, strings.invalidNickname);
        validate(data.fname, regex.nameRegex, strings.invalidName);
		validate(data.lname, regex.nameRegex, strings.invalidName);
        validate(data.dob, regex.dateRegex, strings.invalidDob);
        return data;
    },
    validateAndCleanLoginRequest(data){
        validate(data.password, regex.passwordRegex, strings.invalidPassword);
		validate(data.email, regex.emailRegex, strings.invalidEmail);
        return data;
    },
    validateAndCleanCreateGameRequest(data){
    	validate(data.name, regex.gameNameRegex, strings.invalidGameName);
    	// more to come 
    	return data;
    },
	jsonError(Error){
		return {'error': Error.toString().substring(7)};
	}
}
