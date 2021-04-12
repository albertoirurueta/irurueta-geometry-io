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

import com.irurueta.statistics.UniformRandomizer;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class Chunk3DSTest {

    private static final int MIN_VALUE = 0;
    private static final int MAX_VALUE = 1000;

    @Test
    public void testConstants() {
        assertEquals(Chunk3DS.NULL_CHUNK, 0x0000);
        assertEquals(Chunk3DS.M3DMAGIC, 0x4D4D);
        assertEquals(Chunk3DS.SMAGIC, 0x2D2D);
        assertEquals(Chunk3DS.LMAGIC, 0x2D3D);
        assertEquals(Chunk3DS.MLIBMAGIC, 0x3DAA);
        assertEquals(Chunk3DS.MATMAGIC, 0x3DFF);
        assertEquals(Chunk3DS.CMAGIC, 0xC23D);
        assertEquals(Chunk3DS.M3D_VERSION, 0x0002);
        assertEquals(Chunk3DS.M3D_KFVERSION, 0x0005);

        assertEquals(Chunk3DS.COLOR_F, 0x0010);
        assertEquals(Chunk3DS.COLOR_24, 0x0011);
        assertEquals(Chunk3DS.LIN_COLOR_24, 0x0012);
        assertEquals(Chunk3DS.LIN_COLOR_F, 0x0013);
        assertEquals(Chunk3DS.INT_PERCENTAGE, 0x0030);
        assertEquals(Chunk3DS.FLOAT_PERCENTAGE, 0x0031);

        assertEquals(Chunk3DS.MDATA, 0x3D3D);
        assertEquals(Chunk3DS.MESH_VERSION, 0x3D3E);
        assertEquals(Chunk3DS.MASTER_SCALE, 0x0100);
        assertEquals(Chunk3DS.LO_SHADOW_BIAS, 0x1400);
        assertEquals(Chunk3DS.HI_SHADOW_BIAS, 0x1410);
        assertEquals(Chunk3DS.SHADOW_MAP_SIZE, 0x1420);
        assertEquals(Chunk3DS.SHADOW_SAMPLES, 0x1430);
        assertEquals(Chunk3DS.SHADOW_RANGE, 0x1440);
        assertEquals(Chunk3DS.SHADOW_FILTER, 0x1450);
        assertEquals(Chunk3DS.RAY_BIAS, 0x1460);
        assertEquals(Chunk3DS.O_CONSTS, 0x1500);
        assertEquals(Chunk3DS.AMBIENT_LIGHT, 0x2100);
        assertEquals(Chunk3DS.BIT_MAP, 0x1100);
        assertEquals(Chunk3DS.SOLID_BGND, 0x1200);
        assertEquals(Chunk3DS.V_GRADIENT, 0x1300);
        assertEquals(Chunk3DS.USE_BIT_MAP, 0x1101);
        assertEquals(Chunk3DS.USE_SOLID_BGND, 0x1201);
        assertEquals(Chunk3DS.USE_V_GRADIENT, 0x1301);
        assertEquals(Chunk3DS.FOG, 0x2200);
        assertEquals(Chunk3DS.FOG_BGND, 0x2210);
        assertEquals(Chunk3DS.LAYER_FOG, 0x2302);
        assertEquals(Chunk3DS.DISTANCE_CUE, 0x2300);
        assertEquals(Chunk3DS.DCUE_BGND, 0x2310);
        assertEquals(Chunk3DS.USE_FOG, 0x2201);
        assertEquals(Chunk3DS.USE_LAYER_FOG, 0x2303);
        assertEquals(Chunk3DS.USE_DISTANCE_CUE, 0x2301);

        assertEquals(Chunk3DS.MAT_ENTRY, 0xAFFF);
        assertEquals(Chunk3DS.MAT_NAME, 0xA000);
        assertEquals(Chunk3DS.MAT_AMBIENT, 0xA010);
        assertEquals(Chunk3DS.MAT_DIFFUSE, 0xA020);
        assertEquals(Chunk3DS.MAT_SPECULAR, 0xA030);
        assertEquals(Chunk3DS.MAT_SHININESS, 0xA040);
        assertEquals(Chunk3DS.MAT_SHIN2PCT, 0xA041);
        assertEquals(Chunk3DS.MAT_TRANSPARENCY, 0xA050);
        assertEquals(Chunk3DS.MAT_XPFALL, 0xA052);
        assertEquals(Chunk3DS.MAT_USE_XPFALL, 0xA240);
        assertEquals(Chunk3DS.MAT_REFBLUR, 0xA053);
        assertEquals(Chunk3DS.MAT_SHADING, 0xA100);
        assertEquals(Chunk3DS.MAT_USE_REFBLUR, 0xA250);
        assertEquals(Chunk3DS.MAT_SELF_ILLUM, 0xA080);
        assertEquals(Chunk3DS.MAT_TWO_SIDE, 0xA081);
        assertEquals(Chunk3DS.MAT_DECAL, 0xA082);
        assertEquals(Chunk3DS.MAT_ADDITIVE, 0xA083);
        assertEquals(Chunk3DS.MAT_SELF_ILPCT, 0xA084);
        assertEquals(Chunk3DS.MAT_WIRE, 0xA085);
        assertEquals(Chunk3DS.MAT_FACEMAP, 0xA088);
        assertEquals(Chunk3DS.MAT_PHONGSOFT, 0xA08C);
        assertEquals(Chunk3DS.MAT_WIREABS, 0xA08E);
        assertEquals(Chunk3DS.MAT_WIRE_SIZE, 0xA087);
        assertEquals(Chunk3DS.MAT_TEXMAP, 0xA200);
        assertEquals(Chunk3DS.MAT_SXP_TEXT_DATA, 0xA320);
        assertEquals(Chunk3DS.MAT_TEXMASK, 0xA33E);
        assertEquals(Chunk3DS.MAT_SXP_TEXTMASK_DATA, 0xA32A);
        assertEquals(Chunk3DS.MAT_TEX2MAP, 0xA33A);
        assertEquals(Chunk3DS.MAT_SXP_TEXT2_DATA, 0xA321);
        assertEquals(Chunk3DS.MAT_TEX2MASK, 0xA340);
        assertEquals(Chunk3DS.MAT_SXP_TEXT2MASK_DATA, 0xA32C);
        assertEquals(Chunk3DS.MAT_OPACMAP, 0xA210);
        assertEquals(Chunk3DS.MAT_SXP_OPAC_DATA, 0xA322);
        assertEquals(Chunk3DS.MAT_OPACMASK, 0xA342);
        assertEquals(Chunk3DS.MAT_SXP_OPACMASK_DATA, 0xA32E);
        assertEquals(Chunk3DS.MAT_BUMPMAP, 0xA230);
        assertEquals(Chunk3DS.MAT_SXP_BUMP_DATA, 0xA324);
        assertEquals(Chunk3DS.MAT_BUMPMASK, 0xA344);
        assertEquals(Chunk3DS.MAT_SXP_BUMPMASK_DATA, 0xA330);
        assertEquals(Chunk3DS.MAT_SPECMAP, 0xA204);
        assertEquals(Chunk3DS.MAT_SXP_SPEC_DATA, 0xA325);
        assertEquals(Chunk3DS.MAT_SPECMASK, 0xA348);
        assertEquals(Chunk3DS.MAT_SXP_SPECMASK_DATA, 0xA332);
        assertEquals(Chunk3DS.MAT_SHINMAP, 0xA33C);
        assertEquals(Chunk3DS.MAT_SXP_SHIN_DATA, 0xA326);
        assertEquals(Chunk3DS.MAT_SHINMASK, 0xA346);
        assertEquals(Chunk3DS.MAT_SXP_SHINMASK_DATA, 0xA334);
        assertEquals(Chunk3DS.MAT_SELFIMAP, 0xA33D);
        assertEquals(Chunk3DS.MAT_SXP_SELFI_DATA, 0xA328);
        assertEquals(Chunk3DS.MAT_SELFIMASK, 0xA34A);
        assertEquals(Chunk3DS.MAT_SXP_SELFIMASK_DATA, 0xA336);
        assertEquals(Chunk3DS.MAT_REFLMAP, 0xA220);
        assertEquals(Chunk3DS.MAT_REFLMASK, 0xA34C);
        assertEquals(Chunk3DS.MAT_SXP_REFLMASK_DATA, 0xA338);
        assertEquals(Chunk3DS.MAT_ACUBIC, 0xA310);
        assertEquals(Chunk3DS.MAT_MAPNAME, 0xA300);
        assertEquals(Chunk3DS.MAT_MAP_TILING, 0xA351);
        assertEquals(Chunk3DS.MAT_MAP_TEXBLUR, 0xA353);
        assertEquals(Chunk3DS.MAT_MAP_USCALE, 0xA354);
        assertEquals(Chunk3DS.MAT_MAP_VSCALE, 0xA356);
        assertEquals(Chunk3DS.MAT_MAP_UOFFSET, 0xA358);
        assertEquals(Chunk3DS.MAT_MAP_VOFFSET, 0xA35A);
        assertEquals(Chunk3DS.MAT_MAP_ANG, 0xA35C);
        assertEquals(Chunk3DS.MAT_MAP_COL1, 0xA360);
        assertEquals(Chunk3DS.MAT_MAP_COL2, 0xA362);
        assertEquals(Chunk3DS.MAT_MAP_RCOL, 0xA364);
        assertEquals(Chunk3DS.MAT_MAP_GCOL, 0xA366);
        assertEquals(Chunk3DS.MAT_MAP_BCOL, 0xA368);

        assertEquals(Chunk3DS.NAMED_OBJECT, 0x4000);
        assertEquals(Chunk3DS.N_DIRECT_LIGHT, 0x4600);
        assertEquals(Chunk3DS.DL_OFF, 0x4620);
        assertEquals(Chunk3DS.DL_OUTER_RANGE, 0x465A);
        assertEquals(Chunk3DS.DL_INNER_RANGE, 0x4659);
        assertEquals(Chunk3DS.DL_MULTIPLIER, 0x465B);
        assertEquals(Chunk3DS.DL_EXCLUDE, 0x4654);
        assertEquals(Chunk3DS.DL_ATTENUATE, 0x4625);
        assertEquals(Chunk3DS.DL_SPOTLIGHT, 0x4610);
        assertEquals(Chunk3DS.DL_SPOT_ROLL, 0x4656);
        assertEquals(Chunk3DS.DL_SHADOWED, 0x4630);
        assertEquals(Chunk3DS.DL_LOCAL_SHADOW2, 0x4641);
        assertEquals(Chunk3DS.DL_SEE_CONE, 0x4650);
        assertEquals(Chunk3DS.DL_SPOT_RECTANGULAR, 0x4651);
        assertEquals(Chunk3DS.DL_SPOT_ASPECT, 0x4657);
        assertEquals(Chunk3DS.DL_SPOT_PROJECTOR, 0x4653);
        assertEquals(Chunk3DS.DL_SPOT_OVERSHOOT, 0x4652);
        assertEquals(Chunk3DS.DL_RAY_BIAS, 0x4658);
        assertEquals(Chunk3DS.DL_RAYSHAD, 0x4627);
        assertEquals(Chunk3DS.N_CAMERA, 0x4700);
        assertEquals(Chunk3DS.CAM_SEE_CONE, 0x4710);
        assertEquals(Chunk3DS.CAM_RANGES, 0x4720);
        assertEquals(Chunk3DS.OBJ_HIDDEN, 0x4010);
        assertEquals(Chunk3DS.OBJ_VIS_LOFTER, 0x4011);
        assertEquals(Chunk3DS.OBJ_DOESNT_CAST, 0x4012);
        assertEquals(Chunk3DS.OBJ_DONT_RCVSHADOW, 0x4017);
        assertEquals(Chunk3DS.OBJ_MATTE, 0x4013);
        assertEquals(Chunk3DS.OBJ_FAST, 0x4014);
        assertEquals(Chunk3DS.OBJ_PROCEDURAL, 0x4015);
        assertEquals(Chunk3DS.OBJ_FROZEN, 0x4016);
        assertEquals(Chunk3DS.N_TRI_OBJECT, 0x4100);
        assertEquals(Chunk3DS.POINT_ARRAY, 0x4110);
        assertEquals(Chunk3DS.POINT_FLAG_ARRAY, 0x4111);
        assertEquals(Chunk3DS.FACE_ARRAY, 0x4120);
        assertEquals(Chunk3DS.MSH_MAT_GROUP, 0x4130);
        assertEquals(Chunk3DS.SMOOTH_GROUP, 0x4150);
        assertEquals(Chunk3DS.MSH_BOXMAP, 0x4190);
        assertEquals(Chunk3DS.TEX_VERTS, 0x4140);
        assertEquals(Chunk3DS.MESH_MATRIX, 0x4160);
        assertEquals(Chunk3DS.MESH_COLOR, 0x4165);
        assertEquals(Chunk3DS.MESH_TEXTURE_INFO, 0x4170);

        assertEquals(Chunk3DS.KFDATA, 0xB000);
        assertEquals(Chunk3DS.KFHDR, 0xB00A);
        assertEquals(Chunk3DS.KFSEG, 0xB008);
        assertEquals(Chunk3DS.KFCURTIME, 0xB009);
        assertEquals(Chunk3DS.AMBIENT_NODE_TAG, 0xB001);
        assertEquals(Chunk3DS.OBJECT_NODE_TAG, 0xB002);
        assertEquals(Chunk3DS.CAMERA_NODE_TAG, 0xB003);
        assertEquals(Chunk3DS.TARGET_NODE_TAG, 0xB004);
        assertEquals(Chunk3DS.LIGHT_NODE_TAG, 0xB005);
        assertEquals(Chunk3DS.L_TARGET_NODE_TAG, 0xB006);
        assertEquals(Chunk3DS.SPOTLIGHT_NODE_TAG, 0xB007);
        assertEquals(Chunk3DS.NODE_ID, 0xB030);
        assertEquals(Chunk3DS.NODE_HDR, 0xB010);
        assertEquals(Chunk3DS.PIVOT, 0xB013);
        assertEquals(Chunk3DS.INSTANCE_NAME, 0xB011);
        assertEquals(Chunk3DS.MORPH_SMOOTH, 0xB015);
        assertEquals(Chunk3DS.BOUNDBOX, 0xB014);
        assertEquals(Chunk3DS.POS_TRACK_TAG, 0xB020);
        assertEquals(Chunk3DS.COL_TRACK_TAG, 0xB025);
        assertEquals(Chunk3DS.ROT_TRACK_TAG, 0xB021);
        assertEquals(Chunk3DS.SCL_TRACK_TAG, 0xB022);
        assertEquals(Chunk3DS.MORPH_TRACK_TAG, 0xB026);
        assertEquals(Chunk3DS.FOV_TRACK_TAG, 0xB023);
        assertEquals(Chunk3DS.ROLL_TRACK_TAG, 0xB024);
        assertEquals(Chunk3DS.HOT_TRACK_TAG, 0xB027);
        assertEquals(Chunk3DS.FALL_TRACK_TAG, 0xB028);
        assertEquals(Chunk3DS.HIDE_TRACK_TAG, 0xB029);

        assertEquals(Chunk3DS.POLY_2D, 0x5000);
        assertEquals(Chunk3DS.SHAPE_OK, 0x5010);
        assertEquals(Chunk3DS.SHAPE_NOT_OK, 0x5011);
        assertEquals(Chunk3DS.SHAPE_HOOK, 0x5020);
        assertEquals(Chunk3DS.PATH_3D, 0x6000);
        assertEquals(Chunk3DS.PATH_MATRIX, 0x6005);
        assertEquals(Chunk3DS.SHAPE_2D, 0x6010);
        assertEquals(Chunk3DS.M_SCALE, 0x6020);
        assertEquals(Chunk3DS.M_TWIST, 0x6030);
        assertEquals(Chunk3DS.M_TEETER, 0x6040);
        assertEquals(Chunk3DS.M_FIT, 0x6050);
        assertEquals(Chunk3DS.M_BEVEL, 0x6060);
        assertEquals(Chunk3DS.XZ_CURVE, 0x6070);
        assertEquals(Chunk3DS.YZ_CURVE, 0x6080);
        assertEquals(Chunk3DS.INTERPCT, 0x6090);
        assertEquals(Chunk3DS.DEFORM_LIMIT, 0x60A0);

        assertEquals(Chunk3DS.USE_CONTOUR, 0x6100);
        assertEquals(Chunk3DS.USE_TWEEN, 0x6110);
        assertEquals(Chunk3DS.USE_SCALE, 0x6120);
        assertEquals(Chunk3DS.USE_TWIST, 0x6130);
        assertEquals(Chunk3DS.USE_TEETER, 0x6140);
        assertEquals(Chunk3DS.USE_FIT, 0x6150);
        assertEquals(Chunk3DS.USE_BEVEL, 0x6160);

        assertEquals(Chunk3DS.DEFAULT_VIEW, 0x3000);
        assertEquals(Chunk3DS.VIEW_TOP, 0x3010);
        assertEquals(Chunk3DS.VIEW_BOTTOM, 0x3020);
        assertEquals(Chunk3DS.VIEW_LEFT, 0x3030);
        assertEquals(Chunk3DS.VIEW_RIGHT, 0x3040);
        assertEquals(Chunk3DS.VIEW_FRONT, 0x3050);
        assertEquals(Chunk3DS.VIEW_BACK, 0x3060);
        assertEquals(Chunk3DS.VIEW_USER, 0x3070);
        assertEquals(Chunk3DS.VIEW_CAMERA, 0x3080);
        assertEquals(Chunk3DS.VIEW_WINDOW, 0x3090);

        assertEquals(Chunk3DS.VIEWPORT_LAYOUT_OLD, 0x7000);
        assertEquals(Chunk3DS.VIEWPORT_DATA_OLD, 0x7010);
        assertEquals(Chunk3DS.VIEWPORT_LAYOUT, 0x7001);
        assertEquals(Chunk3DS.VIEWPORT_DATA, 0x7011);
        assertEquals(Chunk3DS.VIEWPORT_DATA_3, 0x7012);
        assertEquals(Chunk3DS.VIEWPORT_SIZE, 0x7020);
        assertEquals(Chunk3DS.NETWORK_VIEW, 0x7030);
    }

    @Test
    public void testConstructor() {
        final Chunk3DS chunk = new Chunk3DS();

        // check default values
        assertEquals(chunk.getChunkId(), -1);
        assertFalse(chunk.isChunkIdAvailable());
        assertEquals(chunk.getSize(), -1);
        assertFalse(chunk.isSizeAvailable());
        assertEquals(chunk.getStartStreamPosition(), -1);
        assertFalse(chunk.isStartStreamPositionAvailable());
        assertEquals(chunk.getEndStreamPosition(), -1);
        assertFalse(chunk.isEndStreamPositionAvailable());
    }

    @Test
    public void testGetSetChunkIdAndAvailability() {
        final Chunk3DS chunk = new Chunk3DS();

        final UniformRandomizer randomizer = new UniformRandomizer(new Random());
        final int id = randomizer.nextInt(MIN_VALUE, MAX_VALUE);

        // check default value
        assertEquals(chunk.getChunkId(), -1);
        assertFalse(chunk.isChunkIdAvailable());

        // set new value
        chunk.setChunkId(id);

        // check correctness
        assertEquals(chunk.getChunkId(), id);
        assertTrue(chunk.isChunkIdAvailable());
    }

    @Test
    public void testGetSetSizeAndAvailability() {
        final Chunk3DS chunk = new Chunk3DS();

        final UniformRandomizer randomizer = new UniformRandomizer(new Random());
        final int size = randomizer.nextInt(MIN_VALUE, MAX_VALUE);

        // check default value
        assertEquals(chunk.getSize(), -1);
        assertFalse(chunk.isSizeAvailable());

        // set new value
        chunk.setSize(size);

        // check correctness
        assertEquals(chunk.getSize(), size);
        assertTrue(chunk.isSizeAvailable());
    }

    @Test
    public void testGetSetStartStreamPositionAndAvailability() {
        final Chunk3DS chunk = new Chunk3DS();

        final UniformRandomizer randomizer = new UniformRandomizer(new Random());
        final int pos = randomizer.nextInt(MIN_VALUE, MAX_VALUE);

        // check default value
        assertEquals(chunk.getStartStreamPosition(), -1);
        assertFalse(chunk.isStartStreamPositionAvailable());

        // set new value
        chunk.setStartStreamPosition(pos);

        // check correctness
        assertEquals(chunk.getStartStreamPosition(), pos);
        assertTrue(chunk.isStartStreamPositionAvailable());
    }

    @Test
    public void testGetSetEndStreamPositionAndAvailability() {
        final Chunk3DS chunk = new Chunk3DS();

        final UniformRandomizer randomizer = new UniformRandomizer(new Random());
        final int pos = randomizer.nextInt(MIN_VALUE, MAX_VALUE);

        // check default value
        assertEquals(chunk.getEndStreamPosition(), -1);
        assertFalse(chunk.isEndStreamPositionAvailable());

        // set new value
        chunk.setEndStreamPosition(pos);

        // check correctness
        assertEquals(chunk.getEndStreamPosition(), pos);
        assertTrue(chunk.isEndStreamPositionAvailable());
    }
}
