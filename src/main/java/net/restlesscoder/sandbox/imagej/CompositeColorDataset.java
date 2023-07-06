package net.restlesscoder.sandbox.imagej;

import net.imagej.Dataset;
import net.imagej.ImageJ;
import net.imagej.ImgPlus;
import net.imagej.axis.Axes;
import net.imagej.display.ColorTables;
import net.imglib2.img.Img;
import net.imglib2.img.planar.PlanarImgs;
import net.imglib2.type.numeric.integer.UnsignedByteType;

public class CompositeColorDataset {
    public static void main(String... args) {
        ImageJ ij = new ImageJ();
        ij.ui().showUI();

        int width = 384, height = 256, channels = 3;
        Img<UnsignedByteType> img = PlanarImgs.unsignedBytes(width, height, channels);
        for (int y=0; y<height; y++) {
            for (int x=0; x<width; x++) {
                img.getAt(x, y, 0).setReal(255 * Math.sin(Math.PI * (y + x) / (width + height)));
                img.getAt(x, y, 1).setReal(255 * Math.sqrt((double) (y * x) / (width * height)));
                img.getAt(x, y, 2).setReal(255 * Math.max(x, y) / Math.max(width, height));
            }
        }

        ImgPlus<UnsignedByteType> imgPlus = new ImgPlus<>(img);
        imgPlus.axis(0).setType(Axes.X);
        imgPlus.axis(1).setType(Axes.Y);
        imgPlus.axis(2).setType(Axes.CHANNEL);
        imgPlus.initializeColorTables(3);
        imgPlus.setColorTable(ColorTables.RED, 0);
        imgPlus.setColorTable(ColorTables.GREEN, 1);
        imgPlus.setColorTable(ColorTables.BLUE, 2);
        imgPlus.setCompositeChannelCount(3);

        Dataset dataset = ij.dataset().create(imgPlus);
        dataset.setRGBMerged(true);

        ij.ui().show(dataset);
    }
}
