var frisby = require("frisby");
var strings = require("../api/universal_strings");
var testHelper = require("./testHelper");


describe("Delete account api tests", function () {
	it("Can delete a user account and make that user unable to login", function() {
		var userDetails = testHelper.createGenericUserMale();
		return frisby.post(testHelper.registerEndpoint, userDetails)
			.expect("status", 200)
			.then(function (user) {
				user = user.json;
				return frisby.del(testHelper.deleteAccountEndpoint+"?jwt="+user.token+"&password="+userDetails.password)
					.expect("status", 200)
					.then(function() {
						return frisby.get(testHelper.adminProfileEndpoint+"?jwt="+user.token)
							.expect("status", 400)
							.expect("jsonStrict", {
								"error": strings.userIdFail
							});
					});
			});
	});
});