import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;

public class DatabaseHelper {

    private static DatabaseHelper INSTANCE = null;

    public static DatabaseHelper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DatabaseHelper();
        }
        return INSTANCE;
    }

    public void test() throws URISyntaxException, SQLException {

        Connection connection = getConnection();

        Statement stmt = connection.createStatement();
        stmt.executeUpdate("DROP TABLE IF EXISTS BOT");
        stmt.executeUpdate("CREATE TABLE BOT (server_id text , channel_id text )");
        stmt.executeUpdate("INSERT INTO BOT(server_id , channel_id) VALUES (" + 123 + "," + 123 + ")");
        ResultSet rs = stmt.executeQuery("SELECT channel_id FROM BOT");
        while (rs.next()) {
            System.out.println("Read from DB: " + rs.getString("channel_id"));
        }
    }


    public void createStorage() {
        try {
            Connection connection = null;
            connection = getConnection();

            Statement stmt = connection.createStatement();
            stmt.executeUpdate("DROP TABLE IF EXISTS BOT");

            int executeUpdate = stmt.executeUpdate("CREATE TABLE BOT (channel_id text)");
            System.out.println("Created Storage " + executeUpdate);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public String queryChannelId(String serverId) {
        try {
            Connection connection = null;
            connection = getConnection();

            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("select * from bot where server_id = '" + serverId + "'");
            while (rs.next()) {
                String channel_id = rs.getString("channel_id");
                System.out.println("queryChannelId:" + channel_id);
                return channel_id;
            }

        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;
    }

    public void saveChannelId(String serverId, String channelId) {
        try {
            Connection connection = null;
            connection = getConnection();

            Statement stmt = connection.createStatement();
            stmt.executeUpdate("INSERT INTO BOT(server_id , channel_id) VALUES (" + serverId + "," + channelId + ")");
            ResultSet rs = stmt.executeQuery("SELECT channel_id FROM BOT");
            while (rs.next()) {
                System.out.println("saveChannelId" + rs.getString("channel_id"));
            }

        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    private Connection getConnection() throws URISyntaxException, SQLException {
        URI dbUri = new URI(System.getenv("DATABASE_URL"));

        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();
        System.out.println(username);
        System.out.println(password);
        String VALIDSSD = dbUrl + "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
        System.out.println(VALIDSSD);
        return DriverManager.getConnection(VALIDSSD, username, password);
    }
}
