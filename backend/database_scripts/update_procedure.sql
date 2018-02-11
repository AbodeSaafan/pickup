CREATE OR REPLACE FUNCTION update_user(uId int, uname varchar, firstName varchar, lastName varchar, gender_type varchar, dob_val varchar, email_val varchar)
RETURNS void as $$
BEGIN
  UPDATE users
  SET username = COALESCE(uname, username),
      fname = COALESCE(firstName, fname),
      lname = COALESCE(lastName, lname),
      gender = COALESCE(gender_type, gender),
      dob = COALESCE(dob_val, dob),
      email = COALESCE(email_val, email)
  WHERE user_id = uId;

END;
$$ LANGUAGE plpgsql;
