Using path finding to solve a type of puzzle that occurs 
in many video games. The basic version that we will be dealing with is this:

.....0...S

....0..... 

0.....0..0 

...0....0. 

.F......0.

.0........ 

.......0..

.0.0..0..0 

0......... 

.00.....0. 

The player starts at the location labelled “S” and wants to reach the finish, labelled “F”. Each 
turn they choose one of the four cardinal directions to move. However, except for S and F the 
floor is covered in frictionless ice, so they will keep sliding in the chosen direction until they 
hit the wall surrounding the area, or one of the rocks (labelled “0”). For example, starting in 
the map given above:

.....0...@ 

....0..... 

0.....0..0 

...0....0. 

.F......0. 

.0........

.......0.. 

.0.0..0..0 

0......... 

.00.....0. 

the player (“@”) moving left would end up here:

.....0@..S 

....0..... 

0.....0..0 

...0....0. 

.F......0.

.0........ 

.......0.. 

.0.0..0..0 

0......... 

.00.....0. 

So we are dealing with the problem of finding a path from S to F, but the reachability relation 
between points is not the usual one.
