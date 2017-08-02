var regex = require('../api/universal_regex');
var strings = require('../api/universal_strings')

module.exports = {
	validateAndCleanRegisterRequest(data){
		if(!(data.nickname && (data.nickname = data.nickname.trim()) && regex.nicknameRegex.test(data.nickname))){
			throw new Error(strings.invalidNickname);
		}
		if(!(data.password && regex.passwordRegex.test(data.password))){
			throw new Error(strings.invalidPassword);	
		}
		if(!(data.fname && data.lname && (data.fname = data.fname.trim()) && (data.lname = data.lname.trim()) 
			&& regex.nameRegex.test(data.fname) && regex.nameRegex.test(data.lname))){
			throw new Error(strings.invalidName);
		}
		if(!(data.gender && (data.gender = data.gender.trim()) && regex.genderRegex.test(data.gender))){
			throw new Error(strings.invalidGender);	
		}
		if(!(data.dob && (data.dob = data.dob.trim()) && regex.dateRegex.test(data.dob))){
			throw new Error(strings.invalidDob);	
		}
		if(!(data.email && (data.email = data.email.trim()) && regex.emailRegex.test(data.email))){
			throw new Error(strings.invalidEmail);	
		}
		return data;
	},
	jsonError(Error){
		return {'error': Error.toString().substring(7)};
	}
}
