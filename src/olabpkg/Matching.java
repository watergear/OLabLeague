package olabpkg;

import java.util.*;
import java.sql.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Servlet implementation class Matching
 */
@WebServlet("/Matching")
public class Matching extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Matching() {
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
			stmtPlayers = conn.createStatement();
			ResultSet rsetPlayers = stmtPlayers.executeQuery("select * from players");
			HashMap<Integer, String> mapPlayers = new HashMap<Integer, String>(); 
			while (rsetPlayers.next()) {
				mapPlayers.put(rsetPlayers.getInt("id"), rsetPlayers.getString("name"));
			}
			
			if ( null != todo )
			{
				Statement stmtAddMatchs = null;
				try {
					DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
					PreparedStatement ps = null;
					if ( todo.equals("addsingle") )
					{
						String insql="insert into singlematchs(playerwin, playerloss, scorewin, scoreloss, date) values(?,?,?,?,?)";
						ps = conn.prepareStatement(insql);
						ps.setInt(1, Integer.parseInt(request.getParameter("playerwin")));
						ps.setInt(2, Integer.parseInt(request.getParameter("playerloss")));
						ps.setInt(3, Integer.parseInt(request.getParameter("scorewin")));
						ps.setInt(4, Integer.parseInt(request.getParameter("scoreloss")));
						java.util.Date utilDate = formatter.parse(request.getParameter("date"));
						java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
						ps.setDate(5, sqlDate);
					}
					else
					if ( todo.equals("adddouble") )
					{
						String insql="insert into doublematchs(playerwina, playerwinb, playerlossa, playerlossb, scorewin, scoreloss, date) values(?,?,?,?,?,?,?)";
						ps = conn.prepareStatement(insql);
						ps.setInt(1, Integer.parseInt(request.getParameter("playerwina")));
						ps.setInt(2, Integer.parseInt(request.getParameter("playerwinb")));
						ps.setInt(3, Integer.parseInt(request.getParameter("playerlossa")));
						ps.setInt(4, Integer.parseInt(request.getParameter("playerlossb")));
						ps.setInt(5, Integer.parseInt(request.getParameter("scorewin")));
						ps.setInt(6, Integer.parseInt(request.getParameter("scoreloss")));
						java.util.Date utilDate = formatter.parse(request.getParameter("date"));
						java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
						ps.setDate(7, sqlDate);
					}
					if ( ps != null )
						ps.executeUpdate();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
			stmtSingleMatchs = conn.createStatement();
			stmtDoubleMatchs = conn.createStatement();
			ResultSet rsetSingleMatchs = stmtSingleMatchs.executeQuery("select * from singlematchs");
			ResultSet rsetDoubleMatchs = stmtDoubleMatchs.executeQuery("select * from doublematchs");
			
			ArrayList<Match> list = new ArrayList<Match>();
			while (rsetSingleMatchs.next()) {
				Match m = new Match();
				m.date = rsetSingleMatchs.getDate("date");
				m.playerWin = mapPlayers.get(rsetSingleMatchs.getInt("playerwin"));
				m.playerLoss = mapPlayers.get(rsetSingleMatchs.getInt("playerloss"));
				m.scoreWin = rsetSingleMatchs.getInt("scorewin");
				m.scoreLoss = rsetSingleMatchs.getInt("scoreloss");
				list.add(m);
			}
			while (rsetDoubleMatchs.next()) {
				Match m = new Match();
				m.date = rsetDoubleMatchs.getDate("date");
				m.playerWin = 
						mapPlayers.get(rsetDoubleMatchs.getInt("playerwina")) + ", " +
						mapPlayers.get(rsetDoubleMatchs.getInt("playerwinb"));
				m.playerLoss = 
						mapPlayers.get(rsetDoubleMatchs.getInt("playerlossa")) + ", " +
						mapPlayers.get(rsetDoubleMatchs.getInt("playerlossb"));
				m.scoreWin = rsetDoubleMatchs.getInt("scorewin");
				m.scoreLoss = rsetDoubleMatchs.getInt("scoreloss");
				list.add(m);
			}
			
			list.sort( (a,b) -> b.date.compareTo(a.date));
			
			request.setAttribute("matchs", list);
			request.setAttribute("players", mapPlayers);
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
		nextPage = "/match.jsp";
		
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
