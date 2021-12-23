package com.nxtdelivery.tabulous.command;

import gg.essential.api.EssentialAPI;
import gg.essential.api.commands.Command;
import gg.essential.api.commands.DefaultHandler;
import com.nxtdelivery.tabulous.Tabulous;

public class TabulousCommand extends Command {
    public TabulousCommand() {
        super(Tabulous.ID, true);
    }

    @DefaultHandler
    public void handle() {
        EssentialAPI.getGuiUtil().openScreen(Tabulous.config.gui());
    }
}