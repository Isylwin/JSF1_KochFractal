package calculate;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import jsf31kochfractalfx.JSF31KochFractalFX;
import jsf31kochfractalfx.KochTask;
import timeutil.TimeStamp;

import java.util.*;
import java.util.concurrent.*;

/**
 * Created by Oscar on 21-Sep-16.
 */
public class KochManager {
    private JSF31KochFractalFX application;
    //private List<Future<List<Edge>>> calcResult;
    private List<KochTask> tasks;

    private ExecutorService threadPool;
    private int threadCounter;
    //private CyclicBarrier barrier;

    private List<Edge> edges;

    private TimeStamp tsCalc;

    public KochManager(JSF31KochFractalFX application) {
        this.application = application;
        this.edges = new ArrayList<>();
        this.tasks = new ArrayList<>();

        this.threadPool = Executors.newFixedThreadPool(3);

        /*this.barrier = new CyclicBarrier(3, () -> {
            tsCalc.setEndBegin("End Calculating edges");
            application.requestDrawEdges();
        });*/

    }

    public void changeLevel(int nxt) {
        //barrier.reset();
        //kochRunnables.forEach(k -> k.cancelCalculation());
        tasks.forEach(KochTask::cancelTask);
        tasks.clear();
        edges.clear();
        threadCounter = 0;
        //application.clearKochPanel();


        for(KochEdgeMode mode : KochEdgeMode.values()) {
            //calcResult.add(threadPool.submit(new KochCallable(mode, this, nxt)));
            KochTask task = new KochTask(this, mode, nxt);

            tasks.add(task);
            threadPool.execute(task);
            application.bindProgressBar(mode, task);
        }

        tsCalc = new TimeStamp();
        tsCalc.setBegin("Start Calculating edges.");

        application.setTextCalc("Calculating...");
        application.setTextDraw("Calculating...");
    }

    public void drawEdges() {
        tsCalc.setEnd("End Calculating edges");
        application.setTextCalc(tsCalc.toString());

        application.clearKochPanel();

        TimeStamp ts = new TimeStamp();

        ts.setBegin("Start drawing fractals");
        edges.forEach(application::drawEdge);
        ts.setEnd("End drawing fractals");

        application.setTextNrEdges(edges.size() + "");
        application.setTextDraw(ts.toString());
    }

    public void await() {
        /*try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }*/
    }

    public synchronized void finishedTask() {
        if(++threadCounter == 3) {
            application.requestDrawEdges();
        }
    }

    public synchronized void addEdge(Edge edge) {
        edges.add(edge);
        Platform.runLater(() ->
                application.drawEdge(new Edge(edge.X1, edge.Y1, edge.X2, edge.Y2, Color.WHITE)));
                //application.drawEdge(edge));

        try {
            Thread.sleep(0, 10);
        } catch (InterruptedException e) {
            Platform.runLater(() ->
            application.clearKochPanel());
        }
    }
}
