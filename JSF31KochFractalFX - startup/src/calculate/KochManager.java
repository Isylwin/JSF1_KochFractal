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
        edges.clear();
        koch.setLevel(nxt);

        TimeStamp ts = new TimeStamp();
        ts.setBegin("Start calculating edges");

        koch.generateLeftEdge();
        koch.generateBottomEdge();
        koch.generateRightEdge();

        ts.setEnd("End calculating edges");

        application.setTextCalc(ts.toString());
        application.setTextNrEdges(edges.size() + "");

        drawEdges();
    }

    public void drawEdges() {
        application.clearKochPanel();

        TimeStamp ts = new TimeStamp();

        ts.setBegin("Start drawing fractals");
        edges.forEach(application::drawEdge);
        ts.setEnd("End drawing fractals");

        application.setTextDraw(ts.toString());
    }

    @Override
    public void update(Observable o, Object arg) {
        edges.add((Edge) arg);
    }
}
