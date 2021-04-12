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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Loader3DSTest {

    @Test
    public void testChunkIds() {
        assertEquals(Loader3DS.ID_MAIN_CHUNK, 0x4D4D);
        assertEquals(Loader3DS.ID_3D_EDITOR_CHUNK, 0x3D3D);
        assertEquals(Loader3DS.ID_OBJECT_BLOCK, 0x4000);
        assertEquals(Loader3DS.ID_TRIANGULAR_MESH, 0x4100);
        assertEquals(Loader3DS.ID_VERTICES_LIST, 0x4110);
        assertEquals(Loader3DS.ID_FACES_DESCRIPTION, 0x4120);
        assertEquals(Loader3DS.ID_FACES_MATERIAL, 0x4130);
        assertEquals(Loader3DS.ID_SMOOTHING_GROUP_LIST, 0x4150);
        assertEquals(Loader3DS.ID_MAPPING_COORDINATES_LIST, 0x4140);
        assertEquals(Loader3DS.ID_LOCAL_COORDINATES_SYSTEM, 0x4160);
        assertEquals(Loader3DS.ID_LIGHT, 0x4600);
        assertEquals(Loader3DS.ID_SPOTLIGHT, 0x4610);
        assertEquals(Loader3DS.ID_CAMERA, 0x4700);
        assertEquals(Loader3DS.ID_MATERIAL_BLOCK, 0xAFFF);
        assertEquals(Loader3DS.ID_MATERIAL_NAME, 0xA000);
        assertEquals(Loader3DS.ID_AMBIENT_COLOR, 0xA010);
        assertEquals(Loader3DS.ID_DIFFUSE_COLOR, 0xA020);
        assertEquals(Loader3DS.ID_SPECULAR_COLOR, 0xA030);
        assertEquals(Loader3DS.ID_TEXTURE_MAP_1, 0xA200);
        assertEquals(Loader3DS.ID_BUMP_MAP, 0xA230);
        assertEquals(Loader3DS.ID_REFLECTION_MAP, 0xA220);
        assertEquals(Loader3DS.ID_MAPPING_FILENAME, 0xA300);
        assertEquals(Loader3DS.ID_MAPPING_PARAMETERS, 0xA351);
        assertEquals(Loader3DS.ID_KEYFRAMER_CHUNK, 0xB000);
        assertEquals(Loader3DS.ID_MESH_INFORMATION_BLOCK, 0xB002);
        assertEquals(Loader3DS.ID_SPOT_LIGHT_INFORMATION_BLOCK, 0xB007);
        assertEquals(Loader3DS.ID_FRAMES, 0xB008);
        assertEquals(Loader3DS.ID_OBJECT_NAME, 0xB010);
        assertEquals(Loader3DS.ID_OBJECT_PIVOT_POINT, 0xB013);
        assertEquals(Loader3DS.ID_POSITION_TRACK, 0xB020);
        assertEquals(Loader3DS.ID_ROTATION_TRACK, 0xB021);
        assertEquals(Loader3DS.ID_SCALE_TRACK, 0xB022);
        assertEquals(Loader3DS.ID_HIERARCHY_POSITION, 0xB030);
    }
}
