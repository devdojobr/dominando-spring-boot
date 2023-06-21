insert into "user" (id,first_name,last_name, email) values (1, 'goku','son','goku.son@dbz.com');
insert into "user" (id,first_name,last_name, email) values (2, 'gohan','son','gohan.son@dbz.com');
insert into "profile" (id,description,name) values (1, 'Profile with all permissions','Administrator');
insert into user_profile (id, user_id,profile_id) values (1,1,1);
insert into user_profile (id, user_id,profile_id) values (2,2,1);