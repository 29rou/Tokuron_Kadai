package mypkg;
 
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
 
public class DoSomethingServlet extends HttpServlet {
 
   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response)
           throws ServletException, IOException {
      response.setContentType("text/html;charset=UTF-8");
      PrintWriter out = response.getWriter();
 
      try {
         out.println("<html><head><title>Do Something</title></head><body>");
         out.println("<h2>Do Somethings...</h2>");
 
         // Retrieve and Display the username and roles
         String userName;
         List<String> roles;
         HttpSession session = request.getSession(false);
         if (session == null) {
            out.println("<h3>You have not login!</h3>");
         } else {
            synchronized (session) {
               userName = (String) session.getAttribute("username");
               roles = (List<String>) session.getAttribute("roles");
            }
 
            out.println("<table>");
            out.println("<tr>");
            out.println("<td>Username:</td>");
            out.println("<td>" + userName + "</td></tr>");
            out.println("<tr>");
            out.println("<td>Roles:</td>");
            out.println("<td>");
            for (String role : roles) {
               out.println(role + " ");
            }
            out.println("</td></tr>");
            out.println("<tr>");
            out.println("</table>");
 
            out.println("<p><a href='logout'>Logout</a></p>");
         }
         out.println("</body></html>");
      } finally {
         out.close();
      }
   }
}