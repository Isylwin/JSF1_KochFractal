package calculate;

import jsf31kochfractalfx.JSF31KochFractalFX;
import timeutil.TimeStamp;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Oscar on 21-Sep-16.
 */
public class KochManager implements Observer {

    private JSF31KochFractalFX application;
    private KochFractal koch;
    private List<Edge> edges = new ArrayList<>();

    public KochManager(JSF31KochFractalFX application) {
        this.application = application;

        koch = new KochFractal();
        koch.addObserver(this);
    }

    public void changeLevel(int nxt) {
        koch.setLevel(nxt);
        TimeStamp ts = new TimeStamp();
        ts.setBegin("Start drawing fractals");
        drawEdges();
        ts.setEnd("End drawing fractals");
        application.setTextCalc(ts.toString());
        application.setTextNrEdges(edges.size() + "");
    }

    public void drawEdges() {
        application.clearKochPanel();
        edges.clear();

        koch.generateLeftEdge();
        koch.generateBottomEdge();
        koch.generateRightEdge();

        for(Edge randje : edges) {
            application.drawEdge(randje);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        edges.add((Edge) arg);
        //application.drawEdge((Edge) arg);
    }
}
