package net.restlesscoder.sandbox.imagej;

import net.imagej.ImageJ;
import net.imagej.ImgPlus;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.roi.geom.real.DefaultWritableRealPointCollection;
import net.imglib2.roi.geom.real.RealPointCollection;
import net.imglib2.img.Img;
import net.imagej.roi.DefaultROITree;

import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Points {
    private static void run() throws IOException {
        ImageJ ij = new ImageJ();

        // open blobs
        Img img = (Img) ij.io().open("/home/curtis/data/imagej/blobs.gif");
        ImgPlus imgPlus = new ImgPlus(img);

        // create points
        List<RealLocalizable> points =  new ArrayList<>(5);
        points.add(new RealPoint(new double[] {50, 200}));
        points.add(new RealPoint(new double[] {180, 90}));
        points.add(new RealPoint(new double[] {62, 76}));
        points.add(new RealPoint(new double[] {90, 110}));
        points.add(new RealPoint(new double[] {12, 80}));
        RealPointCollection< ? > rpc = new DefaultWritableRealPointCollection<>(points);
        
        // create ROITree and add points
        final DefaultROITree tree = new DefaultROITree();
        tree.addROIs(Arrays.asList(rpc));

        // create metadata map
        Map<String, DefaultROITree> metadata = new HashMap<>();
        metadata.put("rois", tree);

        // attach metadata to the imgplus
        imgPlus.getProperties().putAll(metadata);

        // show blobs
        ij.ui().show(imgPlus);
    }

    public static void main(String...args) throws IOException {
        run();
    }
}
