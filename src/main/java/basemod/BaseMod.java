package basemod;

import basemod.interfaces.*;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class BaseMod {
    public static final Logger logger = LogManager.getLogger(BaseMod.class.getName());
    
    private static final String MODNAME = "BaseMod";
    private static final String AUTHOR = "t-larson";
    private static final String DESCRIPTION = "v1.2.0 NL Provides hooks and a console";
    
    private static final int BADGES_PER_ROW = 16;
    private static final float BADGES_X = 640.0f;
    private static final float BADGES_Y = 250.0f;
    public static final float BADGE_W = 40.0f;
    public static final float BADGE_H = 40.0f;
    
    private static InputProcessor oldInputProcessor = null;
    
    private static ArrayList<ModBadge> modBadges;
    private static ArrayList<PostDrawSubscriber> postDrawSubscribers;
    private static ArrayList<PostEnergyRechargeSubscriber> postEnergyRechargeSubscribers;
    private static ArrayList<PostInitializeSubscriber> postInitializeSubscribers;
    private static ArrayList<RenderSubscriber> renderSubscribers;
    private static ArrayList<PostRenderSubscriber> postRenderSubscribers;
    private static ArrayList<PreUpdateSubscriber> preUpdateSubscribers;
    private static ArrayList<PostUpdateSubscriber> postUpdateSubscribers;  
    
    public static DevConsole console;
    public static Gson gson;
    
    public static boolean modSettingsUp = false;
    public static float mapPathDensityMultiplier = 1.0f;
    
    // initialize -
    public static void initialize() {
        logger.info("========================= BASEMOD INIT =========================");
        logger.info("isModded: " + Settings.isModded);
        
        initializeGson();
        
        modBadges = new ArrayList<ModBadge>();
        postDrawSubscribers = new ArrayList<PostDrawSubscriber>();
        postEnergyRechargeSubscribers = new ArrayList<PostEnergyRechargeSubscriber>();
        postInitializeSubscribers = new ArrayList<PostInitializeSubscriber>();
        renderSubscribers = new ArrayList<RenderSubscriber>();
        postRenderSubscribers = new ArrayList<PostRenderSubscriber>();
        preUpdateSubscribers = new ArrayList<PreUpdateSubscriber>();
        postUpdateSubscribers = new ArrayList<PostUpdateSubscriber>();
           
        console = new DevConsole();
        
        logger.info("================================================================");
    }
    
    // initializeGson -
    private static void initializeGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
    }
    
    //
    // Mod badges
    //
    public static void registerModBadge(Texture t, String name, String author, String desc, ModPanel settingsPanel) {
        int modBadgeCount = modBadges.size();
        int col = (modBadgeCount%BADGES_PER_ROW);
        int row = (modBadgeCount/BADGES_PER_ROW);
        float x = (BADGES_X*Settings.scale) + (col*BADGE_W*Settings.scale);
        float y = (BADGES_Y*Settings.scale) - (row*BADGE_H*Settings.scale);
        
        ModBadge badge = new ModBadge(t, x, y, name, author, desc, settingsPanel);
        modBadges.add(badge);
    }
    
    //
    // Localization
    //
    
    // loadCustomRelicStrings - loads custom RelicStrings from provided JSON
    public static void loadCustomRelicStrings(String jsonString) {
        HashMap<String, RelicStrings> customRelicStrings = new HashMap<String, RelicStrings>();
        Type relicType = new TypeToken<HashMap<String, RelicStrings>>(){}.getType();
        customRelicStrings.putAll(gson.fromJson(jsonString, relicType));
        
        Map<String, RelicStrings> relicStrings = (Map<String, RelicStrings>) getPrivateStatic(LocalizedStrings.class, "relics");
        relicStrings.putAll(customRelicStrings);
        setPrivateStaticFinal(LocalizedStrings.class, "relics", relicStrings);
    }
    
    //
    // Publishers
    //
    
    // publishPostDraw -
    public static void publishPostDraw(AbstractCard c) {
        for (PostDrawSubscriber sub : postDrawSubscribers) {
            sub.receivePostDraw(c);
        }
    }
    
    // publishPostEnergyRecharge -
    public static void publishPostEnergyRecharge() {
        for (PostEnergyRechargeSubscriber sub : postEnergyRechargeSubscribers) {
            sub.receivePostEnergyRecharge();
        }
    }
    
    // publishPostInitialize -
    public static void publishPostInitialize() {
        // BaseMod post initialize handling
        ModPanel settingsPanel = new ModPanel();
        settingsPanel.addLabel("", 475.0f, 700.0f, (me) -> {
            if (me.parent.waitingOnEvent) {
                me.text = "Press key";
            } else {
                me.text = "Change console hotkey (" + Keys.toString(DevConsole.toggleKey) + ")";
            }
        });
        
        settingsPanel.addButton(350.0f, 650.0f, (me) -> {
            me.parent.waitingOnEvent = true;
            oldInputProcessor = Gdx.input.getInputProcessor();
            Gdx.input.setInputProcessor(new InputAdapter() {
                @Override
                public boolean keyUp(int keycode) {
                    DevConsole.toggleKey = keycode;
                    me.parent.waitingOnEvent = false;
                    Gdx.input.setInputProcessor(oldInputProcessor);
                    return true;
                }
            });
        });
        
        Texture badgeTexture = new Texture(Gdx.files.internal("img/BaseModBadge.png"));
        registerModBadge(badgeTexture, MODNAME, AUTHOR, DESCRIPTION, settingsPanel);
        
        // Publish
        for (PostInitializeSubscriber sub : postInitializeSubscribers) {
            sub.receivePostInitialize();
        }
    }
    
    // publishRender -
    public static void publishRender(SpriteBatch sb) {
        for (RenderSubscriber sub : renderSubscribers) {
            sub.receiveRender(sb);
        }
    }
    
    // publishPostRender -
    public static void publishPostRender(SpriteBatch sb) {
        for (PostRenderSubscriber sub : postRenderSubscribers) {
            sub.receivePostRender(sb);
        }
    }
    
    // publishPreUpdate -
    public static void publishPreUpdate() {
        for (PreUpdateSubscriber sub : preUpdateSubscribers) {
            sub.receivePreUpdate();
        }
    }
    
    // publishPostUpdate -
    public static void publishPostUpdate() {
        for (PostUpdateSubscriber sub : postUpdateSubscribers) {
            sub.receivePostUpdate();
        }
    }
    
    //
    // Subsciption handlers
    //
    
    // subscribeToPostDraw -
    public static void subscribeToPostDraw(PostDrawSubscriber sub) {
        postDrawSubscribers.add(sub);
    }
    
    // unsubscribeFromPostDraw -
    public static void unsubscribeFromPostDraw(PostDrawSubscriber sub) {
        postDrawSubscribers.remove(sub);
    }
    
    // subscribeToPostEnergyRecharge -
    public static void subscribeToPostEnergyRecharge(PostEnergyRechargeSubscriber sub) {
        postEnergyRechargeSubscribers.add(sub);
    }
    
    // unsubscribeFromPostEnergyRecharge -
    public static void unsubscribeFromPostEnergyRecharge(PostEnergyRechargeSubscriber sub) {
        postEnergyRechargeSubscribers.remove(sub);
    }
    
    // subscribeToPostInitialize -
    public static void subscribeToPostInitialize(PostInitializeSubscriber sub) {
        postInitializeSubscribers.add(sub);
    }
    
    // unsubscribeFromPostRender -
    public static void unsubscribeFromPostInitialize(PostInitializeSubscriber sub) {
        postInitializeSubscribers.remove(sub);
    }
    
    // subscribeToRender -
    public static void subscribeToRender(RenderSubscriber sub) {
        renderSubscribers.add(sub);
    }
    
    // unsubscribeFromRender -
    public static void unsubscribeFromRender(RenderSubscriber sub) {
        renderSubscribers.remove(sub);
    }
    
    // subscribeToPostRender -
    public static void subscribeToPostRender(PostRenderSubscriber sub) {
        postRenderSubscribers.add(sub);
    }
    
    // unsubscribeFromPostRender -
    public static void unsubscribeFromPostRender(PostRenderSubscriber sub) {
        postRenderSubscribers.remove(sub);
    }
    
    // subscribeToPreUpdate -
    public static void subscribeToPreUpdate(PreUpdateSubscriber sub) {
        preUpdateSubscribers.add(sub);
    }
    
    // unsubscribeFromPreUpdate -
    public static void unsubscribeFromPreUpdate(PreUpdateSubscriber sub) {
        preUpdateSubscribers.remove(sub);
    }
    
    // subscribeToPostUpdate -
    public static void subscribeToPostUpdate(PostUpdateSubscriber sub) {
        postUpdateSubscribers.add(sub);
    }
    
    // unsubscribeFromUpdate -
    public static void unsubscribeFromPostUpdate(PostUpdateSubscriber sub) {
        postUpdateSubscribers.remove(sub);
    }
    
    //
    // Reflection hacks
    //
    
    // getPrivateStatic - read private static variables
    private static Object getPrivateStatic(Class objClass, String fieldName) {
        try {
            Field targetField = objClass.getDeclaredField(fieldName);
            targetField.setAccessible(true);
            return targetField.get(null);
        } catch (Exception e) {
            logger.error("Exception occured when getting private static field " + fieldName + " of " + objClass.getName(), e);
        }
        
        return null;
    }
    
    // setFinalStatic - modify (private) static (final) variables
    private static void setPrivateStaticFinal(Class objClass, String fieldName, Object newValue) {
        try {
            Field targetField = objClass.getDeclaredField(fieldName);
            
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(targetField, targetField.getModifiers() & ~Modifier.FINAL);
            
            targetField.setAccessible(true);
            targetField.set(null, newValue);
        } catch (Exception e) {
            logger.error("Exception occured when setting (private) static (final) field " + fieldName + " of " + objClass.getName(), e);
        }
    }
}