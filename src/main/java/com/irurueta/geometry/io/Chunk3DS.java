/*
 * Copyright (C) 2012 Alberto Irurueta Carro (alberto@irurueta.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.irurueta.geometry.io;

import java.io.IOException;

public class Chunk3DS {
    public static final int NULL_CHUNK = 0x0000;
    public static final int M3DMAGIC = 0x4D4D;
    public static final int SMAGIC = 0x2D2D;
    public static final int LMAGIC = 0x2D3D;
    public static final int MLIBMAGIC = 0x3DAA;
    public static final int MATMAGIC = 0x3DFF;
    public static final int CMAGIC = 0xC23D;
    public static final int M3D_VERSION = 0x0002;
    public static final int M3D_KFVERSION = 0x0005;

    public static final int COLOR_F = 0x0010;
    public static final int COLOR_24 = 0x0011;
    public static final int LIN_COLOR_24 = 0x0012;
    public static final int LIN_COLOR_F = 0x0013;
    public static final int INT_PERCENTAGE = 0x0030;
    public static final int FLOAT_PERCENTAGE = 0x0031;

    public static final int MDATA = 0x3D3D;
    public static final int MESH_VERSION = 0x3D3E;
    public static final int MASTER_SCALE = 0x0100;
    public static final int LO_SHADOW_BIAS = 0x1400;
    public static final int HI_SHADOW_BIAS = 0x1410;
    public static final int SHADOW_MAP_SIZE = 0x1420;
    public static final int SHADOW_SAMPLES = 0x1430;
    public static final int SHADOW_RANGE = 0x1440;
    public static final int SHADOW_FILTER = 0x1450;
    public static final int RAY_BIAS = 0x1460;
    public static final int O_CONSTS = 0x1500;
    public static final int AMBIENT_LIGHT = 0x2100;
    public static final int BIT_MAP = 0x1100;
    public static final int SOLID_BGND = 0x1200;
    public static final int V_GRADIENT = 0x1300;
    public static final int USE_BIT_MAP = 0x1101;
    public static final int USE_SOLID_BGND = 0x1201;
    public static final int USE_V_GRADIENT = 0x1301;
    public static final int FOG = 0x2200;
    public static final int FOG_BGND = 0x2210;
    public static final int LAYER_FOG = 0x2302;
    public static final int DISTANCE_CUE = 0x2300;
    public static final int DCUE_BGND = 0x2310;
    public static final int USE_FOG = 0x2201;
    public static final int USE_LAYER_FOG = 0x2303;
    public static final int USE_DISTANCE_CUE = 0x2301;

    public static final int MAT_ENTRY = 0xAFFF;
    public static final int MAT_NAME = 0xA000;
    public static final int MAT_AMBIENT = 0xA010;
    public static final int MAT_DIFFUSE = 0xA020;
    public static final int MAT_SPECULAR = 0xA030;
    public static final int MAT_SHININESS = 0xA040;
    public static final int MAT_SHIN2PCT = 0xA041;
    public static final int MAT_TRANSPARENCY = 0xA050;
    public static final int MAT_XPFALL = 0xA052;
    public static final int MAT_USE_XPFALL = 0xA240;
    public static final int MAT_REFBLUR = 0xA053;
    public static final int MAT_SHADING = 0xA100;
    public static final int MAT_USE_REFBLUR = 0xA250;
    public static final int MAT_SELF_ILLUM = 0xA080;
    public static final int MAT_TWO_SIDE = 0xA081;
    public static final int MAT_DECAL = 0xA082;
    public static final int MAT_ADDITIVE = 0xA083;
    public static final int MAT_SELF_ILPCT = 0xA084;
    public static final int MAT_WIRE = 0xA085;
    public static final int MAT_FACEMAP = 0xA088;
    public static final int MAT_PHONGSOFT = 0xA08C;
    public static final int MAT_WIREABS = 0xA08E;
    public static final int MAT_WIRE_SIZE = 0xA087;
    public static final int MAT_TEXMAP = 0xA200;
    public static final int MAT_SXP_TEXT_DATA = 0xA320;
    public static final int MAT_TEXMASK = 0xA33E;
    public static final int MAT_SXP_TEXTMASK_DATA = 0xA32A;
    public static final int MAT_TEX2MAP = 0xA33A;
    public static final int MAT_SXP_TEXT2_DATA = 0xA321;
    public static final int MAT_TEX2MASK = 0xA340;
    public static final int MAT_SXP_TEXT2MASK_DATA = 0xA32C;
    public static final int MAT_OPACMAP = 0xA210;
    public static final int MAT_SXP_OPAC_DATA = 0xA322;
    public static final int MAT_OPACMASK = 0xA342;
    public static final int MAT_SXP_OPACMASK_DATA = 0xA32E;
    public static final int MAT_BUMPMAP = 0xA230;
    public static final int MAT_SXP_BUMP_DATA = 0xA324;
    public static final int MAT_BUMPMASK = 0xA344;
    public static final int MAT_SXP_BUMPMASK_DATA = 0xA330;
    public static final int MAT_SPECMAP = 0xA204;
    public static final int MAT_SXP_SPEC_DATA = 0xA325;
    public static final int MAT_SPECMASK = 0xA348;
    public static final int MAT_SXP_SPECMASK_DATA = 0xA332;
    public static final int MAT_SHINMAP = 0xA33C;
    public static final int MAT_SXP_SHIN_DATA = 0xA326;
    public static final int MAT_SHINMASK = 0xA346;
    public static final int MAT_SXP_SHINMASK_DATA = 0xA334;
    public static final int MAT_SELFIMAP = 0xA33D;
    public static final int MAT_SXP_SELFI_DATA = 0xA328;
    public static final int MAT_SELFIMASK = 0xA34A;
    public static final int MAT_SXP_SELFIMASK_DATA = 0xA336;
    public static final int MAT_REFLMAP = 0xA220;
    public static final int MAT_REFLMASK = 0xA34C;
    public static final int MAT_SXP_REFLMASK_DATA = 0xA338;
    public static final int MAT_ACUBIC = 0xA310;
    public static final int MAT_MAPNAME = 0xA300;
    public static final int MAT_MAP_TILING = 0xA351;
    public static final int MAT_MAP_TEXBLUR = 0xA353;
    public static final int MAT_MAP_USCALE = 0xA354;
    public static final int MAT_MAP_VSCALE = 0xA356;
    public static final int MAT_MAP_UOFFSET = 0xA358;
    public static final int MAT_MAP_VOFFSET = 0xA35A;
    public static final int MAT_MAP_ANG = 0xA35C;
    public static final int MAT_MAP_COL1 = 0xA360;
    public static final int MAT_MAP_COL2 = 0xA362;
    public static final int MAT_MAP_RCOL = 0xA364;
    public static final int MAT_MAP_GCOL = 0xA366;
    public static final int MAT_MAP_BCOL = 0xA368;

    public static final int NAMED_OBJECT = 0x4000;
    public static final int N_DIRECT_LIGHT = 0x4600;
    public static final int DL_OFF = 0x4620;
    public static final int DL_OUTER_RANGE = 0x465A;
    public static final int DL_INNER_RANGE = 0x4659;
    public static final int DL_MULTIPLIER = 0x465B;
    public static final int DL_EXCLUDE = 0x4654;
    public static final int DL_ATTENUATE = 0x4625;
    public static final int DL_SPOTLIGHT = 0x4610;
    public static final int DL_SPOT_ROLL = 0x4656;
    public static final int DL_SHADOWED = 0x4630;
    public static final int DL_LOCAL_SHADOW2 = 0x4641;
    public static final int DL_SEE_CONE = 0x4650;
    public static final int DL_SPOT_RECTANGULAR = 0x4651;
    public static final int DL_SPOT_ASPECT = 0x4657;
    public static final int DL_SPOT_PROJECTOR = 0x4653;
    public static final int DL_SPOT_OVERSHOOT = 0x4652;
    public static final int DL_RAY_BIAS = 0x4658;
    public static final int DL_RAYSHAD = 0x4627;
    public static final int N_CAMERA = 0x4700;
    public static final int CAM_SEE_CONE = 0x4710;
    public static final int CAM_RANGES = 0x4720;
    public static final int OBJ_HIDDEN = 0x4010;
    public static final int OBJ_VIS_LOFTER = 0x4011;
    public static final int OBJ_DOESNT_CAST = 0x4012;
    public static final int OBJ_DONT_RCVSHADOW = 0x4017;
    public static final int OBJ_MATTE = 0x4013;
    public static final int OBJ_FAST = 0x4014;
    public static final int OBJ_PROCEDURAL = 0x4015;
    public static final int OBJ_FROZEN = 0x4016;
    public static final int N_TRI_OBJECT = 0x4100;
    public static final int POINT_ARRAY = 0x4110;
    public static final int POINT_FLAG_ARRAY = 0x4111;
    public static final int FACE_ARRAY = 0x4120;
    public static final int MSH_MAT_GROUP = 0x4130;
    public static final int SMOOTH_GROUP = 0x4150;
    public static final int MSH_BOXMAP = 0x4190;
    public static final int TEX_VERTS = 0x4140;
    public static final int MESH_MATRIX = 0x4160;
    public static final int MESH_COLOR = 0x4165;
    public static final int MESH_TEXTURE_INFO = 0x4170;

    public static final int KFDATA = 0xB000;
    public static final int KFHDR = 0xB00A;
    public static final int KFSEG = 0xB008;
    public static final int KFCURTIME = 0xB009;
    public static final int AMBIENT_NODE_TAG = 0xB001;
    public static final int OBJECT_NODE_TAG = 0xB002;
    public static final int CAMERA_NODE_TAG = 0xB003;
    public static final int TARGET_NODE_TAG = 0xB004;
    public static final int LIGHT_NODE_TAG = 0xB005;
    public static final int L_TARGET_NODE_TAG = 0xB006;
    public static final int SPOTLIGHT_NODE_TAG = 0xB007;
    public static final int NODE_ID = 0xB030;
    public static final int NODE_HDR = 0xB010;
    public static final int PIVOT = 0xB013;
    public static final int INSTANCE_NAME = 0xB011;
    public static final int MORPH_SMOOTH = 0xB015;
    public static final int BOUNDBOX = 0xB014;
    public static final int POS_TRACK_TAG = 0xB020;
    public static final int COL_TRACK_TAG = 0xB025;
    public static final int ROT_TRACK_TAG = 0xB021;
    public static final int SCL_TRACK_TAG = 0xB022;
    public static final int MORPH_TRACK_TAG = 0xB026;
    public static final int FOV_TRACK_TAG = 0xB023;
    public static final int ROLL_TRACK_TAG = 0xB024;
    public static final int HOT_TRACK_TAG = 0xB027;
    public static final int FALL_TRACK_TAG = 0xB028;
    public static final int HIDE_TRACK_TAG = 0xB029;

    public static final int POLY_2D = 0x5000;
    public static final int SHAPE_OK = 0x5010;
    public static final int SHAPE_NOT_OK = 0x5011;
    public static final int SHAPE_HOOK = 0x5020;
    public static final int PATH_3D = 0x6000;
    public static final int PATH_MATRIX = 0x6005;
    public static final int SHAPE_2D = 0x6010;
    public static final int M_SCALE = 0x6020;
    public static final int M_TWIST = 0x6030;
    public static final int M_TEETER = 0x6040;
    public static final int M_FIT = 0x6050;
    public static final int M_BEVEL = 0x6060;
    public static final int XZ_CURVE = 0x6070;
    public static final int YZ_CURVE = 0x6080;
    public static final int INTERPCT = 0x6090;
    public static final int DEFORM_LIMIT = 0x60A0;

    public static final int USE_CONTOUR = 0x6100;
    public static final int USE_TWEEN = 0x6110;
    public static final int USE_SCALE = 0x6120;
    public static final int USE_TWIST = 0x6130;
    public static final int USE_TEETER = 0x6140;
    public static final int USE_FIT = 0x6150;
    public static final int USE_BEVEL = 0x6160;

    public static final int DEFAULT_VIEW = 0x3000;
    public static final int VIEW_TOP = 0x3010;
    public static final int VIEW_BOTTOM = 0x3020;
    public static final int VIEW_LEFT = 0x3030;
    public static final int VIEW_RIGHT = 0x3040;
    public static final int VIEW_FRONT = 0x3050;
    public static final int VIEW_BACK = 0x3060;
    public static final int VIEW_USER = 0x3070;
    public static final int VIEW_CAMERA = 0x3080;
    public static final int VIEW_WINDOW = 0x3090;

    public static final int VIEWPORT_LAYOUT_OLD = 0x7000;
    public static final int VIEWPORT_DATA_OLD = 0x7010;
    public static final int VIEWPORT_LAYOUT = 0x7001;
    public static final int VIEWPORT_DATA = 0x7011;
    public static final int VIEWPORT_DATA_3 = 0x7012;
    public static final int VIEWPORT_SIZE = 0x7020;
    public static final int NETWORK_VIEW = 0x7030;

    // uint16
    private int chunkId;

    // uint32
    private long size;

    private long startStreamPosition;
    private long endStreamPosition;

    public Chunk3DS() {
        chunkId = -1;
        size = startStreamPosition = endStreamPosition = -1;
    }

    public int getChunkId() {
        return chunkId;
    }

    public void setChunkId(final int chunkId) {
        this.chunkId = chunkId;
    }

    public boolean isChunkIdAvailable() {
        return chunkId >= 0;
    }

    public long getSize() {
        return size;
    }

    public void setSize(final long size) {
        this.size = size;
    }

    public boolean isSizeAvailable() {
        return size >= 0;
    }

    public long getStartStreamPosition() {
        return startStreamPosition;
    }

    public void setStartStreamPosition(final long startStreamPosition) {
        this.startStreamPosition = startStreamPosition;
    }

    public boolean isStartStreamPositionAvailable() {
        return startStreamPosition >= 0;
    }

    public long getEndStreamPosition() {
        return endStreamPosition;
    }

    public void setEndStreamPosition(final long endStreamPosition) {
        this.endStreamPosition = endStreamPosition;
    }

    public boolean isEndStreamPositionAvailable() {
        return endStreamPosition >= 0;
    }

    public static Chunk3DS load(final AbstractFileReaderAndWriter reader)
            throws IOException {
        final Chunk3DS chunk = new Chunk3DS();
        chunk.startStreamPosition = reader.getPosition();
        chunk.chunkId = reader.readUnsignedShort(EndianType.LITTLE_ENDIAN_TYPE);
        chunk.size = reader.readUnsignedInt(EndianType.LITTLE_ENDIAN_TYPE);
        chunk.endStreamPosition = chunk.startStreamPosition + chunk.size;

        return chunk;
    }
}
