CREATE TABLE IF NOT EXISTS Refresh (
	user_id int REFERENCES Users(user_id),
	refresh_token varchar(255),
	PRIMARY KEY(user_id, refresh_token),
	generated int DEFAULT extract(epoch from now())
);