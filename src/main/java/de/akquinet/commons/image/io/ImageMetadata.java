package de.akquinet.commons.image.io;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.imaging.ImageInfo;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.IImageMetadata;
import org.apache.commons.imaging.common.bytesource.ByteSource;
import org.apache.commons.imaging.common.bytesource.ByteSourceArray;
import org.apache.commons.imaging.common.bytesource.ByteSourceFile;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageParser;
import org.apache.commons.imaging.formats.jpeg.iptc.IptcTypes;
import org.apache.commons.imaging.formats.png.PngImageParser;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata.GPSInfo;

/**
 * Classes representing image metadata. This class is also responsible
 * of the extraction of the metadata. The extracted metadata depends from
 * the type of images (Format) and the source of the image (file or others).
 * Pictures can contain different set of metadata.
 * <p/>
 * This class extracts both image info and EXIF metadata is available.
 */
public class ImageMetadata {

    private final int m_height;

    private final int m_width;

    private final Format m_format;

    private final String m_formatDetails;

    private final String m_formatName;

    private final Algorithm m_algorithm;

    private final int m_bitsPerPixel;

    private final ColorType m_colorType;

    private int m_dpiHeight;

    private int m_dpiWidth;

    private final boolean m_isTransparent;

    private final boolean m_isProgressive;

    private final int m_numberOfImages;

    private final boolean m_usesPalette;

    private final Map<String, String> m_metadata = new HashMap<String, String>();

    private final ExtendedImageMetadata m_extended;

    private final Location m_location;

    /**
     * Creates a ImageMetadata for the given Image.
     * This constructor extracts image info and if the format is eligible
     * tries to extract the EXIF metadata
     *
     * @param image the image from where metadata are extracted
     * @throws IOException if metadata cannot be extracted
     */
    public ImageMetadata(Image image) throws IOException {
        ImageInfo info = null;
        ByteSource source = null;
        try {
            if (image.getFile() == null || !image.getFile().exists()) {
                source = new ByteSourceArray(image.getRawBytes());
                info = Imaging.getImageInfo(((ByteSourceArray) source).getAll());
            } else {
                source = new ByteSourceFile(image.getFile());
                info = Imaging.getImageInfo(image.getFile());
            }
        } catch (ImageReadException e) {
            throw new IOException(e);
        }

        m_format = image.getFormat();
        m_width = image.getBufferedImage().getWidth();
        m_height = image.getBufferedImage().getHeight();

        // Extract Image Info
        m_formatName = info.getFormatName();
        m_formatDetails = info.getFormatDetails();

        m_algorithm = Algorithm.getAlgorithm(info.getCompressionAlgorithm());
        m_bitsPerPixel = info.getBitsPerPixel();
        m_colorType = ColorType.getColorType(info.getColorType());

        m_dpiHeight = info.getPhysicalHeightDpi();
        m_dpiWidth = info.getPhysicalWidthDpi();

        if (m_dpiWidth == -1) {
            // No unit, assume 72.
            m_dpiWidth = 72;
        }

        if (m_dpiHeight == -1) {
            // No unit, assume 72.
            m_dpiHeight = 72;
        }

        m_isTransparent = info.isTransparent();
        m_isProgressive = info.isProgressive();

        m_usesPalette = info.usesPalette();

        m_numberOfImages = info.getNumberOfImages();

        if (m_format == Format.JPEG) {
            // JPEG file can contain Exif, IPTC and XMP metadata
            JpegImageParser parser = new JpegImageParser();

            String xmp = null;
            JpegImageMetadata metadata = null;
            try {
                xmp = parser.getXmpXml(source, null);
                // We can cast as we're sure we're parsing a JPEG file.
                metadata = (JpegImageMetadata) parser.getMetadata(source);
            } catch (ImageReadException e) {
                throw new IOException(e);
            }

            if (metadata != null) {
                // Extract Exif.
                TiffImageMetadata exif = metadata.getExif();
                if (exif != null) {
                    List<TiffField> fields = exif.getAllFields();
                    for (TiffField field : fields) {
                        m_metadata.put(field.getTagName(), field.getValueDescription());
                    }
                }
                // Geolocalisation is processed differently
                m_location = extractLocation(metadata);
                // Manage extended metadata
                m_extended = new ExtendedImageMetadata((JpegImageMetadata) metadata, xmp);
            } else {
                m_location = null;
                m_extended = new ExtendedImageMetadata(null, xmp);
            }
        } else if (m_format == Format.PNG) {
            // PNG can only contains XMP
            PngImageParser parser = new PngImageParser();
            String xmp = null;
            IImageMetadata metadata = null;
            try {
                xmp = parser.getXmpXml(source, null);
                metadata = parser.getMetadata(source);
            } catch (ImageReadException e) {
                throw new IOException(e);
            }

            m_location = null;
            m_extended = new ExtendedImageMetadata(null, xmp);
        } else {
            // Unsupported format
            m_extended = new ExtendedImageMetadata(null, null);
            m_location = null;
        }


    }

    private Location extractLocation(IImageMetadata metadata) {
        if (metadata instanceof JpegImageMetadata) {
            GPSInfo gps = null;
            try {
                final TiffImageMetadata exif = ((JpegImageMetadata) metadata).getExif();
                if (exif != null) {
                    gps = exif.getGPS();
                }
            } catch (ImageReadException e) {
                // ignore.
            }
            if (gps != null) {
                return new Location(gps);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Creates a ImageMetadata from a byte array. Exif metadata won't be
     * extracted.
     *
     * @param bytes the byte array
     * @throws IOException if the metadata cannot be extracted
     */
    public ImageMetadata(byte[] bytes) throws IOException {
        this(new Image(bytes));
    }

    /**
     * Creates a ImageMetadata from the given file.
     *
     * @param file the file
     * @throws IOException if the metadata cannot be extracted
     */
    public ImageMetadata(File file) throws IOException {
        this(new Image(file));
    }

    /**
     * Gets the image height in pixels.
     *
     * @return the height
     * @see Image#getHeight()
     */
    public int getHeight() {
        return m_height;
    }

    /**
     * Gets the image width in pixels
     *
     * @return the width
     * @see Image#getWidth()
     */
    public int getWidth() {
        return m_width;
    }

    /**
     * Gets the image format.
     *
     * @return the image format
     * @see Image#getFormat()
     */
    public Format getFormat() {
        return m_format;
    }

    /**
     * Gets the format name.
     *
     * @return the format name
     */
    public String getFormatName() {
        return m_formatName;
    }

    /**
     * Gets format details.
     *
     * @return the format details
     */
    public String getFormatDetails() {
        return m_formatDetails;
    }

    /**
     * Gets the compression algorithm
     *
     * @return the algorithm
     */
    public Algorithm getAlgorithm() {
        return m_algorithm;
    }

    /**
     * Gets the number of bits used to encode one pixel
     *
     * @return the number of bits per pixels
     */
    public int getBitsPerPixel() {
        return m_bitsPerPixel;
    }

    /**
     * Gets the color type of the picture
     *
     * @return the color type
     */
    public ColorType getColorType() {
        return m_colorType;
    }

    /**
     * Gets the number of Dot Per Inch for the image height.
     *
     * @return the DPI for the height
     */
    public int getDpiHeight() {
        return m_dpiHeight;
    }

    /**
     * Gets the number of Dot Per Inch for the image width.
     *
     * @return the DPI for the width
     */
    public int getDpiWidth() {
        return m_dpiWidth;
    }

    /**
     * Does the pictures contains transparent pixels, i.e.
     * alpha colors.
     *
     * @return <code>true</code> if the image contains transparent pixels,
     *         <code>false</code> otherwise
     */
    public boolean isTransparent() {
        return m_isTransparent;
    }

    /**
     * Does the picture is interlaced or not.
     *
     * @return <code>true</code> if the image support progressive loading,
     *         <code>false</code> otherwise
     */
    public boolean isProgressive() {
        return m_isProgressive;
    }

    /**
     * Gets the number of images contained in the picture.
     * This not not count the EXIF thumbnails.
     *
     * @return the number of images
     */
    public int getNumberOfImages() {
        return m_numberOfImages;
    }

    /**
     * Does the image use a custom palette.
     *
     * @return <code>true</code> if the image uses a custom palette,
     *         <code>false</code> otherwise
     */
    public boolean usesPalette() {
        return m_usesPalette;
    }

    /**
     * Gets EXIF metadata if any.
     *
     * @return a copy of the image metadata. An empty {@link Map} is
     *         returned if no EXIF metadata were extracted
     */
    public Map<String, String> getExifMetadata() {
        return new HashMap<String, String>(m_metadata);
    }

    /**
     * Gets IPTC metadata if any. The lookup is done on the original image.
     *
     * @return the image IPTC metadata, they may be empty
     */
    public IPTCMetadata getIPTCMetadata() {
        return m_extended.getIPTC();
    }

    /**
     * Gets the camera make.
     *
     * @return the camera make, <code>null</code> if not contained
     */
    public String getMake() {
        String m = m_metadata.get("Make");
        if (m != null) {
            return removeQuotes(m);
        }
        return null;
    }

    /**
     * Gets the camera model.
     *
     * @return the camera model, <code>null</code> if not contained
     */
    public String getModel() {
        String m = m_metadata.get("Model");
        if (m != null) {
            return removeQuotes(m);
        }
        return null;
    }

    /**
     * Gets the creation date.
     * This methods checks the <code>create date</code> EXIF key. If this key is
     * not present, the <code>modify date</code> key is checked.
     *
     * @return the creation date, <code>null</code> if not contained
     *         or if the date cannot be parsed.
     */
    public Date getCreationDate() {
        String d = m_metadata.get("Date Time");

        if (d == null) {
            d = m_metadata.get("Create Date");
        }

        if (d == null) {
            d = m_metadata.get("Modify Date");
        }

        if (d == null) {
            return null;
        } else {
            d = removeQuotes(d);

            SimpleDateFormat format = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
            try {
                return format.parse(d);
            } catch (ParseException e) {
                return null;
            }
        }
    }

    public String getIPTCCreationDate() {
        return m_extended.getCreationDate();
    }

    public void setIPTCCreationDate(String value) {
        m_extended.setCreationDate(value);
    }

    /**
     * Utility methods removing quotes around EXIF metadata.
     *
     * @param d the EXIF String
     * @return the given input without the first and last quote is present
     */
    public static String removeQuotes(String d) {
        if (d.startsWith("'")) {
            d = d.substring(1);
        }

        if (d.endsWith("'")) {
            d = d.substring(0, d.length() - 1);
        }
        return d;
    }

    /**
     * Gets the picture orientation.
     *
     * @return the orientation determined from the IPTC metadata if present
     *         or from the image dimension.
     */
    public Orientation getOrientation() {
        String iptc = m_extended.getIPTC().getValue(IptcTypes.IMAGE_ORIENTATION);
        if (iptc == null) {
            return Orientation.getOrientationFromDimension(getWidth(), getHeight());
        } else {
            return Orientation.getOrientationFromIPTC(iptc);
        }
    }

    /**
     * Gets the the picture orientation. This methods gets the EXIF orientation
     * code.
     *
     * @return the orientation EXIF code, or <code>-1</code> if the orientation
     *         was not extracted
     * @see ImageMetadata#getOrientation()
     */
    public int getExifOrientation() {
        if (m_metadata.containsKey("Orientation")) {
            return Integer.parseInt(m_metadata.get("Orientation"));
        } else {
            return -1;
        }
    }

    /**
     * Gets the location metadata.
     *
     * @return the location or <code>null</code> if the location was not contained
     *         in the metadata
     */
    public Location getLocation() {
        return m_location;
    }

    /**
     * Gets the IPTC ObjectName if exists
     *
     * @return the ObjectName, or <code>null</code> if
     *         the metadata does not exist
     */
    public String getTitle() {
        return m_extended.getTitle();
    }

    public void setTitle(String value) {
        m_extended.setTitle(value);
    }

    /**
     * Gets the IPTC keywords.
     *
     * @return the list of keywords, empty if there are no keywords
     */
    public List<String> getKeywords() {
        return m_extended.getKeywords();
    }

    public void setKeywords(List<String> values) {
        m_extended.setKeywords(values);
    }

    public String getByLine() {
        return m_extended.getAuthor();
    }

    public void setByLine(String value) {
        m_extended.setAuthor(value);
    }

    public String getByLineTitle() {
        return m_extended.getExtendedAuthor();
    }

    public void setByLineTitle(String value) {
        m_extended.setExtendedAuthor(value);
    }

    public String getHeadLine() {
        return m_extended.getSynopsis();
    }

    public void setHeadline(String value) {
        m_extended.setSynopsis(value);
    }

    public String getCity() {
        return m_extended.getCity();
    }

    public void setCity(String value) {
        m_extended.setCity(value);
    }

    public String getState() {
        return m_extended.getState();
    }

    public void setState(String value) {
        m_extended.setState(value);
    }

    public String getCountry() {
        return m_extended.getCountry();
    }

    public void setCountry(String value) {
        m_extended.setCountry(value);
    }

    public String getCopyright() {
        return m_extended.getCopyright();
    }

    public void setCopyright(String value) {
        m_extended.setCopyright(value);
    }

    public String getCaption() {
        return m_extended.getDescription();
    }

    public void setCaption(String value) {
        m_extended.setDescription(value);
    }

    public String getEditor() {
        return m_extended.getEditor();
    }

    public void setEditor(String value) {
        m_extended.setEditor(value);
    }

    /**
     * Gets the XMP XML snippet.
     * @return the XMP XML snippet or <code>null</code> if the image does not contain XMP metadata
     */
    public String getXmp() {
        try {
            return m_extended.getXMPMetadata();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ExtendedImageMetadata getExtendedMetadata() {
        return m_extended;
    }
}