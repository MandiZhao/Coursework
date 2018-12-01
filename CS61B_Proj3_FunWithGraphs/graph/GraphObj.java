package graph;

/* See restrictions in Graph.java. */

import afu.org.checkerframework.checker.igj.qual.I;

import java.util.ArrayList;
import java.util.Iterator;
import static java.util.Arrays.asList;

/** A partial implementation of Graph containing elements common to
 *  directed and undirected graphs.
 *
 *  @author Mandi Zhao
 */
abstract class GraphObj extends Graph {

    /** A new, empty Graph. */
    GraphObj() {
        _graph = new ArrayList<>();
        _edges = new ArrayList<>();
        ArrayList<Integer> dummy1 = new ArrayList<>();
        dummy1.add(0);
        _graph.add(dummy1);
        int[] dummy = {-1, -1};
        _edges.add(dummy);
        //biggest problem is that, remove() of arraylists shift the indexs, very fucked up
        // so instead of removal, gotta keep the indexs and set that position to something else instead
        // now a vertex "doesn't exit" iff the arraylist on that position is size one, containing only -index
        //Same for the list of edges.
        //summary: dummys here: {-1, -1} for removed edges;
        // size-one ArrayList containing -index if no vertex, 0 if isolated vertex.
        //         and the collection of target vertices??
        // FIXME
    }

    @Override
    public int vertexSize() {
        // FIXME
        int count = 0;
        for (ArrayList<Integer> item : _graph) {
            if (item.size() > 1) count++;
        }
        return count;
    }

    @Override
    public int maxVertex() {
        // FIXME
        int max = 0;
        for (ArrayList<Integer> item : _graph) {  //this is why headers need to be distinct
            int header = 0 - item.get(0);
            if (item.size() > 1) {
                max = header; //need both bigger header and non-empty i.e. at least 2
            }
        }
        return max;
    }

    @Override
    public int edgeSize() { //can't use .size() directly now
        // FIXME
        int count = 0;
        for (int[] pairs : _edges) {
            if (pairs[0] != -1 &&
                    pairs[1] != -1) {
                count++;
            }
        }
        return count;
    }

    @Override
    public abstract boolean isDirected();

    @Override
    public int outDegree(int v) {
        // FIXME
        if (!contains(v)) return 0;
        return _graph.get(v).size() - 2; //minus that identification number "-index"
    }

    @Override
    public abstract int inDegree(int v);

    @Override
    public boolean contains(int u) {
        // FIXME
        if (u > maxVertex()) return false;
        return _graph.get(u).size() > 1; //contains only the header
    }

    @Override
    public boolean contains(int u, int v) { //or just_graph.get(u)?
        // FIXME
        if (!contains(u) || !contains(v)) return false;
        if (!isDirected()
                && _graph.get(v).contains(u)) {
            return true;
        }
        return _graph.get(u).contains(v);
    }

    @Override
    public int add() {
        // FIXME
        int i = 1;
        int max = maxVertex();
        while (i <= max) {
            if (_graph.get(i).size() ==  1) { //empty v.s. one-element
                ArrayList<Integer> toadd = new ArrayList<>();
                toadd.add(-i);
                toadd.add(1, 0);
                _graph.set(i, toadd);
                return i;
            }
            i = i + 1;
        }
        ArrayList<Integer> toadd = new ArrayList<>();
        toadd.add(0, -i);
        toadd.add(1, 0); //exist but empty
        //System.out.println(-i);
        _graph.add(i, toadd);
        return i;
    }

    @Override
    public int add(int u, int v) { //for undirected, one edge gets added only once in _edges
        // but both vertices's destination list get added

        // FIXME
        assert (contains(u) && contains(v));
        if (contains(u, v)) return edgeId(u, v);
        _graph.get(u).add(v);
        int[] newEdge = {u, v};
        _edges.add(newEdge);
        if (!isDirected()) _graph.get(v).add(u);
        return _edges.size() - 1;
    }

    @Override
    public void remove(int v) { //remove from connected vertices, remove from _edges list, then remove self
        if (contains(v)) {
            ArrayList<Integer> got = _graph.get(v);
            Object[] array = got.toArray(); //{-index, some other index, some other, etc}
            int fixSize = array.length;
            for (int k = 2; k < fixSize; k++) {
                int obj = (int) array[k];
                remove(v, obj);
            }
            //for (int j = 1; j < fixSize; j++) { //got has header "-index", can't call size() repeatedly
                //System.out.println( "j is" + j + "size is" + got.size() + "destination" + got.get(j));
                //System.out.println( "v is " + v);
                //remove(v, got.get(j));
            //}
            for (int j = 1; j <= maxVertex(); j++) {
                remove(j, v);
            }
            ArrayList<Integer> empty = new ArrayList<>();
            empty.add(-v); //add header
            _graph.set(v, empty);
        }
        // FIXME
    }

    @Override
    public void remove(int u, int v) { //remove from edges list, remove from both vertices list
        if (!contains(u) || !contains(v)) return;
        for (int i = 1; i < _edges.size(); i++) {
            int[] item = _edges.get(i);
            int[] empty = {-1, -1};
            if (item[0] != -1 && item[1] != -1) {
                if (item[0] == u && item[1] == v) {
                    _graph.get(u).remove(Integer.valueOf(v));
                    //System.out.println(_graph.get(u).size());
                    //System.out.println(idx);
                    //aha, the integer for remove is confusing
                    //System.out.println(dests.get(0));
                    if (!isDirected()) {
                        _graph.get(v).remove(Integer.valueOf(u)); //remove the other end
                    }
                    _edges.set(i, empty);
                    break;
                } else if (!isDirected()) {
                    if (item[1] == u && item[0] == v) {
                        _graph.get(u).remove(Integer.valueOf(v));
                        _graph.get(v).remove(Integer.valueOf(u));
                        _edges.set(i, empty);
                    }
                }

            }
        }
        // FIXME
    }

    @Override
    public Iteration<Integer> vertices() {
        // FIXME
        ArrayList<Integer> temp = new ArrayList<>();
        for (ArrayList<Integer> item : _graph) {
            if (item.size() > 1) temp.add(-item.get(0));
        }
        return Iteration.iteration(temp);
    }

    @Override
    public Iteration<Integer> successors(int v) { //next vertices are stored but undirected?
        // FIXME
        ArrayList<Integer> empty = new ArrayList<>();
        if (!contains(v)) return Iteration.iteration(empty);
        Iterator<Integer> temp = _graph.get(v).iterator();
        temp.next(); //pop off index
        temp.next(); //pop off 0
        return Iteration.iteration(temp);
    }

    @Override
    public abstract Iteration<Integer> predecessors(int v);

    @Override
    public Iteration<int[]> edges() {
        // FIXME
        ArrayList<int[]> temp = new ArrayList<>();
        for (int[] item : _edges) {
            if (item[0] != -1
                    && item[1] != -1) {
                temp.add(item);
            }
        }
        return Iteration.iteration(temp);
    }

    @Override
    protected void checkMyVertex(int v) {
        if (v > maxVertex() || _graph.get(v) == null) {
            throw new IllegalArgumentException("vertex not in Graph");
        }
    }

    @Override
    protected int edgeId(int u, int v) {
        // FIXME
        if (contains(u, v)) {
            for (int[] item : _edges) {
                if (item[0] == u
                        && item[1] == v) {
                    return _edges.indexOf(item);
                }
            }
        }
        return 0;
    }

    /** The 2D ArrayList for storage of vertices and edges.
     * For the index's sake the zero-index is kept (thus
     * adding one more to the size) but always null. */
    private ArrayList<ArrayList<Integer>> _graph;

    /** The list of edges, ordered by time. */
    private ArrayList<int[]> _edges;
    // FIXME
}
