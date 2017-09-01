var pg = require('pg');
const conString = "postgres://postgres:123@localhost:5432/pickup";
var crypto = require('crypto');
var md5 = require('md5');

function checkEmailUniqueness(user, callback){
		var queryString = "SELECT * FROM users WHERE email = '" + user.email +"';";

		const pool = new pg.Pool({connectionString: conString});

		pool.connect((err, client, done) => {
			client.query(queryString, (err, res) => {
  				callback(!(res.rows[0] || err));
  				done();
				pool.end();
			});
		});
}

function registerUser(user, callback){
		var queryString = "INSERT INTO users(nickname, fname, lname, dob, gender, email, password, salt) VALUES($1, $2, $3, $4, $5, $6, $7, $8)";
		var queryParams = [user.nickname, user.fname, user.lname, user.dob, user.gender, user.email, user.hashedPassword, user.salt];

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
        var queryString = "update users SET nickname = $2, fname = $3, lname = $4, dob = $5 where user_id = $1";
        var queryParams = [user['user_id'], user['nickname'], user['fname'], user['lname'], user['dob']];
		console.log(user);
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
	  				console.log("Failed to get user id");
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
	  				console.log("Failed to get user row");
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
	  				console.log("Failed to get refresh token" + err);
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
				console.log(res);
				console.log((res && res.rowCount != 0));
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
	  				console.log("Failed to get user row");
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
                    console.log("Invalid password or email");
                    callback(null, null);
                }
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


module.exports = {
	checkEmailUniqueness,
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
	updateExtendedUser
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
