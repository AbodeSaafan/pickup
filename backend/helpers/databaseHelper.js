var pg = require("pg");
// eslint-disable-next-line no-unused-vars
var env = process.env.NODE_ENV || "dev";
const conString = process.env.DATABASE_URL ? process.env.DATABASE_URL : "postgres://postgres:123@localhost:5432/pickup";
var crypto = require("crypto");
var md5 = require("md5");
const util = require("util");
var tokenHelper = require("./tokenHelper");

function checkEmailUniqueness(user, callback) {
	var queryString = "SELECT * FROM users WHERE email = '" + user.email + "' AND disabled = false;";

	const pool = new pg.Pool({ connectionString: conString });

	pool.connect((err, client, done) => {
		client.query(queryString, (err, res) => {
			callback(!(res.rows[0] || err));
			done();
			pool.end();
		});
	});
}


function checkUsernameUniqueness(user, callback) {
	var queryString = "SELECT * FROM users WHERE username = $1;";
	var queryParams = [user.username];

	const pool = new pg.Pool({ connectionString: conString });

	pool.connect((err, client, done) => {
		client.query(queryString, queryParams, (err, res) => {
			callback(!(res.rows[0] || err));
			done();
			pool.end();
		});
	});
}

function registerUser(user, callback) {
	var queryString = "INSERT INTO users(username, fname, lname, dob, gender, email, password, salt) VALUES($1, $2, $3, $4, $5, $6, $7, $8)";
	var queryParams = [user.username, user.fname, user.lname, user.dob, user.gender, user.email, user.hashedPassword, user.salt];

	const pool = new pg.Pool({ connectionString: conString });

	pool.connect((err, client, done) => {
		client.query(queryString, queryParams, (err, res) => {
			callback(!err && res);
			done();
			pool.end();
		});
	});
}

function getUserId(email, callback) {
	var queryString = "SELECT user_id FROM users WHERE email =  $1;";
	var queryParams = [email];

	const pool = new pg.Pool({ connectionString: conString });

	pool.connect((err, client, done) => {
		client.query(queryString, queryParams, (err, res) => {
			if (!err && res.rows[0].user_id) {
				callback(parseInt(res.rows[0].user_id));
			} else {
				callback(false);
			}
			done();
			pool.end();
		});
	});
}

function getUserRowById(userId, callback) {
	var queryString = "SELECT user_id, username, fname, lname, dob, gender, email FROM users WHERE user_id = $1 AND disabled = false";
	var queryParams = [userId];

	const pool = new pg.Pool({ connectionString: conString });

	pool.connect((err, client, done) => {
		client.query(queryString, queryParams, (err, res) => {
			callback(!err && (res && res.rows[0]));
			done();
			pool.end();
		});
	});
}

function getUsernameById(userId, callback) {
	var queryString = "SELECT fname, username FROM users WHERE user_id = $1 AND disabled = false";
	var queryParams = [userId];
	const pool = new pg.Pool({ connectionString: conString });

	pool.connect((err, client, done) => {
		client.query(queryString, queryParams, (err, res) => {
			if(!err && (res && res.rows[0] && res.rows[0].username && res.rows[0].fname)){
				callback(res.rows[0]);
			}
			else{
				callback(false);
			}
			done();
			pool.end();
		});
	});
}

function getRefreshToken(userId, refreshToken, callback) {
	var queryString = "SELECT * FROM refresh WHERE user_id = $1 and refresh_token = $2;";
	var queryParams = [userId, refreshToken];

	const pool = new pg.Pool({ connectionString: conString });

	pool.connect((err, client, done) => {
		client.query(queryString, queryParams, (err, res) => {
			if (!err && res && res.rows && res.rows[0] && res.rows[0].refresh_token) {
				callback(res.rows[0].refresh_token);
			} else {
				callback(false);
			}
			done();
			pool.end();
		});
	});
}

function deleteRefreshToken(userId, refreshToken, callback) {
	var queryString = "DELETE FROM refresh WHERE user_id = $1 and refresh_token = $2;";
	var queryParams = [userId, refreshToken];

	const pool = new pg.Pool({ connectionString: conString });

	pool.connect((err, client, done) => {
		client.query(queryString, queryParams, (err, res) => {
			callback(!err && (res && res.rowCount != 0));
			done();
			pool.end();
		});
	});
}

function deleteInvalidRefreshTokens(userId, callback) {
	var queryString = "DELETE FROM refresh WHERE user_id = $1;";
	var queryParams = [userId];

	const pool = new pg.Pool({ connectionString: conString });
	pool.connect((err, client, done) => {
		// eslint-disable-next-line no-unused-vars
		client.query(queryString, queryParams, (err, res) => {
			callback();
			done();
			pool.end();
		});
	});
}

function populateExtendedProfile(user, callback) {
	var queryString = "INSERT INTO extended_profile(user_id, username) VALUES($1, $2);";
	//var age = calculateAge(user.dob);
	var queryParams = [user.userId, user.username];

	const pool = new pg.Pool({ connectionString: conString });
	pool.connect((err, client, done) => {
		client.query(queryString, queryParams, (err, res) => {
			callback(!err && res);
			done();
			pool.end();
		});
	});
}

function getExtendedProfile(userID, callback) {
	var queryString =
		"(SELECT * FROM " +
		"(SELECT * FROM ((SELECT user_id, dob, gender FROM users where user_id = $1) user_info INNER JOIN  (SELECT * FROM extended_profile where user_id = $1) extended_profile ON user_info.user_id = extended_profile.user_id)) t1 " +
		"LEFT JOIN " +
		"(SELECT tag top_tag, count(tag) top_tag_count from tags where review_id in (SELECT review_id from reviews where user_id = $1) group by top_tag, review_id ORDER BY top_tag_count DESC LIMIT 1) top_tag_row on 1=1 " +
		"LEFT JOIN " +
		"(SELECT count(*) games_created from games where creator_id = $1) games_created on 1=1 " +
		"LEFT JOIN " +
		"(SELECT count(*) games_joined from gamers where user_id = $1) games_joined on 1=1 )";

	var queryParams = [userID];

	const pool = new pg.Pool({ connectionString: conString });

	pool.connect((err, client, done) => {
		client.query(queryString, queryParams, (err, res) => {
			if (!err && res.rows[0]) {
				callback(res.rows[0]);
			} else {
				callback(false);
			}
			done();
			pool.end();
		});
	});
}

function getRecentGamesForUser(userID, callback) {
	var queryString = "SELECT * FROM GAMES WHERE game_id IN (SELECT game_id FROM gamers WHERE user_id = $1 ORDER BY time_joined DESC) LIMIT 10";
	var queryParams = [userID];

	const pool = new pg.Pool({ connectionString: conString });

	pool.connect((err, client, done) => {
		client.query(queryString, queryParams, (err, res) => {
			if (!err && res.rows) {
				callback(res.rows);
			} else {
				callback(false);
			}
			done();
			pool.end();
		});
	});
}

function checkPasswordWithRefresh(emailIn, passIn, callback) {
	var queryString = "SELECT user_id, salt, password FROM users WHERE email = $1;";
	var queryParams = [emailIn];

	const pool = new pg.Pool({ connectionString: conString });

	pool.connect((err, client, done) => {
		client.query(queryString, queryParams, (err, res) => {
			var rowsRes = res.rows;
			if (rowsRes.length > 0 && md5(rowsRes[0].salt + passIn) === rowsRes[0].password) {//Log in success
				createRefreshToken(rowsRes[0].user_id, (refreshToken) => {
					callback(refreshToken, rowsRes[0].user_id);
				});
			} else {
				callback(null, null);
			}
			done();
			pool.end();
		});
	});
}

function checkPassword(userId, passIn, callback) {
	var queryString = "SELECT user_id, salt, password FROM users WHERE user_id = $1;";
	var queryParams = [userId];

	const pool = new pg.Pool({ connectionString: conString });

	pool.connect((err, client, done) => {
		client.query(queryString, queryParams, (err, res) => {
			var rowsRes = res.rows;
			if (rowsRes.length > 0 && md5(rowsRes[0].salt + passIn) === rowsRes[0].password) {
				callback(true);
			} else {
				callback(false);
			}
			done();
			pool.end();
		});
	});
}

function verifyGameId(gameIdIn, callback) {
	var queryString = "SELECT game_id FROM games WHERE game_id = $1;";
	var queryParams = [gameIdIn];

	const pool = new pg.Pool({ connectionString: conString });

	pool.connect((err, client, done) => {
		client.query(queryString, queryParams, (err, res) => {
			callback(res.rows.length > 0);
			done();
			pool.end();
		});
	});
}

function addGamer(userIdIn, gameIdIn, callback) {
	var queryString = "INSERT INTO gamers(user_id, game_id) VALUES($1, $2) RETURNING game_id";
	var queryParams = [userIdIn, gameIdIn];

	const pool = new pg.Pool({ connectionString: conString });

	pool.connect((err, client, done) => {
		client.query(queryString, queryParams, (err, res) => {
			callback(!err && res && res.rowCount > 0);
			done();
			pool.end();
		});
	});
}

function leaveGame(userIdIn, gameIdIn, callback) {
	var queryString = "DELETE FROM gamers WHERE user_id = $1 AND game_id = $2;";
	var queryParams = [userIdIn, gameIdIn];

	const pool = new pg.Pool({ connectionString: conString });

	pool.connect((err, client, done) => {
		client.query(queryString, queryParams, (err, res) => {
			callback(!err && (res && res.rowCount != 0));
			done();
			pool.end();
		});
	});
}

function updateExtendedUser(userId, skill_level, location, callback) {
	var queryString = "UPDATE extended_profile SET skilllevel = COALESCE($2, skilllevel), location = COALESCE($3, location) WHERE user_id = $1;";
	var queryParams = [userId, skill_level, location];

	const pool = new pg.Pool({ connectionString: conString });
	pool.connect((err, client, done) => {
		client.query(queryString, queryParams, (err, res) => {
			callback(!err && res);
			done();
			pool.end();
		});
	});
}

function getUsers(gameId, userid, callback) {
	var queryString = "SELECT user_id FROM gamers WHERE game_id = $1";
	var queryParams = [gameId];
	const pool = new pg.Pool({ connectionString: conString });

	pool.connect((err, client, done) => {
		client.query(queryString, queryParams, (err, res) => {
			if (!err && res.rows) {
				callback(res.rows);
			} else {
				callback(false);
			}
			done();
			pool.end();
		});
	});
}

function getIfReviewed(reviewerId, user, callback) {
	var queryString = "SELECT * FROM reviews WHERE(reviewer_id = $1 AND user_id = $2);";
	var queryParams = [reviewerId, user];
	const pool = new pg.Pool({ connectionString: conString });
	pool.connect((err, client, done) => {
		client.query(queryString, queryParams, (err, res) => {
			if (res && res.rows && res.rows[0]) {
				callback(true);
			}
			else {
				callback(false);
			}
			done();
			pool.end();
		});
	});
}


function addReview(userId, gameId, reviewerId, rating, callback) {
	var queryString = "INSERT INTO reviews(user_id, game_id, reviewer_id, rating) VALUES($1, $2, $3, $4) RETURNING review_id;";
	var queryParams = [userId, gameId, reviewerId, rating];

	const pool = new pg.Pool({ connectionString: conString });

	pool.connect((err, client, done) => {
		client.query(queryString, queryParams, (err, res) => {
			if (!err && res && res.rows && res.rows[0] && res.rows[0].review_id) {
				callback(res.rows[0].review_id);
			}
			else {
				callback(false);
			}
			done();
			pool.end();
		});
	});
}

function updateReview(userId, gameId, reviewerId, rating, callback) {
	var queryString = "UPDATE reviews SET rating = $4 WHERE user_id = $1 AND game_id = $2 AND reviewer_id = $3 RETURNING review_id";
	var queryParams = [userId, gameId, reviewerId, rating];

	const pool = new pg.Pool({ connectionString: conString });

	pool.connect((err, client, done) => {
		client.query(queryString, queryParams, (err, res) => {
			if (!err && res && res.rows && res.rows[0] && res.rows[0].review_id) {
				callback(res.rows[0].review_id);
			}
			else {
				callback(false);
			}
			done();
			pool.end();
		});
	});
}

function addTag(reviewId, tag, callback) {
	var queryString = "INSERT INTO tags(review_id, tag) VALUES($1, $2)";
	var queryParams = [reviewId, tag];

	const pool = new pg.Pool({ connectionString: conString });

	pool.connect((err, client, done) => {
		client.query(queryString, queryParams, (err, res) => {
			if (!err && res && res.rowCount == 1) {
				callback(true);
			}
			else {
				callback(false);
			}
			done();
			pool.end();
		});
	});
}

function deleteTag(reviewId, callback) {
	var queryString = "DELETE FROM tags WHERE review_id = $1";
	var queryParams = [reviewId];

	const pool = new pg.Pool({ connectionString: conString });

	pool.connect((err, client, done) => {
		client.query(queryString, queryParams, (err, res) => {
			if (!err && res) {
				callback(true);
			}
			else {
				callback(false);
			}
			done();
			pool.end();
		});
	});
}


function createGame(userId, name, type, min_skill, max_skill, totalPlayers, startTime, duration, location, locationNotes, description, gender, ageRange, enforcedParams, callback) {
	var queryString = "INSERT INTO games(creator_id, name, type, min_skill, max_skill, total_players_required, start_time, end_time, location, location_notes, description, gender, age_range, enforced_params)"
		+ "VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12, $13, $14) RETURNING game_id;";
	var dblocation = "(" + location.lat + "," + location.lng + ")";
	var queryParams = [userId, name, type, min_skill, max_skill, totalPlayers, startTime, startTime + duration, dblocation, locationNotes, description, gender, ageRange ? ageRange : [], enforcedParams ? enforcedParams : []];

	const pool = new pg.Pool({ connectionString: conString });

	pool.connect((err, client, done) => {
		client.query(queryString, queryParams, (err, res) => {
			if (!err && res && res.rows && res.rows[0] && res.rows[0].game_id) {
				callback(parseInt(res.rows[0].game_id));
			} else {
				callback(false);
			}
			done();
			pool.end();
		});
	});
}

function ensureGameIsValidToBeCreated(game, userId, callback) {
	var queryString = "SELECT start_time, end_time " +
		"FROM (games FULL OUTER JOIN gamers ON games.game_id=gamers.game_id) " +
		"WHERE (creator_id = $1 OR user_id = $1) AND (($2 >= start_time AND $2 <= end_time) OR ($3 >= start_time AND $3 <= end_time) OR (start_time >= $2 AND start_time <= $3) OR (end_time >= $2 AND end_time <= $3))";
	var end_time = game.start_time + game.duration;
	var queryParams = [userId, game.start_time, end_time];

	const pool = new pg.Pool({ connectionString: conString });

	pool.connect((err, client, done) => {
		client.query(queryString, queryParams, (err, res) => {
			if (!err && res.rows.length == 0) {
				callback(true);
			} else {
				callback(false);
			}
			done();
			pool.end();
		});
	});
}

function ensureGameIsJoinableByPlayer(gameId, userId, callback) {
	var queryString = "SELECT to_json(enforced_params) enforced_params, gender, age_range FROM games WHERE game_id = $1";
	var queryParams = [gameId];

	const pool = new pg.Pool({ connectionString: conString });
	pool.connect((err, client, done) => {
		client.query(queryString, queryParams, (err, res) => {
			var resQuery = res.rows[0];
			// Go through enforced params and verify that user meets requirements (if any)
			if (resQuery.enforced_params != null && resQuery.enforced_params.length > 0) {
				var queryString = "SELECT gender, dob FROM users WHERE user_id = $1";
				var queryParams = [userId];
				const pool = new pg.Pool({ connectionString: conString });
				pool.connect((err, client, done) => {
					client.query(queryString, queryParams, (err, res) => {
						var params = resQuery.enforced_params;
						for (var i = 0; i < params.length; i++) {
							var validParam = params[i] === "gender" ? resQuery.gender === res.rows[0].gender :
								validAge(resQuery.age_range, res.rows[0].dob);
							if (!validParam) {
								callback(false);
								break;
							}
						}

						if (validParam) { // only if all requirements are fulfilled
							callback(true);
						}
						done();
						pool.end();
					});
				});
			} else {
				callback(true);
			}
			done();
			pool.end();
		});
	});
}

function validAge(gameAgeRange, userDob) {
	var startAge = gameAgeRange[0];
	var endAge = gameAgeRange[1];
	var userAge = calculateAge(userDob);
	return startAge <= userAge && userAge <= endAge;
}

function sendFriendInvite(sender, receiver, callback) {
	var queryString = "INSERT INTO friends(user_1, user_2, status) VALUES($1, $2, 'requested');";
	var queryParams = [sender, receiver];

	const pool = new pg.Pool({ connectionString: conString });

	pool.connect((err, client, done) => {
		client.query(queryString, queryParams, (err, res) => {
			callback(!err && res);
			done();
			pool.end();
		});
	});
}

function checkFriendRequestValidation(sender, invited_friend, callback) {
	var queryString = "SELECT * FROM friends WHERE user_1 = $1 AND user_2 = $2 AND status = 'requested'";
	var queryParams = [sender, invited_friend];

	const pool = new pg.Pool({ connectionString: conString });

	pool.connect((err, client, done) => {
		client.query(queryString, queryParams, (err, res) => {
			if (!err && res.rows[0]) {
				callback(res.rows[0]);
			} else {
				callback(false);
			}
			done();
			pool.end();
		});
	});
}

function acceptFriendInvite(invited_friend, sender, callback) {
	var queryString = "UPDATE friends SET user_1 = $2, user_2 = $1, status = 'accepted' WHERE user_1 = $1 AND user_2 = $2";
	var queryParams = [sender, invited_friend];

	const pool = new pg.Pool({ connectionString: conString });

	pool.connect((err, client, done) => {
		client.query(queryString, queryParams, (err, res) => {
			callback(!err && res);
			done();
			pool.end();
		});
	});
}

function checkFriendEntryValidationForDelete(sender, invited_friend, callback) {
	var queryString = "SELECT * FROM friends WHERE (user_1 = $1 OR user_1 = $2) AND (user_2 = $1 OR user_2 = $2) AND (status = 'requested' OR status = 'accepted');";
	var queryParams = [sender, invited_friend];


	const pool = new pg.Pool({ connectionString: conString });

	pool.connect((err, client, done) => {
		client.query(queryString, queryParams, (err, res) => {
			if (!err && res.rows[0]) {
				callback(res.rows[0]);
			} else {
				callback(false);
			}
			done();
			pool.end();
		});
	});
}

function declineFriend(sender, receiver, callback) {
	var queryString = "DELETE FROM friends WHERE (user_1 = $1 OR user_1 = $2) AND (user_2 = $1 OR user_2 = $2)";
	var queryParams = [sender, receiver];

	const pool = new pg.Pool({ connectionString: conString });

	pool.connect((err, client, done) => {
		client.query(queryString, queryParams, (err, res) => {
			callback(!err && (res && res.rowCount != 0));
			done();
			pool.end();
		});
	});
}

function checkFriendEntryValidationForBlock(sender, invited_friend, callback) {
	var queryString = "SELECT * FROM friends WHERE (user_1 = $1 OR user_1 = $2) AND (user_2 = $1 OR user_2 = $2) AND (status = 'requested' OR status = 'accepted');";
	var queryParams = [sender, invited_friend];

	const pool = new pg.Pool({ connectionString: conString });

	pool.connect((err, client, done) => {
		client.query(queryString, queryParams, (err, res) => {
			if (!err && res.rows[0]) {
				callback("update");
			} else if (res.rowCount == 0) {
				callback("insert");
			} else {
				callback(false);
			}
			done();
			pool.end();
		});
	});

}

function blockFriendUpdateEntry(person_blocking, blocked_user, callback) {
	var queryString = "UPDATE friends SET user_1 = $1, user_2 = $2, status = 'blocked' WHERE (user_1 = $1 or user_1 = $2) AND (user_2 = $1 OR user_2 = $2)";
	var queryParams = [person_blocking, blocked_user];

	const pool = new pg.Pool({ connectionString: conString });

	pool.connect((err, client, done) => {
		client.query(queryString, queryParams, (err, res) => {
			callback(!err && res);
			done();
			pool.end();
		});
	});
}

function blockFriendNewEntry(person_blocking, blocked_user, callback) {
	var queryString = "INSERT INTO friends(user_1, user_2, status) VALUES($1, $2, 'blocked');";
	var queryParams = [person_blocking, blocked_user];

	const pool = new pg.Pool({ connectionString: conString });
	pool.connect((err, client, done) => {
		client.query(queryString, queryParams, (err, res) => {
			callback(!err && res);
			done();
			pool.end();
		});
	});
}

function checkIfFriendRequestExists(sender, invited_person, callback) {
	var queryString = "Select * From friends WHERE user_1 = $1 AND user_2 = $2 AND status = 'requested'";
	var queryParams = [sender, invited_person];

	const pool = new pg.Pool({ connectionString: conString });
	pool.connect((err, client, done) => {
		client.query(queryString, queryParams, (err, res) => {
			if (!err && res.rows[0]) {
				callback(res.rows[0]);
			} else {
				callback(false);
			}
			done();
			pool.end();
		});
	});
}

function getUserSkillGenderAge(user_id, callback) {
	var queryString = "SELECT skilllevel, dob, gender FROM extended_profile JOIN users ON users.user_id = extended_profile.user_id WHERE users.user_id = $1;";	
	var queryParams = [user_id];

	const pool = new pg.Pool({ connectionString: conString });
	pool.connect((err, client, done) => {
		client.query(queryString, queryParams, (err, res) => {
			if (!err && res.rows[0] && res.rows[0].dob && res.rows[0].gender && res.rows[0].skilllevel >= 0) {
				callback({
					"age": calculateAge(res.rows[0].dob),
					"skilllevel": res.rows[0].skilllevel,
					"gender": res.rows[0].gender
				});
			} else {
				callback(false);
			}
			done();
			pool.end();
		});
	});
}

function searchObjects(search_request, callback) {
	var queryString = getConstraintQuery(search_request);

	const pool = new pg.Pool({ connectionString: conString });
	pool.connect((err, client, done) => {
		client.query(queryString, [], (err, res) => {
			if (!err && res.rows[0]) {
				callback(res.rows);
			} else {
				callback(false);
			}
			done();
			pool.end();
		});
	});
}

function listAllFriends(user, callback) {
	//list all friends

	var queryString = "select user_id, fname, lname from (" +
		"SELECT user_1 AS user from friends WHERE user_2 = $1 AND status = 'accepted' " +
		"UNION ALL " +
		"SELECT user_2 AS user from friends WHERE user_1 = $1 AND status = 'accepted') t1 " +
		"INNER JOIN users ON users.user_id = t1.user";

	var queryParams = [user];

	const pool = new pg.Pool({ connectionString: conString });
	pool.connect((err, client, done) => {
		client.query(queryString, queryParams, (err, res) => {
			if (!err && res.rowCount >= 0) {
				callback(res.rows);
			}
			else {
				callback(false);
			}
			done();
			pool.end();
		});
	});
}

function listAllBlockedUsers(user, callback) {
	var queryString = "select user_id, fname, lname from " +
		"(Select user_2 from friends where user_1 = $1 AND status = 'blocked') t1 " +
		"INNER JOIN users ON t1.user_2 = users.user_id";

	var queryParams = [user];
	const pool = new pg.Pool({ connectionString: conString });
	pool.connect((err, client, done) => {
		client.query(queryString, queryParams, (err, res) => {
			if (!err && res.rows[0]) {
				callback(res.rows);
			} else {
				callback(false);
			}
			done();
			pool.end();
		});
	});
}

function listAllFriendRequests(user, callback) {
	var queryString = "Select t3.user_1, t3.user_2, t3.fname, t3.lname, t3.status FROM (" +
		"(select * FROM (" +
		"select * from friends where user_1 = $1 AND status = 'requested') t1 " +
		"INNER JOIN users ON users.user_id = t1.user_2) " +
		"UNION ALL " +
		"(select * FROM (" +
		"select * from friends where user_2 = $1 AND status = 'requested') t2 " +
		"INNER JOIN users ON users.user_id = t2.user_1)" +
		") t3";

	var queryParams = [user];


	const pool = new pg.Pool({ connectionString: conString });
	pool.connect((err, client, done) => {
		client.query(queryString, queryParams, (err, res) => {
			if (!err && res.rows[0]) {
				callback(res.rows);
			} else {
				callback(false);
			}
			done();
			pool.end();
		});
	});
}

function disableAccount(user_id, callback) {
	var queryString = "SELECT * FROM delete_user($1);";
	var queryParams = [user_id];

	const pool = new pg.Pool({ connectionString: conString });
	pool.connect((err, client, done) => {
		client.query(queryString, queryParams, (err, res) => {
			callback(!err && res);
			done();
			pool.end();
		});
	});
}

function updateUser(user_id, username, fname, lname, gender, dob, email, callback) {
	var queryString = "SELECT * FROM update_user($1, $2, $3, $4, $5, $6, $7);";
	var queryParams = [user_id, username, fname, lname, gender, dob, email];
	const pool = new pg.Pool({ connectionString: conString });


	pool.connect((err, client, done) => {
		client.query(queryString, queryParams, (err, res) => {
			callback(!err && res);
			done();
			pool.end();
		});
	});
}

function updatePassword(user_id, user_salt, user_new_hashed_Password, callback) {
	var queryString = "UPDATE users SET salt = $2, password = $3 WHERE user_id = $1";
	var queryParams = [user_id, user_salt, user_new_hashed_Password];

	const pool = new pg.Pool({ connectionString: conString });

	pool.connect((err, client, done) => {
		client.query(queryString, queryParams, (err, res) => {
			if(!err && res){
				deleteInvalidRefreshTokens(user_id, () => {
					createRefreshToken(user_id, (refreshToken) => {
						callback(refreshToken);
					});
				});
			}
			else {
				callback(false);
			}
			done();
			pool.end();
		});
	});
}


module.exports = {
	checkEmailUniqueness,
	checkUsernameUniqueness,
	registerUser,
	updateUser,
	getUserId,
	getUserRowById,
	getRefreshToken,
	createRefreshToken,
	deleteRefreshToken,
	populateExtendedProfile,
	getExtendedProfile,
	checkPassword,
	checkPasswordWithRefresh,
	updateExtendedUser,
	getUsers,
	addReview,
	createGame,
	ensureGameIsValidToBeCreated,
	verifyGameId,
	addGamer,
	ensureGameIsJoinableByPlayer,
	leaveGame,
	sendFriendInvite,
	checkFriendRequestValidation,
	acceptFriendInvite,
	checkFriendEntryValidationForDelete,
	declineFriend,
	checkIfFriendRequestExists,
	checkFriendEntryValidationForBlock,
	blockFriendUpdateEntry,
	blockFriendNewEntry,
	getUserSkillGenderAge,
	listAllFriends,
	listAllBlockedUsers,
	searchObjects,
	listAllFriendRequests,
	disableAccount,
	getIfReviewed,
	updateReview,
	addTag,
	deleteTag,
	updatePassword,
	getUsernameById,
	getRecentGamesForUser
};

//////////////// Helpers ////////////////

function calculateAge(userDob) {
	var birth = new Date(userDob);
	var curr = new Date();
	var diff = Math.abs(birth.getTime() - curr.getTime());
	return Math.ceil(diff / (1000 * 3600 * 24 * 365));
}

function createRefreshToken(userId, callback) {
	var refreshToken = crypto.randomBytes(50).toString("hex");
	var queryString = "INSERT INTO refresh(user_id, refresh_token) VALUES($1, $2);";
	var queryParams = [userId, refreshToken];

	const pool = new pg.Pool({ connectionString: conString });

	pool.connect((err, client, done) => {
		client.query(queryString, queryParams, (err, res) => {
			var result = (err && res) ? null : refreshToken;
			callback(result);
			done();
			pool.end();
		});
	});
}

function getConstraintQuery(search_request) {
	var query = "";
	if (search_request.search_object == "game") {
		// Game param validation
		query += "SELECT * FROM games ";
		var queryConstraint = [];
		if (search_request.game_id && search_request.game_id > 0) {
			query += "WHERE game_id = " + search_request.game_id + " order by game_id DESC LIMIT " + search_request.results_max + ";";
			return query;
		}
		else if (search_request.game_name && search_request.game_name != "") {
			queryConstraint.push("name ILIKE '%" + search_request.game_name + "%' ");
		}
		if (search_request.game_type && search_request.game_type != "") {
			queryConstraint.push("type = '" + search_request.game_type + "'");
		}
		if (search_request.game_skill_min && search_request.game_skill_min > 0) {
			queryConstraint.push("min_skill >= " + search_request.game_skill_min);
		}
		if (search_request.game_skill_max && search_request.game_skill_max < 10) {
			queryConstraint.push("max_skill <= " + search_request.game_skill_max);
		}
		if (search_request.game_total_players && search_request.game_total_players > 0) {
			queryConstraint.push("total_players_required >= " + search_request.game_total_players);
		}
		if (search_request.game_start_time && search_request.game_start_time > 0) {
			queryConstraint.push("start_time >= " + search_request.game_start_time);
		}
		if (search_request.game_end_time && search_request.game_end_time > 0) {
			queryConstraint.push("end_time <= " + search_request.game_end_time);
		}
		if (search_request.game_duration) {
			queryConstraint.push("end_time >= start_time + " + search_request.game_duration);
		}
		else {
			queryConstraint.push("end_time >= " + Math.floor(Date.now()/1000));
		}
		if (search_request.game_location && search_request.game_location_range && search_request.game_location_range > 0) {
			var search_point = util.format("(%d, %d)", search_request.game_location.lat, search_request.game_location.lng);
			queryConstraint.push("(SELECT distance(point" + search_point + ", location)) <= " + search_request.game_location_range);
		}
		if (queryConstraint.length > 0) {
			query += "WHERE " + queryConstraint.join(" and ");
		}
		query += " order by game_id DESC LIMIT " + search_request.results_max + ";";

	}
	else if (search_request.search_object == "user") {
		var selfId =  tokenHelper.getUserFromToken(search_request.jwt).user_id;
		// User param validation
		query += "SELECT user_id, username, fname FROM users WHERE username LIKE '%" + search_request.username + "%' and user_id != " + selfId + ";";
	}
	return query;
}
