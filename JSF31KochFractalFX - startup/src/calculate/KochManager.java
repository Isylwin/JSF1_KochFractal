package calculate;

import javafx.application.Platform;
import jsf31kochfractalfx.JSF31KochFractalFX;
import timeutil.TimeStamp;

import java.util.*;

/**
 * Created by Oscar on 21-Sep-16.
 */
public class KochManager {

    private JSF31KochFractalFX application;
    private List<Edge> edges;
    private int threadCount;

    private KochRunnable leftEdge;
    private KochRunnable rightEdge;
    private KochRunnable bottomEdge;

    private TimeStamp tsCalc;

    public KochManager(JSF31KochFractalFX application) {
        this.application = application;
        edges = new ArrayList(1000000);

        leftEdge = new KochRunnable(new KochFractal(), KochEdgeMode.Left, edges, this);
        rightEdge = new KochRunnable(new KochFractal(), KochEdgeMode.Right, edges, this);
        bottomEdge = new KochRunnable(new KochFractal(), KochEdgeMode.Bottom, edges, this);
    }

    public void changeLevel(int nxt) {
        edges.clear();
        leftEdge.cancelCalculation();
        rightEdge.cancelCalculation();
        bottomEdge.cancelCalculation();
        threadCount = 0;

        leftEdge.setLevel(nxt);
        rightEdge.setLevel(nxt);
        bottomEdge.setLevel(nxt);

        Thread leftThread = new Thread(leftEdge);
        Thread rightThread = new Thread(rightEdge);
        Thread bottomThread = new Thread(bottomEdge);

        tsCalc = new TimeStamp();
        tsCalc.setBegin("Start Calculating edges.");

        application.setTextCalc("Calculating...");
        application.setTextDraw("Calculating...");
        leftThread.start();
        rightThread.start();
        bottomThread.start();
    }

    public void drawEdges() {
        application.setTextCalc(tsCalc.toString());

        application.clearKochPanel();

        TimeStamp ts = new TimeStamp();

        ts.setBegin("Start drawing fractals");
        edges.forEach(application::drawEdge);
        ts.setEnd("End drawing fractals");

        application.setTextNrEdges(edges.size() + "");
        application.setTextDraw(ts.toString());
    }

    public synchronized void increaseCount() {
        threadCount++;
        if(threadCount == 3) {
            tsCalc.setEndBegin("End Calculating edges");
            application.requestDrawEdges();
        }
    }

    public synchronized void addEdge(Edge e) {
        edges.add(e);
    }
}
