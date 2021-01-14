package basemod.helpers;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import basemod.interfaces.PostRenderSubscriber;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.screens.stats.AchievementItem;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class CustomAchievementRenderer implements PostRenderSubscriber {
  private static final float MOVE_DURATION = .7f;
  private static final float SHOW_DURATION = 6f;
  private static final float BADGE_TOP_OFFSET = 14;
  private static final float BADGE_LEFT_OFFSET = 14;
  private static final float TEXT_TOP_OFFSET = 54;
  private static final float TEXT_LEFT_OFFSET = 88;
  private static final Logger LOGGER = LogManager.getLogger(CustomAchievementRenderer.class);

  private static List<AbstractGameEffect> achievementsToRender = new ArrayList<>();

  private static SpriteBatch mySpriteBatch;
  private static BitmapFont font;

  public CustomAchievementRenderer() {
    BaseMod.subscribe(this);
  }

  public static void addAchievementToRenderQueue(AchievementItem achievement) {
    if (font == null) {
      initializeFont();
    }
    if (mySpriteBatch == null) {
       mySpriteBatch = new SpriteBatch();
    }

    queuePopupForRendering(achievement);
  }

  private static void queuePopupForRendering(AchievementItem achievement) {
    Texture achievementPopup = ImageMaster.loadImage("img/achievements/achievementpopup.png");
    float popupW = achievementPopup.getWidth();
    float popupH = achievementPopup.getHeight();
    achievementsToRender.add(getAchievementPopupElement(popupW, popupH, achievementPopup, 0, 0));

    TextureAtlas.AtlasRegion achievementBadge = ReflectionHacks.getPrivate(achievement, AchievementItem.class, "img");
    if (achievementBadge != null) {
      FrameBuffer fb = new FrameBuffer(Pixmap.Format.RGBA8888, 64, 64, false);
      achievementBadge.flip(false, true);
      fb.begin();
      Gdx.gl.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
      Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
      Gdx.gl.glColorMask(true, true, true, true);
      OrthographicCamera og = new OrthographicCamera(64, 64);
      mySpriteBatch.setProjectionMatrix(og.combined);
      mySpriteBatch.begin();
      mySpriteBatch.draw(achievementBadge, -32, -32, 64, 64);
      mySpriteBatch.end();
      fb.end();
      achievementBadge.flip(false, true);
      TextureRegion textureRegion = new TextureRegion(fb.getColorBufferTexture(), 0, 0);
      achievementsToRender.add(
          getAchievementPopupElement(popupW, popupH, textureRegion.getTexture(), BADGE_LEFT_OFFSET,
              BADGE_TOP_OFFSET));
    }
    else {
      LOGGER.error("Achievement icon not found of achievement %s", achievement.key);
    }

    FrameBuffer fb = new FrameBuffer(Pixmap.Format.RGBA8888, (int) (popupW - TEXT_LEFT_OFFSET), 12, false);
    TextureRegion texture = new TextureRegion(fb.getColorBufferTexture());
    texture.flip(false, true);
    fb.begin();
    Gdx.gl.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    Gdx.gl.glColorMask(true, true, true, true);
    OrthographicCamera og = new OrthographicCamera((popupW - TEXT_LEFT_OFFSET), 12);
    mySpriteBatch.setProjectionMatrix(og.combined);
    mySpriteBatch.begin();
    String title = ReflectionHacks.getPrivate(achievement, AchievementItem.class, "title");
    FontHelper.renderFont(mySpriteBatch, font, title, -(popupW - TEXT_LEFT_OFFSET)/2, -6, Color.RED);
    mySpriteBatch.end();
    fb.end();
    texture.flip(false, true);
    achievementsToRender.add(getAchievementPopupElement(popupW, popupH, texture.getTexture(), TEXT_LEFT_OFFSET, TEXT_TOP_OFFSET));
  }

  private static void initializeFont() {
    font = FontHelper.cardTypeFont;
  }

  @Override
  public void receivePostRender(SpriteBatch sb) {
    achievementsToRender.forEach(it -> it.render(sb));
    achievementsToRender.forEach(AbstractGameEffect::update);
    achievementsToRender.removeIf(it -> it.isDone);
  }

  private static AbstractGameEffect getAchievementPopupElement(float popupW, float popupH, Texture texture,
      float popupLeftOffset, float popupTopOffset) {
    float elementW = texture.getWidth();
    float elementH = texture.getHeight();
    return new VfxBuilder(texture, Settings.WIDTH - popupW + popupLeftOffset + elementW / 2,
        0 - popupTopOffset - elementH / 2, MOVE_DURATION)
        .setScale(1 / Settings.scale)
        .moveY(0 - popupTopOffset - elementH / 2, 0 + popupH - popupTopOffset - elementH / 2)
        .andThen(SHOW_DURATION)
        .andThen(MOVE_DURATION)
        .moveY(0 + popupH - popupTopOffset - elementH / 2, 0 - popupTopOffset - elementH / 2)
        .build();
  }
}
