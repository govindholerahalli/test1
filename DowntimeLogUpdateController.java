package com.sonata.tm.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.sonata.tm.connectionutil.DaoUtil;
import com.sonata.tm.constants.Numbers;
import com.sonata.tm.constants.TestManagementSessionKeys;
import com.sonata.tm.model.DowntimeLog;

/**
 * Class to insert Downtime log.
 *
 * @author kirankumar.kadapa
 *
 */
@Controller
public class DowntimeLogUpdateController
{

   /**
    * method to get new form.
    *
    * @param request Http Request
    * @param response Http Response
    * @return downtimelog view
    */
   @RequestMapping("/viewDowntim_log")
   public ModelAndView getnewDowntimeLog(HttpServletRequest request,
      HttpServletResponse response)
   {
      if (request.getSession().getAttribute(
         TestManagementSessionKeys.USER_NAME) != null
         && request.getSession().getAttribute(
            TestManagementSessionKeys.PASS_WORD) != null)
      {

         return new ModelAndView("updateDowntimeLog");
      }
      else
      {
         return new ModelAndView("loginForm");
      }

   }

   /**
    * method to save downtime log.
    *
    * @param request Http Request
    * @param response Http Response
    * @return downtimelog status view
    * @throws SQLException SQL Exception
    * @throws IOException IO Exception
    */
   @RequestMapping("/save_DowntimeLog")
   public ModelAndView saveDowntimeLog(HttpServletRequest request,
      HttpServletResponse response) throws SQLException, IOException
   {
      DowntimeLog downtimeLog = new DowntimeLog();
      Double downtime =
         Double.parseDouble(request.getParameter("Downtime"));
      downtimeLog.setComponent(request.getParameter("Component"));
      downtimeLog.setDowntime(downtime);
      downtimeLog.setEnvironment(request.getParameter("Environment"));
      downtimeLog.setProjectName(request.getParameter("Project"));
      downtimeLog.setProjectRelease(request.getParameter("Release"));
      downtimeLog.setMitigation(request.getParameter("Mitigation"));
      downtimeLog.setDowntime((downtime));
      downtimeLog.setReasonsIfKnown(request.getParameter("Reason"));
      downtimeLog.setProjectImpact(request.getParameter("Projectimpact"));
      downtimeLog.setCostImpact(request.getParameter("Costimpact"));
      String username = request.getParameter("username");
      username =
         (String) request.getSession().getAttribute(
            TestManagementSessionKeys.USER_NAME);
      downtimeLog.setEffortLost(Double.parseDouble(request
         .getParameter("EffortLost")));
      String loggedDate = request.getParameter("Date");
      Date loggedUtilDate = new Date(loggedDate);
      java.sql.Date loggedSQLDate =
         new java.sql.Date(loggedUtilDate.getTime());
      Connection mySqldbconnection = null;
      PreparedStatement preparedStatement = null;
      try
      {
         mySqldbconnection = DaoUtil.getMySqlDBConnection();
         String insertTableSQL =
            "INSERT INTO Downtime_Log"
               + "(Log_Date, Environment, Project_Name, Project_Release,Component,"
               + "down_time,Reasons_If_Known,  Project_Impact, Mitigation, "
               + "Cost_Impact,Effort_Lost,username,archive ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
         preparedStatement =
            mySqldbconnection.prepareStatement(insertTableSQL);
         preparedStatement.setDate(Numbers.ONE, loggedSQLDate);
         preparedStatement.setString(Numbers.TWO,
            downtimeLog.getEnvironment());
         preparedStatement.setString(Numbers.THREE,
            downtimeLog.getProjectName());
         preparedStatement.setString(Numbers.FOUR,
            downtimeLog.getProjectRelease());
         preparedStatement.setString(Numbers.FIVE,
            downtimeLog.getComponent());
         preparedStatement.setDouble(Numbers.SIX,
            downtimeLog.getDowntime());
         preparedStatement.setString(Numbers.SEVEN,
            downtimeLog.getReasonsIfKnown());
         preparedStatement.setString(Numbers.EIGHT,
            downtimeLog.getProjectImpact());
         preparedStatement.setString(Numbers.NINE,
            downtimeLog.getMitigation());
         preparedStatement.setString(Numbers.TEN,
            downtimeLog.getCostImpact());
         preparedStatement.setDouble(Numbers.ELEVEN,
            downtimeLog.getEffortLost());
         preparedStatement.setString(Numbers.TWELVE, username);
         preparedStatement.setString(Numbers.THIRTEEN, "no");
         // execute insert SQL stetement
         preparedStatement.executeUpdate();
      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
      ModelAndView modelAndView = new ModelAndView("success");
      return modelAndView;
   }
}
