package basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.util.ArrayList;
public class CardModifierPatches
{
    @SpirePatch(
            clz = AbstractCard.class,
            method = "calculateCardDamage"
    )
    public static class CardModifierCalculateCardDamage
    {
        //modifyDamage
        @SpireInsertPatch(
                locator = DamageLocator.class,
                localvars = {"tmp"}
        )
        public static void damageInsert(AbstractCard __instance, AbstractMonster m, @ByRef float[] tmp) {
            tmp[0] = CardModifierManager.onModifyDamage(tmp[0], __instance, m);
        }

        private static class DamageLocator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "powers");
                int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
                return new int[] {tmp[0]};
            }
        }

        @SpireInsertPatch(
                locator = MultiDamageLocator.class,
                localvars = {"tmp", "i"}
        )
        public static void multiDamageInsert(AbstractCard __instance, AbstractMonster m, float[] tmp, int i) {
            tmp[i] = CardModifierManager.onModifyDamage(tmp[i], __instance, m);
        }

        private static class MultiDamageLocator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "powers");
                int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
                return new int[] {tmp[2]};
            }
        }

        //modifyDamageFinal
        @SpireInsertPatch(
                locator = DamageFinalLocator.class,
                localvars = {"tmp"}
        )
        public static void damageFinalInsert(AbstractCard __instance, AbstractMonster m, @ByRef float[] tmp) {
            tmp[0] = CardModifierManager.onModifyDamageFinal(tmp[0], __instance, m);
        }

        private static class DamageFinalLocator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "powers");
                int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
                return new int[] {tmp[1]};
            }
        }

        @SpireInsertPatch(
                locator = MultiDamageFinalLocator.class,
                localvars = {"tmp", "i"}
        )
        public static void multiDamageFinalInsert(AbstractCard __instance, AbstractMonster m, float[] tmp, int i) {
            tmp[i] = CardModifierManager.onModifyDamageFinal(tmp[i], __instance, m);
        }

        private static class MultiDamageFinalLocator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "powers");
                int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
                return new int[] {tmp[3]};
            }
        }
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = "applyPowersToBlock"
    )
    public static class CardModifierApplyPowersToBlock
    {
        //modifyBlock
        @SpireInsertPatch(
                locator = BlockLocator.class,
                localvars = {"tmp"}
        )
        public static void blockInsert(AbstractCard __instance, @ByRef float[] tmp) {
            tmp[0] = CardModifierManager.onModifyBlock(tmp[0], __instance);
        }

        private static class BlockLocator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "powers");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }

        //modifyBlockFinal
        @SpireInsertPatch(
                locator = BlockFinalLocator.class,
                localvars = {"tmp"}
        )
        public static void blockFinalInsert(AbstractCard __instance, @ByRef float[] tmp) {
            tmp[0] = CardModifierManager.onModifyBlockFinal(tmp[0], __instance);
        }

        private static class BlockFinalLocator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(MathUtils.class, "floor");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = "applyPowers"
    )
    public static class CardModifierOnApplyPowers
    {
        //onApplyPowers
        public static void Postfix(AbstractCard __instance) {
            CardModifierManager.onApplyPowers(__instance);
        }

        //modifyDamage
        @SpireInsertPatch(
                locator = DamageLocator.class,
                localvars = {"tmp"}
        )
        public static void damageInsert(AbstractCard __instance, @ByRef float[] tmp) {
            tmp[0] = CardModifierManager.onModifyDamage(tmp[0], __instance, null);
        }

        private static class DamageLocator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "powers");
                int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
                return new int[] {tmp[0]};
            }
        }

        @SpireInsertPatch(
                locator = MultiDamageLocator.class,
                localvars = {"tmp", "i"}
        )
        public static void multiDamageInsert(AbstractCard __instance, float[] tmp, int i) {
            tmp[i] = CardModifierManager.onModifyDamage(tmp[i], __instance, null);
        }

        private static class MultiDamageLocator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "powers");
                int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
                return new int[] {tmp[2]};
            }
        }

        //modifyDamageFinal
        @SpireInsertPatch(
                locator = DamageFinalLocator.class,
                localvars = {"tmp"}
        )
        public static void damageFinalInsert(AbstractCard __instance, @ByRef float[] tmp) {
            tmp[0] = CardModifierManager.onModifyDamageFinal(tmp[0], __instance, null);
        }

        private static class DamageFinalLocator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "powers");
                int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
                return new int[] {tmp[1]};
            }
        }

        @SpireInsertPatch(
                locator = MultiDamageFinalLocator.class,
                localvars = {"tmp", "i"}
        )
        public static void multiDamageFinalInsert(AbstractCard __instance, float[] tmp, int i) {
            tmp[i] = CardModifierManager.onModifyDamageFinal(tmp[i], __instance, null);
        }

        private static class MultiDamageFinalLocator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "powers");
                int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
                return new int[] {tmp[3]};
            }
        }
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = "initializeDescription"
    )
    public static class CardModifierOnCreateDescription
    {
        private static String storedRawDescription;

        public static void Prefix(AbstractCard __instance) {
            storedRawDescription = __instance.rawDescription;
            CardModifierManager.onCreateDescription(__instance);
        }

        public static void Postfix(AbstractCard __instance) {
            __instance.rawDescription = storedRawDescription;
        }
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = "makeStatEquivalentCopy"
    )
    public static class CardModifierStatEquivalentCopyModifiers
    {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"card"}
        )
        public static void Insert(AbstractCard __instance, AbstractCard card) {
            CardModifierManager.copyModifiers(__instance, card);
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractCard.class, "timesUpgraded");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = "update"
    )
    public static class CardModifierUpdate
    {
        public static void Postfix(AbstractCard __instance) {
            CardModifierManager.onUpdate(__instance);
        }
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = "render",
            paramtypez = {SpriteBatch.class}
    )
    public static class CardModifierRender
    {
        public static void Postfix(AbstractCard __instance, SpriteBatch sb) {
            CardModifierManager.onRender(__instance, sb);
        }
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = "getCost"
    )
    public static class getCardModifierCostString
    {
        public static String Postfix(String __result, AbstractCard __instance) {
            __result = CardModifierManager.getCostString(__instance, __result);
            return __result;
        }
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = "hasEnoughEnergy"
    )
    public static class cardModifierHasEnoughAlternateResource
    {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static SpireReturn<Boolean> Insert(AbstractCard __instance) {
            if (CardModifierManager.hasEnoughAlternateCost(__instance)) {
                return SpireReturn.Return(true);
            } else {
                return SpireReturn.Continue();
            }
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractCard.class, "costForTurn");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = "resetAttributes"
    )
    public static class CardModifierRemoveEndOfTurnModifiers
    {
        public static void Prefix(AbstractCard __instance) {
            CardModifierManager.removeEndOfTurnModifiers(__instance);
        }
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = SpirePatch.CLASS
    )
    public static class CardModifierFields
    {
        public static SpireField<ArrayList<AbstractCardModifier>> cardModifiers = new SpireField<>(ArrayList::new);
    }

    @SpirePatch(
            clz = CardGroup.class,
            method = "moveToDiscardPile"
    )
    public static class CardModifierWhenDiscarded
    {
        public static void Prefix(CardGroup __instance, AbstractCard c) {
            if (__instance == AbstractDungeon.player.hand && !AbstractDungeon.actionManager.turnHasEnded && __instance.contains(c)) {
                System.out.println("was " + c + " manually discarded?");
                CardModifierManager.onCardDiscarded(c);
            }
        }
    }

    @SpirePatch(
            clz = CardGroup.class,
            method = "moveToExhaustPile"
    )
    public static class CardModifierWhenExhausted
    {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void Insert(CardGroup __instance, AbstractCard c) {
            CardModifierManager.onCardExhausted(c);
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractCard.class, "triggerOnExhaust");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "draw",
            paramtypez = {int.class}
    )
    public static class CardModifierWhenDrawn
    {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"c"}
        )
        public static void Insert(AbstractPlayer __instance, int numCards, AbstractCard c) {
            CardModifierManager.onCardDrawn(c);
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractCard.class, "triggerWhenDrawn");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "useCard"
    )
    public static class CardModifierSpendResources
    {
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getClassName().equals(EnergyManager.class.getName()) && m.getMethodName().equals("use")) {
                        String manager = CardModifierManager.class.getName();
                        String energy = EnergyPanel.class.getName();
                        m.replace(
                                "if (" + manager + ".getPreEnergyResourceAmount(c) >= c.costForTurn) {" +
                                            manager + ".spendPreEnergyResource(c);" +
                                        "} else if (" + energy + ".totalCount >= c.costForTurn) {" +
                                            "$proceed($$);" +
                                        "} else if (" + manager + ".getPreEnergyResourceAmount(c) >= c.costForTurn) {" +
                                            manager + ".spendPostEnergyResource(c);" +
                                        "} else {" +
                                            "int tmp = c.costForTurn;" +
                                            "tmp = " + manager + ".spendPreEnergySplittableResource(c);" +
                                            "if (tmp > 0) {" +
                                                "if (tmp > " + energy + ".totalCount) {" +
                                                    "tmp -= " + energy + ".totalCount;" +
                                                    "this.energy.use(" + energy + ".totalCount);" +
                                                    manager + ".spendPostEnergySplittableResource(c, tmp);" +
                                                "} else {" +
                                                    "this.energy.use(tmp);" +
                                                "}" +
                                            "}" +
                                        "}"
                        );
                    }
                }
            };
        }
    }

    @SpirePatch(
            clz = UseCardAction.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {AbstractCard.class, AbstractCreature.class}
    )
    public static class CardModifierOnUseCard
    {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void Insert(UseCardAction __instance, AbstractCard card, AbstractCreature target) {
            CardModifierManager.onUseCard(card, target);
            CardModifierManager.removeWhenPlayedModifiers(card);
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "hand");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}
