package ch.supsi.fscli.backend.core.command;

import ch.supsi.fscli.backend.core.CommandResult;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.provider.parser.CommandSyntax;
import ch.supsi.fscli.backend.i18n.BackendMessageProvider;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class FortuneCommand extends AbstractCommand {
    
    private static final List<String> FORTUNES = Arrays.asList(
        "Questo va risolto alla 'Poltrone e Sofà'",
        "Sapete chi è Mozumo, il più grande cameraman giapponese!",
        "A void pointer is not polymorphic!",
        "SUPERCOW è una supermucca!",
        "Le vie del software sono infinite",
        "C'è tanta karnaugh al fuoco",
        "Avete un architettura UMA NUMA (YE)",
        "Sai che rumore fa quando ti picchio con la RAM? Stack",
        "Sai che rumore fa quando butto una scheda grafica in acqua? GLUT GLUT GLUT",
        "It's not a bug – it's an undocumented feature.",
        "Dai abbiamo quasi FAT...",
        "Facciamo un fork, CodeBroc",
        "Due donne incinte non fanno un figlio in 4 mesi e mezzo",
        "È una vittoria da conigli? Si, ma è meglio di una sconfitta da leoni!",
        "You are not as smart as Linus Torvalds.",
        "NON lavorate mai con i Designer",
        "I datascientist non producono software, martellano soluzioni"
    );
    
    private final Random random;

    public FortuneCommand() {
        super("fortune",
                BackendMessageProvider.get("fortune.description"),
                BackendMessageProvider.get("fortune.usage"));
        this.random = new Random();
    }

    @Override
    public CommandResult execute(FileSystem fs, CommandSyntax syntax) {
        String fortune = FORTUNES.get(random.nextInt(FORTUNES.size()));
        return CommandResult.success(fortune);
    }
    
    @Override
    public boolean shouldExpandWildcards() {
        return false;
    }
}
