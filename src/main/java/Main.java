import com.sun.jna.*;
import com.sun.jna.ptr.PointerByReference;
import cssxsh.fnlfont.ByteTools.Tools;
import cssxsh.fnlfont.FreeType.FreeTypeLIb.*;
import cssxsh.fnlfont.FontPack.FnlFont;

public class Main {
    public static void main (String[] args) {
        //FnlFont rance01fnl = new FnlFont(".\\src\\main\\resources\\Rance01Font.fnl");
        //rance01fnl.Load();
        System.out.println(Tools.getFreeTypeVersion());
        long index = 0;
        int error = 0;
        int fontSize = 0x0180;
        PointerByReference aLib = new PointerByReference();
        FreeType.FT_FaceRec.ByReference.P_Face pFace = new FreeType.FT_FaceRec.ByReference.P_Face();
        error = FreeType.dll.FT_Init_FreeType(aLib);
        System.out.println("FT_Init_FreeType error:" + error);

        error = FreeType.dll.FT_New_Face(aLib.getValue(), "D:\\Users\\CSSXSH\\Documents\\c_work\\FreeTypeTest\\simkai.ttf", 0, pFace);
        System.out.println("FT_New_Face error:" + error);

        error = FreeType.dll.FT_Set_Pixel_Sizes(pFace.value, fontSize, fontSize);//0x00200000
        //error = FreeType.dll.FT_Set_Char_Size(pFace.value, fontSize << 6, fontSize << 6, 0x48, 0x48);
        System.out.println("FT_Set_Char_Size error:" + error);

        pFace.write();
        pFace.writeField("value");
        pFace.value.glyph.write();
        pFace.value.driver.write();
        pFace.value.driver.root.write();
        error = FreeType.dll.FT_Load_Char(pFace.value, 0x0037, 0x00);
        System.out.println("FT_Load_Char error:" + error);

        /*
        index = FreeType.dll.FT_Get_Char_Index(pFace.value, 0x41);
        System.out.println("Index of A: " + index);
        error = FreeType.dll.FT_Load_Glyph(pFace.value, index, 1L << 3);
        System.out.println(", FT_Load_Glyph error:" + error);
        */


        //error = FreeType.dll.FT_Render_Glyph_Internal (pFace.value.driver.root.library, pFace.value, 0x02);
        //System.out.println("FT_Render_Glyph_Internal" + error);
        pFace.value.glyph.bitmap.write();
        pFace.value.glyph.write();
        error = FreeType.dll.FT_Render_Glyph(pFace.value.glyph, 0x02);
        System.out.println(aLib.getValue() +  " " + pFace.value.driver.root.library);
        System.out.println("FT_Render_Glyph error:" + error);

        int length = pFace.value.glyph.bitmap.pitch * pFace.value.glyph.bitmap.rows;
        byte[] data = new byte[length];
        System.out.println("value: " + pFace.value.glyph.bitmap.pitch);
        System.out.println("buffer: " + pFace.value.glyph.bitmap.buffer);
        pFace.value.glyph.bitmap.buffer.read(0x0000, data, 0, length);
        //byte[] pixel = Tools.DrawToPixels(data, pFace.value.glyph.bitmap.width,  length / pFace.value.glyph.bitmap.pitch);
        byte[] bmp = Tools.PixelsToBmp(data, pFace.value.glyph.bitmap.width, pFace.value.glyph.bitmap.rows,true, true);
        Tools.OutputFile(".\\REC\\test.bmp", bmp);
        FreeType.dll.FT_Done_FreeType(aLib.getValue());
    }
}
