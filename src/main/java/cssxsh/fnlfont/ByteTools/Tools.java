package cssxsh.fnlfont.ByteTools;

import com.sun.jna.*;
import com.sun.jna.ptr.*;
import java.io.*;
import java.util.*;
import cssxsh.fnlfont.FreeType.FreeTypeLIb.*;

public class Tools {
    private interface Zlib extends Library {
        Zlib dll = (Zlib) Native.load(".\\DLL\\zlib" + System.getProperty("sun.arch.data.model"), Zlib.class);
        public String zlibVersion();
        public int compress2(byte[] compressedDatas, LongByReference plengths, byte[] originDatas, long originLength, int level);
        //public int compress2(byte[] compressedDatas, long[] plengths, byte[] originDatas, long originLength, int level);
        public long compressBound(long originLength);
        public int uncompress(byte[] originDatas, LongByReference plengths, byte[] compressedDatas, long compressedLength);
    }

    public static String getZlibVersion () {
        return Zlib.dll.zlibVersion();
    }

    public static byte[] Decompress (byte[] compressedData, int bufferLength) {
        byte[] buffer = new byte[bufferLength];
        byte[] originData = new byte[0];
        LongByReference plength = new LongByReference(buffer.length);
        int result = Zlib.dll.uncompress(buffer, plength, compressedData, compressedData.length);
        if (result == 0) {
            originData = Arrays.copyOfRange(buffer, 0, (int) plength.getValue());
        } else {
            System.out.printf("解压失败!错误:%d\n", result);
        }
        return originData;
    }

    public static byte[] Decompress (byte[] compressedData) {
        return Decompress(compressedData, 0x0010000);
    }

    public static byte[] Compress (byte[] originDatas) {
        return Compress(originDatas, 9);
    }

    public static byte[] Compress (byte[] originDatas, int level) {
        byte[] compressedData = new byte[0];
        byte[] buffer = new byte[(int)Zlib.dll.compressBound((long)originDatas.length)];
        LongByReference plength = new LongByReference(buffer.length);
        int result = Zlib.dll.compress2(buffer, plength, originDatas, originDatas.length, level);
        if (result == 0) {
            compressedData = Arrays.copyOfRange(buffer, 0, (int) plength.getValue());
        } else {
            System.out.printf("压缩失败!错误: %d\n", result);
        }
        return compressedData;
    }

    public static String getFreeTypeVersion () {
        PointerByReference aLib = new PointerByReference();
        IntByReference aMajor = new IntByReference();
        IntByReference aMinor = new IntByReference();
        IntByReference aPatch = new IntByReference();

        FreeType.dll.FT_Init_FreeType(aLib);
        FreeType.dll.FT_Library_Version(aLib.getValue(), aMajor, aMinor, aPatch);
        FreeType.dll.FT_Done_FreeType(aLib.getValue());
        return String.format("%d.%d.%d", aMajor.getValue(), aMinor.getValue(), aPatch.getValue());
    }

    public static int CharCodeToIndex (int CharCodeInt, String Code) {
        short[] CharCode = new short[2];
        CharCode[0] = (short)(CharCodeInt / 0x0100);
        CharCode[1] = (short)(CharCodeInt % 0x0100);
        return CharCodeToIndex(CharCode, Code);
    }

    public static int CharCodeToIndex (short[] CharCode, String Code) {
        if (Code.equals("JIS")) {
            if (CharCode[0] == 0x00) {
                if (CharCode[1] >= 0x20 && CharCode[1] <= 0x7E) {
                    return CharCode[1] - 0x20;
                } else if (CharCode[1] >= 0xA1 && CharCode[1] <= 0xDF) {
                    return CharCode[1] - 0x42;
                }
            } else if (CharCode[0] >= 0x81 && CharCode[0] <= 0x9F) {
                if (CharCode[1] >= 0x40 && CharCode[1] <= 0x7E) {
                    return CharCode[0] * 0xBC + CharCode[1] - 0x5E5E;
                } else if (CharCode[1] >= 0x80 && CharCode[1] <= 0xFC) {
                    return CharCode[0] * 0xBC + CharCode[1] - 0x5E5F;
                }
            } else if (CharCode[0] >= 0xE0 && CharCode[0] <= 0xEF) {
                if (CharCode[1] >= 0x40 && CharCode[1] <= 0x7E) {
                    return CharCode[0] * 0xBC + CharCode[1] - 0x8D5E;
                } else if (CharCode[1] >= 0x80 && CharCode[1] <= 0xFC) {
                    return CharCode[0] * 0xBC + CharCode[1] - 0x8D5F;
                }
            }
        } else if (Code.equals("GBK")){
            // TODO: 之后确定修改方式后根据修改的结果构造
            if (CharCode[0] == 0x00) {
                if (CharCode[1] >= 0x20 && CharCode[1] <= 0x7E) {
                    return CharCode[1] - 0x20;
                }
            } else if (CharCode[0] >= 0x81 && CharCode[0] <= 0xFE) {
                if (CharCode[1] >= 0x40 && CharCode[1] <= 0x7E) {
                    return CharCode[0] * 0xBE + CharCode[1] - 0x5F9F;
                } else if (CharCode[1] >= 0x80 && CharCode[1] <= 0xFE) {
                    return CharCode[0] * 0xBE + CharCode[1] - 0x5FA0;
                }
            }
        }
        return -1;
    }

    public static void OutputFile (String pathName, byte[] data) {
        try {
            File file_ = new File(pathName);
            if (!file_.getParentFile().isDirectory()) {
                file_.getParentFile().mkdirs();
            }
            FileOutputStream file = new FileOutputStream(file_);
            file.write(data);
            file.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void OutputFile (String pathName, byte[] data, boolean append) {
        try {
            FileOutputStream file = new FileOutputStream(new File(pathName), append);
            file.write(data);
            file.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static byte[] IntToBytes (long i) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (i >> 0x00);
        bytes[1] = (byte) (i >> 0x08);
        bytes[2] = (byte) (i >> 0x10);
        bytes[3] = (byte) (i >> 0x18);
        return bytes;
    }

    public static byte[] ShortToBytes (int s) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (s >> 0x00);
        bytes[1] = (byte) (s >> 0x08);
        return bytes;
    }

    public static long BytesToInt (byte[] bytes){
        return (long) ((bytes[0] << 0x00) | (bytes[1] << 0x08) | (bytes[2] << 0x10) | (bytes[3] << 0x18));
    }

    public static int BytesToShort (byte[] bytes) {
        return (int) ((bytes[0] << 0x00) | (bytes[1] << 0x08));
    }

    public static byte[] DrawToPixels (byte[] fontData, int width, int height) {
        int width_ = (width + 3) & 0xFFFFFFFC;
        int dataWidth = (((width + 0x07) >> 3) + 3) & 0xFFFFFFFC;
        byte[] Pixels = new byte[height * width_];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width_; x++) {
                int s = ~((byte) x) & 0x07; // s = 7 - (x % 8);
                int t = 1 << s;
                int z = y * dataWidth + (x >> 3);
                Pixels[y * width_ + x] = (byte) (-(fontData[z] & t) >> s);//把八位中的一位(x) 作为bool, 如果为真 对应赋予FF
            }
        }
        return Pixels;
    }

    public static byte[] PixelsToData (byte[] Pixels, int width, int height) {
        int width_ = (width + 3) & 0xFFFFFFFC;
        int dataWidth = (((width + 0x07) >> 3) + 3) & 0xFFFFFFFC;
        byte[] fontData = new byte[height * dataWidth];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int s = ~((byte) x) & 0x07; // s = 7 - (x % 8);
                int t = 1 << s;
                int z = y * dataWidth + (x >> 3);
                fontData[z] |= (Pixels[y * width_ + x] & t);
            }
        }
        return fontData;
    }

    public static String DataToString (byte[] fontData, int width, int height) {
        int dataWidth = (((width + 0x07) >> 3) + 3) & 0xFFFFFFFC;
        String result = "";
        for (int y = height - 1; y >= 0; y--) {
            for (int x = 0; x < dataWidth; x++) {
                result += String.format("%02X", fontData[y * dataWidth + x]);
            }
            result += "\n";
        }
        return result;
    }

    public static byte[] PixelsToBmp (byte[] data, int width, int height, boolean isMONO, boolean isTurn) {
        byte[] result = new byte[0x0036 + (isMONO ? 0x0008 : 0x0400) + data.length];
        // 0x000E byte of bf
        byte[] bfType = new byte[] { 0x42, 0x4D };
        byte[] bfSize = IntToBytes(result.length);
        byte[] bfReserveed = new byte[] { 0x00, 0x00, 0x00, 0x00 };
        byte[] bfOffBits = IntToBytes(0x0036 + (isMONO ? 0x0008: 0x0400));
        System.arraycopy(bfType, 0x00, result, 0x00, bfType.length);
        System.arraycopy(bfSize, 0x00, result, 0x02, bfSize.length);
        System.arraycopy(bfReserveed, 0x00, result, 0x06, bfReserveed.length);
        System.arraycopy(bfOffBits, 0x00, result, 0x0A, bfOffBits.length);
        // 0x0028 byte of bi
        byte[] biSize = IntToBytes(0x28);
        byte[] biWidth = IntToBytes((long) width);
        byte[] biHeight = IntToBytes((long) height * (isTurn ? -1 : 1));
        byte[] biPlanes = ShortToBytes(0x01);
        byte[] biBitCount = ShortToBytes(isMONO ? 0x01: 0x08);
        byte[] biCompression = IntToBytes(0x00);
        byte[] biSizeImage = IntToBytes(data.length);
        byte[] biXPelsPerMeter = IntToBytes(0x00);
        byte[] biYPelsPerMeter = IntToBytes(0x00);
        byte[] biClrUsed = IntToBytes(0x00);
        byte[] biClrImportant = IntToBytes(0x00);
        System.arraycopy(biSize, 0x00, result, 0x0E, bfSize.length);
        System.arraycopy(biWidth, 0x00, result, 0x12, biWidth.length);
        System.arraycopy(biHeight, 0x00, result, 0x16, biHeight.length);
        System.arraycopy(biPlanes, 0x00, result,0x1A, biPlanes.length);
        System.arraycopy(biBitCount, 0x00, result, 0x1C, biBitCount.length);
        System.arraycopy(biCompression, 0x00, result, 0x1E, biCompression.length);
        System.arraycopy(biSizeImage, 0x00,result, 0x22, biSizeImage.length);
        System.arraycopy(biXPelsPerMeter, 0x00, result, 0x26, biXPelsPerMeter.length);
        System.arraycopy(biYPelsPerMeter, 0x00, result, 0x2A,biYPelsPerMeter.length);
        System.arraycopy(biClrUsed, 0x00, result, 0x2E, biClrUsed.length);
        System.arraycopy(biClrImportant, 0x00, result, 0x32, biClrImportant.length);
        if (isMONO) {
            // 0x0020 * 0x04 byte of RGBQUADs of paltte
            byte[] paltte = new byte[] {0x00, 0x00, 0x00, 0x00,-0x01,-0x01,-0x01, 0x00};
            System.arraycopy(paltte, 0x00, result, 0x0036, paltte.length);
        } else {
            // 0x0100 * 0x04 byte of RGBQUADs of palette
            try {
                FileInputStream paletteFile = new FileInputStream(".\\REC\\palette.bin");
                paletteFile.read(result, 0x0036, 0x0400);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.arraycopy(data, 0x00, result, 0x0036 + (isMONO ? 0x0008 : 0x0400), data.length);
        return result;
    }

    public static byte[] PixelsToBmp (byte[] data, int width, int height, boolean isMONO) {
        return PixelsToBmp(data, width, height, isMONO, false);
    }

    public static byte[] PixelsCompleted (byte[] data, FreeType.FT_FaceRec.ByReference face, int height) {
        int width = face.glyph.advance.x.intValue() / 64;
        int dataWidth = (((width + 0x07) >> 3) + 3) & 0xFFFFFFFC;
        byte[] result = new byte[dataWidth * height];
        if (data == null || data.length == 0) {
            return result;
        }
        int s = (height * face.ascender) / (face.ascender - face.descender) - face.glyph.bitmap_top;
        for (int j = 0; j < face.glyph.bitmap.rows; j++) {
            int i = face.glyph.bitmap_left;
            int k;
            int t;
            for (int u = 0; u < face.glyph.bitmap.pitch; u++) {
                byte row = data[j * face.glyph.bitmap.pitch + u];
                for (int e = 0; e < 8; e++) {
                    k = i / 8;
                    t = i % 8;
                    if ((height - j - 1 - s) * dataWidth + k >= result.length || (height - j - 1 - s) * dataWidth + k < 0) {
                        u = face.glyph.bitmap.pitch;
                        break;
                    }
                    int p = row & (0x80 >> e);
                    p <<= e;
                    result[(height - j - 1 - s) * dataWidth + k] |= (p >>> t);
                    i++;
                }
            }
        }

        return result;
    }
}
