#!/bin/bash
tar xvfz postgresql-10.5.tar.gz
cd postgresql-10.5
./configure --enable-coverage --prefix=/Users/Username/Desktop/mut/Psql/postgresql-10.5 
make
make install
mkdir data
/Users/Username/Desktop/mut/postgresql-10.5/bin/initdb -D /Users/Username/Desktop/mut/postgresql-10.5/data 
/Users/Username/Desktop/mut/postgresql-10.5/bin/pg_ctl -D /Users/Username/Desktop/mut/postgresql-10.5/data -l logfile start
/Users/Username/Desktop/mut/postgresql-10.5/bin/createdb test
