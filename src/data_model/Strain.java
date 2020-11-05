package data_model;

import javafx.util.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

import static proj_contract.StrainsContracts.TypeContract.*;

public class Strain {
    private int id;
    private String race, name, description;
    private List<String> flavors;
    private Map<String, List<String>> effects;

    public Strain() {
        this.effects = new HashMap<>();
    }

    public Strain(Pair<String, Object> entity) {
        if (entity != null)
            establishStrainEntity(entity);
    }

    private void establishStrainEntity(Pair<String, Object> entity) {
        JSONObject singleStrainData = (JSONObject) entity.getValue();
        this.name = entity.getKey();
        this.id = ((Number) singleStrainData.get("id")).intValue();
        this.race = (String) singleStrainData.get("race");
        JSONArray flavorsArray = (JSONArray) singleStrainData.get("flavors");
        this.flavors = new ArrayList<>();
        for (Object o : flavorsArray)
            this.flavors.add((String) o);
        JSONObject effectsObject = (JSONObject) singleStrainData.get("effects");
        this.effects = new TreeMap<>();
        Iterator<String> effectsIterator = effectsObject.keySet().iterator();
        while (effectsIterator.hasNext()) {
            String key = effectsIterator.next();
            JSONArray tempArray = (JSONArray) effectsObject.get(key);
            List<String> contentList = new ArrayList<>();
            for (Object o : tempArray)
                contentList.add((String) o);
            effects.put(key, contentList);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<String> getFlavors() {
        return flavors;
    }

    public void setFlavors(List<String> flavors) {
        this.flavors = flavors;
    }

    public Map<String, List<String>> getEffects() {
        return effects;
    }

    public void setPositiveEffects(List<String> positiveEffects) {
        if (positiveEffects != null)
            this.effects.put(POSITIVE, positiveEffects);
    }

    public void setNegativeEffects(List<String> negativeEffects) {
        if (negativeEffects != null)
            this.effects.put(NEGATIVE, negativeEffects);
    }

    public void setMedicalEffects(List<String> medicalEffects) {
        if (medicalEffects != null)
            this.effects.put(MEDICAL, medicalEffects);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Strain strain = (Strain) o;
        if (id != strain.id) return false;
        if (!Objects.equals(race, strain.race)) return false;
        if (!Objects.equals(name, strain.name)) return false;
        if (!Objects.equals(description, strain.description)) return false;
        if (!Objects.equals(flavors, strain.flavors)) return false;
        return Objects.equals(effects, strain.effects);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (race != null ? race.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (flavors != null ? flavors.hashCode() : 0);
        result = 31 * result + (effects != null ? effects.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Strain{" +
                "id=" + id +
                ", race='" + race + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", flavors=" + flavors +
                ", effects=" + effects +
                '}';
    }

    public static class StrainNameComparator implements Comparator<Strain> {
        @Override
        public int compare(Strain o1, Strain o2) {
            return o1.name.compareTo(o2.name);
        }
    }
}
