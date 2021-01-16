package basemod.abstracts;

import basemod.ReflectionHacks;
import basemod.helpers.AchievementJSON;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.ShaderHelper;
import com.megacrit.cardcrawl.screens.stats.AchievementItem;

public class CustomAchievement extends AchievementItem {
  private static final Color LOCKED_COLOR = new Color(1.0F, 1.0F, 1.0F, 0.8F);

  private String customBorderPath;
  private String customLockedBorderPath;

  public CustomAchievement(String key, String title, String desc, String imgUrl, boolean hidden) {
    super(title, desc, "", key, hidden);

    Texture tempImg;
    if (imgUrl == null) {
      imgUrl = "images/achievements/unlocked/0.jpg";
    }
    tempImg = ImageMaster.loadImage(imgUrl);
    if (tempImg != null) {
      ReflectionHacks.setPrivate(this, AchievementItem.class, "img", createAtlasRegion(tempImg));
    }
  }

  private TextureAtlas.AtlasRegion createAtlasRegion(Texture tempImg) {
    return new TextureAtlas.AtlasRegion(tempImg, 0, 0, tempImg.getWidth(), tempImg.getHeight());
  }

  public CustomAchievement(String key, String title, String desc, String imgUrl) {
    this(key, title, desc, imgUrl, false);
  }

  public CustomAchievement(String key, AchievementJSON json, boolean hidden) {
    this(key, json.NAME, json.TEXT, json.IMG, hidden);
  }

  public CustomAchievement(String key, AchievementJSON json) {
    this(key, json, false);
  }

  @Override
  public void render(SpriteBatch sb, float x, float y) {
    if (!this.isUnlocked) {
      sb.setColor(LOCKED_COLOR);
      ShaderHelper.setShader(sb, ShaderHelper.Shader.GRAYSCALE);
    }
    else {
      sb.setColor(Color.WHITE);
    }

    TextureAtlas.AtlasRegion img = ReflectionHacks.getPrivate(this, AchievementItem.class, "img");
    if (this.hb.hovered) {
      sb.draw(img, x - img.packedWidth / 2.0F, y - img.packedHeight / 2.0F, img.packedWidth / 2.0F,
          img.packedHeight / 2.0F, img.packedWidth, img.packedHeight, Settings.scale * 1.1F, Settings.scale * 1.1F,
          0.0F);
    }
    else {
      sb.draw(img, x - img.packedWidth / 2.0F, y - img.packedHeight / 2.0F, img.packedWidth / 2.0F,
          img.packedHeight / 2.0F, img.packedWidth, img.packedHeight, Settings.scale, Settings.scale, 0.0F);
    }

    ShaderHelper.setShader(sb, ShaderHelper.Shader.DEFAULT);

    this.hb.move(x, y);
    this.hb.render(sb);
  }

  public String getCustomBorderPath() {
    return customBorderPath;
  }

  public void setCustomBorderPath(String customBorderPath) {
    this.customBorderPath = customBorderPath;
  }

  public String getCustomLockedBorderPath() {
    return customLockedBorderPath;
  }

  public void setCustomLockedBorderPath(String customLockedBorderPath) {
    this.customLockedBorderPath = customLockedBorderPath;
  }
}
