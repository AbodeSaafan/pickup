var frisby = require("frisby");
var strings = require("../api/universal_strings");
var testHelper = require("./testHelper");

// Get admin profile properly
var userDetails = testHelper.createGenericUserMale();
frisby.create("Register a user using the API with valid credentials to use for delete account testing")
	.post(testHelper.registerEndpoint, userDetails)
	.expectStatus(200)
	.afterJSON(function (user) {
		frisby.create("Delete account")
			.delete(testHelper.deleteAccountEndpoint+"?jwt="+user.token+"&password="+userDetails.password)
			.expectStatus(200)
			.after(function() {
				frisby.create("Get admin profile of user")
					.get(testHelper.adminProfileEndpoint+"?jwt="+user.token)
					.expectStatus(400)
					.expectJSON({
						"error": strings.userIdFail
					})
					.toss();
			})
			.toss();
	})
	.toss();