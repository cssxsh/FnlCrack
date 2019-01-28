package cssxsh.fnlfont.FontPack;

import cssxsh.fnlfont.ByteTools.FnaInputStream;
import cssxsh.fnlfont.ByteTools.Tools;

import java.io.*;

public class FontDataCompressed {
    private int width;
    private int height;
    private long address;
    private long compressedLength;
    private boolean isLoaded;
    private byte[] compressedData;
    private byte[] originData;

    public FontDataCompressed (int width, int height, long address, long length) {
        this.width = width;
        this.height = height;
        this.address = address;
        this.compressedLength = length;
    }

    public int getWidth () {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public long getAddress () {
        return address;
    }

    public long getCompressedLength () {
        return compressedLength;
    }

    public byte[] getCompressedData () {
        if (isLoaded) {
            return compressedData;
        } else {
            return null;
        }
    }

    public byte[] getOriginData () {
        if (isLoaded) {
            return originData;
        } else {
            return null;
        }
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void loadData (File fnlFile) {
        try {
            compressedData = new byte[(int)compressedLength];
            FnaInputStream fnlFileStream = new FnaInputStream(new FileInputStream(fnlFile));
            fnlFileStream.skip(address);
            fnlFileStream.read(compressedData);
            fnlFileStream.close();
            originData = Tools.Decompress(compressedData);
            isLoaded = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
