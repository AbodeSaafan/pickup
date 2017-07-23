var pg = require('pg');
const conString = "postgres://postgres:123@localhost:5432/pickup";
const client = new pg.Client(conString);

module.exports = {
	checkEmailUniqueness(email){
		client.connect();
		client.query('SELECT * FROM users WHERE email = $1', [email], (err, res) => {
  			client.end();
  			if (res.rows.length != 0){
  				throw new Error("Email is not unique")
  			}
		});
	},
	registerUser(user){
		client.connect();
		// register user here
		client.end();
	}
}