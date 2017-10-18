set PGPASSWORD=123
psql -h localhost -p 5432 -U postgres -f dropAll.sql pickup 
psql -h localhost -p 5432 -U postgres -f createAll.sql pickup