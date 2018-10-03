# TestModelJDBC

TODO: Add Modbat's Random_number generator, Add Methods for Fks, Joins.

Added two new ways of generating test-data.

Insert query changed from one to multiple Rows.

Need to fix change_dataType method.

Compile: scalac -cp openmodbat-3.2.jar ModelTemplate.scala

Run: scala /modbat/postgresql-42.2.22.jar:.:  openmodbat-3.2.jar --classpath=. modbat.ModelTemplate -n=1
