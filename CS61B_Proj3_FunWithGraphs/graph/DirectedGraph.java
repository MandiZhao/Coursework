package graph;

/* See restrictions in Graph.java. */

import java.util.ArrayList;

/** Represents a general unlabeled directed graph whose vertices are denoted by
 *  positive integers. Graphs may have self edges.
 *
 *  @author Mandi Zhao
 */
public class DirectedGraph extends GraphObj {

    @Override
    public boolean isDirected() {
        return true;
    }

    @Override
    public int inDegree(int v) {
        // FIXME
        int result = 0;
        for (int i = 1; i <= maxVertex(); i++) {
            if (contains(i, v)) {
                result++;
            }
        }
        return result;
    }

    @Override
    public Iteration<Integer> predecessors(int v) {
        // FIXME

        ArrayList<Integer> temp = new ArrayList<>();
        if (!contains(v)) return Iteration.iteration(temp);
        Iteration<int[]> pairs = edges();
        for (int[] item : pairs) {
            if (item[1] == v) temp.add(item[0]);
        }
        return Iteration.iteration(temp);
    }

    // FIXME

}
