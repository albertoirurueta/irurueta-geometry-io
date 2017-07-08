/**
 * @file
 * This file contains implementation of
 * com.irurueta.geometry.io.MaterialLoaderOBJ
 * 
 * @author Alberto Irurueta (alberto@irurueta.com)
 * @date October 13, 2012
 */
package com.irurueta.geometry.io;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MaterialLoaderOBJTest implements MaterialLoaderListener{
    
    private boolean forceLoaderException;
    
    public MaterialLoaderOBJTest() { }
    
    @BeforeClass
    public static void setUpClass() { }
    
    @AfterClass
    public static void tearDownClass() { }
    
    @Before
    public void setUp() { }
    
    @After
    public void tearDown() { }

    @Override
    public void onLoadStart(MaterialLoader loader) {
        //test locked exception because loader is loading
        try{
            loader.load();
            fail("LockedException expected but not thrown");
        }catch(LockedException e){
        }catch(Throwable t){
            fail("LockedException expected but not thrown");
        }
    }

    @Override
    public void onLoadEnd(MaterialLoader loader) {
        //test locked exception because loader is loading
        try{
            loader.load();
            fail("LockedException expected but not thrown");
        }catch(LockedException e){
        }catch(Throwable t){
            fail("LockedException expected but not thrown");
        }
    }

    @Override
    public boolean onValidateTexture(MaterialLoader loader, Texture texture) {
        //test locked exception because loader is loading
        try{
            loader.load();
            fail("LockedException expected but not thrown");
        }catch(LockedException e){
        }catch(Throwable t){
            fail("LockedException expected but not thrown");
        }
        
        if(texture != null) texture.setValid(!forceLoaderException);
        
        return !forceLoaderException;
    }
    
    @Test
    public void testConstructor() throws IOException{
        
        //Test default constructor
        MaterialLoaderOBJ loader = new MaterialLoaderOBJ();
        
        //check correctness
        assertFalse(loader.isReady());
        assertFalse(loader.areMaterialsAvailable());
        assertNotNull(loader.getMaterials());
        assertEquals(loader.getMaterials().size(), 0);
        assertEquals(loader.getFileSizeLimitToKeepInMemory(),
                MaterialLoaderOBJ.DEFAULT_FILE_SIZE_LIMIT_TO_KEEP_IN_MEMORY);
        assertFalse(loader.hasFile());
        assertEquals(loader.isTextureValidationEnabled(), 
                MaterialLoaderOBJ.DEFAULT_TEXTURE_VALIDATION_ENABLED);
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
        
        
        //Test constructor with file
        File f = new File("./src/test/java/com/irurueta/geometry/io/potro.mtl");
        loader = new MaterialLoaderOBJ(f);
        
        //check correctness
        assertTrue(loader.isReady());
        assertFalse(loader.areMaterialsAvailable());
        assertNotNull(loader.getMaterials());
        assertEquals(loader.getMaterials().size(), 0);
        assertEquals(loader.getFileSizeLimitToKeepInMemory(),
                MaterialLoaderOBJ.DEFAULT_FILE_SIZE_LIMIT_TO_KEEP_IN_MEMORY);
        assertTrue(loader.hasFile());
        assertEquals(loader.isTextureValidationEnabled(), 
                MaterialLoaderOBJ.DEFAULT_TEXTURE_VALIDATION_ENABLED);
        assertFalse(loader.isLocked());
        assertNull(loader.getListener());
        loader.close(); //release file resources
        
        
        //Force IOException
        loader = null;
        f = new File("fake.mtl");
        try{
            loader = new MaterialLoaderOBJ(f);
            fail("IOException expected but not thrown");
        }catch(IOException e){}
        assertNull(loader);
        
        
        //Test constructor with listener
        loader = new MaterialLoaderOBJ(this);
        
        //check correctness
        assertFalse(loader.isReady());
        assertFalse(loader.areMaterialsAvailable());
        assertNotNull(loader.getMaterials());
        assertEquals(loader.getMaterials().size(), 0);
        assertEquals(loader.getFileSizeLimitToKeepInMemory(),
                MaterialLoaderOBJ.DEFAULT_FILE_SIZE_LIMIT_TO_KEEP_IN_MEMORY);
        assertFalse(loader.hasFile());
        assertEquals(loader.isTextureValidationEnabled(), 
                MaterialLoaderOBJ.DEFAULT_TEXTURE_VALIDATION_ENABLED);
        assertFalse(loader.isLocked());
        assertSame(loader.getListener(), this);
        
        
        //Test constructor with file and listener
        f = new File("./src/test/java/com/irurueta/geometry/io/potro.mtl");
        loader = new MaterialLoaderOBJ(f, this);
        
        //check correctness
        assertTrue(loader.isReady());
        assertFalse(loader.areMaterialsAvailable());
        assertNotNull(loader.getMaterials());
        assertEquals(loader.getMaterials().size(), 0);
        assertEquals(loader.getFileSizeLimitToKeepInMemory(),
                MaterialLoaderOBJ.DEFAULT_FILE_SIZE_LIMIT_TO_KEEP_IN_MEMORY);
        assertTrue(loader.hasFile());
        assertEquals(loader.isTextureValidationEnabled(), 
                MaterialLoaderOBJ.DEFAULT_TEXTURE_VALIDATION_ENABLED);
        assertFalse(loader.isLocked());
        assertSame(loader.getListener(), this);
        loader.close(); //release file resources
        
        //Force IOException
        loader = null;
        f = new File("fake.mtl");
        try{
            loader = new MaterialLoaderOBJ(f, this);
            fail("IOException expected but not thrown");
        }catch(IOException e){}
        assertNull(loader);
        
    }
    
    @Test
    public void testLoad() throws IOException, LockedException, 
        NotReadyException, LoaderException{
        
        File f = new File("./src/test/java/com/irurueta/geometry/io/potro.mtl");
        MaterialLoaderOBJ loader = new MaterialLoaderOBJ(f, this);
        
        assertFalse(loader.areMaterialsAvailable());
        assertTrue(loader.getMaterials().isEmpty());
        
        forceLoaderException = false;
        Set<Material> materials = loader.load();
        loader.close();
        
        assertFalse(materials.isEmpty());
        assertTrue(loader.areMaterialsAvailable());    
        assertSame(loader.getMaterials(), materials);
        
        for(Material material : materials){
            assertTrue(material.getId() >= 0 && 
                    material.getId() < materials.size());
        }
        
        //Force LoaderException
        loader = new MaterialLoaderOBJ(f, this);
        forceLoaderException = true;
        try{
            loader.load();
            fail("LoaderException expected but not thrown");
        }catch(LoaderException e){}
        loader.close();
        
        //Force NotReadyException
        loader = new MaterialLoaderOBJ(this);
        try{
            loader.load();
            fail("NotReadyException expected but not thrown");
        }catch(NotReadyException e){}
    }
    
    @Test
    public void testGetMaterialByName() throws IOException, LockedException, 
        NotReadyException, LoaderException{
        
        File f = new File("./src/test/java/com/irurueta/geometry/io/potro.mtl");
        MaterialLoaderOBJ loader = new MaterialLoaderOBJ(f, this);
        
        assertFalse(loader.areMaterialsAvailable());
        assertTrue(loader.getMaterials().isEmpty());
        
        forceLoaderException = false;
        Set<Material> materials = loader.load();
        loader.close();
        
        
        for(Material material : materials){
            MaterialOBJ material2 = (MaterialOBJ)material;
            MaterialOBJ material3 = loader.getMaterialByName(
                    material2.getMaterialName());
            assertSame(material2, material3);
            assertTrue(loader.containsMaterial(material2.getMaterialName()));
        }
        
        //test for non-existing material
        assertNull(loader.getMaterialByName(""));
        assertFalse(loader.containsMaterial(""));
    }
    
    @Test
    public void testGetMaterialByTextureMapName() throws IOException, 
        LockedException, NotReadyException, LoaderException{
        
        File f = new File("./src/test/java/com/irurueta/geometry/io/potro.mtl");
        MaterialLoaderOBJ loader = new MaterialLoaderOBJ(f, this);
        
        assertFalse(loader.areMaterialsAvailable());
        assertTrue(loader.getMaterials().isEmpty());
        
        forceLoaderException = false;
        Set<Material> materials = loader.load();
        loader.close();
        
        
        for(Material material : materials){
            Texture tex1 = material.getAlphaTextureMap();
            Texture tex2 = material.getAmbientTextureMap();
            Texture tex3 = material.getBumpTextureMap();
            Texture tex4 = material.getDiffuseTextureMap();
            Texture tex5 = material.getSpecularTextureMap();
            
            if(tex1 != null){
                Material material2 = loader.getMaterialByTextureMapName(
                        tex1.getFileName());
                assertSame(material, material2);
            }
            
            if(tex2 != null){
                Material material2 = loader.getMaterialByTextureMapName(
                        tex2.getFileName());
                assertSame(material, material2);                
            }
            
            if(tex3 != null){
                Material material2 = loader.getMaterialByTextureMapName(
                        tex3.getFileName());
                assertSame(material, material2);                
            }
            
            if(tex4 != null){
                Material material2 = loader.getMaterialByTextureMapName(
                        tex4.getFileName());
                assertSame(material, material2);                
            }
            
            if(tex5 != null){
                Material material2 = loader.getMaterialByTextureMapName(
                        tex5.getFileName());
                assertSame(material, material2);                
            }
        }
        
        //test for non-existing texture name
        assertNull(loader.getMaterialByTextureMapName(""));
    }
}
