CREATE OR REPLACE FUNCTION delete_user(uId int)
$BODY$
BEGIN
	DELETE FROM refresh WHERE user_id = uId;
	UPDATE email 
END;
$BODY$
language plpgsql;