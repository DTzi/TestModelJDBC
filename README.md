# TestModelJDBC

Compile: scalac -cp openmodbat-3.2.jar ModelTemplate.scala dbSim.scala

Run: scala -cp /Users/Username/Desktop/modbat/postgresql-42.2.22.jar:.: openmodbat-3.2.jar --classpath=. modbat.ModelTemplate -n=100

# Mutation Testing Configuration

1. tar xvfz postgresql-11.2.tar.gz  
2. cd postgresql-11.2  
3. ./configure --prefix=/Users/Username/Desktop/postgresql-11.2  
4. make  
5. make install  
6. mkdir /Users/Username/Desktop/postgresql-11.2/data  
7. initdb -D /Users/Username/Desktop/postgresql-11.2/data  
8. pg_ctl -D /Users/Username/Desktop/postgresql-11.2/data -l logfile start  
9. /Users/Username/Desktop/postgresql-11.2/bin/createdb test  
10. /Users/Username/Desktop/postgresql-11.2/bin/psql test  

# Running the Tests  

1. Running the Tests Against a Temporary Installation  
make check  
2. Running the Tests Against an Existing Installation  
make installcheck

