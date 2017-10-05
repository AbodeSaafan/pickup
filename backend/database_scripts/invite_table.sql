CREATE TABLE IF NOT EXISTS Invites (
	user_1 int REFERENCES Users(user_id),
	user_2 int REFERENCES Users(user_id),
	game_id int REFERENCES Games(game_id),
	PRIMARY KEY(user_1, user_2, game_id)
);