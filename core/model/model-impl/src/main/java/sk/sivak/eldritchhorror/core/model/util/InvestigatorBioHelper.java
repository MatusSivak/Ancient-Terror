package sk.sivak.eldritchhorror.core.model.util;

import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;

public class InvestigatorBioHelper
{
    private InvestigatorBioHelper() {
    }

    public static String getBio(InvestigatorId investigatorId) {
        switch (investigatorId) {
            case THE_BUTLER:
                return "Carson kept the household running through bankruptcies, funerals, and a winter\n" +
                        "without coal. Nothing prepared him for the dinner at which every guest\n" +
                        "remembered a different host. By morning, the portraits had changed and the\n" +
                        "staff had forgotten who paid them. Carson alone had kept a written seating\n" +
                        "plan. He took the servants to safety, locked the dining room, and began\n" +
                        "visiting the addresses in his employer's appointment book.";
            case THE_PRIEST:
                return "Mateo spent years helping families separated by closed borders exchange\n" +
                        "letters. One parcel contained messages from a village demolished before any of\n" +
                        "the writers were born. Their pleas were mundane: medicine, blankets, a way out\n" +
                        "before the bells rang. The postmarks advanced a day each time he looked. He\n" +
                        "packed supplies and went to trace the delivery route, unwilling to dismiss\n" +
                        "suffering simply because its address was impossible.";
            case THE_NUN:
                return "Mary managed a small infirmary where the night staff learned to work quietly.\n" +
                        "Then patients began answering questions no one had asked, all in the same\n" +
                        "unfamiliar voice. She discovered that the voice fell silent whenever she read\n" +
                        "their names aloud. With the patients recovering, she copied the ward register\n" +
                        "and sought other hospitals with similar cases. Someone was turning people into\n" +
                        "messengers, and she intended to find the sender.";
            case THE_EXPLORER:
                return "Ursula earned her living surveying routes for communities left off official\n" +
                        "maps. On one commission, her survey stakes moved overnight into a perfect\n" +
                        "circle around an empty hillside. Beneath it she found a staircase worn smooth\n" +
                        "by feet far larger than human ones. She sealed the entrance, but the same\n" +
                        "circle appeared on her next survey. Now she follows the pattern, marking safe\n" +
                        "paths for anyone who might come after her.";
            case THE_REPORTER:
                return "Rex covered municipal repairs until a printer handed him tomorrow's edition.\n" +
                        "Its account of a bridge collapse named witnesses who had not yet arrived in\n" +
                        "town. He stopped the first disaster, only to find a fresh obituary in his own\n" +
                        "handwriting. His editor called it a publicity stunt. Rex keeps the changing\n" +
                        "proofs in a waterproof satchel and visits each address before the presses can\n" +
                        "make another prediction come true.";
            case THE_PROFESSOR:
                return "Harvey built his reputation by exposing forged antiquities. His most\n" +
                        "embarrassing case was a clay tablet whose supposedly invented script later\n" +
                        "appeared in a student's childhood diary. The student could translate it only\n" +
                        "while asleep, and each translation described a door somewhere in the city.\n" +
                        "Harvey withdrew his published dismissal. He now travels with the tablet's\n" +
                        "fragments, hoping careful scholarship can repair the consequences of a\n" +
                        "careless certainty.";
            case THE_DILETTANTE:
                return "Jenny inherited a controlling interest in a shipping company and expected dull\n" +
                        "meetings. Instead, she found passenger lists for voyages that never sailed,\n" +
                        "with fares paid decades in advance. She quietly bought the company's oldest\n" +
                        "vessel and hired a crew willing to ask questions. Her luggage holds copied\n" +
                        "ledgers, generous emergency funds, and photographs of the missing passengers.\n" +
                        "Whatever her fortune has helped conceal, she means to bring into daylight.";
            case THE_SALESMAN:
                return "Bob could sell an unfashionable hat, but he refused to sell an empty promise.\n" +
                        "That rule became troublesome when customers began returning purchases he had\n" +
                        "never stocked: a compass that pointed beneath the ground, a watch that counted\n" +
                        "backward. Each came with a receipt bearing his signature. He honored every\n" +
                        "refund and packed the objects into his sample case. Somewhere, someone was\n" +
                        "trading on his name. Bob wanted an explanation in person.";
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
        return "Agnes remembered regular customers by their orders. When an unfamiliar man\n" +
                "requested a meal she had served him forty years earlier, she assumed he was\n" +
                "confused. Then he returned with a photograph of the diner standing in an empty\n" +
                "field a century ago. Other customers began arriving with coins from unknown\n" +
                "countries and urgent requests for directions. Agnes keeps a notebook beneath\n" +
                "the counter, mapping where they come from and which roads might bring them\n" +
                "home.";
    }

    private static String createForViolinist() {
        return "Patrice taught violin in rented rooms while restoring damaged sheet music for\n" +
                "a publisher. One water-stained score changed its rests whenever she turned\n" +
                "away. Played exactly as written, it made every open window in the building\n" +
                "show the same deserted courtyard. She stopped before the final bar. Someone\n" +
                "has begun publishing fragments under her name, and Patrice must find the\n" +
                "remaining pages before an unsuspecting orchestra completes the piece.";
    }

    private static String createForHandyman() {
        return "Wilson repaired elevators, boilers, and anything a landlord had neglected. A\n" +
                "call to fix persistent knocking led him to pipes that continued through a\n" +
                "solid foundation without entering the cellar. Every blow from inside matched\n" +
                "the rhythm of a missing workman's hammer. Wilson shut off the building's\n" +
                "machinery and listened. He now carries plans of similar buildings in his tool\n" +
                "bag, tracing a buried structure that none of their architects admits\n" +
                "designing.";
    }

    private static String createForBootlegger() {
        return "Finn knew which roads were watched and which bridges could bear a loaded\n" +
                "truck. During a routine run, he reached a tollhouse that demanded an hour of\n" +
                "his life instead of money. He refused, reversed through the barrier, and found\n" +
                "his cargo aged to dust. The tollhouse has since appeared on other roads. Finn\n" +
                "shares detours with fellow drivers and searches for its keepers, who seem to\n" +
                "be collecting something more valuable than liquor.";
    }

    private static String createForBountyHunter() {
        return "Tony once accepted a fee to retrieve a surveyor who had abandoned a railway\n" +
                "camp. He found the man hiding in a dry well, guarding a bundle of maps that\n" +
                "showed tracks running through occupied houses. The company offered double for\n" +
                "the maps and nothing for the man. Tony broke the contract. Since then, he has\n" +
                "followed the company's recruiters from town to town, collecting testimony and\n" +
                "making certain their next workers survive.";
    }

    private static String createForSpy() {
        return "Trish inspected damaged telegrams for an international insurer. A recurring\n" +
                "error led her to freight shipments whose destinations had been erased from\n" +
                "every map she could find. When she reported it, her supervisor destroyed the\n" +
                "files and arranged her transfer to a nonexistent office. She kept carbon\n" +
                "copies. Traveling under borrowed credentials, she follows the shipments and\n" +
                "leaves coded warnings for the people who still believe the routes are\n" +
                "ordinary.";
    }

    private static String createForSailor() {
        return "Silas worked salvage boats until his crew raised a lifeboat with their own\n" +
                "names carved beneath its seats. Inside lay fresh provisions and a log\n" +
                "recording their deaths three weeks ahead. He burned the log, changed course,\n" +
                "and brought everyone home. Now matching lifeboats are washing ashore in\n" +
                "distant ports. Silas has traded salvage work for passage on endangered ships,\n" +
                "determined that no crew should be buried by a schedule.";
    }

    private static String createForAstronomer() {
        return "Norman maintained the precision clocks used to compare observations between\n" +
                "observatories. One night, every clock lost eleven seconds while his telescope\n" +
                "showed daylight over a sleeping city. No one else remembered the interruption.\n" +
                "He began placing sealed photographic plates beside the instruments; by\n" +
                "morning, they held pictures of unfamiliar coastlines. Norman is assembling\n" +
                "those images into an atlas, searching for the place that keeps borrowing time\n" +
                "from this world.";
    }

    private static String createForEntertainer() {
        return "Marie learned to hold a noisy room with a single unaccompanied note. During a\n" +
                "benefit concert, the audience continued singing after she stopped, repeating a\n" +
                "verse absent from her songbook. A recording revealed another voice beneath\n" +
                "them, patiently giving directions. She canceled the tour and visited the first\n" +
                "address. It was a condemned theater full of abandoned shoes. Marie now uses\n" +
                "her engagements to investigate who has been gathering those listeners.";
    }

    private static String createForSoldier() {
        return "Mark served on a military recovery detail, identifying equipment left after\n" +
                "the fighting. In a deserted field station, he found immaculate uniforms\n" +
                "bearing the names of men still alive at home. His superiors ordered the\n" +
                "discovery buried. He kept one name tag. Years later, its owner vanished and\n" +
                "the tag grew warm in his pocket. Mark has begun locating the others, building\n" +
                "a warning network before the remaining uniforms acquire their occupants.";
    }

    private static String createForRedeemedCultist() {
        return "Diana kept accounts for a burial society that promised no member would die\n" +
                "alone. She discovered that its payments purchased empty graves, each reserved\n" +
                "for a person not yet born. When she challenged the trustees, they invited her\n" +
                "to witness the first arrival. She fled with the membership ledger and the keys\n" +
                "to their storehouses. Now she helps former members disappear while searching\n" +
                "for a way to close the graves permanently.";
    }

    private static String createForExConvict() {
        return "Skids spent his sentence repairing the prison's worn locks. Behind a bricked\n" +
                "doorway, he found a newer lock opening onto a crowded street that disappeared\n" +
                "when the bell rang. He used it once to carry an injured prisoner outside, then\n" +
                "returned before the count. After release, he recognized the same street in a\n" +
                "newspaper photograph from another continent. He carries a handmade key and a\n" +
                "list of people the prison insists never existed.";
    }

    private static String createForExpeditionLeader() {
        return "Leo guided a geological team into a valley where every shouted word returned\n" +
                "in a different voice. He ordered the camp to communicate by written notes and\n" +
                "brought the party out intact. Their abandoned notebooks later arrived by post,\n" +
                "filled with requests to return for equipment no one remembered taking. Leo is\n" +
                "recruiting a small rescue party with strict rules: travel in pairs, keep\n" +
                "written records, and never answer a voice beyond the firelight.";
    }

    private static String createForMusician() {
        return "Jim repaired battered instruments between dance-hall engagements. A trumpet\n" +
                "recovered from a flooded cellar sounded one clear note even with its valves\n" +
                "removed. Whenever he played beside it, nearby listeners remembered a person\n" +
                "missing from their lives. He began collecting their names on the backs of\n" +
                "concert bills. The list has grown longer than his repertoire. Jim takes the\n" +
                "trumpet from venue to venue, helping strangers remember whom they need to\n" +
                "find.";
    }

    private static String createForPolitician() {
        return "Charlie won office by making neglected streets worth living on. While\n" +
                "reviewing demolition orders, he discovered an entire district receiving water,\n" +
                "electricity, and school funding despite having no residents on record. A visit\n" +
                "revealed meals cooling in empty kitchens. Someone had removed the people while\n" +
                "leaving the bureaucracy intact. Charlie has put his staff to work tracing the\n" +
                "missing households, using every permit, committee, and unpaid favor available\n" +
                "to keep the district from being erased.";
    }

    private static String createForMartialArtist() {
        return "Lily taught balance and self-defense to dockworkers after their shifts. When a\n" +
                "student returned from an accident without a shadow, she noticed that he copied\n" +
                "every movement a heartbeat late. She helped him recover by practicing familiar\n" +
                "forms until he could move without watching her. Other altered workers have\n" +
                "since arrived from distant ports. Lily follows their employers' routes, using\n" +
                "patient observation and disciplined movement to recognize what ordinary\n" +
                "examinations miss.";
    }

    private static String createForShaman() {
        return "Akachi was trusted to settle disputes between families before resentment\n" +
                "became violence. At a seasonal gathering, she heard a second argument beneath\n" +
                "the speakers' words, carried by voices no one recognized. Their quarrel\n" +
                "concerned a river that had vanished generations earlier. She persuaded the\n" +
                "families to restore an abandoned spring, and the voices quieted. Now dried\n" +
                "wells across the region are calling her by name, asking her to mediate\n" +
                "disputes with far older consequences.";
    }

    private static String createForPsychic() {
        return "Jacqueline made ends meet reading palms in railway waiting rooms. Her modest\n" +
                "guesses became precise when a traveler left behind a glove: touching it showed\n" +
                "her the last room its owner would ever enter. She found the traveler in time,\n" +
                "but the room appeared again when she handled another lost possession.\n" +
                "Jacqueline now keeps a cabinet of unclaimed objects and visits their owners,\n" +
                "looking for the hidden connection between those approaching the same door.";
    }

    private static String createForRookieCop() {
        return "Tommy's first independent assignment was to inventory property from an\n" +
                "abandoned boardinghouse. Each room held a packed suitcase, and every suitcase\n" +
                "contained the same child's shoe. His report disappeared twice before a senior\n" +
                "officer told him to forget the address. Tommy made a third copy and began\n" +
                "checking missing-person notices himself. He is still learning whom to trust,\n" +
                "but he keeps his evidence carefully labeled and never lets a witness walk home\n" +
                "alone.";
    }

    private static String createForActress() {
        return "Lola specialized in stepping into roles with almost no rehearsal. During a\n" +
                "touring production, she discovered a wardrobe trunk containing costumes for\n" +
                "scenes that had not been written. Wearing one briefly gave her the habits and\n" +
                "memories of a stranger. She bought the trunk to keep it away from the company.\n" +
                "Now she investigates those borrowed lives between performances, searching for\n" +
                "the people whose memories remain vivid long after she changes clothes.";
    }


}
