package engineTester;

import javax.management.modelmbean.ModelMBean;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.TextureLoader;

import entities.Entity;
import entities.Camera;
import entities.Light;
import entities.Player;
import models.RawModel;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import renderEngine.EntityRenderer;
import shaders.StaticShader;
import textures.ModelTexture;
import textures.TerrainTexture;
import terrains.Terrain;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import textures.TerrainTexturePack;

import objConverter.ModelData;

public class MainGameLoop {

    public static void main(String[] args) {

        Random random = new Random();
        DisplayManager.createDisplay();
        Loader loader = new Loader();
        StaticShader shader = new StaticShader();

        //Marks 0,0,0
        RawModel zero = OBJLoader.loadObjModel("zero", loader);
        TexturedModel staticZero = new TexturedModel(zero, new ModelTexture(loader.loadTexture("eden")));
        Entity zeroEntity = new Entity(staticZero, new Vector3f(0, 0, 0), 0, 0, 0, .2f);

        //sun
        RawModel sunModel = OBJLoader.loadObjModel("sun", loader);
        TexturedModel sunStaticModel = new TexturedModel(sunModel, new ModelTexture(loader.loadTexture("sun")));
        Light light = new Light(new Vector3f(3000, 2000, 2000), new Vector3f(1,1,1));
        Entity sun = new Entity(sunStaticModel, new Vector3f(light.getPosition().x, light.getPosition().y, light.getPosition().z), 0, 0, 0, 1);

        //----Texture Mapping-----
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));

        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
        //-----------------------

        //----Entities-----

        TexturedModel grass = new TexturedModel(OBJLoader.loadObjModel("grassModel", loader), 
                new ModelTexture(loader.loadTexture("grassTexture")));
                grass.getTexture().setHasTransparency(true);
                grass.getTexture().setUseFakeLighting(true);

        TexturedModel flower = new TexturedModel(OBJLoader.loadObjModel("grassModel", loader), 
                new ModelTexture(loader.loadTexture("flower")));
                flower.getTexture().setHasTransparency(true);
                flower.getTexture().setUseFakeLighting(true);

        TexturedModel fern = new TexturedModel(OBJLoader.loadObjModel("fern", loader), 
                new ModelTexture(loader.loadTexture("fern")));
                fern.getTexture().setHasTransparency(true);

        ModelData data = OBJFileLoader.loadOBJ("lowPolyTree");
        RawModel treeModel = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
        TexturedModel tree = new TexturedModel(treeModel, 
                 new ModelTexture(loader.loadTexture("lowPolyTree")));

        TexturedModel bunModel = new TexturedModel(OBJLoader.loadObjModel("bunny", loader),
                        new ModelTexture(loader.loadTexture("tan")));
                        bunModel.getTexture().setShineDamper(10);
                        bunModel.getTexture().setReflectivity(1);

        TexturedModel meBun = new TexturedModel(OBJLoader.loadObjModel("bunny", loader),
                        new ModelTexture(loader.loadTexture("black")));
                        meBun.getTexture().setShineDamper(10);
                        meBun.getTexture().setReflectivity(1);
                        
        Player player = new Player(meBun, new Vector3f(0,0,0), 0, 0, 0, 1);
        Camera camera = new Camera(player);

        //----EntitiesEnd-----

        //----Filling lists---
        Terrain[] terrains = {
            new Terrain(-1,-1, loader,texturePack, blendMap),
            new Terrain(-1,0, loader,texturePack, blendMap),
            new Terrain(0,0, loader,texturePack, blendMap),
            new Terrain(0,-1, loader,texturePack, blendMap)
        };

        List<Entity> buns = new ArrayList<>();
        for(int i = 0; i < 250; i++){
            buns.add(new Entity(bunModel, new Vector3f(random.nextFloat() * 800 - 400, 0, random.nextFloat() * -600),0, random.nextFloat()*360,0, random.nextFloat()*3));
        }

        //Plant/Tree Lists
        List<Entity> plants = new ArrayList<>();
        for(int i = 0; i < 250; i++){
            plants.add(new Entity(tree, new Vector3f(random.nextFloat() * 800 - 400, 0, random.nextFloat() * -600),0,random.nextFloat()*360,0, random.nextFloat()*3));
            plants.add(new Entity(grass, new Vector3f(random.nextFloat() * 800 - 400, 0, random.nextFloat() * -600),0,random.nextFloat()*360,0, 1));
            plants.add(new Entity(fern, new Vector3f(random.nextFloat() * 800 - 400, 0, random.nextFloat() * -600),0,random.nextFloat()*360,0, 1));
            plants.add(new Entity(flower, new Vector3f(random.nextFloat() * 800 - 400, 0, random.nextFloat() * -600),0,random.nextFloat()*360,0, 1));
        }
        //----Filling lists End---


        MasterRenderer renderer = new MasterRenderer();
        while (!Display.isCloseRequested()) {
            // game logic
            camera.move();

            player.move();
            renderer.processEntity(player);

            for(Entity bun: buns){
                bun.increaseRotation(0, .02f, 0);
                renderer.processEntity(bun);
            }

            for(Entity plant: plants){
                renderer.processEntity(plant);
            }

            for(Terrain terrain: terrains){
                renderer.processTerrain(terrain);
            }
            
            renderer.processEntity(sun);
            renderer.processEntity(zeroEntity);
            renderer.render(light, camera);
            DisplayManager.updateDisplay();
        }

        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();

    }

}