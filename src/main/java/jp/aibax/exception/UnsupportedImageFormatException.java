package jp.aibax.exception;

import java.io.IOException;

public class UnsupportedImageFormatException extends IOException
{
    private static final long serialVersionUID = 1L;

    public UnsupportedImageFormatException()
    {
        super();
    }

    public UnsupportedImageFormatException(String message)
    {
        super(message);
    }
}
