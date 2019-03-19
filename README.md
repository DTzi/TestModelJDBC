# TestModelJDBC

Compile: scalac -cp openmodbat-3.2.jar ModelTemplate.scala dbSim.scala

Run: scala -cp /Users/Username/Desktop/modbat/postgresql-42.2.22.jar:.: openmodbat-3.2.jar --classpath=. modbat.ModelTemplate -n=100

# Mutation Testing Configuration

1. tar xvfz postgresql-10.5.tar.gz  
2. cd postgresql-10.5  
3. ./configure --prefix=/Users/Username/Desktop/postgresql-10.5 
4. make  
5. make install  
6. mkdir data  
7. /Users/Username/Desktop/postgresql-10.5/bin/initdb -D /Users/Username/Desktop/postgresql-10.5/data  
8. /Users/Username/Desktop/postgresql-10.5/bin/pg_ctl -D /Users/Username/Desktop/postgresql-10.5/data -l logfile start  
9. /Users/Username/Desktop/postgresql-10.5/bin/createdb test  
10. /Users/Username/Desktop/postgresql-10.5/bin/psql test  
11. make installcheck

# Coverage

./configure --enable-coverage ... OTHER OPTIONS ...
make
make check # or other test suite
make coverage-html

To reset the execution counts between test runs, run:
make coverage-clean

