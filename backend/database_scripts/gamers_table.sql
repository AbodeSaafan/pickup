CREATE TABLE IF NOT EXISTS gamers (
	user_id int REFERENCES Users(user_id),
	game_id int REFERENCES Games(game_id),
	PRIMARY KEY(user_id, game_id)
);