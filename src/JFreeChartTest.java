
import java.awt.Color;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.ui.RectangleInsets;

/**
 *
 * @author fdot
 */
public class JFreeChartTest
{

    public static void main(String[] args)
    {
        DefaultXYDataset data = new DefaultXYDataset();
        double[][] values = new double[2][2];
        
        double[] Xs = {1,2,3};
        double[] Ys = {1,2,3};
        values[0] = Xs;
        values[1] = Ys;
        
        data.addSeries("Test", values);
        JFreeChart chart = ChartFactory.createScatterPlot("Time vs Cost", "Time", "Cost", data, PlotOrientation.HORIZONTAL, false, false, false);
        
        chart.setBackgroundPaint(Color.white);

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);

        XYItemRenderer r = plot.getRenderer();
        if (r instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
            renderer.setBaseShapesVisible(true);
            renderer.setBaseShapesFilled(true);
            renderer.setDrawSeriesLineAsPath(true);
        }
        
        
        ChartPanel panel = new ChartPanel(chart);
        panel.setFillZoomRectangle(true);
        panel.setMouseWheelEnabled(true);
        
        JFrame frame = new JFrame("Test");
        frame.setSize(750, 750);
        frame.add(panel);
        frame.setVisible(true);
    }
}
