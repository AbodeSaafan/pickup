var frisby = require("frisby");
var strings = require("../api/universal_strings");
var testHelper = require("./testHelper");

describe("Register api testing", function ()  {
	it("Register a user using the API with valid credentials", function() {
		var genericUser = testHelper.createGenericUserMale();
		return frisby.post(testHelper.registerEndpoint, genericUser) 
			.expect("status", 200)
			.expect("bodyContains", "token")
			.expect("bodyContains", "user_id")
			.expect("bodyContains", "refresh");
	});
	

	it("Register a user using the API with invalid user due to age restriction", function() {
		return frisby.post(testHelper.registerEndpoint, testHelper.createInvalidAgeUser()) 
			.expect("status", 400)
			.expect("jsonStrict", {
				error: strings.ageIsNotAtMinimum
			});
	});
	

	it("Attempt to register a user using the API with missing parameters", function() {
		return frisby.post(testHelper.registerEndpoint, {
			password:"password123",
			fname:"abode",
			lname:"saafan",
			gender:"m",
			dob:"25/03/1996",
			email:"abode@mail.com"
		})
			.expect("status", 400)
			.expect("jsonStrict", {
				error: strings.invalidUsername
			});
	});
	
	
	it("Register a user using the API with an invalid email (already used)", function() {
		var genericUser = testHelper.createGenericUserMale();
		return frisby.post(testHelper.registerEndpoint, genericUser) 
			.expect("status", 200).then(function () {
				return frisby.post(testHelper.registerEndpoint, genericUser)
					.expect("status", 400)
					.expect("bodyContains", "error")
					.expect("jsonStrict", {
						error: strings.uniqueEmailError
					});
			});
	});
});