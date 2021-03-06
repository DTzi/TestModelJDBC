# TestModelJDBC

# Requirements
Scala 2.11.12  
PostgreSQL JDBC Driver postgresql-42.2.22  
openmodbat-3.2   

# How to Compile and Run
Compile: scalac -cp openmodbat-3.2.jar ModelTemplate.scala dbSim.scala

Run: scala -cp postgresql-42.2.22.jar:.: openmodbat-3.2.jar --classpath=. modbat.ModelTemplate -n=100

# PostgreSQL Mutation Testing Configuration

1. tar xvfz postgresql-10.5.tar.gz  
2. cd postgresql-10.5  
3. ./configure --prefix=postgresql-10.5 
4. make  
5. make install  
6. mkdir data  
7. postgresql-10.5/bin/initdb -D /Users/Username/Desktop/postgresql-10.5/data  
8. postgresql-10.5/bin/pg_ctl -D /Users/Username/Desktop/postgresql-10.5/data -l logfile start  
9. postgresql-10.5/bin/createdb test  
10.postgresql-10.5/bin/psql test  
11. make installcheck

# PostgreSQL Test Coverage Examination

./configure --enable-coverage --prefix=postgresql-10.5  
make check # or other test suite  
make coverage-html  

To reset the execution counts between test runs, run:  
make coverage-clean

# Generate dot file for Visualization

 scala -cp postgresql-42.2.22.jar:.:  openmodbat-3.2.jar --classpath=. --mode=dot modbat.ModelTemplate 


