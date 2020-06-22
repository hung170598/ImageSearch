import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by hung1 on 6/21/2020.
 */
public class DBConnector {

    public static Connection getConnection(){
        Connection con = null;
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            String connectURL = "jdbc:mysql://" + SV.HOST_NAME + ":" + SV.HOST_PORT + "/" + SV.DB_NAME;
            con = DriverManager.getConnection(connectURL, SV.USERNAME, SV.PASSWORD);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con;
    }
}
