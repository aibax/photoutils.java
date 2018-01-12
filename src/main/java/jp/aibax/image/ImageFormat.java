package jp.aibax.image;

public enum ImageFormat
{
    // @formatter:off
    JPEG("jpeg", "image/jpeg"),
    PNG("png", "image/png"),
    GIF("gif", "image/gif"),
    BMP("bmp", "image/bmp");
    // @formatter:on

    private final String name;

    private final String contentType;

    private ImageFormat(final String name, final String contentType)
    {
        this.name = name;
        this.contentType = contentType;
    }

    public String getName()
    {
        return this.name;
    }

    public String getContentType()
    {
        return this.contentType;
    }
}
