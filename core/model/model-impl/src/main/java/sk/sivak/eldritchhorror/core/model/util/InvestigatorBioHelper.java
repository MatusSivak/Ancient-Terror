package sk.sivak.eldritchhorror.core.model.util;

import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;

public class InvestigatorBioHelper
{
    private InvestigatorBioHelper() {
    }

    public static String getBio(InvestigatorId investigatorId) {
        switch (investigatorId) {
            case THE_BOUNTY_HUNTER:
                return createForBountyHunter();
            case THE_SPY:
                return createForSpy();
            case THE_SAILOR:
                return createForSailor();
            case THE_ASTRONOMER:
                return createForAstronomer();
            case THE_ENTERTAINER:
                return createForEntertainer();
            case THE_SOLDIER:
                return createForSoldier();
            case THE_REDEEMED_CULTIST:
                return createForRedeemedCultist();
            case THE_EX_CONVICT:
                return createForExConvict();
            case THE_EXPEDITION_LEADER:
                return createForExpeditionLeader();
            case THE_MUSICIAN:
                return createForMusician();
            case THE_POLITICIAN:
                return createForPolitician();
            case THE_MARTIAL_ARTIST:
                return createForMartialArtist();
            case THE_SHAMAN:
                return createForShaman();
            case THE_PSYCHIC:
                return createForPsychic();
            case THE_ROOKIE_COP:
                return createForRookieCop();
            case THE_ACTRESS:
                return createForActress();
            case THE_BOOTLEGGER:
                return createForBootlegger();
            case THE_HANDYMAN:
                return createForHandyman();
            case THE_VIOLINIST:
                return createForViolinist();
            case THE_WAITRESS:
                return createForWaitress();
        }
        throw new IllegalArgumentException();
    }

    private static String createForWaitress() {
        return "Once, Agnes lived quietly,\n" +
                "taking orders and serving up food at a diner in Arkham,\n" +
                "but one night, she fell from a ladder, striking her head.\n" +
                "After that, she could recall a past life\n" +
                "as a sorceress in the ancient world of Hyperborea.\n" +
                "Her mind flooded with memories of arcane lore.\n" +
                "Amazed by this new magical knowledge, Agnes has traveled to London,\n" +
                "to learn more about Hyperborea,\n" +
                "but is she driven by her own curiosity\n" +
                "or compelled by the will of her former self?";
    }

    private static String createForViolinist() {
        return "Hailed as a musical prodigy from her youth,\n" +
                "Patrice has performed for royalty and society's brightest minds\n" +
                "all around the world. For years she thought\n" +
                "that her consciousness simply drifted as she played,\n" +
                "but she's come to believe that an intelligence exists behind her visions.\n" +
                "Somehow the notes form a bridge between her own mind and another.\n" +
                "The more she grasps what her music exposes her to,\n" +
                "the more afraid she becomes. After last night's concert in Sydney,\n" +
                "she's finally decided to take action.";
    }

    private static String createForHandyman() {
        return "Wilson's a practical man. He goes where there's work.\n" +
                "When it's gone, he moves on.\n" +
                "Wilson finds that offering an honest deal gets him treated honestly,\n" +
                "but since he took a job rebuilding a church in Arkham,\n" +
                "he's had a hard time being honest with himself.\n" +
                "Things that used to hide in the deepest pits of the earth\n" +
                "have crawled to the surface. He cannot unsee all the things he's seen,\n" +
                "but he can get to work on fixing the problem.";
    }

    private static String createForBootlegger() {
        return "Finn was making a good living,\n" +
                "running liquor from Canada to cities all along the East Coast.\n" +
                "He never got caught and never lost a delivery.\n" +
                "Now he has taken a job in Chicago\n" +
                "and gave his word that he could get it done.\n" +
                "This time it won't be alcohol he's delivering,\n" +
                "and it won't be the local sheriff trying to catch him.\n" +
                "Finn's caught up in some supernatural conspiracy,\n" +
                "but in the end, he adheres to the same principles.\n" +
                "Deliver the goods. Don't get caught.";
    }

    private static String createForBountyHunter() {
        return "Tony's tracked down low-life scum in every lousy corner of the world,\n" +
                "but nothing was quite as bad as Innsmouth.\n" +
                "Some creep in Boston skipped bail and\n" +
                "tried to hide with family in the small fishing village.\n" +
                "It turned out \"family\" meant vicious fish-like monstrosities\n" +
                "that left Tony bloody and half-drowned.\n" +
                "Ever since, he's found a new kind of dirtbag to hunt.\n" +
                "Ordinary mobster or otherworldly monster,\n" +
                "Tony will take it down and find someone willing to pay him for it.\n" +
                "Now he's got a lead in Bogota. Easy money.";
    }

    private static String createForSpy() {
        return "Everyone expected great things from Trish when she was young.\n" +
                "In school, she excelled in athletics and the sciences,\n" +
                "but she surprised everyone after graduation\n" +
                "by settling into a humble position at a commercial code company.\n" +
                "What almost no one knows is that this particular company\n" +
                "is a front for the Bureau's code-breaking agency, the Black Chamber.\n" +
                "Now she finds herself in the city of Krasnoyarsk meeting another agent\n" +
                "who has important information about an impending threat\n" +
                "from a world beyond our own.";
    }

    private static String createForSailor() {
        return "Even as a child in Innsmouth, Silas had a special connection to the sea.\n" +
                "He's an able and well-reasoned man on land,\n" +
                "but on the ocean he possesses a singular strength and wit.\n" +
                "It's earned him a sterling reputation in every port across the globe,\n" +
                "particularly in Sydney, where Silas set ashore last night.\n" +
                "But this morning, the smell of the briny air carries dread as well as joy.\n" +
                "There is something in his past, something in Innsmouth,\n" +
                "that he knows will some day catch up to him.";
    }

    private static String createForAstronomer() {
        return "The scientific community ridiculed Norman for his claim\n" +
                "that six stars disappeared from the sky.\n" +
                "After exhausting every plausible astronomical explanation for answers,\n" +
                "he took a position at Miskatonic University\n" +
                "and began exploring more improbable possibilities\n" +
                "in the restricted section of their library.\n" +
                "While reading an ancient text of dark prophecies,\n" +
                "Norman found an exact description of the phenomenon he'd observed.\n" +
                "If the tome is to be believed,\n" +
                "a terrible incursion into our world is imminent.";
    }

    private static String createForEntertainer() {
        return "Marie has come a long way from the Louisiana marshes of her youth.\n" +
                "These days, \"The Smoky Velvet\" sings the blues\n" +
                "in elegant nightclubs around the world,\n" +
                "but it was grand-mere's dying wish that sent Marie to the Kingdom of Sarawak,\n" +
                "where an old evil had returned.\n" +
                "Marie doesn't believe she can help,\n" +
                "but only a fool didn't listen to grand-mere.\n" +
                "Now memories of strange nursery rhymes\n" +
                "she used to recite have begun flooding back.\n" +
                "People called grand-mere a witch.\n" +
                "Maybe Marie's got some witchcraft in her blood, too.";
    }

    private static String createForSoldier() {
        return "During the war, Mark witnessed horrors he could not explain,\n" +
                "and he wrote of what he saw in letters to his beloved wife, Sophie.\n" +
                "When Mark returned home, he discovered that Sophie was no longer human.\n" +
                "One of the beasts that Mark had seen overseas\n" +
                "had taken over her body, killing her in the process.\n" +
                "Afterward, Mark's thirst for vengeance lead him to Helsinki,\n" +
                "where some of these creatures had posed as German soldiers during the Great War.";
    }

    private static String createForRedeemedCultist() {
        return "When Diana was initiated into the Order of the Silver Twilight,\n" +
                "she believed it to be nothing more than a community organization.\n" +
                "But as she has learned more of it's true nature,\n" +
                "she has become convinced that a growing evil threatens the world,\n" +
                "and that the Silver Twilight will play a role in that threat.\n" +
                "She believes her best chance to prevent this\n" +
                "is to use her position to sabotage the organization from within.\n" +
                "Carl Sanford, the head of the Order, has recognized her skills\n" +
                "and recently sent her to Panama for additional training.";
    }

    private static String createForExConvict() {
        return "Skids got put away on two counts of bank robbery,\n" +
                "but he never hurt anybody.\n" +
                "His cellmate, Brad Hollins, killed nine people.\n" +
                "Hollins said that an alien creature made him do it.\n" +
                "Over the years, Hollins said a lot of strange things about the \"Ancient Ones.\"\n" +
                "He said that he needed to get to Argentina to stop them.\n" +
                "Skids didn't give it much thought\n" +
                "until the night Hollins burst into flames.\n" +
                "When Skids got paroled,\n" +
                "he couldn't shake the thought of these \"Ancient Ones.\"";
    }

    private static String createForExpeditionLeader() {
        return "Leo Anderson has spent his whole life\n" +
                "getting into the deadliest and most obscure corners of the globe.\n" +
                "Along the way, he's lost good people.\n" +
                "Fever takes some. Others are claimed by wild beasts.\n" +
                "After a recent, disastrous venture in the Yucatan,\n" +
                "Leo barely made it back to Buenos Aires alive.\n" +
                "He's sick of burying the people who trusted him.\n" +
                "But the job's not done yet.\n" +
                "The world is in danger, and crying in his drink won't fix that.\n" +
                "He's picked up a little hired help here,\n" +
                "and in the morning, he'll head back out into the wild.";
    }

    private static String createForMusician() {
        return "Old Jim Culver's music gives sweet comfort to the soul,\n" +
                "and it doesn't matter if that soul belongs to the living or the dead.\n" +
                "Folks in their graves, they love their little chats with Jim.\n" +
                "It used to bother him, but now he's happy for the company.\n" +
                "Lately, some departed souls in San Antonio\n" +
                "have been all riled up about something. " +
                "They're downright terrified.\n" +
                "And anything that scares the dead deserves old Jim's undivided attention.";
    }

    private static String createForPolitician() {
        return "When the press asks if Charlie is planning a run for national office,\n" +
                "he smiles and says that he's focused on the important issues.\n" +
                "The truth is that he would love to launch his campaign,\n" +
                "but right now the most important issue\n" +
                "is preventing the end of the world without causing a panic.\n" +
                "To do this, he's been calling in favors across the country.\n" +
                "Most recently, Charlie's stopped in San Francisco to visit Hearst Castle.\n" +
                "With the help of his friends and his finances,\n" +
                "Charlies believes he can fix this problem without sacrificing a single vote.";
    }

    private static String createForMartialArtist() {
        return "Lily speaks rarely and when she does,\n" +
                "her words are measured and wise.\n" +
                "After a lifetime of disciplined training,\n" +
                "every gesture is graceful, uncluttered by hesitation.\n" +
                "When she was an infant,\n" +
                "an obscure sect of monks believed that she was born for a special purpose,\n" +
                "to face a great evil.\n" +
                "Now, the monks believe that the great evil is at hand,\n" +
                "and they have brought Lily to Shanghai to begin fulfilling her destiny.";
    }

    private static String createForShaman() {
        return "As a young girl in Nigeria, Akachi stayed apart from other children,\n" +
                "preferring the company of imaginary friends.\n" +
                "Her elders feared madness, but the village dibia believed\n" +
                "that she had been chosen by the gods.\n" +
                "The wise old man taught her how to travel between worlds\n" +
                "and how to marshal spirits.\n" +
                "Now, she has become a wise leader herself,\n" +
                "traveling across Africa and teaching others to protect themselves.\n" +
                "She's recently arrived in Cape Town and from here,\n" +
                "she will track down the dark forces that threaten humanity.";
    }

    private static String createForPsychic() {
        return "At first, Jacqueline's dreams of fire and destruction seemed like a curse.\n" +
                "Monsters ran rampant through city streets\n" +
                "and some greater darkness loomed on the horizon.\n" +
                "However, she has recently learned to control her visions\n" +
                "and observe events in detail.\n" +
                "Yesterday, she traveled from Boston to Minneapolis\n" +
                "to explore an abandoned warehouse she'd seen in her dreams.\n" +
                "Inside, she found evidence of a terrible cult\n" +
                "that had practiced unspeakable rituals there.\n" +
                "Jacqueline hopes to use what she's learned\n" +
                "to prevent the terrible future that haunts her sleep.";
    }

    private static String createForRookieCop() {
        return "Most members of the Muldoon family\n" +
                "are part of the Boston police force in some way or another.\n" +
                "Not long ago, the youngest brother Tommy got his badge.\n" +
                "Armed with an eye for detail, fierce courage, and his trusty rifle Becky,\n" +
                "young Muldoon is dedicated to law enforcement.\n" +
                "Now he finds himself in Anchorage\n" +
                "at the request of his cousin, the local sheriff.\n" +
                "Something has been killing people, and off the record,\n" +
                "his cousin says it's a wendigo.\n" +
                "Tommy's not sure he believes any of that,\n" +
                "but catching killers is in his blood.";
    }

    private static String createForActress() {
        return "Around the world, Lola has performed dramatic roles for sold-out houses.\n" +
                "However, after being cast in the controversial play, The King in Yellow,\n" +
                "Lola needed to \"take some time\" to recover for her \"exhaustion.\"\n" +
                "Now that she has checked herself out of the asylum,\n" +
                "she's ready for her big comeback.\n" +
                "But this time she'll play a different role in the fight\n" +
                "against the horrors that threaten this world.\n" +
                "She's started by traveling to Tokyo\n" +
                "to track down the other surviving cast member\n" +
                "of her previous theatrical endeavor.";
    }


}
