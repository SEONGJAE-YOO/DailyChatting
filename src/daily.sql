use Daily;
create database Daily;
show databases;
show tables;
drop database daily;

create table userCheck(
	Login_id char(15) primary key,
    chat_room varchar(20) 
);
drop table userCheck;
create table User(
   user_id char(15) primary key,
    user_name varchar(16),
    user_pw varchar(16)
)engine innoDB DEFAULT CHARSET=utf8; -- 회원리스트
drop table user;
create table Chat_list(
	user_id char(15),
    friend_id char(15),
	friend_name varchar(16),
    foreign key(user_id) references User(user_id)
)engine innoDB DEFAULT CHARSET=utf8; -- 친구리스트 
drop table Chat_list;
create table Room (
   room_index int primary key auto_increment,
    room_name varchar(20),
    room_member int
)engine innoDB DEFAULT CHARSET=utf8; -- 방 리스트
drop table room;
create table Chatting(
   chatting_index int,
   user_name text,
   user_text text
)engine innoDB DEFAULT CHARSET=utf8; -- 채팅방text

drop table chatting;
create table User_chat (
   chat_index int ,
    user_id char(15),
    user_name varchar(16),
    foreign key (chat_index) references room (room_index)
)engine innoDB DEFAULT CHARSET=utf8; -- 채팅방맴버
drop table User_chat;

update User set user_name = '테스트1' where user_id = 'user1';

delete from chat_list where friend_id = 'kjund1';
delete from chat_list;
delete from user;
delete from user_chat;
delete from room;
delete from chatting;
delete from userCheck;

select * from userCheck;
select * from chatting;
select * from user ;
select * from chat_list;
select * from room;
select * from room;
select * from user_chat;
select * from Chat_list where user_id = 'kjund123';
select * from user where user_id != 'kjund123';
select * from user where user_id ='user1' or user_id ='user2';

insert into User values('user1','test1','1234');
insert into User values('user2','test2','1234');
insert into User values('user3','test3','1234');
insert into User values('user4','test4','1234');
insert into User values('user5,','test5','1234');
insert into user values('qorghkfkd','qorghkfkd','1');
insert into user values('dbtjdwo','dbtjdwo','1');
insert into user values('wjsdndwo','wjsdndwo','1');
insert into user values('dnjsdbsgml','dnjsdbsgml','1');
insert into user values('wkdwnfl','wkdwnfl','1');