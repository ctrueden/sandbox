
package net.restlesscoder.sandbox.ome;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import loci.common.services.ServiceException;
import loci.common.xml.XMLTools;
import loci.formats.services.OMEXMLService;
import loci.formats.services.OMEXMLServiceImpl;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Recapitulates * {@link OMEXMLService#transformToLatestVersion(String)} to
 * better understand what is happening at each step of the way.
 */
public class OMESchemaTransforms {

	public static void main(final String... args) throws Exception {
		System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");

		final String path = "/Users/curtis/Desktop/ome-pretty.xml";
		final String xml = new String(Files.readAllBytes(Paths.get(path)), //
			Charset.forName("UTF-8"));
		System.out.println("OME-XML is " + xml.length() + " characters.");

		transformToLatestVersion(xml);
		System.out.println("== WE DID IT ==");
	}

	private static final String XSLT_PATH = "/transforms/";
	private static final String XSLT_2003FC = XSLT_PATH +
		"2003-FC-to-2008-09.xsl";
	private static final String XSLT_200706 = XSLT_PATH +
		"2007-06-to-2008-09.xsl";
	private static final String XSLT_200802 = XSLT_PATH +
		"2008-02-to-2008-09.xsl";
	private static final String XSLT_200809 = XSLT_PATH +
		"2008-09-to-2009-09.xsl";
	private static final String XSLT_200909 = XSLT_PATH +
		"2009-09-to-2010-04.xsl";
	private static final String XSLT_201004 = XSLT_PATH +
		"2010-04-to-2010-06.xsl";
	private static final String XSLT_201006 = XSLT_PATH +
		"2010-06-to-2011-06.xsl";
	private static final String XSLT_201106 = XSLT_PATH +
		"2011-06-to-2012-06.xsl";
	private static final String XSLT_201206 = XSLT_PATH +
		"2012-06-to-2013-06.xsl";
	private static final String XSLT_201306 = XSLT_PATH +
		"2013-06-to-2015-01.xsl";
	private static final String XSLT_201501 = XSLT_PATH +
		"2015-01-to-2016-06.xsl";

	private static Templates update2003FC, update200706, update200802,
			update200809, update200909, update201004, update201006, update201106,
			update201206, update201306, update201501;

	/** @see OMEXMLService#transformToLatestVersion(String) */
	public static String transformToLatestVersion(String xml)
		throws ServiceException
	{
		final OMEXMLServiceImpl omexmlService = new OMEXMLServiceImpl();
		final String version = omexmlService.getOMEXMLVersion(xml);
		if (null == version) {
			throw new ServiceException("Could not get OME-XML version");
		}
		if (version.equals(omexmlService.getLatestVersion())) return xml;
		System.out.println("Attempting to update XML with version: " + version);
		System.out.println("Initial dump: " + dump(xml));

		String transformed = null;
		try {
			if (version.equals("2003-FC")) {
				xml = verifyOMENamespace(xml);
				System.out.println("Running UPDATE_2003FC stylesheet.");
				if (update2003FC == null) {
					update2003FC = XMLTools.getStylesheet(XSLT_2003FC,
						OMEXMLServiceImpl.class);
				}
				transformed = transform(xml, update2003FC);
			}
			else if (version.equals("2007-06")) {
				xml = verifyOMENamespace(xml);
				System.out.println("Running UPDATE_200706 stylesheet.");
				if (update200706 == null) {
					update200706 = XMLTools.getStylesheet(XSLT_200706,
						OMEXMLServiceImpl.class);
				}
				transformed = transform(xml, update200706);
			}
			else if (version.equals("2008-02")) {
				xml = verifyOMENamespace(xml);
				System.out.println("Running UPDATE_200802 stylesheet.");
				if (update200802 == null) {
					update200802 = XMLTools.getStylesheet(XSLT_200802,
						OMEXMLServiceImpl.class);
				}
				transformed = transform(xml, update200802);
			}
			else transformed = xml;
			System.out.println("== XML updated to at least 2008-09 ==\n" + dump(
				transformed));

			if (version.compareTo("2009-09") < 0) {
				transformed = verifyOMENamespace(transformed);
				System.out.println("Running UPDATE_200809 stylesheet.");
				if (update200809 == null) {
					update200809 = XMLTools.getStylesheet(XSLT_200809,
						OMEXMLServiceImpl.class);
				}
				transformed = transform(transformed, update200809);
			}
			System.out.println("== XML updated to at least 2009-09 ==\n" + dump(
				transformed));

			if (version.compareTo("2010-04") < 0) {
				transformed = verifyOMENamespace(transformed);
				System.out.println("Running UPDATE_200909 stylesheet.");
				if (update200909 == null) {
					update200909 = XMLTools.getStylesheet(XSLT_200909,
						OMEXMLServiceImpl.class);
				}
				transformed = transform(transformed, update200909);
			}
			else transformed = xml;
			System.out.println("== XML updated to at least 2010-04 ==\n" + dump(
				transformed));

			if (version.compareTo("2010-06") < 0) {
				transformed = verifyOMENamespace(transformed);
				System.out.println("Running UPDATE_201004 stylesheet.");
				if (update201004 == null) {
					update201004 = XMLTools.getStylesheet(XSLT_201004,
						OMEXMLServiceImpl.class);
				}
				transformed = transform(transformed, update201004);
			}
			else transformed = xml;
			System.out.println("== XML updated to at least 2010-06 ==\n" + dump(
				transformed));

			if (version.compareTo("2011-06") < 0) {
				transformed = verifyOMENamespace(transformed);
				System.out.println("Running UPDATE_201006 stylesheet.");
				if (update201006 == null) {
					update201006 = XMLTools.getStylesheet(XSLT_201006,
						OMEXMLServiceImpl.class);
				}
				transformed = transform(transformed, update201006);
			}
			else transformed = xml;
			System.out.println("== XML updated to at least 2011-06 ==\n" + dump(
				transformed));

			if (version.compareTo("2012-06") < 0) {
				transformed = verifyOMENamespace(transformed);
				System.out.println("Running UPDATE_201106 stylesheet.");
				if (update201106 == null) {
					update201106 = XMLTools.getStylesheet(XSLT_201106,
						OMEXMLServiceImpl.class);
				}
				transformed = transform(transformed, update201106);
			}
			else transformed = xml;
			System.out.println("== XML updated to at least 2012-06 ==\n" + dump(
				transformed));

			if (version.compareTo("2013-06") < 0) {
				transformed = verifyOMENamespace(transformed);
				System.out.println("Running UPDATE_201206 stylesheet.");
				if (update201206 == null) {
					update201206 = XMLTools.getStylesheet(XSLT_201206,
						OMEXMLServiceImpl.class);
				}
				transformed = transform(transformed, update201206);
			}
			else transformed = xml;
			System.out.println("== XML updated to at least 2013-06 ==\n" + dump(
				transformed));

			if (version.compareTo("2015-01") < 0) {
				transformed = verifyOMENamespace(transformed);
				System.out.println("Running UPDATE_201306 stylesheet.");
				if (update201306 == null) {
					update201306 = XMLTools.getStylesheet(XSLT_201306,
						OMEXMLServiceImpl.class);
				}
				transformed = transform(transformed, update201306);
			}
			else transformed = xml;
			System.out.println("== XML updated to at least 2015-01 ==\n" + dump(
				transformed));

			if (version.compareTo("2016-06") < 0) {
				transformed = verifyOMENamespace(transformed);
				System.out.println("Running UPDATE_201501 stylesheet.");
				if (update201501 == null) {
					update201501 = XMLTools.getStylesheet(XSLT_201501,
						OMEXMLServiceImpl.class);
				}
				transformed = transform(transformed, update201501);
			}
			else transformed = xml;
			System.out.println("== XML updated to at least 2016-06 ==\n" + dump(
				transformed));

			// fix namespaces
			transformed = stripNamespaces(transformed);
			System.out.println("== Final transformed XML dump ==\n" + transformed);
			return transformed;
		}
		catch (final IOException e) {
			System.out.println("Could not transform version " + version +
				" OME-XML.");
		}
		return null;
	}

	private static String transform(String xml, Templates xslt) throws IOException {
		return stripNamespaces(XMLTools.transformXML(xml, xslt));
//		return XMLTools.transformXML(xml, xslt);
	}

	private static String dump(final String xml) {
//		return "";
  	return stripNamespaces(xml);
	}

	private static String stripNamespaces(String transformed) {
		transformed = transformed.replaceAll("<ns.*?:", "<");
		transformed = transformed.replaceAll("xmlns:ns.*?=", "xmlns:OME=");
		transformed = transformed.replaceAll("</ns.*?:", "</");
		return transformed;
	}

	/** Ensures that an xmlns:ome element exists. */
	private static String verifyOMENamespace(final String xml) {
		try {
			final Document doc = XMLTools.parseDOM(xml);
			final Element e = doc.getDocumentElement();
			final String omeNamespace = e.getAttribute("xmlns:ome");
			if (omeNamespace == null || omeNamespace.equals("")) {
				e.setAttribute("xmlns:ome", e.getAttribute("xmlns"));
			}
			return XMLTools.getXML(doc);
		}
		catch (final ParserConfigurationException pce) {}
		catch (final TransformerConfigurationException tce) {}
		catch (final TransformerException te) {}
		catch (final SAXException se) {}
		catch (final IOException ioe) {}
		return null;
	}

}
