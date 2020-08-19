import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Timer;

import java.awt.Color;
import java.awt.Graphics;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import static org.dreambot.api.methods.Calculations.random;

@ScriptManifest(
        author = "Nico",
        description = "Makes potions",
        category = Category.HERBLORE,
        version = 1.0,
        name = "Potion Maker"
)
public class PotionMaker extends AbstractScript {

    public Timer timer = new Timer();
    private int beginningXp;

    // Super restore potion
    private final int SNAPDRAGON_POTION_UNF_ID = 3004;
    private final int RED_SPIDERS_EGGS_ID = 223;

    @Override
    public void onStart() {
        // Assure bank is set on quantity X (14)
        super.onStart();
        beginningXp = getSkills().getExperience(Skill.HERBLORE);
    }

    @Override
    public int onLoop() {
        getBank().open();
        sleepUntil(() -> getBank().isOpen(), random(2000, 2500));
        getBank().withdraw(SNAPDRAGON_POTION_UNF_ID, 14);
        sleepUntil(() -> getInventory().contains(SNAPDRAGON_POTION_UNF_ID), random(2000, 2500));
        getBank().withdraw(RED_SPIDERS_EGGS_ID, 14);
        sleepUntil(() -> getInventory().contains(RED_SPIDERS_EGGS_ID), random(2000, 2500));
        getKeyboard().closeInterfaceWithESC();
        sleepUntil(() -> !getBank().isOpen(), random(2000, 2500));
        getInventory().getItemInSlot(13).useOn(getInventory().getItemInSlot(17));
        sleepUntil(() -> getWidgets().getWidgetChild(270, 5) != null, random(2000, 2500));
        sleep(100, 150);
        getKeyboard().type(" ");
        sleep(100, 150);
        getMouse().moveMouseOutsideScreen();
        sleepUntil(() -> !getInventory().contains(RED_SPIDERS_EGGS_ID), random(19000, 20000));
        takeBreak();
        getBank().open();
        sleepUntil(() -> getBank().isOpen(), random(2000, 2500));
        getBank().depositAllItems();
        return random(750, 1000);
    }

    @Override
    public void onPaint(Graphics graphics) {
        super.onPaint(graphics);
        graphics.setColor(Color.BLACK);
        graphics.drawString("Time running: " + timer.formatTime(), 330, 363);
        graphics.drawString("Total XP gained: " + formatInt(calculateXP()), 330, 381);
        graphics.drawString("Hourly XP rate: " + formatInt(timer.getHourlyRate(calculateXP())), 330, 399);
        graphics.drawString("XP until level 83: " + formatInt(remainingXpUntilGoal()), 330, 417);
        graphics.drawString("Hours until level 83: " + decimalFormat(remainingXpUntilGoal() / (double)
                timer.getHourlyRate(calculateXP())), 330, 435);
    }

    private String formatInt(int num) {
        return NumberFormat.getNumberInstance(Locale.US).format(num);
    }

    private String decimalFormat(double num) {
        return new DecimalFormat("0.00").format(num);
    }

    private int calculateXP() {
        int currentXp = getSkills().getExperience(Skill.HERBLORE);
        return currentXp - beginningXp;
    }

    private int remainingXpUntilGoal() {
        final int LVL_83_XP = 2673114;
        return LVL_83_XP - getSkills().getExperience(Skill.HERBLORE);
    }

    private void takeBreak() {
        int random = random(82);
        if (random == 0) {
            int twoMinutes = 2 * 60 * 1000;
            int threeMinutes = 3 * 60 * 1000;
            logInfo("Taking a break.");
            sleep(twoMinutes, threeMinutes);
        }
    }
}
