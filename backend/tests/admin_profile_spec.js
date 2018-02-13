var frisby = require("frisby");
var testHelper = require("./testHelper");


// Get admin profile properly
frisby.create("Register a user using the API with valid credentials to use for profile testing")
	.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
	.expectStatus(200)
	.afterJSON(function (body) {
		frisby.create("Get admin profile of user")
			.get(testHelper.adminProfileEndpoint+"?jwt="+body.token)
			.expectStatus(200)
			.expectBodyContains("user_id")
			.expectBodyContains("username")
			.expectBodyContains("fname")
			.expectBodyContains("lname")
			.expectBodyContains("gender")
			.expectBodyContains("dob")
			.expectBodyContains("email")
			.toss();
	})
	.afterJSON(function () {
		frisby.create("Get admin profile of user")
			.get(testHelper.adminProfileEndpoint+"?jwt=sabdhiadas")
			.expectStatus(400)
			.toss();
	})
	.toss();


//Update ALL fields of Admin User
frisby.create("Register a user using the API with valid credentials to use for profile testing")
	.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
	.expectStatus(200)
	.afterJSON(function (body) {
		frisby.create("Get admin profile of user")
			.get(testHelper.adminProfileEndpoint+"?jwt="+body.token)
			.expectStatus(200)
			.afterJSON(function() {
				frisby.create("Update admin profile of user")
					.put(testHelper.adminProfileEndpoint, testHelper.createGenericUserUpdate(body.token))
					.expectStatus(200)
					.afterJSON(function(UpdateUser) {
						frisby.create("Verify update of user details")
							.get(testHelper.adminProfileEndpoint+"?jwt="+body.token)
							.expectStatus(200)
							.expectJSON({
								user_id: body.user_id,
								username: UpdateUser.username,
								fname: UpdateUser.fname,
								lname: UpdateUser.lname,
								gender: UpdateUser.gender,
								dob: UpdateUser.dob,
								email: UpdateUser.email
							})
							.toss();
					})
					.toss();
			})
			.toss();
	})
	.toss();




//Update only first and last name field of Admin User
frisby.create("Register a user using the API with valid credentials to use for profile testing")
	.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
	.expectStatus(200)
	.afterJSON(function (body) {
		frisby.create("Update admin profile of user with firstName and lastName")
			.put(testHelper.adminProfileEndpoint, testHelper.createGenericUserUpdateWithFnameLname(body.token))
			.expectStatus(200)
			.afterJSON(function(UpdateUser) {
				frisby.create("Verify update of user details")
					.get(testHelper.adminProfileEndpoint+"?jwt="+body.token)
					.expectStatus(200)
					.expectJSON({
						user_id: body.user_id,
						username: body.username,
						fname: UpdateUser.fname,
						lname: UpdateUser.lname,
						gender: body.gender,
						dob: body.dob,
						email: body.email
					})
					.toss();
			})
			.toss();
	})
	.toss();




//Update only username and email field of Admin User
var user = testHelper.createGenericUserMale();
frisby.create("Register a user using the API with valid credentials to use for profile testing")
	.post(testHelper.registerEndpoint, user)
	.expectStatus(200)
	.afterJSON(function (body) {
		frisby.create("Update admin profile of user with username and email")
			.put(testHelper.adminProfileEndpoint, testHelper.createGenericUserUpdateWithUsernameEmail(body.token, user.password))
			.expectStatus(200)
			.afterJSON(function(UpdateUser) {
				frisby.create("Verify update of user details")
					.get(testHelper.adminProfileEndpoint+"?jwt="+body.token)
					.expectStatus(200)
					.expectJSON({
						user_id: body.user_id,
						username: UpdateUser.username,
						fname: body.fname,
						lname: body.lname,
						gender: body.gender,
						dob: body.dob,
						email: UpdateUser.email
					})
					.toss();
			})
			.toss();
	})
	.toss();


//Update only gender and dob field of Admin User

frisby.create("Register a user using the API with valid credentials to use for profile testing")
	.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
	.expectStatus(200)
	.afterJSON(function (body) {
		frisby.create("Update admin profile of user with username and email")
			.put(testHelper.adminProfileEndpoint, testHelper.createGenericUserUpdateWithGenderDob(body.token))
			.expectStatus(200)
			.afterJSON(function(UpdateUser) {
				frisby.create("Verify update of user details")
					.get(testHelper.adminProfileEndpoint+"?jwt="+body.token)
					.expectStatus(200)
					.expectJSON({
						user_id: body.user_id,
						username: body.username,
						fname: body.fname,
						lname: body.lname,
						gender: UpdateUser.gender,
						dob: UpdateUser.dob,
						email: body.email
					})
					.toss();
			})
			.toss();
	})
	.toss();


//Update no field of Admin User
frisby.create("Register a user using the API with valid credentials to use for profile testing")
	.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
	.expectStatus(200)
	.afterJSON(function (body) {
		frisby.create("Update admin profile of user with username and email")
			.put(testHelper.adminProfileEndpoint, {
				jwt: body.token,
				username:"",
				password:"",
				fname:"",
				lname: "",
				gender:"",
				dob:"",
				email:""
			})
			.expectStatus(200)
			.afterJSON(function() {
				frisby.create("Verify update of user details")
					.get(testHelper.adminProfileEndpoint+"?jwt="+body.token)
					.expectStatus(200)
					.expectJSON({
						user_id: body.user_id,
						username: body.username,
						fname: body.fname,
						lname: body.lname,
						gender: body.gender,
						dob: body.dob,
						email: body.email
					})
					.toss();
			})
			.toss();
	})
	.toss();


//Updating with invalid details

frisby.create("Register a user using the API with valid credentials to use for profile testing")
	.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
	.expectStatus(200)
	.afterJSON(function (body) {
		frisby.create("Update admin profile of user with invalid username, email and fname")
			.put(testHelper.adminProfileEndpoint, {
				jwt: body.token,
				username:"*",
				password:"",
				fname:"123",
				lname: "",
				gender:"",
				dob:"",
				email:"invalid_email"
			})
			.expectStatus(400)
			.toss();
	})
	.toss();


//Updating with invalid jwt

frisby.create("Register a user using the API with valid credentials to use for profile testing")
	.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
	.expectStatus(200)
	.afterJSON(function () {
		frisby.create("Update admin profile of user with invalid jwt")
			.put(testHelper.adminProfileEndpoint, {
				jwt: "123",
				username:"rads286",
				password:"",
				fname:"",
				lname: "",
				gender:"",
				dob:"",
				email:"rads18@gmail.com"
			})
			.expectStatus(400)
			.toss();
	})
	.toss();
