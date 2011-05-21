package de.akquinet.commons.image.io.test;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

import de.akquinet.commons.image.io.Format;
import de.akquinet.commons.image.io.Image;
import de.akquinet.commons.image.io.ImageMetadata;
import de.akquinet.commons.image.io.ImageMetadata.Algorithm;
import de.akquinet.commons.image.io.ImageMetadata.ColorType;
import de.akquinet.commons.image.io.ImageMetadata.Orientation;

public class ImageMetadataTest {

    public static final File JPG = new File("src/test/resources/jpg/keepout-security-restricted-access.jpg");

    public static final File JPG_2 = new File("src/test/resources/jpg/IMG_6091.JPG");

    public static final File JPG_3 = new File("src/test/resources/jpg/P1000869.JPG");

    public static final File JPG_4 = new File("src/test/resources/jpg/flore.JPG");

    public static final File JPG_5 = new File("src/test/resources/jpg/clement.jpg");

    public static final File GIF = new File("src/test/resources/gif/PIC-iCarta-Small.gif");

    public static final File PNG = new File("src/test/resources/png/beastie.png");

    public static final File PNG_2 = new File("src/test/resources/png/wilber-huge-alpha.png");

    @Test
    public void testMetadataExtractionOnJPG() throws IOException {
        Image img = new Image(JPG);
        new ImageMetadata(img);

        img = new Image(JPG_2);
        new ImageMetadata(img);

        img = new Image(JPG_3);
        new ImageMetadata(img);

        img = new Image(JPG_4);
        new ImageMetadata(img);

        img = new Image(JPG_5);
        new ImageMetadata(img);
    }

    @Test
    public void testMetadataForJPG() throws IOException {
        Image img = new Image(JPG);
        ImageMetadata metadata = img.getMetadata();

        // Size + Format
        Assert.assertEquals(Format.JPEG, metadata.getFormat());
        Assert.assertEquals(Algorithm.JPEG, metadata.getAlgorithm());
        Assert.assertEquals("JPEG (Joint Photographic Experts Group) Format", metadata.getFormatName());
        Assert.assertEquals("Jpeg/JFIF v.1.1", metadata.getFormatDetails());
        Assert.assertEquals(500, metadata.getWidth());
        Assert.assertEquals(300, metadata.getHeight());

        // Creation date
        Assert.assertNotNull(metadata.getCreationDate());
        System.out.println(metadata.getCreationDate());

        // Color Type
        Assert.assertEquals(ColorType.RGB, metadata.getColorType());

        // DPI
        Assert.assertEquals(72, metadata.getDpiWidth());
        Assert.assertEquals(72, metadata.getDpiHeight());

        // Orientation
        Assert.assertEquals(1, metadata.getExifOrientation());
        Assert.assertEquals(Orientation.LANDSCAPE, metadata.getOrientation());

        // Misc details
        Assert.assertEquals(1, metadata.getNumberOfImages());
        Assert.assertEquals(24, metadata.getBitsPerPixel());

        // Missing metadata
        Assert.assertFalse(metadata.isProgressive());
        Assert.assertFalse(metadata.isTransparent());
        Assert.assertFalse(metadata.usesPalette());

        Assert.assertNull(metadata.getMake());
        Assert.assertNull(metadata.getModel());
    }

    @Test
    public void testMetadataForJPG_2() throws IOException {
        Image img = new Image(JPG_2);
        ImageMetadata metadata = img.getMetadata();

        // Size + Format
        Assert.assertEquals(Format.JPEG, metadata.getFormat());
        Assert.assertEquals(Algorithm.JPEG, metadata.getAlgorithm());
        Assert.assertEquals("JPEG (Joint Photographic Experts Group) Format", metadata.getFormatName());
        Assert.assertEquals(3888, metadata.getWidth());
        Assert.assertEquals(2592, metadata.getHeight());

        // Creation date
        Assert.assertNotNull(metadata.getCreationDate());
        System.out.println(metadata.getCreationDate());

        // Color Type
        Assert.assertEquals(ColorType.RGB, metadata.getColorType());

        // DPI
        Assert.assertEquals(72, metadata.getDpiWidth());
        Assert.assertEquals(72, metadata.getDpiHeight());

        // Orientation
        Assert.assertEquals(1, metadata.getExifOrientation());
        Assert.assertEquals(Orientation.LANDSCAPE, metadata.getOrientation());

        // Misc details
        Assert.assertEquals(1, metadata.getNumberOfImages());
        Assert.assertEquals(24, metadata.getBitsPerPixel());

        // Missing metadata
        Assert.assertFalse(metadata.isProgressive());
        Assert.assertFalse(metadata.isTransparent());
        Assert.assertFalse(metadata.usesPalette());

        Assert.assertNull(metadata.getLocalization());

        // Exif
        Assert.assertEquals("Canon", metadata.getMake());
        Assert.assertEquals("Canon EOS 400D DIGITAL", metadata.getModel());

    }

    @Test
    public void testMetadataForJPG_3() throws IOException {
        Image img = new Image(JPG_3);
        ImageMetadata metadata = img.getMetadata();

        // Size + Format
        Assert.assertEquals(Format.JPEG, metadata.getFormat());
        Assert.assertEquals(Algorithm.JPEG, metadata.getAlgorithm());
        Assert.assertEquals(4000, metadata.getWidth());
        Assert.assertEquals(3000, metadata.getHeight());

        // Creation date
        Assert.assertNotNull(metadata.getCreationDate());

        // Color Type
        Assert.assertEquals(ColorType.RGB, metadata.getColorType());

        // DPI
        Assert.assertEquals(180, metadata.getDpiWidth());
        Assert.assertEquals(180, metadata.getDpiHeight());

        // Orientation
        Assert.assertEquals(1, metadata.getExifOrientation());
        Assert.assertEquals(Orientation.LANDSCAPE, metadata.getOrientation());

        // Misc details
        Assert.assertEquals(1, metadata.getNumberOfImages());
        Assert.assertEquals(24, metadata.getBitsPerPixel());

        // Missing metadata
        Assert.assertFalse(metadata.isProgressive());
        Assert.assertFalse(metadata.isTransparent());
        Assert.assertFalse(metadata.usesPalette());

        Assert.assertEquals("Panasonic", metadata.getMake());
        Assert.assertEquals("DMC-FT1",metadata.getModel());
    }


    @Test
    public void testMetadataForJPG_4() throws IOException {
        Image img = new Image(JPG_4);
        ImageMetadata metadata = img.getMetadata();

        // Size + Format
        Assert.assertEquals(Format.JPEG, metadata.getFormat());
        Assert.assertEquals(Algorithm.JPEG, metadata.getAlgorithm());
        Assert.assertEquals(2592, metadata.getWidth());
        Assert.assertEquals(1936, metadata.getHeight());

        // Creation date
        Assert.assertNotNull(metadata.getCreationDate());

        // Color Type
        Assert.assertEquals(ColorType.RGB, metadata.getColorType());

        // DPI
        Assert.assertEquals(72, metadata.getDpiWidth());
        Assert.assertEquals(72, metadata.getDpiHeight());

        // Orientation
        Assert.assertEquals(6, metadata.getExifOrientation());
        Assert.assertEquals(Orientation.PORTRAIT, metadata.getOrientation());

        // Misc details
        Assert.assertEquals(1, metadata.getNumberOfImages());
        Assert.assertEquals(24, metadata.getBitsPerPixel());

        // Missing metadata
        Assert.assertFalse(metadata.isProgressive());
        Assert.assertFalse(metadata.isTransparent());
        Assert.assertFalse(metadata.usesPalette());

        Assert.assertEquals("Apple", metadata.getMake());
        Assert.assertEquals("iPhone 4",metadata.getModel());

        // Geolocalization
        Assert.assertNotNull(metadata.getLocalization());
        Assert.assertNotNull(metadata.getLocalization().latitudeRef);
        Assert.assertNotNull(metadata.getLocalization().longitudeRef);
        Assert.assertNotNull(metadata.getLocalization().latitudeDegrees);
        Assert.assertNotNull(metadata.getLocalization().longitudeDegrees);
        Assert.assertNotNull(metadata.getLocalization().latitudeMinutes);
        Assert.assertNotNull(metadata.getLocalization().longitudeMinutes);

    }

    @Test
    public void testMetadataForGIF() throws IOException {
        Image img = new Image(GIF);
        ImageMetadata metadata = img.getMetadata();

        // Size + Format
        Assert.assertEquals(Format.GIF, metadata.getFormat());
        Assert.assertEquals(Algorithm.LZW, metadata.getAlgorithm());
        Assert.assertEquals("GIF Graphics Interchange Format", metadata.getFormatName());
        Assert.assertEquals("Gif 89a", metadata.getFormatDetails());

        // Creation date
        Assert.assertNull(metadata.getCreationDate());

        // Color Type
        Assert.assertEquals(ColorType.RGB, metadata.getColorType());

        // DPI
        Assert.assertEquals(72, metadata.getDpiWidth());
        Assert.assertEquals(72, metadata.getDpiHeight());

        // Orientation
        Assert.assertEquals(Orientation.UNKNOWN, metadata.getOrientation());

        // Misc details
        Assert.assertEquals(21, metadata.getBitsPerPixel());

        Assert.assertFalse(metadata.isProgressive());
        Assert.assertTrue(metadata.isTransparent());
        Assert.assertTrue(metadata.usesPalette());

        Assert.assertNull(metadata.getMake());
        Assert.assertNull(metadata.getModel());
    }

    @Test
    public void testMetadataForPNG() throws IOException {
        Image img = new Image(PNG);
        ImageMetadata metadata = img.getMetadata();

        // Size + Format
        Assert.assertEquals(Format.PNG, metadata.getFormat());
        Assert.assertEquals(Algorithm.PNG, metadata.getAlgorithm());

        // Creation date
        Assert.assertNull(metadata.getCreationDate());

        // Color Type
        Assert.assertEquals(ColorType.RGB, metadata.getColorType());

        // DPI
        Assert.assertEquals(-1, metadata.getDpiWidth());
        Assert.assertEquals(-1, metadata.getDpiHeight());

        // Orientation
        Assert.assertEquals(Orientation.UNKNOWN, metadata.getOrientation());

        // Misc details
        Assert.assertEquals(24, metadata.getBitsPerPixel());

        Assert.assertFalse(metadata.isProgressive());
        Assert.assertFalse(metadata.isTransparent());
        Assert.assertFalse(metadata.usesPalette());

        Assert.assertNull(metadata.getMake());
        Assert.assertNull(metadata.getModel());
    }

    @Test
    public void testMetadataForPNG_2() throws IOException {
        Image img = new Image(PNG_2);
        ImageMetadata metadata = img.getMetadata();

        // Size + Format
        Assert.assertEquals(Format.PNG, metadata.getFormat());
        Assert.assertEquals(Algorithm.PNG, metadata.getAlgorithm());

        // Creation date
        Assert.assertNull(metadata.getCreationDate());

        // Color Type
        Assert.assertEquals(ColorType.RGB, metadata.getColorType());

        // DPI
        Assert.assertEquals(-1, metadata.getDpiWidth());
        Assert.assertEquals(-1, metadata.getDpiHeight());

        // Orientation
        Assert.assertEquals(Orientation.UNKNOWN, metadata.getOrientation());

        // Misc details
        Assert.assertEquals(32, metadata.getBitsPerPixel());

        Assert.assertFalse(metadata.isProgressive());
        Assert.assertTrue(metadata.isTransparent());
        Assert.assertFalse(metadata.usesPalette());

        Assert.assertNull(metadata.getMake());
        Assert.assertNull(metadata.getModel());
    }




}
