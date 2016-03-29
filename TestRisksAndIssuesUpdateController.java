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
import com.sonata.tm.model.TestRisksAndIssues;

/**
 * Class to update Test Risk Issues.
 *
 * @author kirankumar.kadapa
 *
 */
@Controller
public class TestRisksAndIssuesUpdateController
{

   /**
    * method to return risk issue form.
    *
    * @param request Http Request
    * @param response Http Response
    * @return risk issue form for active user.
    */
   @RequestMapping("/view_testRisksAndIssues")
   public ModelAndView getNewForm(HttpServletRequest request,
      HttpServletResponse response)
   {
      if (request.getSession().getAttribute(
         TestManagementSessionKeys.USER_NAME) != null
         && request.getSession().getAttribute(
            TestManagementSessionKeys.PASS_WORD) != null)
      {
         return new ModelAndView("test_risks_and_issues");
      }
      else
      {
         return new ModelAndView("loginForm");
      }
   }

   /**
    * method to insert risk issue.
    *
    * @param request Http Request
    * @param response Http Response
    * @return risk issue status
    * @throws SQLException SQL Exception
    * @throws IOException IO Exception
    */
   @RequestMapping("/save_testRisksAndIssues")
   public ModelAndView saveTestRisksAndIssues(HttpServletRequest request,
      HttpServletResponse response) throws SQLException, IOException
   {

      TestRisksAndIssues testRisksAndIssues = new TestRisksAndIssues();
      String impact = request.getParameter("Impact");
      String releaseName = request.getParameter("ReleaseName");
      String projectName = request.getParameter("ProjectName");
      String rIDiscription = request.getParameter("RIDiscription");
      String owner = request.getParameter("Owner");
      String rIStatus = request.getParameter("RIStatus");
      String rIMStrategy = request.getParameter("RIMStrategy");
      String riskOrIssue = request.getParameter("RiskOrIssue");
      String priority = request.getParameter("Priority");
      String severity = request.getParameter("Severity");
      Date date = new Date();
      String date1 = request.getParameter("ResolutionDate");
      String date2 = request.getParameter("ResolvedDate");
      Date resolutionDate = new Date(date1);
      Date resolvedDate = new Date(date2);
      java.sql.Date resolutionSQLDate =
         new java.sql.Date(resolutionDate.getTime());
      java.sql.Date resolvedSQLDate =
         new java.sql.Date(resolvedDate.getTime());
      java.sql.Date sQLloggedDate = new java.sql.Date(date.getTime());

      testRisksAndIssues.setImpact(impact);
      testRisksAndIssues.setOwner(owner);
      testRisksAndIssues.setRistatus(rIStatus);

      testRisksAndIssues.setProjectName(projectName);
      testRisksAndIssues.setReleaseName(releaseName);
      testRisksAndIssues.setRidiscription(rIDiscription);
      testRisksAndIssues.setRimstrategy(rIMStrategy);
      testRisksAndIssues.setRiskOrIssue(riskOrIssue);
      testRisksAndIssues.setPriority(priority);
      testRisksAndIssues.setSeverity(severity);
      String username = request.getParameter("username");

      username =
         (String) request.getSession().getAttribute(
            TestManagementSessionKeys.USER_NAME);

      Connection mySqldbconnection = null;
      PreparedStatement preparedStatement = null;
      try
      {
         mySqldbconnection = DaoUtil.getMySqlDBConnection();
         String insertTableSQL =
            "INSERT INTO TestRisk_and_Issues"
               + "(Release_Name, Project_Name, Risk_Issue_Description, Impact,"
               + "Owner,Risk_Issue_Status, Resolution_Target_Date, Resolved_Date,"
               + "username,Log_Date,Risk_Issue_M_Strategy, archive, riskorissue,"
               + "priority, severity ) VALUES"
               + "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
         preparedStatement =
            mySqldbconnection.prepareStatement(insertTableSQL);
         preparedStatement.setString(Numbers.ONE,
            testRisksAndIssues.getReleaseName());
         preparedStatement.setString(Numbers.TWO,
            testRisksAndIssues.getProjectName());
         preparedStatement.setString(Numbers.THREE,
            testRisksAndIssues.getRidiscription());
         preparedStatement.setString(Numbers.FOUR,
            testRisksAndIssues.getImpact());
         preparedStatement.setString(Numbers.FIVE,
            testRisksAndIssues.getOwner());
         preparedStatement.setString(Numbers.SIX,
            testRisksAndIssues.getRistatus());
         preparedStatement.setDate(Numbers.SEVEN, resolutionSQLDate);
         preparedStatement.setDate(Numbers.EIGHT, resolvedSQLDate);
         preparedStatement.setString(Numbers.NINE, username);
         preparedStatement.setDate(Numbers.TEN, sQLloggedDate);
         preparedStatement.setString(Numbers.ELEVEN,
            testRisksAndIssues.getRimstrategy());
         /* archive */
         preparedStatement.setString(Numbers.TWELVE, "no");
         /* risk or issue */
         preparedStatement.setString(Numbers.THIRTEEN,
            testRisksAndIssues.getRiskOrIssue());
         /* priority */
         preparedStatement.setString(Numbers.FOURTEEN,
            testRisksAndIssues.getPriority());
         /* severity */
         preparedStatement.setString(Numbers.FIFTEEN,
            testRisksAndIssues.getSeverity());
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
