package olabpkg;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.sql.DataSource;
import javax.naming.*;

/**
 * Servlet implementation class Scoring
 */
@WebServlet("/Scoring")
public class Scoring extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Scoring() {
        super();
        // TODO Auto-generated constructor stub
    }

    DataSource pool;  // Database connection pool
    
    @Override
    public void init( ) throws ServletException {
       try {
          // Create a JNDI Initial context to be able to lookup the DataSource
          InitialContext ctx = new InitialContext();
          // Lookup the DataSource, which will be backed by a pool
          //   that the application server provides.
          pool = (DataSource)ctx.lookup("java:comp/env/jdbc/OLabLeague");
          if (pool == null)
             throw new ServletException("Unknown DataSource 'jdbc/OLabLeague'");
       } catch (NamingException ex) {
          ex.printStackTrace();
       }
    }
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
		
		String todo = request.getParameter("todo");
		
		Connection conn = null;
		Statement stmtPlayers = null;
		Statement stmtSingleMatchs = null;
		Statement stmtDoubleMatchs = null;
		try {
			conn = pool.getConnection();
			
			if ( null != todo )
			{
				Statement stmtAddPlayers = null;
				try {
					PreparedStatement ps = null;
					if ( todo.equals("addplayer") )
					{
						String name = request.getParameter("name");
						if ( name != null && !name.equals("") )
						{
							String insql="insert into players(name) values(?)";
							ps = conn.prepareStatement(insql);
							ps.setString(1, name);
						}
					}
					if ( ps != null )
						ps.executeUpdate();
				}catch(Exception e){
					e.printStackTrace();
				}
			}			
			
			stmtPlayers = conn.createStatement();
			ResultSet rsetPlayers = stmtPlayers.executeQuery("select * from players");
			stmtSingleMatchs = conn.createStatement();
			stmtDoubleMatchs = conn.createStatement();
			ResultSet rsetSingleMatchs = stmtSingleMatchs.executeQuery("select playerwin, playerloss from singlematchs");
			ResultSet rsetDoubleMatchs = stmtDoubleMatchs.executeQuery("select playerwina, playerwinb, playerlossa, playerlossb from doublematchs");
			ArrayList<Achievement> list = new ArrayList<Achievement>();
			while (rsetPlayers.next()) {
				Achievement a = new Achievement();
				a.name = rsetPlayers.getString("name");
				int playerid =  rsetPlayers.getInt("id");
				int totalscore = 0;
				
				rsetSingleMatchs.first();
				rsetSingleMatchs.previous();
				while (rsetSingleMatchs.next()) {
					if ( playerid == rsetSingleMatchs.getInt("playerwin") )
						totalscore += 2;
					else
					if ( playerid == rsetSingleMatchs.getInt("playerloss") )
					{
						totalscore += 1;
					}
				}
				
				rsetDoubleMatchs.first();
				rsetDoubleMatchs.previous();
				while (rsetDoubleMatchs.next()) {
					if ( playerid == rsetDoubleMatchs.getInt("playerwina") 
						|| playerid == rsetDoubleMatchs.getInt("playerwinb") )
						totalscore += 2;
					else
						if ( playerid == rsetDoubleMatchs.getInt("playerlossa") 
						|| playerid == rsetDoubleMatchs.getInt("playerlossb") )
					{
						totalscore += 1;
					}
				}
				
				a.score = totalscore;
				list.add(a);
			}
			request.setAttribute("scores", list);
        } catch (SQLException ex) {
            ex.printStackTrace();
		} finally {
		   try {
		      if (stmtPlayers != null) stmtPlayers.close();
		      if (stmtSingleMatchs != null) stmtSingleMatchs.close();
		      if (stmtDoubleMatchs != null) stmtDoubleMatchs.close();
		      if (conn != null) conn.close();  // return to pool
		   } catch (SQLException ex) {
		       ex.printStackTrace();
		   }
		}
		
		String nextPage = "";
		nextPage = "/score.jsp";
		
		ServletContext servletContext = getServletContext();
		RequestDispatcher requestDispatcher = servletContext.getRequestDispatcher(nextPage);
		requestDispatcher.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
