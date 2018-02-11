var frisby = require("frisby");
var strings = require("../api/universal_strings");
var testHelper = require("./testHelper");

var genericUser = testHelper.createGenericUserMale();

// Creating a user with valid creds
frisby.create("Register a user using the API with valid credentials")
	.post(testHelper.registerEndpoint, genericUser) 
	.expectStatus(200)
	.expectHeaderContains("content-type", "application/json")
	.expectBodyContains("token")
	.expectBodyContains("user_id")
	.expectBodyContains("refresh")
	.toss();

// Sending a register request without some params
frisby.create("Attempt to register a user using the API with missing parameters")
	.post(testHelper.registerEndpoint, {
		password:"password123",
		fname:"abode",
		lname:"saafan",
		gender:"m",
		dob:"25/03/1996",
		email:"abode@mail.com"
	})
	.expectStatus(400)
	.expectHeaderContains("content-type", "application/json")
	.expectJSON({
		error: strings.invalidUsername
	})
	.toss();

// Invalid since we just created one above and we are trying to re-use it
frisby.create("Register a user using the API with an invalid email")
	.post(testHelper.registerEndpoint, genericUser)
	.expectStatus(400)
	.expectHeaderContains("content-type", "application/json")
	.expectBodyContains("error")
	.expectJSON({
		error: strings.emailError
	})
	.toss();