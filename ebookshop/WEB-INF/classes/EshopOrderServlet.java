// Saved as "ebookshop\WEB-INF\classes\QueryServlet.java".
import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
 
public class EshopOrderServlet extends HttpServlet {  // JDK 6 and above only
 
   // The doGet() runs once per HTTP GET request to this servlet.
   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response)
                     throws ServletException, IOException {
      // Set the MIME type for the response message
      response.setContentType("text/html");
      // Get a output writer to write the response message into the network socket
      PrintWriter out = response.getWriter();
      String dbName = System.getProperty("RDS_DB_NAME");
      String userName = System.getProperty("RDS_USERNAME");
      String password = System.getProperty("RDS_PASSWORD");
      String hostname = System.getProperty("RDS_HOSTNAME");
      String port = System.getProperty("RDS_PORT");
      String jdbcUrl = "jdbc:mysql://" + hostname + ":" +
        port + "/" + dbName + "?user=" + userName + "&password=" + password;
      out.println("<html><head><title>Query Results</title></head><body>");
      
      Connection conn = null;
      Statement setupStatement = null;
      Statement readStatement = null;
      ResultSet rset = null;
      String results = "";
      int numresults = 0;
      String statement = null;
      try {
         // Step 1: Create a database "Connection" object
         // For MySQL
         conn = DriverManager.getConnection(jdbcUrl);  // <<== Check
         // For MS Access
         // conn = DriverManager.getConnection("jdbc:odbc:ebookshopODBC");
         
         // Step 2: Create a "Statement" object inside the "Connection"
         readStatement = conn.createStatement();
      // Retrieve the books' id. Can order more than one books.
      String[] ids = request.getParameterValues("id");
      if (ids != null) {
      String sqlStr;
      int count;
      
      // Process each of the books
      for (int i = 0; i < ids.length; ++i) {
            // Update the qty of the table books
            sqlStr = "UPDATE books SET qty = qty - 1 WHERE id = " + ids[i];
            out.println("<p>" + sqlStr + "</p>");  // for debugging
            count = readStatement.executeUpdate(sqlStr);
            out.println("<p>" + count + " record updated.</p>");
      
            // Create a transaction record
            sqlStr = "INSERT INTO order_records (id, qty_ordered) VALUES ("
                  + ids[i] + ", 1)";
            out.println("<p>" + sqlStr + "</p>");  // for debugging
            count = readStatement.executeUpdate(sqlStr);
            out.println("<p>" + count + " record inserted.</p>");
            out.println("<h3>Your order for book id=" + ids[i]
                  + " has been confirmed.</h3>");
      }
      out.println("<h3>Thank you.<h3>");
      } else { // No book selected
      out.println("<h3>Please go back and select a book...</h3>");
      }
      out.println("</body></html>");
      } catch (SQLException ex) {
         // Handle any errors
            out.println("<p>SQLException: " + ex.getMessage()+"</p>");
            out.println("<p>SQLState: " + ex.getSQLState()+"</p>");
            out.println("<p>VendorError: " + ex.getErrorCode()+"</p>");
         ex.printStackTrace();
      } finally {
         out.close();
         try {
            // Step 5: Close the Statement and Connection
            if (readStatement != null) readStatement.close();
            if (conn != null) conn.close();
         } catch (SQLException ex) {
            ex.printStackTrace();
         }
      }
   }
}