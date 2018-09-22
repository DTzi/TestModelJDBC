# TestModelJDBC

Uploaded Model with Transitions for create_table, add_columns, add_data.
Save the random_numbers in a Vector.

TODO. Add Modbat's Random_number generator function.
TODO. Add Methods for Pks, Fks, ChangeDataType, Joins.

Compile: scalac -cp openmodbat-3.2.jar ModelTemplate.scala

Run: scala /modbat/postgresql-42.2.22.jar:.:  openmodbat-3.2.jar --classpath=. modbat.ModelTemplate -n=1
