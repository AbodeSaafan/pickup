CREATE OR REPLACE FUNCTION add_to_player_count_function() RETURNS TRIGGER AS
$BODY$
BEGIN
	UPDATE games SET total_players_added = total_players_added + 1 WHERE game_id = new.game_id;
	RETURN new;
END;
$BODY$
language plpgsql;

CREATE OR REPLACE FUNCTION subtract_from_player_count_function() RETURNS TRIGGER AS
$BODY$
BEGIN
	UPDATE games SET total_players_added = total_players_added - 1 WHERE game_id = old.game_id;
	RETURN new;
END;
$BODY$
language plpgsql;

CREATE OR REPLACE FUNCTION update_player_avg_review() RETURNS TRIGGER AS 
$BODY$
BEGIN
	
	UPDATE extended_profile SET average_review = (SELECT AVG(rating) from Reviews WHERE user_id = new.user_id) 
	WHERE user_id = new.user_id;
	RETURN new;
END;
$BODY$
language plpgsql;

CREATE TRIGGER review_change_for_player AFTER INSERT OR DELETE OR UPDATE ON reviews
FOR EACH ROW EXECUTE PROCEDURE update_player_avg_review();

CREATE TRIGGER player_joined_game_trigger BEFORE INSERT ON gamers 
FOR EACH ROW EXECUTE PROCEDURE add_to_player_count_function(); 

CREATE TRIGGER player_left_game_trigger AFTER DELETE ON gamers 
FOR EACH ROW EXECUTE PROCEDURE subtract_from_player_count_function(); 