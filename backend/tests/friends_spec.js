var frisby = require("frisby");
var strings = require("../api/universal_strings");
var testHelper = require("./testHelper");

describe("Friends api testing", function () {
	it("Should allow a user to send a friend reequest to another valid user", function() {
		return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
			.expect("status", 200)
			.expect("bodyContains", "token")
			.then(function (user) {
				user = user.json;
				return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
					.expect("status", 200)
					.expect("bodyContains", "user_id")
					.then(function (friend) {
						friend = friend.json;
						return frisby.post(testHelper.sendfriendsEndpoint, testHelper.createGenericFriendRequest(user.token, friend.user_id))
							.expect("status", 200);
					});
			});
	});

	it("Should not allow user 2 to send a friend request to user 1 if user 1 has sent the request already", function() {
		return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
			.expect("status", 200)
			.expect("bodyContains", "token")
			.then(function (user) {
				user = user.json;
				return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
					.expect("status", 200)
					.expect("bodyContains", "user_id")
					.then(function (friend) {
						friend = friend.json;
						return frisby.post(testHelper.sendfriendsEndpoint, testHelper.createGenericFriendRequest(user.token, friend.user_id))
							.expect("status", 200)
							.then(function() {
								return frisby.post(testHelper.sendfriendsEndpoint, testHelper.createGenericFriendRequest(friend.token, user.user_id))
									.expect("status", 400)
									.expect("jsonStrict", {
										error: strings.FriendRequestExists
									});
							});
					});
			});
	});

	it("Should allow a user to accept a friend request they have", function() {
		return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
			.expect("status", 200)
			.expect("bodyContains", "token")
			.then(function (user) {
				user = user.json;
				return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
					.expect("status", 200)
					.expect("bodyContains", "user_id")
					.then(function (friend) {
						friend = friend.json;
						return frisby.post(testHelper.sendfriendsEndpoint, testHelper.createGenericFriendRequest(user.token, friend.user_id))
							.expect("status", 200)
							.then(function () {
								return frisby.put(testHelper.acceptFriendEndpoint+"?jwt="+friend.token+"&userId="+user.user_id)
									.expect("status", 200);
							});
					});
			});
	});

	it("Should fail when a user tries to accept a friend request that does not exist", function() {
		return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
			.expect("status", 200)
			.expect("bodyContains", "token")
			.then(function (user) {
				user = user.json;
				return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
					.expect("status", 200)
					.expect("bodyContains", "user_id")
					.then(function (friend) {
						friend = friend.json;
						return frisby.put(testHelper.acceptFriendEndpoint+"?jwt="+friend.token+"&userId="+user.user_id)
							.expect("status", 400)
							.expect("jsonStrict", {
								error: strings.InvalidFriendRequest
							});
					});
			});
	});

	it("Should not allow a user to accept a friend request on behalf of other users", function() {
		return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
			.expect("status", 200)
			.expect("bodyContains", "token")
			.then(function (user1) {
				user1 = user1.json;
				return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
					.expect("status", 200)
					.expect("bodyContains", "user_id")
					.then(function (user2) {
						user2 = user2.json;
						return frisby.post(testHelper.sendfriendsEndpoint, testHelper.createGenericFriendRequest(user1.token, user2.user_id))
							.expect("status", 200)
							.then(function () {
								return frisby.put(testHelper.acceptFriendEndpoint+"?jwt="+user1.token+"&userId="+user2.user_id)
									.expect("status", 400)
									.expect("jsonStrict", {
										error: strings.InvalidFriendRequest
									});
							});
					});
			});
	});

	it("It should allow a user to decline a valid incoming friend request", function() {
		return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
			.expect("status", 200)
			.expect("bodyContains", "token")
			.then(function (user) {
				user = user.json;
				return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
					.expect("status", 200)
					.expect("bodyContains", "user_id")
					.then(function (friend) {
						friend = friend.json;
						return frisby.post(testHelper.sendfriendsEndpoint, testHelper.createGenericFriendRequest(user.token, friend.user_id))
							.expect("status", 200)
							.then(function () {
								return frisby.del(testHelper.deleteFriendEndpoint+"?jwt="+friend.token+"&userId="+user.user_id)
									.expect("status", 200);
							});
					});
			});
	});

	it("Should allow a user to delete a friend from their friend's list", function() {
		return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
			.expect("status", 200)
			.expect("bodyContains", "token")
			.then(function (user1) {
				user1 = user1.json;
				return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
					.expect("status", 200)
					.expect("bodyContains", "user_id")
					.then(function (user2) {
						user2 = user2.json;
						return frisby.post(testHelper.sendfriendsEndpoint, testHelper.createGenericFriendRequest(user1.token, user2.user_id))
							.expect("status", 200)
							.then(function () {
								return frisby.put(testHelper.acceptFriendEndpoint+"?jwt="+user2.token+"&userId="+user1.user_id)
									.expect("status", 200)
									.then(function () {
										return frisby.del(testHelper.deleteFriendEndpoint+"?jwt="+user1.token+"&userId="+user2.user_id)
											.expect("status", 200);
									});
							});
					});
			});
	});

	it("Should allow a user to see their current friends on their friends list", function() {
		return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
			.expect("status", 200)
			.expect("bodyContains", "token")
			.expect("bodyContains", "user_id")
			.then(function (user1) {
				user1 = user1.json;
				var user2Details = testHelper.createGenericUserMale();
				return frisby.post(testHelper.registerEndpoint, user2Details)
					.expect("status", 200)
					.expect("bodyContains", "token")
					.expect("bodyContains", "user_id")
					.then(function (user2) {
						user2 = user2.json;
						var user3Details = testHelper.createGenericUserMale();
						return frisby.post(testHelper.registerEndpoint, user3Details)
							.expect("status", 200)
							.expect("bodyContains", "token")
							.expect("bodyContains", "user_id")
							.then(function (user3) {
								user3 = user3.json;
								return frisby.post(testHelper.sendfriendsEndpoint, testHelper.createGenericFriendRequest(user1.token, user2.user_id))
									.expect("status", 200)
									.then(function () {
										return frisby.post(testHelper.sendfriendsEndpoint, testHelper.createGenericFriendRequest(user3.token, user1.user_id))
											.expect("status", 200)
											.then(function () {
												return frisby.put(testHelper.acceptFriendEndpoint+"?jwt="+user2.token+"&userId="+user1.user_id)
													.expect("status", 200)
													.then(function () {
														return frisby.put(testHelper.acceptFriendEndpoint+"?jwt="+user1.token+"&userId="+user3.user_id)
															.expect("status", 200)
															.then(function () {
																return frisby.get(testHelper.listFriendsEndpoint+"?jwt="+user1.token)
																	.expect("status", 200)
																	.expect("jsonStrict", "friends.0", {
																		user_id: user2.user_id,
																		fname: user2Details.fname,
																		lname: user2Details.lname,
																	})
																	.expect("jsonStrict", "friends.1", {
																		user_id: user3.user_id,
																		fname: user3Details.fname,
																		lname: user3Details.lname,
																	});
															});
													});
											});
									});
							});
					});
			});
	});

	it("Should allow a user to block someone who they are not friends with upon recieving a request from that user", function() {
		return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
			.expect("status", 200)
			.expect("bodyContains", "token")
			.then(function (user1) {
				user1 = user1.json;
				return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
					.expect("status", 200)
					.expect("bodyContains", "user_id")
					.then(function (user2) {
						user2 = user2.json;
						return frisby.post(testHelper.sendfriendsEndpoint, testHelper.createGenericFriendRequest(user1.token, user2.user_id))
							.expect("status", 200)
							.then(function () {
								return frisby.put(testHelper.blockFriendEndpoint+"?jwt="+user2.token+"&userId="+user1.user_id)
									.expect("status", 200);
							});
					});
			});
	});

	it("Should allow a user to block someone who they are friends with", function() {
		return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
			.expect("status", 200)
			.expect("bodyContains", "token")
			.then(function (user1) {
				user1 = user1.json;
				return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
					.expect("status", 200)
					.expect("bodyContains", "user_id")
					.then(function (user2) {
						user2 = user2.json;
						return frisby.post(testHelper.sendfriendsEndpoint, testHelper.createGenericFriendRequest(user1.token, user2.user_id))
							.expect("status", 200)
							.then(function () {
								return frisby.put(testHelper.acceptFriendEndpoint+"?jwt="+user2.token+"&userId="+user1.user_id)
									.expect("status", 200)
									.then(function () {
										return frisby.put(testHelper.blockFriendEndpoint+"?jwt="+user1.token+"&userId="+user2.user_id)
											.expect("status", 200);
									});
							});
					});
			});
	});

	it("Should allow a user to block someone who they are not friends with even without a request from that user", function() {
		return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
			.expect("status", 200)
			.expect("bodyContains", "token")
			.expect("bodyContains", "user_id")
			.then(function (user1) {
				user1 = user1.json;
				return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
					.expect("status", 200)
					.expect("bodyContains", "token")
					.expect("bodyContains", "user_id")
					.then(function (user2) {
						user2 = user2.json;
						return frisby.post(testHelper.sendfriendsEndpoint, testHelper.createGenericFriendRequest(user1.token, user2.user_id))
							.expect("status", 200)
							.then(function () {
								return frisby.del(testHelper.deleteFriendEndpoint+"?jwt="+user2.token+"&userId="+user1.user_id)
									.expect("status", 200)
									.then(function () {
										return frisby.put(testHelper.blockFriendEndpoint+"?jwt="+user2.token+"&userId="+user1.user_id)
											.expect("status", 200);
									});
							});
					});
			});
	});


	it("Should be able to list all the blocked friends of a user", function() {
		return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
			.expect("status", 200)
			.expect("bodyContains", "token")
			.expect("bodyContains", "user_id")
			.then(function (user1) {
				user1 = user1.json;
				var user2Details = testHelper.createGenericUserMale();
				return frisby.post(testHelper.registerEndpoint, user2Details)
					.expect("status", 200)
					.expect("bodyContains", "token")
					.expect("bodyContains", "user_id")
					.then(function (user2) {
						user2 = user2.json;
						var user3Details = testHelper.createGenericUserMale();
						return frisby.post(testHelper.registerEndpoint, user3Details)
							.expect("status", 200)
							.expect("bodyContains", "token")
							.expect("bodyContains", "user_id")
							.then(function (user3) {
								user3 = user3.json;
								return frisby.post(testHelper.sendfriendsEndpoint, testHelper.createGenericFriendRequest(user2.token, user1.user_id))
									.expect("status", 200)
									.then(function () {
										return frisby.post(testHelper.sendfriendsEndpoint, testHelper.createGenericFriendRequest(user3.token, user1.user_id))
											.expect("status", 200)
											.then(function () {
												return frisby.put(testHelper.acceptFriendEndpoint+"?jwt="+user1.token+"&userId="+user2.user_id)
													.expect("status", 200)
													.then(function () {
														return frisby.del(testHelper.deleteFriendEndpoint+"?jwt="+user1.token+"&userId="+user3.user_id)
															.expect("status", 200)
															.then(function () {
																return frisby.put(testHelper.blockFriendEndpoint+"?jwt="+user1.token+"&userId="+user2.user_id)
																	.expect("status", 200)
																	.then(function () {
																		return frisby.put(testHelper.blockFriendEndpoint+"?jwt="+user1.token+"&userId="+user3.user_id)
																			.expect("status", 200)
																			.then(function () {
																				return frisby.get(testHelper.listBlockedUsersEndpoint+"?jwt="+user1.token)
																					.expect("status", 200)
																					.expect("jsonStrict", "blockedUsers.?", {
																						user_id: user2.user_id,
																						fname: user2Details.fname,
																						lname: user2Details.lname,
																					})
																					.expect("jsonStrict", "blockedUsers.?", {
																						user_id: user3.user_id,
																						fname: user3Details.fname,
																						lname: user3Details.lname
																					});
																			});
																	});
															});
													});
											});
									});
							});
					});
			});
	});

	it("Should list all the friend requests that a user has", function() {
		return frisby.post(testHelper.registerEndpoint, testHelper.createGenericUserMale())
			.expect("status", 200)
			.expect("bodyContains", "token")
			.expect("bodyContains", "user_id")
			.then(function (user1) {
				user1 = user1.json;
				var user2Details = testHelper.createGenericUserMale();
				return frisby.post(testHelper.registerEndpoint, user2Details)
					.expect("status", 200)
					.expect("bodyContains", "token")
					.expect("bodyContains", "user_id")
					.then(function (user2) {
						var user3Details = testHelper.createGenericUserMale();
						user2 = user2.json;
						return frisby.post(testHelper.registerEndpoint, user3Details)
							.expect("status", 200)
							.expect("bodyContains", "token")
							.expect("bodyContains", "user_id")
							.then(function (user3) {
								user3 = user3.json;
								var user4Details = testHelper.createGenericUserMale();
								return frisby.post(testHelper.registerEndpoint, user4Details)
									.expect("status", 200)
									.expect("bodyContains", "token")
									.expect("bodyContains", "user_id")
									.then(function (user4) {
										user4 = user4.json;
										return frisby.post(testHelper.sendfriendsEndpoint, testHelper.createGenericFriendRequest(user1.token, user2.user_id))
											.expect("status", 200)
											.then(function() {
												return frisby.post(testHelper.sendfriendsEndpoint, testHelper.createGenericFriendRequest(user3.token, user1.user_id))
													.expect("status", 200)
													.then(function() {
														return frisby.post(testHelper.sendfriendsEndpoint, testHelper.createGenericFriendRequest(user1.token, user4.user_id))
															.expect("status", 200)
															.then(function() {
																return frisby.get(testHelper.listFriendRequestEndpoint+"?jwt="+user1.token)
																	.expect("status", 200)
																	.expect("jsonStrict", "ByUser.?", {
																		user_id: user2.user_id,
																		fname: user2Details.fname,
																		lname: user2Details.lname,
																		status: "requested"
																	})
																	.expect("jsonStrict", "ByUser.?", {
																		user_id: user4.user_id,
																		fname: user4Details.fname,
																		lname: user4Details.lname,
																		status: "requested"
																	})
																	.expect("jsonStrict", "ForUser.?", {
																		user_id: user3.user_id,
																		fname: user3Details.fname,
																		lname: user3Details.lname,
																		status: "requested"
																	});
															});
													});
											});
									});
							});
					});
			});
	});
	
});