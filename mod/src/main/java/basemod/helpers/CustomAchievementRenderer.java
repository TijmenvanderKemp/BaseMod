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
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Disposable;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.screens.stats.AchievementItem;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CustomAchievementRenderer implements PostRenderSubscriber {
  private static final float MOVE_DURATION = .7f;
  private static final float SHOW_DURATION = 6f;
  private static final float POPUP_WIDTH = 239f;
  private static final float POPUP_HEIGHT = 95f;
  private static final float BADGE_TOP_OFFSET = 14;
  private static final float BADGE_LEFT_OFFSET = 14;
  private static final int BADGE_WIDTH = 64;
  private static final int BADGE_HEIGHT = 64;
  private static final float TEXT_TOP_OFFSET = 54;
  private static final float TEXT_LEFT_OFFSET = 88;
  private static final int TEXT_HEIGHT = 12;
  private static final Logger LOGGER = LogManager.getLogger(CustomAchievementRenderer.class);

  private static List<GameEffectAndDisposable> achievementsToRenderWithAssociatedDisposable = new ArrayList<>();

  private static class GameEffectAndDisposable {
    private AbstractGameEffect gameEffect;
    private Disposable disposable;

    public GameEffectAndDisposable(AbstractGameEffect gameEffect, Disposable disposable) {
      this.gameEffect = gameEffect;
      this.disposable = disposable;
    }
  }

  private static SpriteBatch myBadgeSpriteBatch;
  private static SpriteBatch myTitleSpriteBatch;
  private static BitmapFont font;

  public CustomAchievementRenderer() {
    BaseMod.subscribe(this);
  }

  @Override
  public void receivePostRender(SpriteBatch sb) {
    for (GameEffectAndDisposable gameEffectAndDisposable : achievementsToRenderWithAssociatedDisposable) {
      gameEffectAndDisposable.gameEffect.render(sb);
      gameEffectAndDisposable.gameEffect.update();
      if (gameEffectAndDisposable.gameEffect.isDone) {
        gameEffectAndDisposable.disposable.dispose();
      }
    }
    achievementsToRenderWithAssociatedDisposable.removeIf(it -> it.gameEffect.isDone);
  }

  public static void addAchievementToRenderQueue(AchievementItem achievement) {
    if (font == null) {
      initializeFont();
    }
    if (myBadgeSpriteBatch == null) {
      myBadgeSpriteBatch = new SpriteBatch();
    }
    if (myTitleSpriteBatch == null) {
      myTitleSpriteBatch = new SpriteBatch();
    }

    queuePopupForRendering();
    queueBadgeForRendering(achievement);
    queueTitleForRendering(achievement);
  }

  private static void initializeFont() {
    font = FontHelper.cardTypeFont;
  }

  private static void queuePopupForRendering() {
    Texture achievementPopup = ImageMaster.loadImage("img/achievements/achievementpopup.png");
    achievementsToRenderWithAssociatedDisposable.add(
        new GameEffectAndDisposable(
            getAchievementPopupElement(achievementPopup, 0, 0),
            achievementPopup
        )
    );
  }

  private static void queueBadgeForRendering(AchievementItem achievement) {
    TextureAtlas.AtlasRegion achievementBadge = ReflectionHacks.getPrivate(achievement, AchievementItem.class, "img");
    if (achievementBadge != null) {

      achievementBadge.flip(false, true);

      FrameBuffer fb = createFrameBuffer(BADGE_WIDTH, BADGE_HEIGHT);
      onFrameBuffer(fb, frameBuffer ->
          onSpriteBatch(myBadgeSpriteBatch, BADGE_WIDTH, BADGE_HEIGHT, sb ->
              sb.draw(achievementBadge, -BADGE_WIDTH / 2f, -BADGE_HEIGHT / 2f, BADGE_WIDTH, BADGE_HEIGHT)));

      achievementBadge.flip(false, true);

      achievementsToRenderWithAssociatedDisposable.add(
          new GameEffectAndDisposable(
              getAchievementPopupElement(getTextureFromFrameBuffer(fb), BADGE_LEFT_OFFSET, BADGE_TOP_OFFSET),
              fb
          )
      );
    }
    else {
      LOGGER.error("Achievement icon not found of achievement %s", achievement.key);
    }
  }

  private static void queueTitleForRendering(AchievementItem achievement) {
    String title = ReflectionHacks.getPrivate(achievement, AchievementItem.class, "title");

    FrameBuffer fb = createFrameBuffer((int) (POPUP_WIDTH - TEXT_LEFT_OFFSET), TEXT_HEIGHT);
    onFrameBuffer(fb, frameBuffer ->
        onSpriteBatch(myTitleSpriteBatch, (int) (POPUP_WIDTH - TEXT_LEFT_OFFSET), TEXT_HEIGHT, sb ->
//            FontHelper.renderFont(mySpriteBatch, font, title, -(POPUP_WIDTH - TEXT_LEFT_OFFSET) / 2, -TEXT_HEIGHT / 2f,
            FontHelper.renderFont(sb, font, title, -100, 0, Color.RED)));

    achievementsToRenderWithAssociatedDisposable.add(
        new GameEffectAndDisposable(
            getAchievementPopupElement(getTextureFromFrameBuffer(fb), TEXT_LEFT_OFFSET, TEXT_TOP_OFFSET),
            fb
        )
    );
  }

  private static FrameBuffer createFrameBuffer(int width, int height) {
    return new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
  }

  private static void onFrameBuffer(FrameBuffer fb, Consumer<FrameBuffer> block) {
    fb.begin();
    Gdx.gl.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    Gdx.gl.glColorMask(true, true, true, true);
    block.accept(fb);
    fb.end();
  }

  private static void onSpriteBatch(SpriteBatch sb, int viewportWidth, int viewportHeight, Consumer<SpriteBatch> block) {
    OrthographicCamera og = new OrthographicCamera(viewportWidth, viewportHeight);
    sb.setProjectionMatrix(og.combined);
    sb.begin();
    block.accept(sb);
    sb.end();
  }

  private static Texture getTextureFromFrameBuffer(FrameBuffer fb) {
    TextureRegion textureRegion = new TextureRegion(fb.getColorBufferTexture(), 0, 0);
    textureRegion.flip(false, true);
    return textureRegion.getTexture();
  }

  private static AbstractGameEffect getAchievementPopupElement(Texture texture, float popupLeftOffset,
      float popupTopOffset) {
    float elementW = texture.getWidth();
    float elementH = texture.getHeight();
    return new VfxBuilder(texture, Settings.WIDTH - POPUP_WIDTH + popupLeftOffset + elementW / 2,
        0 - popupTopOffset - elementH / 2, MOVE_DURATION)
        .setScale(1 / Settings.scale)
        .moveY(0 - popupTopOffset - elementH / 2, 0 + POPUP_HEIGHT - popupTopOffset - elementH / 2)
        .andThen(SHOW_DURATION)
        .andThen(MOVE_DURATION)
        .moveY(0 + POPUP_HEIGHT - popupTopOffset - elementH / 2, 0 - popupTopOffset - elementH / 2)
        .build();
  }
}
