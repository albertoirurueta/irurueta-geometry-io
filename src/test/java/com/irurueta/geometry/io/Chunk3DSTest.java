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
        assertEquals(0x0000, Chunk3DS.NULL_CHUNK);
        assertEquals(0x4D4D, Chunk3DS.M3DMAGIC);
        assertEquals(0x2D2D, Chunk3DS.SMAGIC);
        assertEquals(0x2D3D, Chunk3DS.LMAGIC);
        assertEquals(0x3DAA, Chunk3DS.MLIBMAGIC);
        assertEquals(0x3DFF, Chunk3DS.MATMAGIC);
        assertEquals(0xC23D, Chunk3DS.CMAGIC);
        assertEquals(0x0002, Chunk3DS.M3D_VERSION);
        assertEquals(0x0005, Chunk3DS.M3D_KFVERSION);

        assertEquals(0x0010, Chunk3DS.COLOR_F);
        assertEquals(0x0011, Chunk3DS.COLOR_24);
        assertEquals(0x0012, Chunk3DS.LIN_COLOR_24);
        assertEquals(0x0013, Chunk3DS.LIN_COLOR_F);
        assertEquals(0x0030, Chunk3DS.INT_PERCENTAGE);
        assertEquals(0x0031, Chunk3DS.FLOAT_PERCENTAGE);

        assertEquals(0x3D3D, Chunk3DS.MDATA);
        assertEquals(0x3D3E, Chunk3DS.MESH_VERSION);
        assertEquals(0x0100, Chunk3DS.MASTER_SCALE);
        assertEquals(0x1400, Chunk3DS.LO_SHADOW_BIAS);
        assertEquals(0x1410, Chunk3DS.HI_SHADOW_BIAS);
        assertEquals(0x1420, Chunk3DS.SHADOW_MAP_SIZE);
        assertEquals(0x1430, Chunk3DS.SHADOW_SAMPLES);
        assertEquals(0x1440, Chunk3DS.SHADOW_RANGE);
        assertEquals(0x1450, Chunk3DS.SHADOW_FILTER);
        assertEquals(0x1460, Chunk3DS.RAY_BIAS);
        assertEquals(0x1500, Chunk3DS.O_CONSTS);
        assertEquals(0x2100, Chunk3DS.AMBIENT_LIGHT);
        assertEquals(0x1100, Chunk3DS.BIT_MAP);
        assertEquals(0x1200, Chunk3DS.SOLID_BGND);
        assertEquals(0x1300, Chunk3DS.V_GRADIENT);
        assertEquals(0x1101, Chunk3DS.USE_BIT_MAP);
        assertEquals(0x1201, Chunk3DS.USE_SOLID_BGND);
        assertEquals(0x1301, Chunk3DS.USE_V_GRADIENT);
        assertEquals(0x2200, Chunk3DS.FOG);
        assertEquals(0x2210, Chunk3DS.FOG_BGND);
        assertEquals(0x2302, Chunk3DS.LAYER_FOG);
        assertEquals(0x2300, Chunk3DS.DISTANCE_CUE);
        assertEquals(0x2310, Chunk3DS.DCUE_BGND);
        assertEquals(0x2201, Chunk3DS.USE_FOG);
        assertEquals(0x2303, Chunk3DS.USE_LAYER_FOG);
        assertEquals(0x2301, Chunk3DS.USE_DISTANCE_CUE);

        assertEquals(0xAFFF, Chunk3DS.MAT_ENTRY);
        assertEquals(0xA000, Chunk3DS.MAT_NAME);
        assertEquals(0xA010, Chunk3DS.MAT_AMBIENT);
        assertEquals(0xA020, Chunk3DS.MAT_DIFFUSE);
        assertEquals(0xA030, Chunk3DS.MAT_SPECULAR);
        assertEquals(0xA040, Chunk3DS.MAT_SHININESS);
        assertEquals(0xA041, Chunk3DS.MAT_SHIN2PCT);
        assertEquals(0xA050, Chunk3DS.MAT_TRANSPARENCY);
        assertEquals(0xA052, Chunk3DS.MAT_XPFALL);
        assertEquals(0xA240, Chunk3DS.MAT_USE_XPFALL);
        assertEquals(0xA053, Chunk3DS.MAT_REFBLUR);
        assertEquals(0xA100, Chunk3DS.MAT_SHADING);
        assertEquals(0xA250, Chunk3DS.MAT_USE_REFBLUR);
        assertEquals(0xA080, Chunk3DS.MAT_SELF_ILLUM);
        assertEquals(0xA081, Chunk3DS.MAT_TWO_SIDE);
        assertEquals(0xA082, Chunk3DS.MAT_DECAL);
        assertEquals(0xA083, Chunk3DS.MAT_ADDITIVE);
        assertEquals(0xA084, Chunk3DS.MAT_SELF_ILPCT);
        assertEquals(0xA085, Chunk3DS.MAT_WIRE);
        assertEquals(0xA088, Chunk3DS.MAT_FACEMAP);
        assertEquals(0xA08C, Chunk3DS.MAT_PHONGSOFT);
        assertEquals(0xA08E, Chunk3DS.MAT_WIREABS);
        assertEquals(0xA087, Chunk3DS.MAT_WIRE_SIZE);
        assertEquals(0xA200, Chunk3DS.MAT_TEXMAP);
        assertEquals(0xA320, Chunk3DS.MAT_SXP_TEXT_DATA);
        assertEquals(0xA33E, Chunk3DS.MAT_TEXMASK);
        assertEquals(0xA32A, Chunk3DS.MAT_SXP_TEXTMASK_DATA);
        assertEquals(0xA33A, Chunk3DS.MAT_TEX2MAP);
        assertEquals(0xA321, Chunk3DS.MAT_SXP_TEXT2_DATA);
        assertEquals(0xA340, Chunk3DS.MAT_TEX2MASK);
        assertEquals(0xA32C, Chunk3DS.MAT_SXP_TEXT2MASK_DATA);
        assertEquals(0xA210, Chunk3DS.MAT_OPACMAP);
        assertEquals(0xA322, Chunk3DS.MAT_SXP_OPAC_DATA);
        assertEquals(0xA342, Chunk3DS.MAT_OPACMASK);
        assertEquals(0xA32E, Chunk3DS.MAT_SXP_OPACMASK_DATA);
        assertEquals(0xA230, Chunk3DS.MAT_BUMPMAP);
        assertEquals(0xA324, Chunk3DS.MAT_SXP_BUMP_DATA);
        assertEquals(0xA344, Chunk3DS.MAT_BUMPMASK);
        assertEquals(0xA330, Chunk3DS.MAT_SXP_BUMPMASK_DATA);
        assertEquals(0xA204, Chunk3DS.MAT_SPECMAP);
        assertEquals(0xA325, Chunk3DS.MAT_SXP_SPEC_DATA);
        assertEquals(0xA348, Chunk3DS.MAT_SPECMASK);
        assertEquals(0xA332, Chunk3DS.MAT_SXP_SPECMASK_DATA);
        assertEquals(0xA33C, Chunk3DS.MAT_SHINMAP);
        assertEquals(0xA326, Chunk3DS.MAT_SXP_SHIN_DATA);
        assertEquals(0xA346, Chunk3DS.MAT_SHINMASK);
        assertEquals(0xA334, Chunk3DS.MAT_SXP_SHINMASK_DATA);
        assertEquals(0xA33D, Chunk3DS.MAT_SELFIMAP);
        assertEquals(0xA328, Chunk3DS.MAT_SXP_SELFI_DATA);
        assertEquals(0xA34A, Chunk3DS.MAT_SELFIMASK);
        assertEquals(0xA336, Chunk3DS.MAT_SXP_SELFIMASK_DATA);
        assertEquals(0xA220, Chunk3DS.MAT_REFLMAP);
        assertEquals(0xA34C, Chunk3DS.MAT_REFLMASK);
        assertEquals(0xA338, Chunk3DS.MAT_SXP_REFLMASK_DATA);
        assertEquals(0xA310, Chunk3DS.MAT_ACUBIC);
        assertEquals(0xA300, Chunk3DS.MAT_MAPNAME);
        assertEquals(0xA351, Chunk3DS.MAT_MAP_TILING);
        assertEquals(0xA353, Chunk3DS.MAT_MAP_TEXBLUR);
        assertEquals(0xA354, Chunk3DS.MAT_MAP_USCALE);
        assertEquals(0xA356, Chunk3DS.MAT_MAP_VSCALE);
        assertEquals(0xA358, Chunk3DS.MAT_MAP_UOFFSET);
        assertEquals(0xA35A, Chunk3DS.MAT_MAP_VOFFSET);
        assertEquals(0xA35C, Chunk3DS.MAT_MAP_ANG);
        assertEquals(0xA360, Chunk3DS.MAT_MAP_COL1);
        assertEquals(0xA362, Chunk3DS.MAT_MAP_COL2);
        assertEquals(0xA364, Chunk3DS.MAT_MAP_RCOL);
        assertEquals(0xA366, Chunk3DS.MAT_MAP_GCOL);
        assertEquals(0xA368, Chunk3DS.MAT_MAP_BCOL);

        assertEquals(0x4000, Chunk3DS.NAMED_OBJECT);
        assertEquals(0x4600, Chunk3DS.N_DIRECT_LIGHT);
        assertEquals(0x4620, Chunk3DS.DL_OFF);
        assertEquals(0x465A, Chunk3DS.DL_OUTER_RANGE);
        assertEquals(0x4659, Chunk3DS.DL_INNER_RANGE);
        assertEquals(0x465B, Chunk3DS.DL_MULTIPLIER);
        assertEquals(0x4654, Chunk3DS.DL_EXCLUDE);
        assertEquals(0x4625, Chunk3DS.DL_ATTENUATE);
        assertEquals(0x4610, Chunk3DS.DL_SPOTLIGHT);
        assertEquals(0x4656, Chunk3DS.DL_SPOT_ROLL);
        assertEquals(0x4630, Chunk3DS.DL_SHADOWED);
        assertEquals(0x4641, Chunk3DS.DL_LOCAL_SHADOW2);
        assertEquals(0x4650, Chunk3DS.DL_SEE_CONE);
        assertEquals(0x4651, Chunk3DS.DL_SPOT_RECTANGULAR);
        assertEquals(0x4657, Chunk3DS.DL_SPOT_ASPECT);
        assertEquals(0x4653, Chunk3DS.DL_SPOT_PROJECTOR);
        assertEquals(0x4652, Chunk3DS.DL_SPOT_OVERSHOOT);
        assertEquals(0x4658, Chunk3DS.DL_RAY_BIAS);
        assertEquals(0x4627, Chunk3DS.DL_RAYSHAD);
        assertEquals(0x4700, Chunk3DS.N_CAMERA);
        assertEquals(0x4710, Chunk3DS.CAM_SEE_CONE);
        assertEquals(0x4720, Chunk3DS.CAM_RANGES);
        assertEquals(0x4010, Chunk3DS.OBJ_HIDDEN);
        assertEquals(0x4011, Chunk3DS.OBJ_VIS_LOFTER);
        assertEquals(0x4012, Chunk3DS.OBJ_DOESNT_CAST);
        assertEquals(0x4017, Chunk3DS.OBJ_DONT_RCVSHADOW);
        assertEquals(0x4013, Chunk3DS.OBJ_MATTE);
        assertEquals(0x4014, Chunk3DS.OBJ_FAST);
        assertEquals(0x4015, Chunk3DS.OBJ_PROCEDURAL);
        assertEquals(0x4016, Chunk3DS.OBJ_FROZEN);
        assertEquals(0x4100, Chunk3DS.N_TRI_OBJECT);
        assertEquals(0x4110, Chunk3DS.POINT_ARRAY);
        assertEquals(0x4111, Chunk3DS.POINT_FLAG_ARRAY);
        assertEquals(0x4120, Chunk3DS.FACE_ARRAY);
        assertEquals(0x4130, Chunk3DS.MSH_MAT_GROUP);
        assertEquals(0x4150, Chunk3DS.SMOOTH_GROUP);
        assertEquals(0x4190, Chunk3DS.MSH_BOXMAP);
        assertEquals(0x4140, Chunk3DS.TEX_VERTS);
        assertEquals(0x4160, Chunk3DS.MESH_MATRIX);
        assertEquals(0x4165, Chunk3DS.MESH_COLOR);
        assertEquals(0x4170, Chunk3DS.MESH_TEXTURE_INFO);

        assertEquals(0xB000, Chunk3DS.KFDATA);
        assertEquals(0xB00A, Chunk3DS.KFHDR);
        assertEquals(0xB008, Chunk3DS.KFSEG);
        assertEquals(0xB009, Chunk3DS.KFCURTIME);
        assertEquals(0xB001, Chunk3DS.AMBIENT_NODE_TAG);
        assertEquals(0xB002, Chunk3DS.OBJECT_NODE_TAG);
        assertEquals(0xB003, Chunk3DS.CAMERA_NODE_TAG);
        assertEquals(0xB004, Chunk3DS.TARGET_NODE_TAG);
        assertEquals(0xB005, Chunk3DS.LIGHT_NODE_TAG);
        assertEquals(0xB006, Chunk3DS.L_TARGET_NODE_TAG);
        assertEquals(0xB007, Chunk3DS.SPOTLIGHT_NODE_TAG);
        assertEquals(0xB030, Chunk3DS.NODE_ID);
        assertEquals(0xB010, Chunk3DS.NODE_HDR);
        assertEquals(0xB013, Chunk3DS.PIVOT);
        assertEquals(0xB011, Chunk3DS.INSTANCE_NAME);
        assertEquals(0xB015, Chunk3DS.MORPH_SMOOTH);
        assertEquals(0xB014, Chunk3DS.BOUNDBOX);
        assertEquals(0xB020, Chunk3DS.POS_TRACK_TAG);
        assertEquals(0xB025, Chunk3DS.COL_TRACK_TAG);
        assertEquals(0xB021, Chunk3DS.ROT_TRACK_TAG);
        assertEquals(0xB022, Chunk3DS.SCL_TRACK_TAG);
        assertEquals(0xB026, Chunk3DS.MORPH_TRACK_TAG);
        assertEquals(0xB023, Chunk3DS.FOV_TRACK_TAG);
        assertEquals(0xB024, Chunk3DS.ROLL_TRACK_TAG);
        assertEquals(0xB027, Chunk3DS.HOT_TRACK_TAG);
        assertEquals(0xB028, Chunk3DS.FALL_TRACK_TAG);
        assertEquals(0xB029, Chunk3DS.HIDE_TRACK_TAG);

        assertEquals(0x5000, Chunk3DS.POLY_2D);
        assertEquals(0x5010, Chunk3DS.SHAPE_OK);
        assertEquals(0x5011, Chunk3DS.SHAPE_NOT_OK);
        assertEquals(0x5020, Chunk3DS.SHAPE_HOOK);
        assertEquals(0x6000, Chunk3DS.PATH_3D);
        assertEquals(0x6005, Chunk3DS.PATH_MATRIX);
        assertEquals(0x6010, Chunk3DS.SHAPE_2D);
        assertEquals(0x6020, Chunk3DS.M_SCALE);
        assertEquals(0x6030, Chunk3DS.M_TWIST);
        assertEquals(0x6040, Chunk3DS.M_TEETER);
        assertEquals(0x6050, Chunk3DS.M_FIT);
        assertEquals(0x6060, Chunk3DS.M_BEVEL);
        assertEquals(0x6070, Chunk3DS.XZ_CURVE);
        assertEquals(0x6080, Chunk3DS.YZ_CURVE);
        assertEquals(0x6090, Chunk3DS.INTERPCT);
        assertEquals(0x60A0, Chunk3DS.DEFORM_LIMIT);

        assertEquals(0x6100, Chunk3DS.USE_CONTOUR);
        assertEquals(0x6110, Chunk3DS.USE_TWEEN);
        assertEquals(0x6120, Chunk3DS.USE_SCALE);
        assertEquals(0x6130, Chunk3DS.USE_TWIST);
        assertEquals(0x6140, Chunk3DS.USE_TEETER);
        assertEquals(0x6150, Chunk3DS.USE_FIT);
        assertEquals(0x6160, Chunk3DS.USE_BEVEL);

        assertEquals(0x3000, Chunk3DS.DEFAULT_VIEW);
        assertEquals(0x3010, Chunk3DS.VIEW_TOP);
        assertEquals(0x3020, Chunk3DS.VIEW_BOTTOM);
        assertEquals(0x3030, Chunk3DS.VIEW_LEFT);
        assertEquals(0x3040, Chunk3DS.VIEW_RIGHT);
        assertEquals(0x3050, Chunk3DS.VIEW_FRONT);
        assertEquals(0x3060, Chunk3DS.VIEW_BACK);
        assertEquals(0x3070, Chunk3DS.VIEW_USER);
        assertEquals(0x3080, Chunk3DS.VIEW_CAMERA);
        assertEquals(0x3090, Chunk3DS.VIEW_WINDOW);

        assertEquals(0x7000, Chunk3DS.VIEWPORT_LAYOUT_OLD);
        assertEquals(0x7010, Chunk3DS.VIEWPORT_DATA_OLD);
        assertEquals(0x7001, Chunk3DS.VIEWPORT_LAYOUT);
        assertEquals(0x7011, Chunk3DS.VIEWPORT_DATA);
        assertEquals(0x7012, Chunk3DS.VIEWPORT_DATA_3);
        assertEquals(0x7020, Chunk3DS.VIEWPORT_SIZE);
        assertEquals(0x7030, Chunk3DS.NETWORK_VIEW);
    }

    @Test
    public void testConstructor() {
        final Chunk3DS chunk = new Chunk3DS();

        // check default values
        assertEquals(-1, chunk.getChunkId());
        assertFalse(chunk.isChunkIdAvailable());
        assertEquals(-1, chunk.getSize());
        assertFalse(chunk.isSizeAvailable());
        assertEquals(-1, chunk.getStartStreamPosition());
        assertFalse(chunk.isStartStreamPositionAvailable());
        assertEquals(-1, chunk.getEndStreamPosition());
        assertFalse(chunk.isEndStreamPositionAvailable());
    }

    @Test
    public void testGetSetChunkIdAndAvailability() {
        final Chunk3DS chunk = new Chunk3DS();

        final UniformRandomizer randomizer = new UniformRandomizer(new Random());
        final int id = randomizer.nextInt(MIN_VALUE, MAX_VALUE);

        // check default value
        assertEquals(-1, chunk.getChunkId());
        assertFalse(chunk.isChunkIdAvailable());

        // set new value
        chunk.setChunkId(id);

        // check correctness
        assertEquals(id, chunk.getChunkId());
        assertTrue(chunk.isChunkIdAvailable());
    }

    @Test
    public void testGetSetSizeAndAvailability() {
        final Chunk3DS chunk = new Chunk3DS();

        final UniformRandomizer randomizer = new UniformRandomizer(new Random());
        final int size = randomizer.nextInt(MIN_VALUE, MAX_VALUE);

        // check default value
        assertEquals(-1, chunk.getSize());
        assertFalse(chunk.isSizeAvailable());

        // set new value
        chunk.setSize(size);

        // check correctness
        assertEquals(size, chunk.getSize());
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
        assertEquals(pos, chunk.getStartStreamPosition());
        assertTrue(chunk.isStartStreamPositionAvailable());
    }

    @Test
    public void testGetSetEndStreamPositionAndAvailability() {
        final Chunk3DS chunk = new Chunk3DS();

        final UniformRandomizer randomizer = new UniformRandomizer(new Random());
        final int pos = randomizer.nextInt(MIN_VALUE, MAX_VALUE);

        // check default value
        assertEquals(-1, chunk.getEndStreamPosition());
        assertFalse(chunk.isEndStreamPositionAvailable());

        // set new value
        chunk.setEndStreamPosition(pos);

        // check correctness
        assertEquals(pos, chunk.getEndStreamPosition());
        assertTrue(chunk.isEndStreamPositionAvailable());
    }
}
