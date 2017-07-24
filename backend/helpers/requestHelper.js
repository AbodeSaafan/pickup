var nicknameRegex = /^[a-z0-9]{4,10}$/;
var passwordRegex = /^[a-z0-9A-Z]{6,20}$/;
var nameRegex = /^[a-zA-Z]*$/;
var genderRegex = /^[m|f|o]$/;
var dateRegex = /^[0-3][0-9]\/[0-1][0-9]\/[0-9]{4}$/; // DD/MM/YY
var emailRegex = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;


module.exports = {
	validateAndCleanRegisterRequest(data){
		if(!(data.nickname && (data.nickname = data.nickname.trim()) && nicknameRegex.test(data.nickname))){
			throw new Error("Invalid nickname");
		}
		if(!(data.password && passwordRegex.test(data.password))){
			throw new Error("Invalid password");	
		}
		if(!(data.fname && data.lname && (data.fname = data.fname.trim()) && (data.lname = data.lname.trim()) && nameRegex.test(data.fname) && nameRegex.test(data.lname))){
			throw new Error("Invalid name");
		}
		if(!(data.gender && (data.gender = data.gender.trim()) && genderRegex.test(data.gender))){
			throw new Error("Invalid gender parameter");	
		}
		if(!(data.dob && (data.dob = data.dob.trim()) && dateRegex.test(data.dob))){
			throw new Error("Invalid date of birth");	
		}
		if(!(data.email && (data.email = data.email.trim()) && emailRegex.test(data.email))){
			throw new Error("Invalid email");	
		}
		return data;
	}
}
