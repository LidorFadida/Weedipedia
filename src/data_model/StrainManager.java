package data_model;

import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.application.Preloader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;
import mysql_model.TableQuery.TableQueryBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import proj_exeptions.QueryException;
import proj_helpers.JSONHelper;
import proj_helpers.MySQLHelper;
import sample.DataTaskLoader;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static proj_contract.SQLContract.*;
import static proj_contract.StrainsContracts.APIContract.*;
import static proj_contract.StrainsContracts.EffectsContract.*;
import static proj_contract.StrainsContracts.Flavors.*;
import static proj_contract.StrainsContracts.RaceContract.*;
import static proj_contract.StrainsContracts.StrainEffectsDetail.EFFECTS_DETAILS;
import static proj_contract.StrainsContracts.StrainFlavorsDetail.FLAVORS_DETAILS;
import static proj_contract.StrainsContracts.Strains.*;
import static proj_contract.StrainsContracts.TypeContract.*;

@SuppressWarnings({"unchecked"})
public class StrainManager {

    public static final int MAX_ENTITIES = 1969;

    /**
     * Call this method only from a background / Loader Thread due massive usage
     * at the first initiation (preforming heavy work tasks through the api calls).
     *
     * @param appTarget the {@code Application} reference to inform {@link DataTaskLoader} app about any progress.
     */
    public static void constructStrainsJDBC(Application appTarget) {
        try (Connection mySqlDBConnection = MySQLHelper.getMySqlDBConnection()) {
            Statement statement = mySqlDBConnection.createStatement();
            ResultSet set = statement.executeQuery(SHOW_DATABASES);
            while (set.next())
                if (set.getString(DATABASE).equals(DB_NAME))
                    return;
            MySQLHelper.createDataBaseNotExists(DB_NAME, mySqlDBConnection);
            constructTables(mySqlDBConnection);
            LauncherImpl.notifyPreloader(appTarget, new Preloader.ProgressNotification(-2));
            initializeBaseData(mySqlDBConnection);
            LauncherImpl.notifyPreloader(appTarget, new Preloader.ProgressNotification(-1));
            establishStrainsData(appTarget);
        } catch (QueryException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * mocking all the api strains data as a {@code JSONObject} instance
     * and creating a java valid object to represent each strain as a separated entities.
     *
     * @see #insertStrainsJDBC(List, Application) method.
     */
    private static void establishStrainsData(Application application) {
        JSONObject object = JSONHelper.getWebJSONObject(ALL_URL);
        Iterator<String> iterator = object.keySet().iterator();
        List<Strain> allStrains = new ArrayList<>();
        while (iterator.hasNext()) {
            String key = iterator.next();
            allStrains.add(new Strain(new Pair<>(key, object.get(key))));
        }
        allStrains.sort(new Strain.StrainNameComparator());
        insertStrainsJDBC(allStrains, application);
    }

    /**
     * Massive usage.
     * this method invokes in the end of {@link #establishStrainsData(Application)} after all strains established
     * and ready to be inserted into the {@code JDBC}.
     *
     * @param allStrains  the java {@code List<Strain>} object contains all the api strains data.
     * @param application the {@code Application} reference to inform {@link DataTaskLoader} app about any progress.
     * @see #establishStrainsData(Application)
     */
    private static void insertStrainsJDBC(List<Strain> allStrains, Application application) {
        StringBuilder effectsBuilder = new StringBuilder(), flavorsBuilder = new StringBuilder(), strainsBuilder = new StringBuilder();
        Statement statement;
        try (Connection connection = MySQLHelper.getMySqlDBConnection()) {
            connection.setCatalog(DB_NAME);
            statement = connection.createStatement();
            for (int i = 0; i < allStrains.size(); i++) {
                strainsBuilder.append(INSERT_IGNORE_INTO).append(STRAINS).append(SPACE).append(PARENTHESES_RIGHT).append(STRAIN_ID).append(SINGLE_HINT_MARK).append(STRAIN_NAME).append(SINGLE_HINT_MARK).append(RACE_NAME).append(PARENTHESES_LEFT)
                        .append(SPACE).append(VALUES).append(PARENTHESES_RIGHT).append(allStrains.get(i).getId()).append(SINGLE_HINT_MARK).append(SINGLE_QUOTE_MARK).append(allStrains.get(i).getName().replace('\'', ' ')).append(SINGLE_QUOTE_MARK).append(SINGLE_HINT_MARK).append(SINGLE_QUOTE_MARK).append(allStrains.get(i).getRace()).append(SINGLE_QUOTE_MARK).append(PARENTHESES_LEFT);
                statement.execute(strainsBuilder.toString());
                strainsBuilder.delete(0, strainsBuilder.length());
                for (String flavor : allStrains.get(i).getFlavors()) {
                    flavorsBuilder.append(INSERT_IGNORE_INTO).append(FLAVORS_DETAILS).append(SPACE).append(PARENTHESES_RIGHT).append(STRAIN_ID).append(SINGLE_HINT_MARK)
                            .append(FLAVOR_ID).append(PARENTHESES_LEFT).append(SPACE).append(VALUES).append(PARENTHESES_RIGHT).append(allStrains.get(i).getId())
                            .append(SINGLE_HINT_MARK).append(PARENTHESES_RIGHT).append(SELECT).append(FLAVOR_ID).append(SPACE).append(FROM).append(FLAVORS)
                            .append(SPACE).append(WHERE).append(FLAVOR_NAME).append(SPACE).append(EQUAL_OPERATOR).append(SPACE).append(SINGLE_QUOTE_MARK).append(flavor).append(SINGLE_QUOTE_MARK)
                            .append(PARENTHESES_LEFT).append(PARENTHESES_LEFT);
                    statement.execute(flavorsBuilder.toString());
                    flavorsBuilder.delete(0, flavorsBuilder.length());
                }
                for (String s : allStrains.get(i).getEffects().keySet()) {
                    List<String> effects = allStrains.get(i).getEffects().get(s);
                    for (String effect : effects) {
                        effectsBuilder.append(INSERT_IGNORE_INTO).append(EFFECTS_DETAILS).append(SPACE).append(PARENTHESES_RIGHT).append(STRAIN_ID)
                                .append(SINGLE_HINT_MARK).append(EFFECT_ID).append(SINGLE_HINT_MARK).append(TYPE_ID).append(PARENTHESES_LEFT).append(SPACE)
                                .append(VALUES).append(PARENTHESES_RIGHT).append(allStrains.get(i).getId()).append(SINGLE_HINT_MARK)
                                .append(PARENTHESES_RIGHT).append(SELECT).append(EFFECT_ID).append(SPACE).append(FROM).append(EFFECTS).append(SPACE)
                                .append(WHERE).append(EFFECT_NAME).append(SPACE).append(EQUAL_OPERATOR).append(SINGLE_QUOTE_MARK).append(effect).append(SINGLE_QUOTE_MARK)
                                .append(PARENTHESES_LEFT).append(SINGLE_HINT_MARK).append(PARENTHESES_RIGHT).append(SELECT).append(TYPE_ID).append(SPACE).append(FROM).append(TYPE)
                                .append(SPACE).append(WHERE).append(TYPE_NAME).append(SPACE).append(EQUAL_OPERATOR).append(SINGLE_QUOTE_MARK);
                        switch (s) {
                            case "positive":
                                effectsBuilder.append(POSITIVE);
                                break;
                            case "negative":
                                effectsBuilder.append(NEGATIVE);
                                break;
                            case "medical":
                                effectsBuilder.append(MEDICAL);
                                break;
                        }
                        effectsBuilder.append(SINGLE_QUOTE_MARK)
                                .append(PARENTHESES_LEFT).append(PARENTHESES_LEFT);
                        statement.execute(effectsBuilder.toString());
                        effectsBuilder.delete(0, effectsBuilder.length());
                    }
                }
                LauncherImpl.notifyPreloader(application, new Preloader.ProgressNotification((100 * i) / MAX_ENTITIES)); // notify the app loader about this method progress - > inside the loop.
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructing tables if not exists in the database.
     *
     * @param mySqlDBConnection the {@code JDBC} {@code Connection} instance
     * @throws QueryException if {@code TableQueryBuilder } create() method was called and  the {@link TableQueryBuilder} does not holds any table columns.
     * @throws SQLException
     */
    private static void constructTables(Connection mySqlDBConnection) throws QueryException, SQLException {
        MySQLHelper.createTableNotExists(DB_NAME, TYPE, new TableQueryBuilder(TYPE_ID)
                .appendColumnQuery(TYPE_ID, QueryType.INTEGER, true, true)
                .appendColumnQuery(TYPE_NAME, QueryType.TEXT, false, false)
                .create(), mySqlDBConnection);
        MySQLHelper.createTableNotExists(DB_NAME, RACES, new TableQueryBuilder(RACE_ID)
                .appendColumnQuery(RACE_ID, QueryType.INTEGER, true, true)
                .appendColumnQuery(RACE_NAME, QueryType.TEXT, false, false)
                .create(), mySqlDBConnection);
        MySQLHelper.createTableNotExists(DB_NAME, EFFECTS, new TableQueryBuilder(EFFECT_ID)
                .appendColumnQuery(EFFECT_ID, QueryType.INTEGER, true, true)
                .appendColumnQuery(EFFECT_NAME, QueryType.TEXT, true, false)
                .appendColumnQuery(TYPE_ID, QueryType.INTEGER, true, false)
                .create(), mySqlDBConnection);
        MySQLHelper.createTableNotExists(DB_NAME, FLAVORS, new TableQueryBuilder(FLAVOR_ID)
                .appendColumnQuery(FLAVOR_ID, QueryType.INTEGER, true, true)
                .appendColumnQuery(FLAVOR_NAME, QueryType.TEXT, true, false)
                .create(), mySqlDBConnection);
        MySQLHelper.createTableNotExists(DB_NAME, STRAINS, new TableQueryBuilder(STRAIN_ID)
                .appendColumnQuery(STRAIN_ID, QueryType.INTEGER, true, false)
                .appendColumnQuery(STRAIN_NAME, QueryType.TEXT, true, false)
                .appendColumnQuery(RACE_NAME, QueryType.TEXT, true, false)
                .create(), mySqlDBConnection);
        MySQLHelper.createTableNotExists(DB_NAME, FLAVORS_DETAILS, new TableQueryBuilder(null)
                .appendColumnQuery(STRAIN_ID, QueryType.INTEGER, true, false)
                .appendColumnQuery(FLAVOR_ID, QueryType.INTEGER, true, false)
                .create(), mySqlDBConnection);
        MySQLHelper.createTableNotExists(DB_NAME, EFFECTS_DETAILS, new TableQueryBuilder(null)
                .appendColumnQuery(STRAIN_ID, QueryType.INTEGER, true, false)
                .appendColumnQuery(EFFECT_ID, QueryType.INTEGER, true, false)
                .appendColumnQuery(TYPE_ID, QueryType.INTEGER, true, false)
                .create(), mySqlDBConnection);
    }

    /**
     * initializing the base (repeating) data to the default tables using the strain api.
     * values like Race , Type , Effects , Flavors.
     *
     * @param mySqlDBConnection the {@code JDBC} {@code Connection} instance
     * @throws SQLException
     */
    private static void initializeBaseData(Connection mySqlDBConnection) throws SQLException {
        String sql = SELECT_ALL_FROM + EFFECTS;
        Statement statement = mySqlDBConnection.createStatement();
        ResultSet set = statement.executeQuery(sql);
        if (set.isBeforeFirst())
            return;
        //Type
        MySQLHelper.insertIntoSingle(mySqlDBConnection, DB_NAME, TYPE
                , QueryType.TEXT, new Pair<>(TYPE_NAME, POSITIVE));
        MySQLHelper.insertIntoSingle(mySqlDBConnection, DB_NAME, TYPE
                , QueryType.TEXT, new Pair<>(TYPE_NAME, NEGATIVE));
        MySQLHelper.insertIntoSingle(mySqlDBConnection, DB_NAME, TYPE
                , QueryType.TEXT, new Pair<>(TYPE_NAME, MEDICAL));
        //Race
        MySQLHelper.insertIntoSingle(mySqlDBConnection, DB_NAME, RACES
                , QueryType.TEXT, new Pair<>(RACE_NAME, SATIVA));
        MySQLHelper.insertIntoSingle(mySqlDBConnection, DB_NAME, RACES
                , QueryType.TEXT, new Pair<>(RACE_NAME, INDICA));
        MySQLHelper.insertIntoSingle(mySqlDBConnection, DB_NAME, RACES
                , QueryType.TEXT, new Pair<>(RACE_NAME, HYBRID));
        //Effects
        JSONArray effectsArray = JSONHelper.getWebJSONArray(EFFECTS_URL);
        StringBuilder stringBuilder = new StringBuilder();
        if (effectsArray != null) {
            for (Object o : effectsArray) {
                JSONObject temp = (JSONObject) o;
                stringBuilder.append(INSERT_INTO).append(EFFECTS).append(SPACE)
                        .append(PARENTHESES_RIGHT).append(EFFECT_NAME).append(SINGLE_HINT_MARK).append(TYPE_ID).append(PARENTHESES_LEFT).append(SPACE)
                        .append(VALUES).append(PARENTHESES_RIGHT).append(SPACE).append(SINGLE_QUOTE_MARK).append(temp.get("effect")).append(SINGLE_QUOTE_MARK).append(SINGLE_HINT_MARK)
                        .append(PARENTHESES_RIGHT).append(SELECT).append(TYPE_ID).append(SPACE).append(FROM).append(TYPE).append(SPACE)
                        .append(WHERE).append(TYPE_NAME).append(SPACE).append(EQUAL_OPERATOR).append(SPACE)
                        .append(SINGLE_QUOTE_MARK).append(temp.get("type")).append(SINGLE_QUOTE_MARK)
                        .append(PARENTHESES_LEFT).append(PARENTHESES_LEFT);
                statement.execute(stringBuilder.toString());
                stringBuilder.delete(0, stringBuilder.length());
            }
        }
        //Flavors
        JSONArray flavorsArray = JSONHelper.getWebJSONArray(FLAVORS_URL);
        if (flavorsArray != null) {
            for (Object o : flavorsArray) {
                MySQLHelper.insertIntoSingle(mySqlDBConnection, DB_NAME, FLAVORS, QueryType.TEXT, new Pair<>(FLAVOR_NAME, o));
            }
        }
    }
    /**
     * extracting from the {@code JDBC} the strain data using his name with a query.
     *
     * @param name the name of the desired {@code Strain}
     * @return a valid api like {@code Strain} instance including all the data.
     */
    public Strain getStrainData(String name) {
        List<String> positive = new ArrayList<>(), negative = new ArrayList<>(), medical = new ArrayList<>(), flavors = new ArrayList<>();
        Strain strain = new Strain();
        try (Connection connection = MySQLHelper.getMySqlDBConnection()) {
            connection.setCatalog(DB_NAME);
            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery(SELECT_STRAIN_BY_NAME + SINGLE_QUOTE_MARK + name + SINGLE_QUOTE_MARK);
            while (set.next()) {
                strain.setName(set.getString(STRAIN_NAME));
                strain.setRace(set.getString(RACE_NAME));
                strain.setId(set.getInt(STRAIN_ID));
            }
            ResultSet effectsSet = statement.executeQuery(SELECT_STRAIN_EFFECTS_BY_ID + strain.getId());
            while (effectsSet.next()) {
                switch (effectsSet.getString(2)) {
                    case POSITIVE:
                        positive.add(effectsSet.getString(1));
                        break;
                    case NEGATIVE:
                        negative.add(effectsSet.getString(1));
                        break;
                    case MEDICAL:
                        medical.add(effectsSet.getString(1));
                        break;
                }
            }
            strain.setPositiveEffects(positive);
            strain.setNegativeEffects(negative);
            strain.setMedicalEffects(medical);
            ResultSet flavorsSet = statement.executeQuery(SELECT_STRAIN_FLAVORS_BY_ID + strain.getId());
            while (flavorsSet.next())
                flavors.add(flavorsSet.getString(1));
            strain.setFlavors(flavors);
            strain.setDescription((String) JSONHelper.getWebJSONObject(STRAIN_DESC_URL + strain.getId()).get("desc"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return strain;
    }

    /**
     * extract all the strains names inside the {@code JDBC}
     *
     * @return {@code ObservableList<String>} contains all the strains names in the {@code JDBC}
     */
    public ObservableList<String> getStrainsNames() {
        try (Connection connection = MySQLHelper.getMySqlDBConnection()) {
            connection.setCatalog(DB_NAME);
            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery(SELECT_ALL_STRAIN_NAMES);
            List<String> namesList = new ArrayList<>();
            while (set.next()) {
                namesList.add(set.getString(STRAIN_NAME));
            }
            return FXCollections.observableList(namesList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Extracting all the strains name matching the given {@param race}
     * (sativa,indica,hybrid).
     *
     * @param race the desired race to be extracted.
     * @return {@code ObservableList<String>} contains all the strains names in the {@code JDBC}
     * matching the {@param race} given.
     */
    public ObservableList<String> getStrainsByRace(String race) {
        StringBuilder stringBuilder = new StringBuilder();
        try (Connection connection = MySQLHelper.getMySqlDBConnection()) {
            connection.setCatalog(DB_NAME);
            Statement statement = connection.createStatement();
            stringBuilder.append(SELECT_ALL_STRAIN_NAMES).append(WHERE).append(RACE_NAME).append(SPACE).append(EQUAL_OPERATOR).append(SINGLE_QUOTE_MARK).append(race).append(SINGLE_QUOTE_MARK);
            ResultSet set = statement.executeQuery(stringBuilder.toString());
            List<String> namesList = new ArrayList<>();
            while (set.next()) {
                namesList.add(set.getString(STRAIN_NAME));
            }
            return FXCollections.observableList(namesList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Searching in the {@code JDBC} using {@code LIKE} with wildcard '%nn%' (contains in any part of the name).
     * there is no sophisticated logic in this method, clean and simple.
     *
     * @param query the entity client want to search.
     * @return {@code ObservableList<String>} object with all the strains names in the {@code JDBC}
     * matching the given {@param query}
     */
    public ObservableList<String> getSearchedStrains(String query) {
        List<String> names = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        try (Connection connection = MySQLHelper.getMySqlDBConnection()) {
            connection.setCatalog(DB_NAME);
            Statement statement = connection.createStatement();
            stringBuilder.append(SELECT_ALL_STRAIN_NAMES).append(WHERE).append(STRAIN_NAME).append(SPACE).append(LIKE)
                    .append(SINGLE_QUOTE_MARK).append(SINGLE_PERCENTAGE).append(query).append(SINGLE_PERCENTAGE).append(SINGLE_QUOTE_MARK);
            ResultSet set = statement.executeQuery(stringBuilder.toString());
            while (set.next()) {
                names.add(set.getString(STRAIN_NAME));
            }
            return FXCollections.observableList(names);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }
}
