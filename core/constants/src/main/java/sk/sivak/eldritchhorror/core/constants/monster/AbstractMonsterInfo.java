package sk.sivak.eldritchhorror.core.constants.monster;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;

import java.util.UUID;

/**
 * @author msivak
 */
public abstract class AbstractMonsterInfo implements MonsterInfo {

    private boolean alive;
    private String uuid;
    private boolean reckoning;
    private boolean epic;
    private String name;
    private Integer toughness;
    private Integer currentHealth;
    private Integer horror;
    private Integer damage;
    private int horrorTestModifier;
    private int damageTestModifier;
    private Stat horrorTestType = Stat.WILL;
    private Stat damageTestType = Stat.STRENGTH;
    private LocationId currentLocation;
    private MonsterId monsterId;
    private String reckoningText;
    private String specialText;
    private String spawnText;


    public AbstractMonsterInfo(String name, int horrorTestModifier, Integer horror, int damageTestModifier, Integer damage, Integer toughness) {
        this.name = name;
        generateUuid();
        this.horrorTestModifier = horrorTestModifier;
        this.horror = horror;
        this.damageTestModifier = damageTestModifier;
        this.damage = damage;
        this.toughness = toughness;
        this.currentHealth = this.toughness;
    }

    @Override
    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    public void generateUuid() {
        this.uuid = UUID.randomUUID().toString();
    }


    @Override
    public String getReckoningText() {
        return reckoningText;
    }

    public void setReckoningText(String reckoningText) {
        this.reckoningText = reckoningText;
    }

    @Override
    public String getSpawnText() {
        return spawnText;
    }

    public void setSpawnText(String spawnText) {
        this.spawnText = spawnText;
    }

    @Override
    public String getSpecialText() {
        return specialText;
    }

    public void setSpecialText(String specialText) {
        this.specialText = specialText;
    }

    @Override
    public boolean hasReckoning() {
        return reckoning;
    }

    public void setReckoning(boolean reckoning) {
        this.reckoning = reckoning;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Integer getToughness() {
        return toughness;
    }

    public void setToughness(Integer toughness) {
        this.toughness = toughness;
    }

    @Override
    public Integer getCurrentHealth() {
        return currentHealth;
    }

    public void setCurrentHealth(Integer currentHealth) {
        this.currentHealth = currentHealth;
    }

    @Override
    public Integer getHorror() {
        return horror;
    }

    public void setHorror(Integer horror) {
        this.horror = horror;
    }

    @Override
    public Integer getDamage() {
        return damage;
    }

    public void setDamage(Integer damage) {
        this.damage = damage;
    }

    @Override
    public int getHorrorTestModifier() {
        return horrorTestModifier;
    }

    public void setHorrorTestModifier(int horrorTestModifier) {
        this.horrorTestModifier = horrorTestModifier;
    }

    @Override
    public int getDamageTestModifier() {
        return damageTestModifier;
    }

    public void setDamageTestModifier(int damageTestModifier) {
        this.damageTestModifier = damageTestModifier;
    }

    @Override
    public Stat getHorrorTestType() {
        return horrorTestType;
    }

    public void setHorrorTestType(Stat horrorTestType) {
        this.horrorTestType = horrorTestType;
    }

    @Override
    public Stat getDamageTestType() {
        return damageTestType;
    }

    public void setDamageTestType(Stat damageTestType) {
        this.damageTestType = damageTestType;
    }

    @Override
    public LocationId getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(LocationId currentLocation) {
        this.currentLocation = currentLocation;
    }

    @Override
    public MonsterId getMonsterId() {
        return monsterId;
    }

    public void setMonsterId(MonsterId monsterId) {
        this.monsterId = monsterId;
    }

    @Override
    public boolean isEpic() {
        return epic;
    }

    public void setEpic(boolean epic) {
        this.epic = epic;
    }

    @Override
    public String getNameInSelectComponent() {
        return getName();
    }
}
