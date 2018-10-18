# TestModelJDBC

Transitions are now flexible. Added Joins, removed clunky code, cleaned code, added new transitions.

TODO: Rename transitions.

TODO: Add Modbat's Random_number generator.

Need to fix change_dataType Exception.

Compile: scalac -cp openmodbat-3.2.jar ModelTemplate.scala

Run: scala /modbat/postgresql-42.2.22.jar:.:  openmodbat-3.2.jar --classpath=. modbat.ModelTemplate -n=1
