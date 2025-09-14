package asia.virtualmc.vLib.integration.uni_dialog;

import asia.virtualmc.vLib.Main;
import asia.virtualmc.vLib.utilities.annotations.Internal;
import asia.virtualmc.vLib.utilities.messages.AdventureUtils;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import io.github.projectunified.unidialog.core.dialog.Dialog;
import io.github.projectunified.unidialog.core.opener.DialogOpener;
import io.github.projectunified.unidialog.paper.PaperDialogManager;
import org.bukkit.entity.Player;

public class UniDialogUtils {
    private static PaperDialogManager dialogManager;

    @Internal
    public static void load() {
        dialogManager = new PaperDialogManager(Main.getInstance());
        dialogManager.register();
        ConsoleUtils.info("Successfully hooked into: UniDialogs");
    }

//    public static int getInt(Player player, String title) {
//        DialogOpener dialogOpener = dialogManager.createConfirmationDialog()
//                .title(AdventureUtils.toComponent(title))
//                .canCloseWithEscape(true)
//                .afterAction(Dialog.AfterAction.CLOSE)
//                .body(builder -> builder.text().text("This is a sample dialog. Do you want to proceed?"))
//                .input("name", builder -> builder.textInput().label("Enter your name:"))
//                .yesAction(builder -> builder.label("Confirm"))
//                .noAction(builder -> builder.label("Cancel"))
//                .opener();
//
//        dialogOpener.open(player.getUniqueId());
//    }
}
