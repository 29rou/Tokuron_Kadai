// Saved as "ebookshop\WEB-INF\classes\QueryServlet.java".
import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
 
public class QueryPostServlet extends HttpServlet {  // JDK 6 and above only
 
   // The doGet() runs once per HTTP GET request to this servlet.
   @Override
   public void doPost(HttpServletRequest request, HttpServletResponse response)
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
      // Load the JDBC driver
      try {
      out.println("<p>Loading driver...</p>");
      Class.forName("com.mysql.jdbc.Driver");
      out.println("<p>Driver loaded!</p>");
      } catch (ClassNotFoundException e) {
      throw new RuntimeException("<h2>Cannot find the driver in the classpath!</h2>", e);
      }
      
      Connection conn = null;
      Statement setupStatement = null;
      Statement readStatement = null;
      ResultSet resultSet = null;
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

         // Step 3: Execute a SQL SELECT query
         String sqlStr = "SELECT * FROM books WHERE author = "
               + "'" + request.getParameter("author") + "'"
               + " AND qty > 0 ORDER BY author ASC, title ASC";
 
         // Print an HTML page as output of query
         out.println("<h2>Thank you for your query.</h2>");
         out.println("<p>You query is: " + sqlStr + "</p>"); // Echo for debugging
         resultSet = readStatement.executeQuery(sqlStr); // Send the query to the server
 
         // Step 4: Process the query result
         int count = 0;
         while(resultSet.next()) {
            // Print a paragraph <p>...</p> for each row
            out.println("<p>" + resultSet.getString("author")
                  + ", " + resultSet.getString("title")
                  + ", $" + resultSet.getDouble("price") + "</p>");
            ++count;
         }
         out.println("<p>==== " + count + " records found ====</p>");
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