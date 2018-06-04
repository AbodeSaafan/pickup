var frisby = require("frisby");
var testHelper = require("./testHelper");
var strings = require("../api/universal_strings");


describe("Admin/Private profile api testing", function () {
	it("Getting admin profile, successful path then bad token", function(doneFn) {
		return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
			.expect("status", 200)
			.then(function (userApiDetails) {
				userApiDetails = userApiDetails.json;
				return frisby.get(testHelper.adminProfileEndpoint+"?jwt="+userApiDetails.token)
					.expect("status", 200)
					.expect("bodyContains", "user_id")
					.expect("bodyContains", "username")
					.expect("bodyContains", "fname")
					.expect("bodyContains", "lname")
					.expect("bodyContains", "gender")
					.expect("bodyContains", "dob")
					.expect("bodyContains", "email");
			})
			.then(function () {
				return frisby.get(testHelper.adminProfileEndpoint+"?jwt=sabdhiadas")
					.expect("status", 400);
			}).done(doneFn);
	});
	


	it("Can update all the fields of a user's admin profile", function(doneFn) {
		return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
			.expect("status", 200)
			.then(function (userApiDetails) {
				userApiDetails = userApiDetails.json;
				return frisby.get(testHelper.adminProfileEndpoint+"?jwt="+userApiDetails.token)
					.expect("status", 200)
					.then(function() {
						return frisby.put(testHelper.adminProfileEndpoint, testHelper.createGenericUserUpdate(userApiDetails.token))
							.expect("status", 200)
							.then(function(updatedUserDetails) {
								updatedUserDetails = updatedUserDetails.json;
								return frisby.get(testHelper.adminProfileEndpoint+"?jwt="+userApiDetails.token)
									.expect("status", 200)
									.expect("jsonStrict", {
										user_id: userApiDetails.user_id,
										username: updatedUserDetails.username,
										fname: updatedUserDetails.fname,
										lname: updatedUserDetails.lname,
										gender: updatedUserDetails.gender,
										dob: updatedUserDetails.dob,
										email: updatedUserDetails.email
									}).done(doneFn);
							});
					});
			});
	});


	it("Can update the first and last name fields of a user's admin profile", function(doneFn) {
		var user = testHelper.createGenericUserMale();
		return frisby.post(testHelper.registerEndpoint, user)
			.expect("status", 200)
			.then(function (userApiDetails) {
				userApiDetails = userApiDetails.json;
				return frisby.put(testHelper.adminProfileEndpoint, testHelper.createGenericUserUpdateWithFnameLname(userApiDetails.token))
					.expect("status", 200)
					.then(function(updatedUserDetails) {
						updatedUserDetails = updatedUserDetails.json;
						return frisby.get(testHelper.adminProfileEndpoint+"?jwt="+userApiDetails.token)
							.expect("jsonStrict", {
								user_id: userApiDetails.user_id,
								username: user.username,
								fname: updatedUserDetails.fname,
								lname: updatedUserDetails.lname,
								gender: user.gender,
								dob: user.dob,
								email: user.email
							}).done(doneFn);
					});
			});
	});

	it("Can update the username and email fields of a user's admin profile", function(doneFn) {
		var user = testHelper.createGenericUserMale();
		return frisby.post(testHelper.registerEndpoint, user)
			.expect("status", 200)
			.then(function (userApiDetails) {
				userApiDetails = userApiDetails.json;
				return frisby.put(testHelper.adminProfileEndpoint, testHelper.createGenericUserUpdateWithUsernameEmail(userApiDetails.token, user.password))
					.expect("status", 200)
					.then(function(updatedUserDetails) {
						updatedUserDetails = updatedUserDetails.json;
						return frisby.get(testHelper.adminProfileEndpoint+"?jwt="+userApiDetails.token)
							.expect("status", 200)
							.expect("jsonStrict", {
								user_id: userApiDetails.user_id,
								username: updatedUserDetails.username,
								fname: user.fname,
								lname: user.lname,
								gender: user.gender,
								dob: user.dob,
								email: updatedUserDetails.email
							}).done(doneFn);
					});
			});
	});

	it("Can update the gender and dob fields of a user's admin profile", function(doneFn) {
		var user = testHelper.createGenericUserMale();
		return frisby.post(testHelper.registerEndpoint, user)
			.expect("status", 200)
			.then(function (userApiDetails) {
				userApiDetails = userApiDetails.json;
				return frisby.put(testHelper.adminProfileEndpoint, testHelper.createGenericUserUpdateWithGenderDob(userApiDetails.token))
					.expect("status", 200)
					.then(function(updatedUserDetails) {
						updatedUserDetails = updatedUserDetails.json;
						return frisby.get(testHelper.adminProfileEndpoint+"?jwt="+userApiDetails.token)
							.expect("status", 200)
							.expect("jsonStrict", {
								user_id: userApiDetails.user_id,
								username: user.username,
								fname: user.fname,
								lname: user.lname,
								gender: updatedUserDetails.gender,
								dob: updatedUserDetails.dob,
								email: user.email
							}).done(doneFn);
					});
			});
	});


	it("Updating no field of an admin user profile by passing in empty params", function(doneFn) {
		var user = testHelper.createGenericUserMale();
		return frisby.post(testHelper.registerEndpoint, user)
			.expect("status", 200)
			.then(function (userApiDetails) {
				userApiDetails = userApiDetails.json;
				return frisby.put(testHelper.adminProfileEndpoint, {
					jwt: userApiDetails.token,
					username:"",
					password:"",
					fname:"",
					lname: "",
					gender:"",
					dob:"",
					email:""
				})
					.expect("status", 200)
					.then(function() {
						return frisby.get(testHelper.adminProfileEndpoint+"?jwt="+userApiDetails.token)
							.expect("status", 200)
							.expect("jsonStrict", {
								user_id: userApiDetails.user_id,
								username: user.username,
								fname: user.fname,
								lname: user.lname,
								gender: user.gender,
								dob: user.dob,
								email: user.email
							}).done(doneFn);
					});
			});
	});


	it("Fails to update if details passed in are invalid", function(doneFn) {
		return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
			.expect("status", 200)
			.then(function (userApiDetails) {
				userApiDetails = userApiDetails.json;
				return frisby.put(testHelper.adminProfileEndpoint, {
					jwt: userApiDetails.token,
					username:"*",
					password:"",
					fname:"123",
					lname: "",
					gender:"",
					dob:"",
					email:"invalid_email"
				})
					.expect("status", 400).done(doneFn);
			});
	});


	it("Fails to update if jwt is invalid", function(doneFn) {
		return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
			.expect("status", 200)
			.then(function () {
				return frisby.put(testHelper.adminProfileEndpoint, {
					jwt: "123",
					username:"rads286",
					password:"",
					fname:"",
					lname: "",
					gender:"",
					dob:"",
					email:"rads18@gmail.com"
				})
					.expect("status", 400).done(doneFn);
			});
	});

	it("Does not allow updating DOB to an invalid date", function(doneFn) {
		var user = testHelper.createGenericUserMale();
		return frisby.post(testHelper.registerEndpoint, user)
			.expect("status", 200)
			.then(function (userApiDetails) {
				userApiDetails = userApiDetails.json;
				return frisby.put(testHelper.adminProfileEndpoint, testHelper.createGenericUserUpdateWithInvalidDob(userApiDetails.token))
					.expect("status", 400)
					.expect("jsonStrict", {
						error: strings.ageIsNotAtMinimum
					})
					.then(function () {
						return frisby.get(testHelper.adminProfileEndpoint + "?jwt=" + userApiDetails.token)
							.expect("status", 200)
							.expect("jsonStrict", {
								user_id: userApiDetails.user_id,
								username: user.username,
								fname: user.fname,
								lname: user.lname,
								gender: user.gender,
								dob: user.dob,
								email: user.email
							}).done(doneFn);
					});
			});
	});
});