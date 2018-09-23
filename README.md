# TestModelJDBC

Uploaded Model with Transitions for create_table, add_columns, add_data.

TODO: Add Modbat's Random_number generator, Add Methods for Pks, Fks, ChangeDataType, Joins.

Compile: scalac -cp openmodbat-3.2.jar ModelTemplate.scala

Run: scala /modbat/postgresql-42.2.22.jar:.:  openmodbat-3.2.jar --classpath=. modbat.ModelTemplate -n=1
