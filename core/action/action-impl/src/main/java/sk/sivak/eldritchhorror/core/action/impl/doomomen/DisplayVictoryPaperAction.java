package sk.sivak.eldritchhorror.core.action.impl.doomomen;

import java8.features.stream.Stream;
import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.MysteryCardId;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneId;
import sk.sivak.eldritchhorror.core.constants.firebase.HallOfFameData;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorInfo;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Locale;
import java.util.List;
import java.util.Properties;

public class DisplayVictoryPaperAction implements Action<Object, Void> {

    @Override
    public Single<Void> execute() {
        return Single.create(onSub -> {
            MysteryCardId mysteryCardId = ServicePlatform.get().getMysteryDeck().getCurrentMysteryCard().getMysteryCardId();

            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().showTypewriterPaper(true);
            ServicePlatform.get().getEncounterService().typeHeader(toString(mysteryCardId) + " ending");
            ServicePlatform.get().getEncounterService().typeFlavor(loadFlavor(mysteryCardId));
            TypewriterUtils.confirmInfos("[#GOOD]Congratulations!\nYou banished the Ancient One.[]").subscribe(()-> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();

                ServicePlatform.get().getEncounterService().showTypewriterPaper(true);
                ServicePlatform.get().getEncounterService().typeHeader(" \nThe Hall of Fame welcomes you!");
                ServicePlatform.get().getEncounterService().readInput("Your name:").subscribe(name -> {
                    ServicePlatform.get().getService().hold();

                    HallOfFameData hallOfFameData = new HallOfFameData();
                    hallOfFameData.setTimestamp(69);
                    hallOfFameData.setDifficulty(ServicePlatform.get().getModel().getDifficulty());
                    hallOfFameData.setDoom(ServicePlatform.get().getDoomTrack().getCurrentDoom());
                    hallOfFameData.setName(name);
                    hallOfFameData.setRounds(ServicePlatform.get().getModel().getCurrentRound());
                    hallOfFameData.setInvestigatorsCount(ServicePlatform.get().getModel().getReferenceCard().getPlayers());
                    List<String> investigators = new LinkedList<>();
                    List<InvestigatorInfo> availableInvestigators = ServicePlatform.get().getInvestigators().getAvailableInvestigators();
                    for (InvestigatorId investigatorId : InvestigatorId.values()) {
                        if (!Stream.anyMatch(availableInvestigators, ai -> ai.getInvestigatorId() == investigatorId)) {
                            investigators.add(investigatorId.toString());
                        }
                    }
                    hallOfFameData.setInvestigators(investigators);
                    AncientOneId ancientOneId = ServicePlatform.get().getModel().getAncientOne().getAncientOneInfo().getAncientOneId();
                    hallOfFameData.setAncientOneId(ancientOneId);
                    hallOfFameData.setSolvedMysteries(ServicePlatform.get().getMysteryDeck().getSolvedMysteries(ancientOneId));

                    ServicePlatform.get().getTypewriterController().recordHallOfFame(hallOfFameData);
                    ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                    ServicePlatform.get().getService().release();
                });

                ServicePlatform.get().getService().release();
            });
            ServicePlatform.get().getService().release();
            onSub.onSuccess(null);
        });
    }

    @Override
    public void setInput(Object input) {

    }

    private static String loadFlavor(MysteryCardId mysteryCardId) {
        String fileName = "epilogue.properties";

        URL resource = getLocalizedResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("Resource was null");
        }
        Properties properties = new Properties();
        try (InputStreamReader reader = new InputStreamReader(resource.openStream(), StandardCharsets.UTF_8)) {
            properties.load(reader);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        return properties.getProperty(mysteryCardId.toString());
    }

    private static URL getLocalizedResource(String fileName) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String language = Locale.getDefault().getLanguage();
        if (language != null && !language.trim().isEmpty()) {
            int extension = fileName.lastIndexOf('.');
            String localizedName = extension >= 0
                    ? fileName.substring(0, extension) + "_" + language + fileName.substring(extension)
                    : fileName + "_" + language;
            URL localizedResource = classLoader.getResource(localizedName);
            if (localizedResource != null) {
                return localizedResource;
            }
        }
        return classLoader.getResource(fileName);
    }


    private static String toString(MysteryCardId mysteryCardId) {
        String[] words = mysteryCardId.toString().split("_");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            word = word.toLowerCase();
            result.append(word.substring(0, 1).toUpperCase()).append(word.substring(1).toLowerCase()).append(" ");
        }
        return new StringBuilder(result.substring(0, result.length() - 1)).toString();
    }
}
