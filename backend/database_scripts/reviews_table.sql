CREATE TABLE IF NOT EXISTS Reviews (
	review_id BIGSERIAL UNIQUE,
	user_id int REFERENCES Users(user_id),
	game_id int REFERENCES Games(game_id),
	reviewer_id int,
	rating int,
	PRIMARY KEY(user_id, reviewer_id)
);

CREATE TABLE IF NOT EXISTS Tags (
	review_id int REFERENCES Reviews(review_id),
	tag int,
	PRIMARY KEY(review_id, tag)
);