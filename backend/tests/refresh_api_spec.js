var frisby = require("frisby");
var strings = require("../api/universal_strings");
var testHelper = require("./testHelper");

// Creating a user with valid creds for testing
describe("Refresh api tests", function () {
	it("Should be able to create refresh token, delete it, and only validate real tokens", function() {
		return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserMale()) 
			.expect("status", 200)
			.expect("bodyContains", "token")
			.expect("bodyContains", "refresh")
			.then(function (user) {
				user = user.json;
				return frisby.get(testHelper.refreshEndpoint+"?jwt="+user.token+"&refresh="+user.refresh)
					.expect("status", 200)
					.expect("bodyContains", "token")
					.then(function () {
						return frisby.del(testHelper.refreshEndpoint, {jwt:user.token, refresh:user.refresh})
							.expect("status", 200)
							.then(function () {
								return frisby.del(testHelper.refreshEndpoint, {jwt: user.token, refresh:user.refresh})
									.expect("status", 400)
									.expect("jsonStrict", {
										error: strings.refreshDoesNotExist
									});
							});
					});
			});
	});
});