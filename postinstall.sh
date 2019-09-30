#!/bin/bash
tar xvfz postgresql-10.5.tar.gz
cd postgresql-10.5
./configure --prefix=/Users/najinblue3/Desktop/mut/postgresql-10.5 
make
make install
mkdir data
/Users/najinblue3/Desktop/mut/postgresql-10.5/bin/initdb -D /Users/najinblue3/Desktop/mut/postgresql-10.5/data 
/Users/najinblue3/Desktop/mut/postgresql-10.5/bin/pg_ctl -D /Users/najinblue3/Desktop/mut/postgresql-10.5/data -l logfile start
/Users/najinblue3/Desktop/mut/postgresql-10.5/bin/createdb test
