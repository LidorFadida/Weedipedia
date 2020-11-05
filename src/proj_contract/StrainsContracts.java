package proj_contract;

public interface StrainsContracts {

    interface APIContract {
        String API_KEY = "O7XhZEi";
        String ALL_URL = "http://strainapi.evanbusse.com/" + API_KEY + "/strains/search/all";
        String EFFECTS_URL = "http://strainapi.evanbusse.com/" + API_KEY + "/searchdata/effects";
        String FLAVORS_URL = "http://strainapi.evanbusse.com/" + API_KEY + "/searchdata/flavors";
        String STRAIN_DESC_URL = "http://strainapi.evanbusse.com/" + API_KEY + "/strains/data/desc/";
    }

    interface TypeContract {
        String TYPE = "Type";
        String TYPE_ID = "TypeID";
        String TYPE_NAME = "TypeName";
        String POSITIVE = "Positive";
        String NEGATIVE = "Negative";
        String MEDICAL = "Medical";
    }

    interface RaceContract {
        String RACES = "Races";
        String RACE_ID = "RaceID";
        String RACE_NAME = "RaceName";
        String SATIVA = "Sativa";
        String INDICA = "Indica";
        String HYBRID = "Hybrid";
    }

    interface EffectsContract {
        String EFFECTS = "Effects";
        String EFFECT_ID = "EffectID";
        String EFFECT_NAME = "EffectName";
    }

    interface Flavors {
        String FLAVORS = "Flavors";
        String FLAVOR_NAME = "FlavorName";
        String FLAVOR_ID = "FlavorID";
    }

    interface Strains {
        String STRAINS = "Strains";
        String STRAIN_ID = "StrainID";
        String STRAIN_NAME = "StrainName";
    }

    interface StrainFlavorsDetail {
        String FLAVORS_DETAILS = "StrainFlavorsDetail";
    }

    interface StrainEffectsDetail {
        String EFFECTS_DETAILS = "StrainEffectsDetail";
    }
}
