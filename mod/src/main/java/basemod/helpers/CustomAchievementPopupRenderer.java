package basemod.helpers;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import basemod.interfaces.PostRenderSubscriber;
import basemod.patches.com.megacrit.cardcrawl.unlock.UnlockTracker.RenderBordersOverCustomAchievementInGrid;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
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
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.stats.AchievementItem;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class CustomAchievementPopupRenderer implements PostRenderSubscriber {
  private static final float MOVE_DURATION = .7f;
  private static final float SHOW_DURATION = 6f;
  private static final float POPUP_WIDTH = 239f;
  private static final float POPUP_HEIGHT = 95f;
  private static final float BADGE_TOP_OFFSET = 14;
  private static final float BADGE_LEFT_OFFSET = 14;
  private static final int BADGE_WIDTH = 64;
  private static final int BADGE_HEIGHT = 64;
  private static final float TEXT_TOP_OFFSET = 27;
  private static final float TEXT_LEFT_OFFSET = 88;
  private static final float TEXT_WIDTH = POPUP_WIDTH - TEXT_LEFT_OFFSET;
  private static final int TEXT_HEIGHT = 40;
  private static final Logger LOGGER = LogManager.getLogger(CustomAchievementPopupRenderer.class);

  private static final List<GameEffectAndDisposable> achievementsToRenderWithAssociatedDisposable = new ArrayList<>();

  private static class GameEffectAndDisposable {
    private final AbstractGameEffect gameEffect;
    private final Disposable disposable;

    public GameEffectAndDisposable(AbstractGameEffect gameEffect, Disposable disposable) {
      this.gameEffect = gameEffect;
      this.disposable = disposable;
    }
  }

  private static SpriteBatch mySpriteBatch;
  private static BitmapFont font;

  public CustomAchievementPopupRenderer() {
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
    if (mySpriteBatch == null) {
      mySpriteBatch = new SpriteBatch();
    }

    queuePopupForRendering();
    queueBadgeForRendering(achievement);
    queueBadgeBorderForRendering(achievement);
    queueTextForRendering(achievement);
  }

  private static void initializeFont() {
    // Loading a custom font does not work because of FreeType error code 2 (this probably means wrong file type)
    // So I'm using the font that's included with the base game that looks the most like the Steam achievement font
    // which is Helvetica Regular I believe.
    FileHandle fileHandle = Gdx.files.internal("font/gre/Roboto-Regular.ttf");
    ReflectionHacks.setPrivateStatic(FontHelper.class, "fontFile", fileHandle);
    font = FontHelper.prepFont(12.5f, true);
  }

  private static void queuePopupForRendering() {
    Texture achievementPopup = ImageMaster.loadImage("img/achievements/achievementpopup.png");
    TextureAtlas.AtlasRegion atlasRegion = new TextureAtlas.AtlasRegion(
        achievementPopup,
        0,
        0,
        achievementPopup.getWidth(),
        achievementPopup.getHeight());

    achievementsToRenderWithAssociatedDisposable.add(
        new GameEffectAndDisposable(
            getAchievementPopupElement(atlasRegion, 0, 0),
            achievementPopup
        )
    );
  }

  private static void queueBadgeForRendering(AchievementItem achievement) {
    TextureAtlas.AtlasRegion achievementBadge = ReflectionHacks.getPrivate(achievement, AchievementItem.class, "img");
    if (achievementBadge != null) {

      FrameBuffer fb = createFrameBuffer(BADGE_WIDTH, BADGE_HEIGHT);
      beginSpriteBatch(mySpriteBatch, BADGE_WIDTH, BADGE_HEIGHT);
      mySpriteBatch.draw(achievementBadge, -BADGE_WIDTH / 2f, -BADGE_HEIGHT / 2f, BADGE_WIDTH, BADGE_HEIGHT);
      mySpriteBatch.end();
      fb.end();

      achievementsToRenderWithAssociatedDisposable.add(
          new GameEffectAndDisposable(
              getAchievementPopupElement(getAtlasRegionFromFrameBuffer(fb), BADGE_LEFT_OFFSET, BADGE_TOP_OFFSET),
              fb
          )
      );
    }
    else {
      LOGGER.error("Achievement icon not found of achievement {}", achievement.key);
    }
  }

  private static void queueBadgeBorderForRendering(AchievementItem achievement) {
    CustomAchievementBorderTextureLoader.loadDefaultBorderTextures();
    TextureRegion achievementBadgeBorder = CustomAchievementBorderTextureLoader.chooseTextureRegion(achievement);
    if (achievementBadgeBorder != null) {

      FrameBuffer fb = createFrameBuffer(BADGE_WIDTH, BADGE_HEIGHT);
      beginSpriteBatch(mySpriteBatch, BADGE_WIDTH, BADGE_HEIGHT);
      mySpriteBatch.draw(achievementBadgeBorder, -BADGE_WIDTH / 2f, -BADGE_HEIGHT / 2f, BADGE_WIDTH, BADGE_HEIGHT);
      mySpriteBatch.end();
      fb.end();

      achievementsToRenderWithAssociatedDisposable.add(
          new GameEffectAndDisposable(
              getAchievementPopupElement(getAtlasRegionFromFrameBuffer(fb), BADGE_LEFT_OFFSET, BADGE_TOP_OFFSET),
              fb
          )
      );
    }
    else {
      LOGGER.error("Achievement icon not found of achievement {}", achievement.key);
    }
  }

  private static void queueTextForRendering(AchievementItem achievement) {
    UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("AchievementUnlocked");
    String title = ReflectionHacks.getPrivate(achievement, AchievementItem.class, "title");
    String text = uiStrings.TEXT[0] + " NL NL " + title;

    FrameBuffer fb = createFrameBuffer((int) TEXT_WIDTH, TEXT_HEIGHT);
    beginSpriteBatch(mySpriteBatch, (int) TEXT_WIDTH, TEXT_HEIGHT);
    // TODO TK: For some weird reason, the y is TEXT_HEIGHT more than I would expect to have to use. I would expect to
    //  use -TEXT_HEIGHT/2, like the badge above, but then the text disappears.
    FontHelper.renderSmartText(mySpriteBatch, font, text, -TEXT_WIDTH / 2, TEXT_HEIGHT / 2f, TEXT_WIDTH,
        font.getLineHeight() * 1.2f, new Color(0xCCCCCCFF));
    mySpriteBatch.end();
    fb.end();

    achievementsToRenderWithAssociatedDisposable.add(
        new GameEffectAndDisposable(
            getAchievementPopupElement(getAtlasRegionFromFrameBuffer(fb), TEXT_LEFT_OFFSET, TEXT_TOP_OFFSET),
            fb
        )
    );
  }

  private static FrameBuffer createFrameBuffer(int width, int height) {
    FrameBuffer frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
    frameBuffer.begin();
    Gdx.gl.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    Gdx.gl.glColorMask(true, true, true, true);
    return frameBuffer;
  }

  private static void beginSpriteBatch(SpriteBatch sb, int viewportWidth, int viewportHeight) {
    OrthographicCamera og = new OrthographicCamera(viewportWidth, viewportHeight);
    sb.setProjectionMatrix(og.combined);
    sb.begin();
  }

  private static TextureAtlas.AtlasRegion getAtlasRegionFromFrameBuffer(FrameBuffer fb) {
    TextureAtlas.AtlasRegion atlasRegion = new TextureAtlas.AtlasRegion(fb.getColorBufferTexture(), 0, 0, fb.getWidth(),
        fb.getHeight());
    atlasRegion.flip(false, true);
    return atlasRegion;
  }

  private static AbstractGameEffect getAchievementPopupElement(TextureAtlas.AtlasRegion atlasRegion,
      float popupLeftOffset,
      float popupTopOffset) {
    float elementW = atlasRegion.getTexture().getWidth();
    float elementH = atlasRegion.getTexture().getHeight();
    return new VfxBuilder(atlasRegion, Settings.WIDTH - POPUP_WIDTH + popupLeftOffset + elementW / 2,
        0 - popupTopOffset - elementH / 2, MOVE_DURATION)
        .setScale(1 / Settings.scale)
        .moveY(0 - popupTopOffset - elementH / 2, 0 + POPUP_HEIGHT - popupTopOffset - elementH / 2)
        .andThen(SHOW_DURATION)
        .andThen(MOVE_DURATION)
        .moveY(0 + POPUP_HEIGHT - popupTopOffset - elementH / 2, 0 - popupTopOffset - elementH / 2)
        .build();
  }
}
