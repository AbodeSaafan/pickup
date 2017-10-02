CREATE TYPE friend_status_type AS ENUM ('requested', 'accepted', 'blocked');

CREATE TABLE IF NOT EXISTS Friends (
	user_1 int REFERENCES Users(user_id),
	user_2 int REFERENCES Users(user_id),
	status friend_status_type,
	PRIMARY KEY(user_1, user_2)
);