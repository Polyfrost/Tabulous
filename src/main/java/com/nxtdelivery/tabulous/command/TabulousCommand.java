package com.nxtdelivery.tabulous.command;

import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;
import com.nxtdelivery.tabulous.Tabulous;

@Command(value = "tabulous", aliases = {"tab"})
public class TabulousCommand {

    @Main
    public void handle() {
        Tabulous.config.openGui();
    }
}
