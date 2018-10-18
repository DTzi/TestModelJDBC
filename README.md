# TestModelJDBC

Transitions are now flexible. Added Joins, removed clunky code, added new transitions.

TODO: Rename transitions, add Exception Handling.

TODO: Add Modbat's Random_number generator.

Need to clean change_dataType transition.

Compile: scalac -cp openmodbat-3.2.jar ModelTemplate.scala

Run: scala /modbat/postgresql-42.2.22.jar:.:  openmodbat-3.2.jar --classpath=. modbat.ModelTemplate -n=1
