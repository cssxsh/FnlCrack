package cssxsh.fnlfont.FreeType;

import com.sun.jna.*;
import com.sun.jna.ptr.*;

import java.util.*;

public class FreeTypeLIb {
    public interface FreeType extends Library {
        FreeType dll = (FreeType) Native.load(".\\DLL\\libfreetype" + System.getProperty("sun.arch.data.model"), FreeType.class);

        public static class FT_Glyph_Metrics extends Structure {
            public NativeLong width;
            public NativeLong height;

            public NativeLong horiBearingX;
            public NativeLong horiBearingY;
            public NativeLong horiAdvance;

            public NativeLong vertBearingX;
            public NativeLong vertBearingY;
            public NativeLong vertAdvance;

            public static class ByReference extends FT_Glyph_Metrics implements Structure.ByReference { }
            public static class ByValue extends FT_Glyph_Metrics implements Structure.ByValue { }

            @Override
            protected List<String> getFieldOrder () {
                List<String> Field = new ArrayList<String>();
                Field.add("width");
                Field.add("height");
                Field.add("horiBearingX");
                Field.add("horiBearingY");
                Field.add("horiAdvance");
                Field.add("vertBearingX");
                Field.add("vertBearingY");
                Field.add("vertAdvance");
                return Field;
            }
        }

        public static class FT_Bitmap_Size extends Structure {
            public Short height;
            public Short width;

            public NativeLong size;

            public NativeLong x_ppem;
            public NativeLong y_ppem;

            public static class ByReference extends FT_Bitmap_Size implements Structure.ByReference { }
            public static class ByValue extends FT_Bitmap_Size implements Structure.ByValue { }

            @Override
            protected List<String> getFieldOrder() {
                List<String> Field = new ArrayList<String>();
                Field.add("height");
                Field.add("width");
                Field.add("size");
                Field.add("x_ppem");
                Field.add("y_ppem");
                return Field;
            }
        }

        public static class FT_CharMapRec extends Structure {
            public int encoding;
            public short platform_id;
            public short encoding_id;

            public static class ByReference extends FT_Bitmap_Size implements Structure.ByReference { }
            public static class ByValue extends FT_Bitmap_Size implements Structure.ByValue { }
            public static class P_CharMap extends Structure {
                public FT_CharMapRec.ByReference value;

                public static class ByReference extends P_CharMap implements Structure.ByReference { }

                @Override
                protected List<String> getFieldOrder() {
                    List<String> Field = new ArrayList<String>();
                    Field.add("value");
                    return Field;
                }
            }

            @Override
            protected List<String> getFieldOrder() {
                List<String> Field = new ArrayList<String>();
                Field.add("face");
                Field.add("encoding");
                Field.add("platform_id");
                Field.add("encoding_id");
                return Field;
            }
        }

        public static class FT_FaceRec extends Structure {
            public NativeLong num_faces;
            public NativeLong face_index;

            public NativeLong face_flags;
            public NativeLong style_flags;

            public NativeLong num_glyphs;

            public String family_name;
            public String style_name;

            public int num_fixed_sizes;
            public FT_Bitmap_Size.ByReference available_sizes;

            public int num_charmaps;
            public Pointer charmaps;

            public FT_Generic.ByValue generic;

            public FT_BBox.ByValue bbox;

            public short units_per_EM;
            public short ascender;
            public short descender;
            public short height;

            public short max_advance_width;
            public short max_advance_height;

            public short underline_position;
            public short underline_thickness;

            public FT_GlyphSlotRec.ByReference glyph;
            public FT_SizeRec.ByReference size;
            public FT_CharMapRec.ByReference charmap;

            public FT_DriverRec.ByReference driver;
            public Pointer memory_;
            public Pointer stream;

            public FT_ListRec.ByValue sizes_list;

            public FT_Generic.ByValue autohint;
            public Pointer extensions;

            public Pointer internal;

            public static class ByReference extends FT_FaceRec implements Structure.ByReference { }
            public static class ByValue extends FT_FaceRec implements Structure.ByValue { }
            public static class P_Face extends Structure {
                public FT_FaceRec.ByReference value;

                public static class ByReference extends P_Face implements Structure.ByReference { }

                @Override
                protected List<String> getFieldOrder() {
                    List<String> Field = new ArrayList<String>();
                    Field.add("value");
                    return Field;
                }
            }

            @Override
            protected List<String> getFieldOrder() {
                List<String> Field = new ArrayList<String>();
                Field.add("num_faces");
                Field.add("face_index");
                Field.add("face_flags");
                Field.add("style_flags");
                Field.add("num_glyphs");
                Field.add("family_name");
                Field.add("style_name");
                Field.add("num_fixed_sizes");
                Field.add("available_sizes");
                Field.add("num_charmaps");
                Field.add("charmaps");
                Field.add("generic");
                Field.add("bbox");
                Field.add("units_per_EM");
                Field.add("ascender");
                Field.add("descender");
                Field.add("height");
                Field.add("max_advance_width");
                Field.add("max_advance_height");
                Field.add("underline_position");
                Field.add("underline_thickness");
                Field.add("glyph");
                Field.add("size");
                Field.add("charmap");
                Field.add("driver");
                Field.add("memory_");
                Field.add("stream");
                Field.add("sizes_list");
                Field.add("autohint");
                Field.add("extensions");
                Field.add("internal");
                return Field;
            }
        }

        public static class FT_Size_Metrics extends Structure {
            public Short x_ppem;
            public Short y_ppem;

            public NativeLong x_scale;
            public NativeLong y_scale;

            public NativeLong ascender;
            public NativeLong descender;
            public NativeLong height;
            public NativeLong max_advance;

            public static class ByReference extends FT_Size_Metrics implements Structure.ByReference { }
            public static class ByValue extends FT_Size_Metrics implements Structure.ByValue { }

            @Override
            protected List<String> getFieldOrder() {
                List<String> Field = new ArrayList<String>();
                Field.add("x_ppem");
                Field.add("y_ppem");
                Field.add("x_scale");
                Field.add("y_scale");
                Field.add("ascender");
                Field.add("descender");
                Field.add("height");
                Field.add("max_advance");
                return Field;
            }
        }

        public static class FT_SizeRec extends Structure {
            public FT_FaceRec.ByReference face;
            public FT_Generic.ByValue generic;
            public FT_Size_Metrics.ByValue metrics;
            public Pointer internal;
            //public FT_Size_InternalRec.ByReference internal;

            public static class ByReference extends FT_SizeRec implements Structure.ByReference { }
            public static class ByValue extends FT_SizeRec implements Structure.ByValue { }

            @Override
            protected List<String> getFieldOrder() {
                List<String> Field = new ArrayList<String>();
                Field.add("face");
                Field.add("generic");
                Field.add("metrics");
                Field.add("internal");
                return Field;
            }
        }

        public static class FT_GlyphSlotRec extends Structure {
            public Pointer library;
            public Pointer face;
            public Pointer next;
            public int reserved;
            public FT_Generic.ByValue generic;

            public FT_Glyph_Metrics.ByValue metrics;
            public NativeLong linearHoriAdvance;
            public NativeLong linearVertAdvance;
            public FT_Vector.ByValue advance;

            public int format;

            public FT_Bitmap.ByValue bitmap;
            public int bitmap_left;
            public int bitmap_top;

            public FT_Outline.ByValue outline;

            public int num_subglyphs;
            //public FT_SubGlyph
            public Pointer subglyphs;

            public Pointer control_data;
            public NativeLong control_len;

            public NativeLong lsb_delta;
            public NativeLong rsb_delta;

            public Pointer other;
            //public
            public Pointer internal;


            public static class ByReference extends FT_GlyphSlotRec implements Structure.ByReference { }
            public static class ByValue extends FT_GlyphSlotRec implements Structure.ByValue { }

            @Override
            protected List<String> getFieldOrder () {
                List<String> Field = new ArrayList<String>();
                Field.add("library");
                Field.add("face");
                Field.add("next");
                Field.add("reserved");
                Field.add("generic");
                Field.add("metrics");
                Field.add("linearHoriAdvance");
                Field.add("linearVertAdvance");
                Field.add("advance");
                Field.add("format");
                Field.add("bitmap");
                Field.add("bitmap_left");
                Field.add("bitmap_top");
                Field.add("outline");
                Field.add("num_subglyphs");
                Field.add("subglyphs");
                Field.add("control_data");
                Field.add("control_len");
                Field.add("lsb_delta");
                Field.add("rsb_delta");
                Field.add("other");
                Field.add("internal");
                return Field;
            }
        }

        public static class FT_Bitmap extends Structure {
            public int rows;
            public int width;
            public int pitch;
            public Pointer buffer;
            public short num_grays;
            public byte pixel_mode;
            public byte palette_mode;
            public Pointer palette;

            public static class ByReference extends FT_Bitmap implements Structure.ByReference { }
            public static class ByValue extends FT_Bitmap implements Structure.ByValue { }

            @Override
            protected List<String> getFieldOrder () {
                List<String> Field = new ArrayList<String>();
                Field.add("rows");
                Field.add("width");
                Field.add("pitch");
                Field.add("buffer");
                Field.add("num_grays");
                Field.add("pixel_mode");
                Field.add("palette_mode");
                Field.add("palette");
                return Field;
            }
        }

        public static class FT_Outline extends Structure {
            public short n_contours;
            public short n_points;

            public FT_Vector.ByReference points;
            public ByteByReference tags;
            public ShortByReference contours;

            public int flags;

            public static class ByReference extends FT_Outline implements Structure.ByReference { }
            public static class ByValue extends FT_Outline implements Structure.ByValue { }

            @Override
            protected List<String> getFieldOrder () {
                List<String> Field = new ArrayList<String>();
                Field.add("n_contours");
                Field.add("n_points");
                Field.add("points");
                Field.add("tags");
                Field.add("contours");
                Field.add("flags");
                return Field;
            }
        }

        public static class FT_Vector extends Structure {
            public NativeLong x;
            public NativeLong y;

            public static class ByReference extends FT_Vector implements Structure.ByReference { }
            public static class ByValue extends FT_Vector implements Structure.ByValue { }

            @Override
            protected List<String> getFieldOrder () {
                List<String> Field = new ArrayList<String>();
                Field.add("x");
                Field.add("y");
                return Field;
            }
        }

        public static class FT_Generic extends Structure {
            public Pointer data;
            public Pointer finalizer;

            public static class ByReference extends FT_Generic implements Structure.ByReference { }
            public static class ByValue extends FT_Generic implements Structure.ByValue { }

            @Override
            protected List<String> getFieldOrder () {
                List<String> Field = new ArrayList<String>();
                Field.add("data");
                Field.add("finalizer");
                return Field;
            }
        }

        public static class FT_BBox extends Structure {
            public NativeLong xMin, yMin;
            public NativeLong xMax, yMax;

            public static class ByReference extends FT_BBox implements Structure.ByReference { }
            public static class ByValue extends FT_BBox implements Structure.ByValue { }

            @Override
            protected List<String> getFieldOrder () {
                List<String> Field = new ArrayList<String>();
                Field.add("xMin");
                Field.add("yMin");
                Field.add("xMax");
                Field.add("yMax");
                return Field;
            }
        }

        public static class FT_DriverRec extends Structure {
            public FT_ModuleRec.ByValue root;
            public Pointer clazz;
            public FT_ListRec.ByValue faces_list;
            public Pointer glyph_loader;

            public static class ByReference extends FT_DriverRec implements Structure.ByReference { }
            public static class ByValue extends FT_DriverRec implements Structure.ByValue { }

            @Override
            protected List<String> getFieldOrder () {
                List<String> Field = new ArrayList<String>();
                Field.add("root");
                Field.add("clazz");
                Field.add("faces_list");
                Field.add("glyph_loader");
                return Field;
            }
        }

        public static class FT_ModuleRec extends Structure {
            public Pointer clazz;
            public Pointer library;
            public Pointer memory;

            public static class ByReference extends FT_ModuleRec implements Structure.ByReference { }
            public static class ByValue extends FT_ModuleRec implements Structure.ByValue { }

            @Override
            protected List<String> getFieldOrder () {
                List<String> Field = new ArrayList<String>();
                Field.add("clazz");
                Field.add("library");
                Field.add("memory");
                return Field;
            }
        }

        public static class FT_ListRec extends Structure {
            public Pointer head;
            public Pointer tail;

            public static class ByReference extends FT_ListRec implements Structure.ByReference { }
            public static class ByValue extends FT_ListRec implements Structure.ByValue { }

            @Override
            protected List<String> getFieldOrder () {
                List<String> Field = new ArrayList<String>();
                Field.add("head");
                Field.add("tail");
                return Field;
            }
        }


        public void FT_Library_Version (Pointer library, IntByReference aMajor, IntByReference aMinor, IntByReference aPatch);

        public int FT_Init_FreeType (PointerByReference aLibrary);

        public int FT_Done_FreeType (Pointer library);

        public int FT_New_Face (Pointer library, String pathName, long faceIndex, FT_FaceRec.ByReference.P_Face aFace);

        public int FT_Set_Char_Size (FT_FaceRec.ByReference Face, long charWidth, long charHeight, long horzResolution, long vertResolution);

        public int FT_Set_Pixel_Sizes (FT_FaceRec.ByReference Face, int pixel_width, int pixel_height);

        public long FT_Get_Char_Index (FT_FaceRec.ByReference Face, long charCode);

        public int FT_Load_Glyph (FT_FaceRec.ByReference Face, long glyphIndex, long loadFlags);

        public int FT_Render_Glyph (FT_GlyphSlotRec.ByReference glyph, int FT_Render_Mode);

        public int FT_Render_Glyph_Internal (Pointer library, FT_FaceRec.ByReference Face,  int FT_Render_Mode);

        public int FT_Outline_Embolden (Pointer aOutline, float strength);

        public int FT_Load_Char (FT_FaceRec.ByReference Face, long charCode, long loadFlags);
    }
}