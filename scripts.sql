create table socks(
id bigint primary key auto_increment,
color varchar(20) not null,
cotton_part int check (cotton_part > 0 and cotton_part <=100) not null,
quantity int check (quantity >= 0) not null
);
