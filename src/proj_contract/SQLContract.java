package proj_contract;

public interface SQLContract {
    String SELECT = "SELECT ";
    String FROM = "FROM ";
    String SHOW_DATABASES = "SHOW DATABASES";
    String DATABASE = "Database";
    String LIKE = "LIKE";
    String SELECT_ALL_FROM = "SELECT * FROM ";
    String SELECT_ALL_STRAIN_NAMES = "SELECT StrainName FROM Strains ";
    String SELECT_STRAIN_BY_NAME = "SELECT * FROM Strains WHERE StrainName = ";
    String SELECT_STRAIN_NAME_BY_ID = "SELECT Strains.StrainName ,Strains.RaceName FROM Strains WHERE Strains.StrainID = ";
    String SELECT_STRAIN_FLAVORS_BY_ID = "SELECT  Flavors.FlavorName " +
            " From StrainFlavorsDetail " +
            " JOIN Strains " +
            " ON StrainFlavorsDetail.StrainID = Strains.StrainID " +
            " JOIN Flavors " +
            " ON StrainFlavorsDetail.FlavorID = Flavors.FlavorID " +
            "WHERE StrainFlavorsDetail.StrainID = ";
    String SELECT_STRAIN_EFFECTS_BY_ID = "select Effects.EffectName , Type.TypeName " +
            "FROM StrainEffectsDetail " +
            "JOIN Strains " +
            "ON StrainEffectsDetail.StrainID = Strains.StrainID " +
            "JOIN Effects " +
            "ON StrainEffectsDetail.EffectID = Effects.EffectID " +
            "JOIN Type " +
            "ON Effects.TypeID = Type.TypeID " +
            "WHERE StrainEffectsDetail.StrainID = ";
    String DB_URL = "jdbc:mysql://localhost:3306/";
    String DB_USER_NAME = "root";
    String DB_PASSWORD = "204474993Lf"; // -- change this to your db connection password
    String DB_NAME = "Weedipedia";
    String CREATE_DB_NOT_EXISTS = "CREATE DATABASE IF NOT EXISTS ";
    String CREATE_TABLE_NOT_EXISTS = "CREATE TABLE IF NOT EXISTS ";
    String INSERT_INTO = "INSERT INTO ";
    String INSERT_IGNORE_INTO = "INSERT IGNORE INTO ";
    String VALUES = "VALUES ";
    String WHERE = "WHERE ";
    String UNKNOWN = "Unknown";
    String NO_DESC = "No Description at the moment :( .";
    char SPACE = ' ';
    char EQUAL_OPERATOR = '=';
    char PARENTHESES_LEFT = ')';
    char PARENTHESES_RIGHT = '(';
    char SINGLE_PERCENTAGE = '%';
    char SINGLE_QUOTE_MARK = '\'';
    char SINGLE_HINT_MARK = ',';
    char QUESTION_MARK = '?';
    int MIN_TABLE_Q_SIZE = 2;

    enum QueryType {
        CHAR("CHAR"), TEXT("TEXT"), INTEGER("INTEGER"), BOOLEAN("BOOLEAN"), NOT_NULL("NOT NULL"), PRIMARY_KEY("PRIMARY KEY"), AUTO_INCREMENT("AUTO_INCREMENT");
        private final String value;

        QueryType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
