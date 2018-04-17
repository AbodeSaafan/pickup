var frisby = require("frisby");
var strings = require("../api/universal_strings");
var testHelper = require("./testHelper");

var genericUser = testHelper.createGenericUserMale();

// Sending a login request with an incorrect password
frisby.create("Attempt to login a user using the wrong password")
	.post(testHelper.loginEndpoint, {
		email: genericUser.email,
		password: "wrongPassword"
	})
	.expectStatus(400)
	.expectHeaderContains("content-type", "application/json")
	.expectJSON({
		error: strings.invalidPasswordSignin
	})
	.toss();

// Sending a login request with a correct email/password combination
frisby.create("Register a user using the API with valid credentials to use for testing login")
	.post(testHelper.registerEndpoint, genericUser) 
	.expectStatus(200)
	.expectHeaderContains("content-type", "application/json")
	.expectBodyContains("token")
	.expectBodyContains("user_id")
	.expectBodyContains("refresh")
	.afterJSON(function () {
		frisby.create("Logging in using valid credentials")
			.post(testHelper.loginEndpoint, {
				email: genericUser.email,
				password: genericUser.password
			})
			.expectStatus(200)
			.expectBodyContains("token")
			.expectBodyContains("user_id")
			.expectBodyContains("token")
			.toss();
	})
	.toss();