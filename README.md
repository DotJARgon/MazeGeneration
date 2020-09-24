# MazeGeneration

This is a simple maze generator that utilizes effectively a random walk and a sort of reverse flood fill algorithm I developed (thought I am sure already exists).

We can think of the algorithm as follows (psuedo code):

1. while we have available nodes:
2.   pick a random node
3.   given node, and node has available empty nodes around it:
4.      pick a random node branching from this one
5.      add that node to given node's branches
6.      repeat step 3 given this new node

And that is really it, it is a simple algorithm, and means there are no cross roads, only one single path, which means this maze could theoretically be solved by keeping to one side
