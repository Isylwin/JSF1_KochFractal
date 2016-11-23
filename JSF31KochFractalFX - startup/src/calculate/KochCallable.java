package calculate;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

/**
 * Created by Oscar on 05-Oct-16.
 */
public class KochCallable implements Callable<List<Edge>>, Observer {
    private KochFractal koch;
    private KochManager manager;

    private KochEdgeMode mode;
    private List<Edge> edges;

    private boolean isCancelled;

    public KochCallable(KochEdgeMode mode, KochManager manager, int level) {
        this.koch = new KochFractal();
        this.koch.addObserver(this);
        this.mode = mode;
        this.edges = new ArrayList<>();
        this.manager = manager;
        this.koch.setLevel(level);
    }

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    @Override
    public List<Edge> call() throws Exception {
        isCancelled = false;
        switch (mode) {
            case Left:
                this.koch.generateLeftEdge();
                break;
            case Right:
                this.koch.generateRightEdge();
                break;
            case Bottom:
                this.koch.generateBottomEdge();
                break;
        }

        try {
            return edges;
        } finally {
            manager.await();
        }
    }

    /**
     * This method is called whenever the observed object is changed. An
     * application calls an <tt>Observable</tt> object's
     * <code>notifyObservers</code> method to have all the object's
     * observers notified of the change.
     *
     * @param o   the observable object.
     * @param arg an argument passed to the <code>notifyObservers</code>
     */
    @Override
    public synchronized void update(Observable o, Object arg) {
        edges.add((Edge) arg);
    }
}
