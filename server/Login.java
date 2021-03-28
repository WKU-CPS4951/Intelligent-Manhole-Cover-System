/*source code: https://blog.csdn.net/qq_42289906/article/details/80707060*/
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;

public class Login extends HttpServlet{
    
    @Override
    protected void doPost(HttpServletRequest request,HttpServletResponse response)
    throws ServletException, IOException{
        response.setContentType("text/html;charset=utf-8");
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");

        try(PrintWriter out=response.getWriter()) {
            //get the received email and password
            String email=request.getParameter("email").trim();
            String password=request.getParameter("password").trim();
            Boolean verifyResult=verifyLogin(email,password);

            if(verifyResult){
                out.print(true);
            }else{
                out.print(false);
            }
        }

    }
    private Boolean verifyLogin(String email,String password){
        User user=UserDAO.queryUser(email);
        return null!=user&&password.equals(user.getPassword());
    }
}
