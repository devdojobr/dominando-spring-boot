insert into user (id,first_name,last_name, email) values (99, 'goku','son','goku.son@dbz.com');
insert into user (id,first_name,last_name, email) values (100, 'gohan','son','gohan.son@dbz.com');
insert into profile (id,description,name) values (999, 'Profile with all permissions','Administrator');
insert into user_profile (id, user_id,profile_id) values (1,99, 999);
insert into user_profile (id, user_id,profile_id) values (2, 100,999);