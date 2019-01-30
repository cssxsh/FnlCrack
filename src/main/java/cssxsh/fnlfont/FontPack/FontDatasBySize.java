package cssxsh.fnlfont.FontPack;

public class FontDatasBySize {
    private int BySize;
    private long number;
    private long v;
    private FontDataCompressed[] datas;

    public FontDatasBySize(int size, long v, long number) {
        this.BySize = size;
        this.v = v;
        this.number = number;
        this.datas = new FontDataCompressed[(int)number];
    }

    public long getSize () {
        return BySize;
    }

    public long getNumber () {
        return number;
    }

    public long getV () {
        return v;
    }

    public FontDataCompressed getData (int index) {
        return datas[index];
    }

    public FontDataCompressed[] getDataAll (int index) {
        return datas;
    }

    public void setData (int index, int width, long address, long length) {
        datas[index] = new FontDataCompressed(width, BySize, address, length);
    }
}
