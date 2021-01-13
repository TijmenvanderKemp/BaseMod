package basemod.helpers;

import basemod.BaseMod;
import com.badlogic.gdx.Gdx;

import java.nio.charset.StandardCharsets;

public class AchievementJSON {
  public String NAME;
  public String TEXT;
  public String IMG;
  public String IMG_LOCKED;

  public static AchievementJSON fromJson(String path) {
    return BaseMod.gson.fromJson(
        Gdx.files.internal(path).readString(String.valueOf(StandardCharsets.UTF_8)),
        AchievementJSON.class
    );
  }
}
