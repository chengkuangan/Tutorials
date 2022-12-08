#!/bin/bash
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" -d "$POSTGRES_DB"  <<-EOSQL
     create schema if not exists $SCHEMA;
     create table $SCHEMA.orders (
        orderid text PRIMARY KEY, 
        orderdate timestamptz NOT NULL, 
        sku text NOT NULL, 
        description text NOT NULL, 
        amount double precision NOT NULL);
     create table $SCHEMA.customer (
        custId integer PRIMARY KEY, 
        name text);
     create table $SCHEMA.customerOrders (
        custId integer, 
        orderid text,
        PRIMARY KEY (custId, orderid));
     insert into $SCHEMA.customer (custId, name) values (1, 'Andrea Dallas');
     insert into $SCHEMA.customer (custId, name) values (2, 'Jessica Bell');
     insert into $SCHEMA.customer (custId, name) values (3, 'Anita Albert');
     insert into $SCHEMA.customer (custId, name) values (4, 'Jane Pico');
     insert into $SCHEMA.customer (custId, name) values (5, 'Danny Flamingo');
     insert into $SCHEMA.customer (custId, name) values (6, 'Daniel White');
     insert into $SCHEMA.customer (custId, name) values (7, 'John Doe');
     insert into $SCHEMA.customer (custId, name) values (8, 'Jenny Doe');
     insert into $SCHEMA.customer (custId, name) values (9, 'Anabelle Watson');
     insert into $SCHEMA.customer (custId, name) values (10, 'Kenny Kent');
     insert into $SCHEMA.customer (custId, name) values (11, 'Petricia Louis');
     insert into $SCHEMA.customer (custId, name) values (12, 'Clark Kent');
     insert into $SCHEMA.customer (custId, name) values (13, 'Lara Craft');
     insert into $SCHEMA.customer (custId, name) values (14, 'Anderson Hilton');
     insert into $SCHEMA.customer (custId, name) values (15, 'London Hilton');
     insert into $SCHEMA.customer (custId, name) values (16, 'George Gold');
     insert into $SCHEMA.customer (custId, name) values (17, 'Mac Apple');
     insert into $SCHEMA.customer (custId, name) values (18, 'Wednesday Orange');
     insert into $SCHEMA.customer (custId, name) values (19, 'Bill Douglas');
     insert into $SCHEMA.customer (custId, name) values (20, 'Clinton Hill');
     insert into $SCHEMA.customer (custId, name) values (21, 'Monday Blue');
     insert into $SCHEMA.customer (custId, name) values (22, 'Sky High');
     insert into $SCHEMA.customer (custId, name) values (23, 'Ocean Deep');
     insert into $SCHEMA.customer (custId, name) values (24, 'Star Sparkling');
     insert into $SCHEMA.customer (custId, name) values (25, 'Space Unlimited');
     insert into $SCHEMA.customer (custId, name) values (26, 'Nano Macro');
     insert into $SCHEMA.customer (custId, name) values (27, 'Mini Migthy');
     insert into $SCHEMA.customer (custId, name) values (28, 'Huge Small');
     insert into $SCHEMA.customer (custId, name) values (29, 'Alfred Hugo');
     insert into $SCHEMA.customer (custId, name) values (30, 'Apple Green');
EOSQL