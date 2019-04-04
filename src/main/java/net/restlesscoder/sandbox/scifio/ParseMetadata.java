
package net.restlesscoder.sandbox.scifio;

import io.scif.FieldPrinter;
import io.scif.Format;
import io.scif.ImageMetadata;
import io.scif.Metadata;

import java.io.File;

import net.imagej.ImageJ;

import org.scijava.io.location.FileLocation;

public class ParseMetadata {

	public static void main(final String... args) throws Exception {
		ImageJ ij = new ImageJ();
		File file = ij.ui().chooseFile(null, null);
		String filePath = file.getAbsolutePath();
		Format format = ij.scifio().format().getFormat(new FileLocation(filePath));
		Metadata metadata = format.createParser().parse(new FileLocation(filePath));
		System.out.println(new FieldPrinter(metadata));
		ImageMetadata imageMeta = metadata.get(0);
		System.out.println(imageMeta);
	}
}
