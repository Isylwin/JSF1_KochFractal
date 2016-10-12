package calculate;

import javafx.application.Platform;
import jsf31kochfractalfx.JSF31KochFractalFX;
import timeutil.TimeStamp;

import java.util.*;
import java.util.concurrent.*;

/**
 * Created by Oscar on 21-Sep-16.
 */
public class KochManager {
    private JSF31KochFractalFX application;
    private List<Future<List<Edge>>> calcResult;

    private ExecutorService threadPool;
    private CyclicBarrier barrier;
    //private List<KochRunnable> kochRunnables;

    private TimeStamp tsCalc;

    public KochManager(JSF31KochFractalFX application) {
        this.application = application;
        this.calcResult = new ArrayList<>();

        //this.kochRunnables = new ArrayList<>();
        this.threadPool = Executors.newFixedThreadPool(3);

        this.barrier = new CyclicBarrier(3, () -> {
            tsCalc.setEndBegin("End Calculating edges");
            application.requestDrawEdges();
        });

        //for(KochEdgeMode mode : KochEdgeMode.values()) {
            //kochRunnables.add(new KochRunnable(new KochFractal(), mode, edges, this));
        //}
    }

    public void changeLevel(int nxt) {
        calcResult.clear();
        barrier.reset();
        //kochRunnables.forEach(k -> k.cancelCalculation());

        for(KochEdgeMode mode : KochEdgeMode.values()) {
            calcResult.add(threadPool.submit(new KochCallable(mode, this, nxt)));
        }

        //kochRunnables.forEach(k -> k.setLevel(nxt));

        //kochRunnables.forEach(k -> threadPool.execute(k));

        tsCalc = new TimeStamp();
        tsCalc.setBegin("Start Calculating edges.");

        application.setTextCalc("Calculating...");
        application.setTextDraw("Calculating...");
    }

    public void drawEdges() {
        application.setTextCalc(tsCalc.toString());

        application.clearKochPanel();

        TimeStamp ts = new TimeStamp();

        List<Edge> edges = new ArrayList<>();
        calcResult.forEach(c -> {
            try {
                edges.addAll(c.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

        ts.setBegin("Start drawing fractals");
        edges.forEach(application::drawEdge);
        ts.setEnd("End drawing fractals");

        application.setTextNrEdges(edges.size() + "");
        application.setTextDraw(ts.toString());
    }

    public void await() {
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    /*public synchronized void increaseCount() {
        threadCount++;
        if(threadCount == 3) {

            application.requestDrawEdges();
        }
    }*/

    /*public synchronized void addEdge(Edge e) {
        edges.add(e);
    }*/
}
