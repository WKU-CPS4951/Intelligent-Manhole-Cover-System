/* code source: https://blog.csdn.net/qq_42289906/article/details/80707060*/
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
public class DBmanager extends HttpServlet {
    ServletConfig config;
    private static String userName;
    private static String passWord;
    private static String url="jdbc:mysql://120.27.146.176:3306/users";
    private static Connection connection;

    @Override
    public void init(ServletConfig config) throws ServletException{
        //define a servletconfig, database user, password and url
        super.init(config);
        this.config=config;
        userName=config.getInitParameter("DBUsername");
        passWord=config.getInitParameter("DBPassword");
        url=config.getInitParameter("connectionURL");
    }

    public static Connection getConnection() {
        //create a Mysql driver object and register in DriverManager
        try{
            Class.forName("com.mysql.jdbc.Driver");
        //build the connection to the database
            connection=DriverManager.getConnection(url,userName,passWord);
        }catch(ClassNotFoundException| SQLException ex)
        {
            Logger.getLogger(DBmanager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return connection;
    }
    //shutdown the connection when result, statement and connection are valid
    public static void closeAll(Connection connection,Statement statement, ResultSet resultSet){
        try{
            if(resultSet!=null){
                resultSet.close();
            }
            if(statement!=null){
                statement.close();
            }
            if(connection!=null){
                connection.close();
            }
        }catch(SQLException ex){
            Logger.getLogger(DBmanager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    //shutdown the connection when statement and connection are valid.resultSet 
    //is not required bacause the operation doesn't need to return the result
    public static void closeAll(Connection connection,Statement statement){
        try{
            if(statement!=null){
                statement.close();
            }
            if(connection!=null){
                connection.close();
            }
        }catch(SQLException ex){
            Logger.getLogger(DBmanager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
