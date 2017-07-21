var usernameRegex = /^[a-z0-9]{4,10}$/;
var passwordRegex = /^[a-z0-9A-Z]{6,20}$/;
var nameRegex = /^[a-zA-Z]*$/;
var genderRegex = /^[m|f|o]$/;
var dateRegex = /^[0-3][0-9]\/[0-1][0-9]\/[0-9]{4}$/; // DD/MM/YY
var emailRegex = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;


module.exports = {
	validateRegisterRequest(data){
		if(!(data.username && usernameRegex.test(data.username))){
			console.log(data.username);
			throw new Error("Invalid username");
		}
		if(!(data.password && passwordRegex.test(data.password))){
			throw new Error("Invalid password");	
		}
		if(!(data.fname && data.lname && nameRegex.test(data.fname) && nameRegex.test(data.lname))){
			throw new Error("Invalid name");
		}
		if(!(data.gender && genderRegex.test(data.gender))){
			throw new Error("Invalid gender parameter");	
		}
		if(!(data.dob && dateRegex.test(data.dob))){
			throw new Error("Invalid date of birth");	
		}
		if(!(data.email && emailRegex.test(data.email))){
			throw new Error("Invalid email");	
		}
		return true;
	}
}
