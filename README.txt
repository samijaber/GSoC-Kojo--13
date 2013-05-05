Simple prototype on connecting to a VM (for debugging purposes). So far, it simply connects ProgramA to the Wrapper for factorial.

---------------------------------
How to run:

1) java -Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=y -cp ".:scala- library.jar" Wrapper

2) scala ProgramA 8000
