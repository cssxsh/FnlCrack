package cssxsh.fnlfont.FontPack;

import com.sun.jna.ptr.PointerByReference;
import cssxsh.fnlfont.ByteTools.*;
import cssxsh.fnlfont.FreeType.*;

import java.awt.*;
import java.io.*;
import java.util.*;

public class FnlFont {
    private static byte[] fileHeaderBytes = new byte[] { 0x46, 0x4E, 0x41, 0x00 };  //Srting: FNA
    private static long Reserved = 0;
    private long fileSizeByHeader = 0;
    private File fnlFile;
    private FnaInputStream fnlFileStream;
    private String fnlFileName;
    private long memorySize = 0;
    private FontDataByType fnlFontDataTypes[];
    private int NumByTypes = 0;
    private boolean isLoaded = false;


    public FnlFont (String FilePath) {
        fnlFile = new File(FilePath);
        fnlFileName = fnlFile.getName().replaceAll("[.][^.]+$", "");
    }

    public FnlFont (File FnlFile) {
        fnlFile = FnlFile;
        fnlFileName = fnlFile.getName().replaceAll("[.][^.]+$", "");
    }

    public void Load () {
        System.out.printf("分析开始, Zlib version: %s\n", Tools.getZlibVersion());
        try {
            fnlFileStream = new FnaInputStream(new FileInputStream(fnlFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (AnalyseHeader()) {
            AnalyseTable();
            isLoaded = true;
        } else {
            System.out.println("文件"+ fnlFileName +"损坏!");
        }
    }

    // TODO: 这种输出方法文件碎片太多了, 需要重写
/*
    public void OutputBinFile (int type) {
        if (isLoaded) {
            for (FontDatasBySize datas : fnlFontDataTypes[type - 256].getBySizesAll()) {
                long size = datas.getSize();
                long num = datas.getNumber();
                File folderBySize = new File(".\\rec\\" + fnlFileName + "\\" + size +"\\");
                try {
                    if (!folderBySize.exists()) {
                        folderBySize.mkdirs();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < num; i++) {
                    String pathName = folderBySize.getPath() + String.format("\\%04x-%02x.bin", i, datas.getData(i).getWidth());
                    if (!datas.getData(i).isLoaded()) {
                        datas.getData(i).loadData(fnlFile);
                    }
                    try {
                        FileOutputStream binFile = new FileOutputStream(pathName);
                        binFile.write(datas.getData(i).getOriginData());
                        binFile.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("字号" + datas.getSize() + "解压完成: " + folderBySize.getPath());
            }
        } else {
            System.out.println("文件" + fnlFileName + "未加载!");
        }
    }

    public void OutputBinFile (int type, int size) {
        FontDatasBySize datas = getDatasBySize(size);
        long num = datas.getNumber();
        File folderBySize = new File(".\\rec\\" + fnlFileName + "\\" + size +"\\");
        try {
            if (!folderBySize.exists()) {
                folderBySize.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < num; i++) {
            String pathName = folderBySize.getPath() + String.format("\\%04x-%02x.bin", i, datas.getData(i).getWidth());
            if (!datas.getData(i).isLoaded()) {
                datas.getData(i).loadData(fnlFile);
            }
            try {
                FileOutputStream binFile = new FileOutputStream(pathName);
                binFile.write(datas.getData(i).getOriginData());
                binFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("字号 " + datas.getSize() + "解压完成: " + folderBySize.getPath());
    }

    public void OutputBinFile (int size, int index) {
        FontDatasBySize datas = getDatasBySize(size);
        FontDataCompressed data = datas.getData(index);
        File folderBySize = new File(".\\rec\\" + fnlFileName + "\\" + size +"\\");
        try {
            if (!folderBySize.exists()) {
                folderBySize.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String pathName = folderBySize.getPath() + String.format("\\%04x-%02x.bin", index, data.getWidth());
        if (!data.isLoaded()) {
            data.loadData(fnlFile);
        }
        try {
            FileOutputStream binFile = new FileOutputStream(pathName);
            binFile.write(data.getOriginData());
            binFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
*/
    public void OutInfo(int type, int size, int index) {
        if (isLoaded) {
            FontDatasBySize tempDatas = getDatasBySize(type, size);
            FontDataCompressed data = tempDatas.getData(index);
            //System.out.printf("字号0x%02X, 码号0x%04X, 字宽0x%02X, 压缩地址0x%08X, 压缩长度0x%08X\n", (int)tempDatas.getSize(), index, data.getWidth(), data.getAddress(), data.getCompressedLength());
            // TODO: 写一个输出byte数组的方法
            if (!data.isLoaded()) {
                data.loadData(fnlFile);
            }
            String text = Tools.DataToString(data.getOriginData(), data.getWidth(), data.getHeight());
            System.out.print(text);
            byte[] pixels = Tools.DrawToPixels(data.getOriginData(), data.getWidth(), data.getHeight());
            byte[] bmpFile = Tools.PixelsToBmp(pixels, data.getWidth(), data.getHeight(), true);
            String path = String.format(".\\rec\\%d\\%04X-%02X.bmp", size, index,data.getWidth());
            Tools.OutputFile(path, bmpFile);
        } else {
            System.out.println("文件" + fnlFileName + "未加载!");
        }
    }

    public File getFnlFile() {
        return fnlFile;
    }

    public String getFnlFileName() {
        return fnlFileName;
    }

    public FontDataCompressed getFontData (int type, int size, int index) {
        FontDatasBySize tempDatas = getDatasBySize(type, size);
        return tempDatas.getData(index);
    }

    public FontDatasBySize getDatasBySize (int type, int size) {
        if (isLoaded){
            return fnlFontDataTypes[type - 256].getBySize(size);
        } else {
            System.out.println("文件" + fnlFileName + "未加载!");
        }
        return null;
    }

    private boolean AnalyseTable () {
        // TODO: 模仿SO引擎文件检查字体表
        boolean result = true;
        try {
            NumByTypes = (int) fnlFileStream.readUnsignedIntRight();
            if (NumByTypes < 0) {
                System.out.println("AnalyseTable [0]");
                result = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (result) {
            System.out.println(fnlFileName + "一共" + NumByTypes + "种字形");
            fnlFontDataTypes = new FontDataByType[NumByTypes];
            try {
                for (int i = 0; i < NumByTypes; i++) {
                    long numBySizes = fnlFileStream.readUnsignedIntRight();
                    fnlFontDataTypes[i] = new FontDataByType(i + 256, (int)numBySizes);
                    System.out.println("字形: " + fnlFontDataTypes[i].getByType()+ "\t共" + fnlFontDataTypes[i].getNumBySize() + "种字号");
                    for (int j = 0; j < fnlFontDataTypes[i].getNumBySize(); j++) {
                        long size = fnlFileStream.readUnsignedIntRight();
                        long v = fnlFileStream.readUnsignedIntRight();
                        long num = fnlFileStream.readUnsignedIntRight();

                        FontDatasBySize bySize = new FontDatasBySize((int)size, v, num);
                        System.out.println("字号 " + bySize.getSize() + "\t共" + bySize.getNumber() + "个数据压缩包");
                        for (int k = 0; k < num; k++) {
                            int width = fnlFileStream.readUnsignedShortRight();
                            long address = fnlFileStream.readUnsignedIntRight();
                            long length = fnlFileStream.readUnsignedIntRight();
                            bySize.setData(k, width, address, length);
                        }
                        fnlFontDataTypes[i].setBySize(j,bySize);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private boolean AnalyseHeader () {
        // TODO: 模仿SO引擎文件检查文件头
        byte[] tempBytes = new byte[4];
        boolean result = true;
        try {
            fnlFileStream.read(tempBytes);
            if (!Arrays.equals(tempBytes, fileHeaderBytes)) {
                System.out.println("AnalyseHeader [0]");
                result = false;
            }
            if (fnlFileStream.readUnsignedIntRight() != Reserved) {
                System.out.println("AnalyseHeader [1]");
                result = false;
            }
            fileSizeByHeader = fnlFileStream.readUnsignedIntRight();
            if (fileSizeByHeader != fnlFile.length()) {
                System.out.println("AnalyseHeader [2]" + fileSizeByHeader);
                //result = false;
            }
            memorySize = fnlFileStream.readUnsignedIntRight();
            if (memorySize <= 3) {
                System.out.println("AnalyseHeader [3]");
                result = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    // TODO: 这是一个测试版方法,之后写一个完整版的
    public void TransformTFF (File TTF) {
        if (isLoaded) {

            System.out.println("TreeType Version:" + Tools.getFreeTypeVersion());
            byte[] header = new byte[] {
                    0x46, 0x4E, 0x41, 0x00,
                    0x00, 0x00, 0x00, 0x00,
                    0x00, 0x25,-0x3c, 0X09,
                    0x18,-0x42, 0x02, 0x00,
                    0x01, 0x00, 0x00, 0x00,
                    0x01, 0x00, 0x00, 0x00,
                    0x58, 0x00, 0x00, 0x00,
                    0x0F, 0x00, 0x00, 0x00,
                   -0x1D, 0x5D, 0x00, 0x00,
            };
            long indexoffnl = 0;
            int error = 0;
            int fontSize = 0x0058;
            int charNum = 0x5DE3;
            PointerByReference aLib = new PointerByReference();
            FreeTypeLIb.FreeType.FT_FaceRec.ByReference.P_Face pFace = new FreeTypeLIb.FreeType.FT_FaceRec.ByReference.P_Face();
            error = FreeTypeLIb.FreeType.dll.FT_Init_FreeType(aLib);
            System.out.println("FT_Init_FreeType error:" + error);

            error = FreeTypeLIb.FreeType.dll.FT_New_Face(aLib.getValue(), TTF.getPath(), 0, pFace);
            System.out.println("FT_New_Face error:" + error);
            error = FreeTypeLIb.FreeType.dll.FT_Set_Pixel_Sizes(pFace.value, fontSize, fontSize);//0x00200000
            System.out.println("FT_Set_Char_Size error:" + error);
            long startAddress = 0x18 + 0x0C + 0x0A * charNum;//0x18 + 0x0C + 0x0A * 0x4632;
            long newAddress = startAddress;
            FontDataCompressed[] bb = new FontDataCompressed[charNum];

            for (int i = 0x0020; i < 0x007F; i++) {
                indexoffnl = Tools.CharCodeToIndex(i,"GBK");
                if (indexoffnl != -1) {
                    try {
                        String gbk = new String(Tools.ShortToBytes(i), "GBK");
                        byte[] utf_16 = gbk.getBytes("UTF-16");
                        long code = utf_16[2] * 0x0100 + utf_16[3];
                        error = FreeTypeLIb.FreeType.dll.FT_Load_Char(pFace.value, code, 0x00);
                        //System.out.println("FT_Load_Char error:" + error);
                        error = FreeTypeLIb.FreeType.dll.FT_Render_Glyph(pFace.value.glyph, 0x02);
                        //System.out.println("FT_Render_Glyph error:" + error);
                        int length = pFace.value.glyph.bitmap.pitch * pFace.value.glyph.bitmap.rows;
                        byte[] data = new byte[length];
                        if (length != 0) {
                            pFace.value.glyph.bitmap.buffer.read(0x0000, data, 0, length);
                        }
                        data = Tools.PixelsCompleted(data, pFace.value, fontSize);
                        //byte[] bmp = Tools.PixelsToBmp(data, pFace.value.glyph.advance.x.intValue() / 64, fontSize,true);
                        //Tools.OutputFile(".\\REC\\test.bmp", bmp);
                        byte[] dataCompress = Tools.Compress(data);
                        bb[(int) indexoffnl] = new FontDataCompressed(pFace.value.glyph.advance.x.intValue() / 64, 0x58, newAddress, dataCompress);
                        newAddress += dataCompress.length;
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }

            for (int i = 0x8140; i < 0xFEFF; i++) {
                indexoffnl = Tools.CharCodeToIndex(i,"GBK");
                if (indexoffnl != -1) {
                    try {
                        String gbk = new String(new byte[] { (byte) (i / 0x0100), (byte) (i % 0x0100)}, "GBK");
                        byte[] utf_16 = gbk.getBytes("UTF-16");
                        long code = (utf_16[2] << 8  & 0x0000FF00) | (utf_16[3] & 0x000000FF);
                        long index = FreeTypeLIb.FreeType.dll.FT_Get_Char_Index(pFace.value, code);
                        if (index == 0) {
                            //System.out.println("code: " + code);
                        }
                        error = FreeTypeLIb.FreeType.dll.FT_Load_Glyph(pFace.value, index, 1L << 3);
                        //System.out.println(", FT_Load_Glyph error:" + error);
                        error = FreeTypeLIb.FreeType.dll.FT_Render_Glyph(pFace.value.glyph, 0x02);
                        //System.out.println("FT_Render_Glyph error:" + error);
                        int length = pFace.value.glyph.bitmap.pitch * pFace.value.glyph.bitmap.rows;
                        byte[] data = new byte[length];
                        if (length != 0) {
                            pFace.value.glyph.bitmap.buffer.read(0x0000, data, 0, length);
                        }else {
                            System.out.println("code: " + i + "->" + gbk + "error");
                        }
                        data = Tools.PixelsCompleted(data, pFace.value, fontSize);
                        if (indexoffnl == 0x4DAE) {
                            System.out.println(gbk);
                            byte[] bmp = Tools.PixelsToBmp(data, pFace.value.glyph.advance.x.intValue() / 64, fontSize,true);
                            Tools.OutputFile(".\\REC\\test.bmp", bmp);
                        }
                        byte[] dataCompress = Tools.Compress(data );
                        bb[(int) indexoffnl] = new FontDataCompressed(pFace.value.glyph.advance.x.intValue() / 64, 0x58, newAddress, dataCompress);
                        newAddress += dataCompress.length;
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }


            byte[] data = new byte[0x0A * charNum];
            byte[] dataC = new byte[(int) (newAddress - startAddress)];
            long length = 0;
            byte[] isnull = new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
            for (int i = 0; i < charNum; i++) {
                FontDataCompressed Index = bb[i];
                if (Index != null) {
                    System.arraycopy(Tools.ShortToBytes(Index.getWidth()), 0x00, data, i * 0x0A, 0x02);
                    System.arraycopy(Tools.IntToBytes(Index.getAddress()), 0x00, data, i * 0x0A + 0x02, 0x04);
                    System.arraycopy(Tools.IntToBytes(Index.getCompressedData().length), 0x00, data, i * 0x0A + 0x06, 0x04);

                    if (Index.getCompressedData() != null) {
                        System.arraycopy(Index.getCompressedData(), 0x00, dataC, (int) length, Index.getCompressedData().length);
                        length += Index.getCompressedData().length;
                    }
                } else {
                    //System.arraycopy(isnull, 0x00, data, i * 0x0A, 0x0A);
                }
            }

            Tools.OutputFile(fnlFile.getPath() + "_", header, true);
            Tools.OutputFile(fnlFile.getPath() + "_", data, true);
            Tools.OutputFile(fnlFile.getPath() + "_", dataC, true);
        } else {
            System.out.println("文件" + fnlFileName + "未加载!");
        }
    }

    public void TransformTFF (String pathNameTTF) {
        TransformTFF(new File(pathNameTTF));
    }
}
