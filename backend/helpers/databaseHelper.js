var pg = require('pg');
const conString = "postgres://postgres:123@localhost:5432/pickup";
const client = new pg.Client(conString);

module.exports = {
	checkEmailUniqueness(email){
		client.connect();
		client.query('SELECT * FROM users WHERE email = $1', [email], (err, res) => {
  			console.log(err ? err.stack : res.rows[0].message);
  			client.end();
		});
	},
	registerUser(userId, reqBody){
		client.connect();
		// register user here
		client.end();
	}
}