package cssxsh.fnlfont.FontPack;

import cssxsh.fnlfont.ByteTools.FnaInputStream;
import cssxsh.fnlfont.ByteTools.Tools;
import sun.font.CreatedFontTracker;

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
        try {
            fnlFile = new File(FilePath);
            fnlFileStream = new FnaInputStream(new FileInputStream(fnlFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        fnlFileName = fnlFile.getName().replaceAll("[.][^.]+$", "");
    }

    public void Load () {
        System.out.printf("分析开始, Zlib version: %s\n", Tools.getZlibVersion());
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
            return fnlFontDataTypes[type - 256].getBySizes(size);
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
                    for (FontDatasBySize bySize : fnlFontDataTypes[i].getBySizesAll()) {
                        long size = fnlFileStream.readUnsignedIntRight();
                        long v = fnlFileStream.readUnsignedIntRight();
                        long num = fnlFileStream.readUnsignedIntRight();
                        bySize = new FontDatasBySize((int)size, v, num);
                        System.out.println("字号 " + bySize.getSize() + "\t共" + bySize.getNumber() + "个数据压缩包");
                        for (int j = 0; j < num; j++) {
                            int width = fnlFileStream.readUnsignedShortRight();
                            long address = fnlFileStream.readUnsignedIntRight();
                            long length = fnlFileStream.readUnsignedIntRight();
                            bySize.setData(j, width, address, length);
                        }
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

    public void CreatedNewFnlFont () {

    }
}
