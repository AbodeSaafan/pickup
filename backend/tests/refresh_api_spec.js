var frisby = require("frisby");
var strings = require("../api/universal_strings");
var testHelper = require("./testHelper");

// Creating a user with valid creds for testing
frisby.create("Register a user using the API with valid credentials to use for refresh testing")
	.post(testHelper.registerEndpoint, testHelper.createGenericUserMale()) 
	.expectStatus(200)
	.expectHeaderContains("content-type", "application/json")
	.expectBodyContains("token")
	.expectBodyContains("refresh")
	.afterJSON(function (user) {
		frisby.create("Get a new jwt token refresh")
			.get(testHelper.refreshEndpoint+"?jwt="+user.token+"&refresh="+user.refresh)
			.expectStatus(200)
			.expectBodyContains("token")
			.toss();
	})
	.afterJSON(function (user) {
		frisby.create("Delete refresh token")
			.delete(testHelper.refreshEndpoint, {jwt:user.token, refresh:user.refresh})
			.expectStatus(200)
			.after(function () {
				frisby.create("Delete refresh token that does not exist")
					.delete(testHelper.refreshEndpoint, {jwt: user.token, refresh:user.refresh})
					.expectStatus(400)
					.expectJSON({
						error: strings.refreshDoesNotExist
					})
					.toss();
			})
			.toss();
	})
	.toss();