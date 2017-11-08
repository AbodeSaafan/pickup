CREATE OR REPLACE FUNCTION delete_user(uId int)
RETURNS void as $$
BEGIN
	DELETE FROM refresh WHERE user_id = uId;
	UPDATE users SET disabled = true WHERE user_id = uId;
	DELETE FROM gamers where user_id = uId;
END;
$$ LANGUAGE plpgsql;