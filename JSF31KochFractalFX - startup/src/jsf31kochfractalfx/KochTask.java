package jsf31kochfractalfx;

import calculate.Edge;
import calculate.KochEdgeMode;
import calculate.KochFractal;
import calculate.KochManager;
import javafx.concurrent.Task;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * @author Oscar de Leeuw
 * @author Yannica
 */
public class KochTask extends Task<Void> implements Observer {
    private KochFractal koch;
    private KochManager manager;

    private KochEdgeMode mode;
    private long maxEdges;
    private long edgeCounter;
    private boolean isCancelled;

    public KochTask(KochManager manager, KochEdgeMode mode, int level) {
        this.koch = new KochFractal();
        this.koch.setLevel(level);
        this.koch.addObserver(this);

        this.manager = manager;
        this.mode = mode;

        this.maxEdges = koch.getNrOfEdges()/3;
    }

    @Override
    protected Void call() throws Exception {
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

        return null;
    }

    public void cancelTask() {
        this.koch.cancel();
        this.isCancelled = true;
    }

    @Override
    public void update(Observable o, Object arg) {
        Edge edge = (Edge) arg;

        manager.addEdge(edge);
        updateProgress(++edgeCounter,maxEdges);
        updateMessage("Nr edges: " + edgeCounter);
    }

    @Override
    protected void done() {
        if(!isCancelled) {
            manager.finishedTask();
        }
    }
}
