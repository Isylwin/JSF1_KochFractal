package calculate;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Oscar on 28-Sep-16.
 */
public class KochRunnable implements Runnable, Observer {
    private KochFractal koch;
    private KochEdgeMode mode;
    private List<Edge> edges;
    private KochManager manager;
    private boolean isCancelled;


    public KochRunnable(KochFractal koch, KochEdgeMode mode, List<Edge> edges, KochManager manager) {
        this.koch = koch;
        this.mode = mode;
        this.edges = edges;
        this.manager = manager;
        koch.addObserver(this);
    }

    public void setLevel(int lvl) {
        koch.setLevel(lvl);
    }

    public void cancelCalculation() {
        koch.cancel();
        isCancelled = true;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        isCancelled = false;
        switch (mode) {
            case Left:
                koch.generateLeftEdge();
                break;
            case Right:
                koch.generateRightEdge();
                break;
            case Bottom:
                koch.generateBottomEdge();
                break;
        }

        if(!isCancelled) {
            manager.increaseCount();
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
    public void update(Observable o, Object arg) {
           manager.addEdge((Edge) arg);
    }
}
