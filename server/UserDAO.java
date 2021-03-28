/*code source: https://blog.csdn.net/qq_42289906/article/details/80707060*/
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.json.JSONObject;
public class UserDAO {
    public static User queryUser(String email){
        //define sql statement and the return result
        Connection connection=DBmanager.getConnection();
        PreparedStatement preparedStatement=null;
        ResultSet resultSet=null;
        //create the statement to query the email in user_Info
        StringBuilder sqlStatement=new StringBuilder();
        sqlStatement.append("select * from user_Info where email=?");

        try{
            preparedStatement=connection.prepareStatement(sqlStatement.toString());
            preparedStatement.setString(1, email);
            //execute the query for email and password
            resultSet=preparedStatement.executeQuery();
            User user=new User();
            if(resultSet.next()){
                user.setEmail(resultSet.getString("email"));
                user.setPassword(resultSet.getString("password"));
                return user;
            }else{
                return null;
            }
        }catch(SQLException ex){
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }finally {
            DBmanager.closeAll(connection, preparedStatement,resultSet);
        }
    }

}
