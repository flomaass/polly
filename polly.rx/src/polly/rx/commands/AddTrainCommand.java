package polly.rx.commands;

import polly.rx.core.TrainBillV2;
import polly.rx.core.TrainManagerV2;
import polly.rx.MyPlugin;
import polly.rx.entities.TrainEntityV2;
import de.skuzzle.polly.sdk.Command;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.Parameter;
import de.skuzzle.polly.sdk.Signature;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.exceptions.DuplicatedSignatureException;
import de.skuzzle.polly.sdk.model.User;


public class AddTrainCommand extends Command {
    
    private TrainManagerV2 trainManager;

    public AddTrainCommand(MyPolly polly, TrainManagerV2 trainManager) 
            throws DuplicatedSignatureException {
        
        super(polly, "train");
        this.createSignature("F�gt ein neues Training zur Rechnung des Benutzers hinzu.", 
            MyPlugin.ADD_TRAIN_PERMISSION,
            new Parameter("Benutzer", Types.USER),
            new Parameter("Rechnung", Types.STRING));
        this.createSignature("Zeigt die offene Rechnungssumme f�r einen Benutzer an.",
            MyPlugin.ADD_TRAIN_PERMISSION,
            new Parameter("Benutzer", Types.USER),
            new Parameter("Details?", Types.BOOLEAN));
        this.createSignature("F�gt ein neues Training zur Rechnung des Benutzers hinzu.",
            MyPlugin.ADD_TRAIN_PERMISSION,
            new Parameter("Benutzer", Types.USER),
            new Parameter("Rechnung", Types.STRING),
            new Parameter("Faktor", Types.NUMBER));
        this.createSignature("Zeigt die offene Rechnungssumme f�r einen Benutzer an.",
            MyPlugin.ADD_TRAIN_PERMISSION,
            new Parameter("Benutzer", Types.USER));
        this.setHelpText("Befehl zum Verwalten von Capi Trainings.");
        this.setRegisteredOnly();
        this.trainManager = trainManager;
    }

    
    
    @Override
    protected boolean executeOnBoth(User executer, String channel,
        Signature signature) {

        double mod = 1.0;
        if (this.match(signature, 0)) {
            String userName = signature.getStringValue(0);
            String train = signature.getStringValue(1);
            
            this.addTrain(executer, channel, userName, train, mod);
        } else if (this.match(signature, 2)) {
            String userName = signature.getStringValue(0);
            String train = signature.getStringValue(1);
            mod = signature.getNumberValue(2);
            
            this.addTrain(executer, channel, userName, train, mod);
        } else if (this.match(signature, 1)) {
            String userName = signature.getStringValue(0);
            boolean showAll = signature.getBooleanValue(1);
            
            TrainBillV2 bill = this.trainManager.getBill(executer, userName);
            this.outputTrain(showAll, bill, channel);
        } else if (this.match(signature, 3)) {
            String userName = signature.getStringValue(0);
            
            TrainBillV2 bill = this.trainManager.getBill(executer, userName);

            this.outputTrain(false, bill, channel);
        }
        return false;
    }
    
    
    
    private void outputTrain(boolean detailed, TrainBillV2 bill, String channel) {
        if (detailed) {
            for (TrainEntityV2 train : bill.getTrains()) {
                this.reply(channel, train.format(this.getMyPolly().formatting()));
            }
            this.reply(channel, "=========================");
        }
        
        this.reply(channel, bill.toString());
    }
    
    
    
    private void addTrain(User trainer, String channel, String forUser, 
        String train, double mod) {
        
        try {
            TrainEntityV2 te = TrainEntityV2.parseString(trainer.getId(), forUser, mod, train);
            this.trainManager.addTrain(te);
            
            if (te.getDuration() != 0) {
                // HACK: this requires the Remind Plugin to be installed and running!
                String command = ":remind \"Training abgeschlossen\" " + 
                        (te.getDuration() / 1000) + "s";
                this.getMyPolly().commands().executeString(
                        command, 
                        trainer.getCurrentNickName(), 
                        true, trainer, this.getMyPolly().irc());
            }
            this.reply(channel, "Posten gespeichert.");
        } catch (Exception e) {
            e.printStackTrace();
            this.reply(channel, "Fehler beim Speichern.");
        }
    }
}