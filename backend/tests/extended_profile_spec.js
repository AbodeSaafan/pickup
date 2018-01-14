var frisby = require("frisby");
var strings = require("../api/universal_strings");
var testHelper = require("./testHelper");


// Creating a user with valid creds for testing. Getting the same user id of the user requesting the profile just for
// testing purposes
var newUser = testHelper.createGenericUser()
frisby.create("Register a user using the API with valid credentials to use for extendedProfile testing")
	.post(testHelper.registerEndpoint, newUser)
	.expectStatus(200)
	.expectHeaderContains("content-type", "application/json")
	.expectBodyContains("token")
	.expectBodyContains("user_id")
	.expectBodyContains("refresh")
	.afterJSON(function (body) {
		frisby.create("Get extendedProfile of user")
			.get(testHelper.extendedProfileEndpoint+"?jwt=" + body.token + "&username=" + newUser.username)
			.expectStatus(200)
			.expectJSON({
				user_id: parseInt(body.user_id),
				age: body.age,
				gender: body.gender,
				location: null,
				average_review: 0,
				top_tag: null,
				top_tag_count: null
			})
			.toss();
	})
	.toss();


// Creating a user with valid creds for testing
frisby.create("Register a user using the API with valid credentials to use for extendedProfile testing")
	.post(testHelper.registerEndpoint, testHelper.createGenericUser())
	.expectStatus(200)
	.expectHeaderContains("content-type", "application/json")
	.expectBodyContains("token")
	.expectBodyContains("user_id")
	.expectBodyContains("refresh")
	.afterJSON(function (body) {
		frisby.create("Update extendedProfile of user")
			.put(testHelper.extendedProfileEndpoint, testHelper.createGenericExtendedProfile (body.token))
			.expectStatus(200)
			.toss();
	})
	.toss();



// Get an Extended Profile after updating skill_level and location
var newUser = testHelper.createGenericUser()
frisby.create("Register a user using the API with valid credentials to use for extendedProfile testing")
	.post(testHelper.registerEndpoint, newUser)
	.expectStatus(200)
	.expectHeaderContains("content-type", "application/json")
	.expectBodyContains("token")
	.expectBodyContains("user_id")
	.expectBodyContains("refresh")
	.afterJSON(function (body) {
		frisby.create("Update extendedProfile of user")
			.put(testHelper.extendedProfileEndpoint, testHelper.createGenericExtendedProfile (body.token))
			.expectStatus(200)
			.afterJSON (function (result) {
				frisby.create("Get extendedProfile of user")
					.get(testHelper.extendedProfileEndpoint+"?jwt="+body.token + "&username=" + newUser.username)
					.expectStatus(200)
					.expectJSON({
						user_id: parseInt(body.user_id),
						age: body.age,
						gender: body.gender,
						location: "(" + result.Users_Location.lat + "," + result.Users_Location.lng + ")",
						average_review: 0,
						top_tag: null,
						top_tag_count: null
					})
					.toss();
			})
			.toss();
	})
	.toss();



// Using a bad token and a non-existing username to getExtendedProfile should fail
frisby.create("Get extendedProfile of user")
	.get(testHelper.extendedProfileEndpoint+"?jwt=**&username=DoesNotExist")
	.expectStatus(400)
	.toss();

// Using no parameter should fail getExtendedProfile
frisby.create("Get extendedProfile of user")
	.get(testHelper.extendedProfileEndpoint+"?jwt=")
	.expectStatus(400)
	.toss();

// Using a username that does not exist should fail
frisby.create("Register a user using the API with valid credentials to use for extendedProfile testing")
    .post(testHelper.registerEndpoint, testHelper.createGenericUser())
    .expectStatus(200)
    .expectHeaderContains("content-type", "application/json")
    .expectBodyContains("token")
    .expectBodyContains("user_id")
    .expectBodyContains("refresh")
    .afterJSON(function (body) {
        frisby.create("Update extendedProfile of user")
            .put(testHelper.extendedProfileEndpoint, testHelper.createGenericExtendedProfile (body.token))
            .expectStatus(200)
            .afterJSON (function (result) {
                frisby.create("Get extendedProfile of user")
                    .get(testHelper.extendedProfileEndpoint+"?jwt="+body.token + "&username=DoesNotExist")
                    .expectStatus(400)
                    .toss();
            })
            .toss();
    })
    .toss();

//invalid skill_level
frisby.create("Register a user using the API with valid credentials to use for extendedProfile testing")
	.post(testHelper.registerEndpoint, testHelper.createGenericUser())
	.expectStatus(200)
	.expectHeaderContains("content-type", "application/json")
	.expectBodyContains("token")
	.expectBodyContains("user_id")
	.expectBodyContains("refresh")
	.afterJSON(function (body) {
		frisby.create("Update extendedProfile of user with invalid skill_level")
			.put(testHelper.extendedProfileEndpoint, testHelper.createInvalidSkillLevelForExtendedProfile (body.token))
			.expectStatus(400)
			.expectJSON({
				error: strings.invalidGameSkill
			})
			.toss();
	})
	.toss();

//invalid location
frisby.create("Register a user using the API with valid credentials to use for extendedProfile testing")
	.post(testHelper.registerEndpoint, testHelper.createGenericUser())
	.expectStatus(200)
	.expectHeaderContains("content-type", "application/json")
	.expectBodyContains("token")
	.expectBodyContains("user_id")
	.expectBodyContains("refresh")
	.afterJSON(function (body) {
		frisby.create("Update extendedProfile of user with invalid skill_level")
			.put(testHelper.extendedProfileEndpoint, testHelper.createInvalidLocationForExtendedProfile (body.token))
			.expectStatus(400)
			.expectJSON({
				error: strings.invalidGameLocation
			})
			.toss();
	})
	.toss();

//invalid parameters for Update Call
frisby.create("Update extendedProfile of user with invalid skill_level")
	.put(testHelper.extendedProfileEndpoint, testHelper.createInvalidLocationForExtendedProfile (""))
	.expectStatus(400)
	.toss();
