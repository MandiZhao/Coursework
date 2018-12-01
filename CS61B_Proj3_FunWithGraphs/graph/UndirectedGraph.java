package graph;

/* See restrictions in Graph.java. */

import java.util.ArrayList;
import java.util.Iterator;

/** Represents an undirected graph.  Out edges and in edges are not
 *  distinguished.  Likewise for successors and predecessors.
 *
 *  @author Mandi Zhao
 */
public class UndirectedGraph extends GraphObj {

    @Override
    public boolean isDirected() {
        return false;
    }

    @Override
    public int inDegree(int v) {
        // FIXME
        int count = 0;
        for (int[] item : edges()) { //in undirected graph we make one edge represented by only one pair
            if (item[0] == v
                || item[1] == v) count++;
        }
        return count;
    }

    @Override
    public Iteration<Integer> predecessors(int v) { //use _edges
        // FIXME
        ArrayList<Integer> empty = new ArrayList<>();
        if (!contains(v)) return Iteration.iteration(empty);
        Iterator<int[]> allEdge = edges();
        while (allEdge.hasNext()) {
            int[] edge = allEdge.next();
            if (edge[0] == v && edge[1] == v) {
                empty.add(edge[1]);
            } else if (edge[0] == v) {
                empty.add(edge[1]);
            } else if (edge[1] == v) {
                empty.add(edge[0]);
            }
        }
        return Iteration.iteration(empty);
    }

    // FIXME

}
