var frisby = require('frisby');
var request = require('request');
var strings = require('../api/universal_strings');
var testHelper = require('./testHelper');
const util = require('util');


//Send a friend request to valid User

frisby.create('Sending a Friend Request: Creating a user to send a friend request')
.post(testHelper.registerEndpoint, testHelper.createGenericUser())
.expectStatus(200)
.expectBodyContains('token')
.afterJSON(function (user) {
  frisby.create('Creating a new user to send the request to')
  .post(testHelper.registerEndpoint, testHelper.createGenericUser())
  .expectStatus(200)
  .expectBodyContains('user_id')
  .afterJSON(function (friend) {
    frisby.create("Send a friend request")
    .post(testHelper.sendfriendsEndpoint, testHelper.createGenericFriendRequest(user.token, friend.user_id))
    .expectStatus(200)
    .toss();
  })
  .toss();
})
.toss();


//Invalid Test Case - Check that if User1 sends friend invite to user2
//User2 can't send friend invite to user1

frisby.create('Invalid Test Case: Sending Bi-directional Friend Requests: Creating a user to send a friend request')
.post(testHelper.registerEndpoint, testHelper.createGenericUser())
.expectStatus(200)
.expectBodyContains('token')
.afterJSON(function (user) {
  frisby.create('Creating a new user to send the request to')
  .post(testHelper.registerEndpoint, testHelper.createGenericUser())
  .expectStatus(200)
  .expectBodyContains('user_id')
  .afterJSON(function (friend) {
    frisby.create("User1 sends a friend request to User2")
    .post(testHelper.sendfriendsEndpoint, testHelper.createGenericFriendRequest(user.token, friend.user_id))
    .expectStatus(200)
    .afterJSON(function(error) {
      frisby.create("User2 sends a friend request to User1")
      .post(testHelper.sendfriendsEndpoint, testHelper.createGenericFriendRequest(friend.token, user.user_id))
      .expectStatus(400)
      .expectJSON({
        error: strings.FriendRequestExists
      })
      .toss()
    })
    .toss();
  })
  .toss();
})
.toss();


//Accept a friend request with Valid credentials

frisby.create('Accept a Friend Request: Creating a user to send a friend request')
.post(testHelper.registerEndpoint, testHelper.createGenericUser())
.expectStatus(200)
.expectBodyContains('token')
.afterJSON(function (user) {
  frisby.create('Creating a new user to send the request to')
  .post(testHelper.registerEndpoint, testHelper.createGenericUser())
  .expectStatus(200)
  .expectBodyContains('user_id')
  .afterJSON(function (friend) {
    frisby.create("Send a friend request")
    .post(testHelper.sendfriendsEndpoint, testHelper.createGenericFriendRequest(user.token, friend.user_id))
    .expectStatus(200)
    .afterJSON(function (acceptFriend) {
      frisby.create("Accept the friend request")
      .put(testHelper.acceptFriendEndpoint+"?jwt="+friend.token+"&userID="+user.user_id)
      .expectStatus(200)
      .toss()
    })
    .toss();
  })
  .toss();
})
.toss();


//Accept a friend request when there is no friend request/no entry in DB
frisby.create('Invalid Accept Request: Creating a user to send a friend request')
.post(testHelper.registerEndpoint, testHelper.createGenericUser())
.expectStatus(200)
.expectBodyContains('token')
.afterJSON(function (user) {
  frisby.create('Creating a new user to send the request to')
  .post(testHelper.registerEndpoint, testHelper.createGenericUser())
  .expectStatus(200)
  .expectBodyContains('user_id')
  .afterJSON(function (friend) {
    frisby.create("Accept the friend request")
    .put(testHelper.acceptFriendEndpoint+"?jwt="+friend.token+"&userID="+user.user_id)
    .expectStatus(400)
    .expectJSON({
      error: strings.InvalidFriendRequest
    })
    .toss();
  })
  .toss();
})
.toss();

//Invalid Test Case
//User1 Accepts a friend request
//Which User1 has sent to User2
frisby.create('Invalid Accept Request: Creating a user to send a friend request')
.post(testHelper.registerEndpoint, testHelper.createGenericUser())
.expectStatus(200)
.expectBodyContains('token')
.afterJSON(function (user1) {
  frisby.create('Creating a new user to send the request to')
  .post(testHelper.registerEndpoint, testHelper.createGenericUser())
  .expectStatus(200)
  .expectBodyContains('user_id')
  .afterJSON(function (user2) {
    frisby.create("User1 sends a friend request to User2")
    .post(testHelper.sendfriendsEndpoint, testHelper.createGenericFriendRequest(user1.token, user2.user_id))
    .expectStatus(200)
    .afterJSON(function (acceptFriend) {
      frisby.create("User1 accept the friend request")
      .put(testHelper.acceptFriendEndpoint+"?jwt="+user1.token+"&userID="+user2.user_id)
      .expectStatus(400)
      .expectJSON({
        error: strings.InvalidFriendRequest
      })
      .toss()
    })
    .toss();
  })
  .toss();
})
.toss()


//Decline a friend request (When recieving user declines the request)
frisby.create('Decline a Friend Request: Creating a user to send a friend request')
.post(testHelper.registerEndpoint, testHelper.createGenericUser())
.expectStatus(200)
.expectBodyContains('token')
.afterJSON(function (user) {
  frisby.create('Creating a new user to send the request to')
  .post(testHelper.registerEndpoint, testHelper.createGenericUser())
  .expectStatus(200)
  .expectBodyContains('user_id')
  .afterJSON(function (friend) {
    frisby.create("Send a friend request")
    .post(testHelper.sendfriendsEndpoint, testHelper.createGenericFriendRequest(user.token, friend.user_id))
    .expectStatus(200)
    .afterJSON(function (declineFriend) {
      frisby.create("Delete the friend request")
      .delete(testHelper.deleteFriendEndpoint+"?jwt="+friend.token+"&userID="+user.user_id)
      .expectStatus(200)
      .toss()
    })
    .toss()
  })
  .toss()
})
.toss()


//Remove an Existing Friend (User1 removes User2)
frisby.create('Remove a Friend: Creating a user to send a friend request')
.post(testHelper.registerEndpoint, testHelper.createGenericUser())
.expectStatus(200)
.expectBodyContains('token')
.afterJSON(function (user1) {
  frisby.create('Creating a new user to send the request to')
  .post(testHelper.registerEndpoint, testHelper.createGenericUser())
  .expectStatus(200)
  .expectBodyContains('user_id')
  .afterJSON(function (user2) {
    frisby.create("Send a friend request")
    .post(testHelper.sendfriendsEndpoint, testHelper.createGenericFriendRequest(user1.token, user2.user_id))
    .expectStatus(200)
    .afterJSON(function (acceptFriend) {
      frisby.create("Accept the friend request")
      .put(testHelper.acceptFriendEndpoint+"?jwt="+user2.token+"&userID="+user1.user_id)
      .expectStatus(200)
      .afterJSON(function (deleteFriend) {
        frisby.create("User1 removes User2 as their friend")
        .delete(testHelper.deleteFriendEndpoint+"?jwt="+user1.token+"&userID="+user2.user_id)
        .expectStatus(200)
        .toss();
      })
      .toss()
    })
    .toss()
  })
  .toss()
})
.toss()



//List all friends for a User

frisby.create('List all friends of a user: Creating a user to send a friend request')
.post(testHelper.registerEndpoint, testHelper.createGenericUser())
.expectStatus(200)
.expectBodyContains('token')
.expectBodyContains('user_id')
.afterJSON(function (user1) {
  frisby.create('Creating a new user to send the request to')
  .post(testHelper.registerEndpoint, testHelper.createGenericUser())
  .expectStatus(200)
  .expectBodyContains('token')
  .expectBodyContains('user_id')
  .afterJSON(function (user2) {
    frisby.create('Creating another user')
    .post(testHelper.registerEndpoint, testHelper.createGenericUser())
    .expectStatus(200)
    .expectBodyContains('token')
    .expectBodyContains('user_id')
    .afterJSON(function (user3) {
      frisby.create("user1 sends a friend request to user2")
      .post(testHelper.sendfriendsEndpoint, testHelper.createGenericFriendRequest(user1.token, user2.user_id))
      .expectStatus(200)
      .afterJSON(function (sendAnotherRequest) {
        frisby.create("user3 sends a friend request to user1")
        .post(testHelper.sendfriendsEndpoint, testHelper.createGenericFriendRequest(user3.token, user1.user_id))
        .expectStatus(200)
        .afterJSON(function (acceptFriend1) {
          frisby.create("user2 accepts user1's friend request")
          .put(testHelper.acceptFriendEndpoint+"?jwt="+user2.token+"&userID="+user1.user_id)
          .expectStatus(200)
          .afterJSON(function (acceptFriend2) {
            frisby.create("user1 accepts user3's friend request")
            .put(testHelper.acceptFriendEndpoint+"?jwt="+user1.token+"&userID="+user3.user_id)
            .expectStatus(200)
            .afterJSON(function (listALlFriends) {
              frisby.create("List all friends for user1")
              .get(testHelper.listFriendsEndpoint+"?jwt="+user1.token)
              .expectStatus(200)
              .toss();
            })
            .toss();
          })
          .toss();
        })
        .toss();
      })
      .toss();
    })
    .toss();
  })
  .toss();
})
.toss();



//User2 blocks User1 upon recieving request from User1 (Success)
frisby.create('Block a Friend after Friend Request: Creating a user to send a friend request')
.post(testHelper.registerEndpoint, testHelper.createGenericUser())
.expectStatus(200)
.expectBodyContains('token')
.afterJSON(function (user1) {
  frisby.create('Creating a new user to send the request to')
  .post(testHelper.registerEndpoint, testHelper.createGenericUser())
  .expectStatus(200)
  .expectBodyContains('user_id')
  .afterJSON(function (user2) {
    frisby.create("Send a friend request")
    .post(testHelper.sendfriendsEndpoint, testHelper.createGenericFriendRequest(user1.token, user2.user_id))
    .expectStatus(200)
    .afterJSON(function (blockFriend) {
      frisby.create("User2 blocks user1 after recieving the friend request")
      .put(testHelper.blockFriendEndpoint+"?jwt="+user2.token+"&userID="+user1.user_id)
      .expectStatus(200)
      .toss()
    })
    .toss()
  })
  .toss()
})
.toss()



//User2 blocks User1 after becoming friends (Success)
frisby.create('Block an existing Friend: Creating a user to send a friend request')
.post(testHelper.registerEndpoint, testHelper.createGenericUser())
.expectStatus(200)
.expectBodyContains('token')
.afterJSON(function (user1) {
  frisby.create('Creating a new user to send the request to')
  .post(testHelper.registerEndpoint, testHelper.createGenericUser())
  .expectStatus(200)
  .expectBodyContains('user_id')
  .afterJSON(function (user2) {
    frisby.create("Send a friend request")
    .post(testHelper.sendfriendsEndpoint, testHelper.createGenericFriendRequest(user1.token, user2.user_id))
    .expectStatus(200)
    .afterJSON(function (acceptFriend) {
      frisby.create("User2 accepts User1's friend request")
      .put(testHelper.acceptFriendEndpoint+"?jwt="+user2.token+"&userID="+user1.user_id)
      .expectStatus(200)
      .afterJSON(function (blockFriend) {
        frisby.create("User1 blocks user2 after becoming friends")
        .put(testHelper.blockFriendEndpoint+"?jwt="+user1.token+"&userID="+user2.user_id)
        .expectStatus(200)
        .toss()
      })
      .toss()
    })
    .toss()
  })
  .toss()
})
.toss()



//User1 blocks User2 after user1 deletes friend request sent from User2
frisby.create('Block a user after deleting friend Request: Creating a user to send a friend request')
.post(testHelper.registerEndpoint, testHelper.createGenericUser())
.expectStatus(200)
.expectBodyContains('token')
.expectBodyContains('user_id')
.afterJSON(function (user1) {
  frisby.create('Creating a new user to send the request to')
  .post(testHelper.registerEndpoint, testHelper.createGenericUser())
  .expectStatus(200)
  .expectBodyContains('token')
  .expectBodyContains('user_id')
  .afterJSON(function (user2) {
    frisby.create("User1 sends a friend request to user2")
    .post(testHelper.sendfriendsEndpoint, testHelper.createGenericFriendRequest(user1.token, user2.user_id))
    .expectStatus(200)
    .afterJSON(function (declineFriend) {
      frisby.create("User2 deletes User1's friend request")
      .delete(testHelper.deleteFriendEndpoint+"?jwt="+user2.token+"&userID="+user1.user_id)
      .expectStatus(200)
      .afterJSON(function (blockUser) {
        frisby.create("User2 blocks User 1")
        .put(testHelper.blockFriendEndpoint+"?jwt="+user2.token+"&userID="+user1.user_id)
        .expectStatus(200)
        .toss();
      })
      .toss()
    })
    .toss()
  })
  .toss()
})
.toss()

//List all blocked friends for a User

frisby.create('List all blocked users for a user: Creating a user to send a friend request')
.post(testHelper.registerEndpoint, testHelper.createGenericUser())
.expectStatus(200)
.expectBodyContains('token')
.expectBodyContains('user_id')
.afterJSON(function (user1) {
  frisby.create('Creating a new user to send the request to')
  .post(testHelper.registerEndpoint, testHelper.createGenericUser())
  .expectStatus(200)
  .expectBodyContains('token')
  .expectBodyContains('user_id')
  .afterJSON(function (user2) {
    frisby.create('Creating another user')
    .post(testHelper.registerEndpoint, testHelper.createGenericUser())
    .expectStatus(200)
    .expectBodyContains('token')
    .expectBodyContains('user_id')
    .afterJSON(function (user3) {
      frisby.create("user2 sends a friend request to user1")
      .post(testHelper.sendfriendsEndpoint, testHelper.createGenericFriendRequest(user2.token, user1.user_id))
      .expectStatus(200)
      .afterJSON(function (sendAnotherRequest) {
        frisby.create("user3 sends a friend request to user1")
        .post(testHelper.sendfriendsEndpoint, testHelper.createGenericFriendRequest(user3.token, user1.user_id))
        .expectStatus(200)
        .afterJSON(function (acceptFriend1) {
          frisby.create("user1 accepts user2's friend request")
          .put(testHelper.acceptFriendEndpoint+"?jwt="+user1.token+"&userID="+user2.user_id)
          .expectStatus(200)
          .afterJSON(function (deleteFriend2) {
            frisby.create("user1 deletes user3's friend request")
            .delete(testHelper.deleteFriendEndpoint+"?jwt="+user1.token+"&userID="+user3.user_id)
            .expectStatus(200)
            .afterJSON(function (blockFriend1) {
              frisby.create("User1 blocks User2")
              .put(testHelper.blockFriendEndpoint+"?jwt="+user1.token+"&userID="+user2.user_id)
              .expectStatus(200)
              .afterJSON(function (blockFriend2) {
                frisby.create("User1 blocks User3")
                .put(testHelper.blockFriendEndpoint+"?jwt="+user1.token+"&userID="+user3.user_id)
                .expectStatus(200)
                .afterJSON(function (getBlockedUsers) {
                  frisby.create("List Blocked Users for User1")
                  .get(testHelper.listBlockedUsersEndpoint+"?jwt="+user1.token)
                  .expectStatus(200)
                  .toss();
                })
                .toss()
              })
              .toss();
            })
            .toss();
          })
          .toss();
        })
        .toss();
      })
      .toss();
    })
    .toss();
  })
  .toss();
})
.toss();
