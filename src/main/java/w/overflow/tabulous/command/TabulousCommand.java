package w.overflow.tabulous.command;

import w.overflow.tabulous.Tabulous;
import gg.essential.api.EssentialAPI;
import gg.essential.api.commands.Command;
import gg.essential.api.commands.DefaultHandler;

public class TabulousCommand extends Command {
    public TabulousCommand() {
        super(Tabulous.ID, true);
    }

    @DefaultHandler
    public void handle() {
        EssentialAPI.getGuiUtil().openScreen(Tabulous.config.gui());
    }
}