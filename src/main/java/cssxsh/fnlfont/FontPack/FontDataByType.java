package cssxsh.fnlfont.FontPack;

public class FontDataByType {
    private int ByType;
    private FontDatasBySize[] BySizes;

    public FontDataByType(int type, int number) {
        this.ByType = type;
        this.BySizes = new FontDatasBySize[number];
    }

    public long getByType () {
        return ByType;
    }

    public long getNumBySize () {
        return BySizes.length;
    }

    public FontDatasBySize getBySize (int size) {
        for (FontDatasBySize i : BySizes) {
            if (i.getSize() == size) {
                return i;
            }
        }
        return null;
    }

    public void setBySize (int index, FontDatasBySize bySize) {
        BySizes[index] = bySize;
    }

    public FontDatasBySize[] getBySizesAll () {
        return BySizes;
    }
}
