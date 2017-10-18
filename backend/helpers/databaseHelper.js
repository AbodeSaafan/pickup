var pg = require('pg');
var env = process.env.NODE_ENV || 'dev';
const conString = process.env.DATABASE_URL ? process.env.DATABASE_URL : "postgres://postgres:123@localhost:5432/pickup";
var crypto = require('crypto');
var md5 = require('md5');

function checkEmailUniqueness(user, callback){
		var queryString = "SELECT * FROM users WHERE email = '" + user.email +"';";

		const pool = new pg.Pool({connectionString: conString});

		pool.connect((err, client, done) => {
			client.query(queryString, (err, res) => {
				  console.log(err);
  				callback(!(res.rows[0] || err));
  				done();
				pool.end();
			});
		});
}

function checkUsernameUniqueness(user, callback){
		var queryString = "SELECT * FROM users WHERE username = $1;";
		var queryParams = [user.username];

		const pool = new pg.Pool({connectionString: conString});

		pool.connect((err, client, done) => {
			client.query(queryString, queryParams, (err, res) => {
  				callback(!(res.rows[0] || err));
  				done();
				pool.end();
			});
		});
}

function registerUser(user, callback){
		var queryString = "INSERT INTO users(username, fname, lname, dob, gender, email, password, salt) VALUES($1, $2, $3, $4, $5, $6, $7, $8)";
		var queryParams = [user.username, user.fname, user.lname, user.dob, user.gender, user.email, user.hashedPassword, user.salt];

		const pool = new pg.Pool({connectionString: conString});

		pool.connect((err, client, done) => {
			client.query(queryString, queryParams, (err, res) => {
  				callback(!err);
  				done();
				pool.end();
			});
		});
}

function updateUser(user, callback){
        var queryString = "UPDATE users SET fname = $2, lname = $3, dob = $4 where user_id = $1";
        var queryParams = [user['user_id'], user['fname'], user['lname'], user['dob']];

        const pool = new pg.Pool({connectionString: conString});

        pool.connect((err, client, done) => {
            client.query(queryString, queryParams, (err, res) => {
            callback(!err);
        	done();
        	pool.end();
			});
    	});
}

function getUserId(email, callback){
		var queryString = "SELECT user_id FROM users WHERE email =  $1;";
		var queryParams = [email];

		const pool = new pg.Pool({connectionString: conString});

		pool.connect((err, client, done) => {
			client.query(queryString, queryParams, (err, res) => {
  				if(!err && res.rows[0].user_id){
	  				callback(res.rows[0].user_id);
  				} else {
					callback(false);
  				}
  				done();
  				pool.end();
			});
		});
}

function getUserRowById(userId, callback){
		var queryString = "SELECT * FROM users WHERE user_id = $1";
		var queryParams = [userId];

		const pool = new pg.Pool({connectionString: conString});

		pool.connect((err, client, done) => {
			client.query(queryString, queryParams, (err, res) => {
  				if(!err && res.rows[0]){
	  				callback(res.rows[0]);
  				} else {
					callback(false);
  				}
  				done();
  				pool.end();
			});
		});
}

function getRefreshToken(userId, refreshToken, callback){
		var queryString = "SELECT * FROM refresh WHERE user_id = $1 and refresh_token = $2;";
		var queryParams = [userId, refreshToken];

		const pool = new pg.Pool({connectionString: conString});

		pool.connect((err, client, done) => {
			client.query(queryString, queryParams, (err, res) => {
  				if(!err && res.rows[0].refresh_token){
  					callback(res.rows[0].refresh_token);
  				} else {
					callback(false);
  				}
  				done();
  				pool.end();
			});
		});
}

function deleteRefreshToken(userId, refreshToken, callback){
		var queryString = "DELETE FROM refresh WHERE user_id = $1 and refresh_token = $2;";
		var queryParams = [userId, refreshToken];

		const pool = new pg.Pool({connectionString: conString});

		pool.connect((err, client, done) => {
			client.query(queryString, queryParams, (err, res) => {
				callback(!err && (res && res.rowCount != 0));
  				done();
  				pool.end();
			});
		});
}

function populateExtendedProfile(user, callback) {
        var queryString = "INSERT INTO extended_profile(user_id, age, gender) VALUES($1, $2, $3);";
        var age = calculateAge(new Date(user.dob.substring(6)));
        var queryParams = [user.userId, age, user.gender];

        const pool = new pg.Pool({connectionString: conString});
		pool.connect((err, client, done) => {
			client.query(queryString, queryParams, (err, res) => {
  				callback(!err);
  				done();
				pool.end();
			});
		});
}

function getExtendedProfile(userID, callback) {
		var queryString = "SELECT * FROM extended_profile WHERE user_id = $1";
		var queryParams = [userID];
		const pool = new pg.Pool({connectionString: conString});

		pool.connect((err, client, done) => {
			client.query(queryString, queryParams, (err, res) => {
  				if(!err && res.rows[0]){
	  				callback(res.rows[0]);
  				} else {
					callback(false);
  				}
  				done();
  				pool.end();
			});
		});
}

function checkPassword(emailIn, passIn, callback){
        var queryString = "SELECT user_id, salt, password FROM users WHERE email = $1;";
        var queryParams = [emailIn];

        const pool = new pg.Pool({connectionString: conString});

        pool.connect((err, client, done) => {
            client.query(queryString, queryParams, (err, res) => {
                var rowsRes = res.rows;
                if(rowsRes.length > 0 && md5(rowsRes[0].salt + passIn) === rowsRes[0].password){//Log in success
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

function verifyGameId(gameIdIn, callback){
    var queryString = "SELECT game_id FROM games WHERE game_id = $1;";
    var queryParams = [gameIdIn];

    const pool = new pg.Pool({connectionString: conString});

    pool.connect((err, client, done) => {
        client.query(queryString, queryParams, (err, res) => {
            callback(res.rows.length > 0);
            done();
            pool.end();
        });
    });
}

function addGamer(userIdIn, gameIdIn, callback){
    var queryString = "INSERT INTO gamers(user_id, game_id) VALUES($1, $2) RETURNING game_id";
    var queryParams = [userIdIn, gameIdIn];

    const pool = new pg.Pool({connectionString: conString});

    pool.connect((err, client, done) => {
        client.query(queryString, queryParams, (err, res) => {
            callback(res.rows.length > 0);
            done();
            pool.end();
        });
    });
}

function leaveGame(userIdIn, gameIdIn, callback){
    var queryString = "DELETE FROM gamers WHERE user_id = $1 AND game_id = $2;";
    var queryParams = [userIdIn, gameIdIn];

    const pool = new pg.Pool({connectionString: conString});

    pool.connect((err, client, done) => {
        client.query(queryString, queryParams, (err, res) => {
            callback(res.rows.length > 0);
            done();
            pool.end();
        });
    });
}

function updateExtendedUser (userId, skill_level, location, callback) {
		var queryString = "UPDATE extended_profile SET skilllevel = $1, location = $2 WHERE user_id = $3;"
		var queryParams = [skill_level, location, userId]

		const pool = new pg.Pool({connectionString: conString});
		pool.connect((err, client, done) => {
			client.query(queryString, queryParams, (err, res) => {
  				callback(!err);
  				done();
				pool.end();
			});
		});
}

function getUsers (gameId, callback){
	var queryString = "SELECT user_id FROM gamers WHERE game_id = $1;";
	var queryParams = [gameId];

	const pool = new pg.Pool({connectionString: conString});

		pool.connect((err, client, done) => {
			client.query(queryString, queryParams, (err, res) => {
  				if(!err && res.rows.user_id){
  					callback(res.rows.user_id);
  				} else {
					callback(false);
  				}
  				done();
  				pool.end();
			});
		});
}

function addReview (userId, gameId, reviewerId, rating, tags, callback){
		var queryString = "INSERT INTO reviews(user_id, game_id, reviewer_id, rating, tags) VALUES($1, $2, $3, $4, $5)";
		var queryParams = [userId, gameId, reviewerId, rating, tags];

		const pool = new pg.Pool({connectionString: conString});

		pool.connect((err, client, done) => {
			client.query(queryString, queryParams, (err, res) => {
  				callback(!err);
  				done();
				pool.end();
			});
		});
}

function createGame (userId, name, type, skill, totalPlayers, startTime, duration, location, locationNotes, description, gender, ageRange, enforcedParams, callback){
	var queryString =  "INSERT INTO games(creator_id, name, type, skill, total_players_required, start_time, end_time, location, location_notes, description, gender, age_range, enforced_params)"
		+ "VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12, $13) RETURNING game_id;"
		var dblocation = '(' + location.lat + ',' + location.lng + ')';
	var queryParams = [userId, name, type, skill, totalPlayers, startTime, startTime+duration, dblocation, locationNotes, description, gender, ageRange, enforcedParams];

	const pool = new pg.Pool({connectionString: conString});

	pool.connect((err, client, done) => {
		client.query(queryString, queryParams, (err, res) => {
  			if(!err && res && res.rows && res.rows[0] && res.rows[0].game_id){
  				callback(res.rows[0].game_id);
  			} else {
  				callback(false);
  			}
  			done();
			pool.end();
		});
	});
}

function ensureGameIsValidToBeCreated (game, userId, callback){
	var queryString = "SELECT start_time, end_time " +
		"FROM (games FULL OUTER JOIN gamers ON games.game_id=gamers.game_id) " +
		"WHERE (creator_id = $1 OR user_id = $1) AND (($2 >= start_time AND $2 <= end_time) OR ($3 >= start_time AND $3 <= end_time) OR (start_time >= $2 AND start_time <= $3) OR (end_time >= $2 AND end_time <= $3))";
	var end_time = game.start_time + game.duration;
	var queryParams = [userId, game.start_time, end_time];

	const pool = new pg.Pool({connectionString: conString});

	pool.connect((err, client, done) => {
		client.query(queryString, queryParams, (err, res) => {
			if(!err && res.rows.length == 0){
				callback(true);
			} else{
				callback(false);
			}
			done();
			pool.end();
		});
	});
}

function ensureGameIsJoinableByPlayer(gameId, userId, callback){
    var queryString = "SELECT total_players_required, total_players_added, enforced_params FROM games WHERE game_id = $1";
    var queryParams = [gameId];

    const pool = new pg.Pool({connectionString: conString});
    pool.connect((err, client, done) => {
        client.query(queryString, queryParams, (err, res) => {
            // Check space in the game
            if (res.rows[0].total_players_required - res.rows[0].total_players_added > 0){
                // TODO: Go through enforced params and verify that user meets requirements (if any)
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


function leaveGame(gameId, numPlayers, callback){
    // TODO: The following:
    // 1) Delete user from the game
    callback(true);
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
	updateExtendedUser,
	getUsers,
	addReview,
	createGame,
	ensureGameIsValidToBeCreated,
  verifyGameId,
	addGamer,
	ensureGameIsJoinableByPlayer,
  leaveGame
}

//////////////// Helpers ////////////////

// Taken mostly from https://stackoverflow.com/questions/4060004/calculate-age-given-the-birth-date-in-the-format-yyyymmdd
function calculateAge(birthday) {
    var ageDifMs = Date.now() - birthday.getTime();
    var ageDate = new Date(ageDifMs); // miliseconds from epoch
    return Math.abs(ageDate.getUTCFullYear() - 1970);
}

function createRefreshToken(userId, callback){
    var refreshToken = crypto.randomBytes(50).toString('hex');
    var queryString = "INSERT INTO refresh(user_id, refresh_token) VALUES($1, $2);";
    var queryParams = [userId, refreshToken];

    const pool = new pg.Pool({connectionString: conString});

    pool.connect((err, client, done) => {
        client.query(queryString, queryParams, (err, res) => {
            var result = err ? null : refreshToken;
            callback(result);
            done();
            pool.end();
        });
    });
}
