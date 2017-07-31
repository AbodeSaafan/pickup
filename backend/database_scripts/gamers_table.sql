CREATE TABLE IF NOT EXISTS gamers (
	user_id int REFERENCES Users(user_id),
	game_id int REFERENCES Game(game_id),
	PRIMARY KEY(user_id, game_id)
);