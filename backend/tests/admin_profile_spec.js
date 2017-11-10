var frisby = require("frisby");
var testHelper = require("./testHelper");

// Get admin profile properly
frisby.create("Register a user using the API with valid credentials to use for profile testing")
	.post(testHelper.registerEndpoint, testHelper.createGenericUser())
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