/*
 * Copyright (C)2006 TUI UK Ltd
 *
 * TUI UK Ltd, Columbus House, Westwood Way, Westwood Business Park,
 * Coventry, United Kingdom CV4 8TT
 *
 * Telephone - (024)76282828
 *
 * All rights reserved - The copyright notice above does not evidence any
 * actual or intended publication of this source code.
 *
 * $RCSfile: EditAndDeleteController.java$
 *
 * $Revision: $
 *
 * $Date: Oct 27, 2015$
 *
 * Author: kirankumar.kadapa
 *
 *
 * $Log: $
 */
package com.sonata.tm.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.sonata.tm.connectionutil.DaoUtil;
import com.sonata.tm.constants.Numbers;
import com.sonata.tm.constants.TestManagementSessionKeys;
import com.sonata.tm.helper.ProjectReleaseComparator;
import com.sonata.tm.model.DetailedTestStatus;
import com.sonata.tm.model.DowntimeLog;
import com.sonata.tm.model.MasterProjectReleaseOrderEnum;
import com.sonata.tm.model.MasterProjectsRelease;
import com.sonata.tm.model.TestRisksAndIssues;

/**
 * Class to edit delete and update records in db.
 *
 * @author kirankumar.kadapa
 *
 */
@Controller
@SuppressWarnings("deprecation")
public class EditAndDeleteController
{

   /*
    * Following parameters are used frequently in the class.
    */
   /**
    * date format in db response.
    */
   private static final String DB_DATE_FORMAT = "yyyy-MM-dd";

   /**
    * date format read from jsp.
    */
   private static final String JSP_DATE_FORMAT = "dd-MMM-yyyy";

   /**
    * jsp field - to perform edit function.
    */
   private static final String EDIT = "Edit";

   /**
    * jsp field - to perform delete function.
    */
   private static final String DELETE = "Delete";

   /**
    * jsp field - to perform archive function.
    */
   private static final String ARCHIVE = "Archive";

   /**
    * admin user field value is in properties file.
    */
   private static final String ADMIN_USER = "admin_user";

   /**
    * jsp field - to perform edit function.
    */
   private static final String EDIT_BUTTON = "edit_button";

   /**
    * jsp field - to perform delete function.
    */
   private static final String DELETE_BUTTON = "delete_button";

   /**
    * gets down time logs.
    *
    * @param request HttpRequest
    * @param response HttpResponse
    * @return modelAndView
    * @throws Exception Exception
    */
   @RequestMapping("/downtimeloglist")
   public ModelAndView getDownTimeLog(HttpServletRequest request,
      HttpServletResponse response) throws Exception
   {
      ModelAndView modelAndView = new ModelAndView("viewdowntimelogs");
      ArrayList<DowntimeLog> downTimeLogs = new ArrayList<DowntimeLog>();

      Connection mySqldbconnection = null;
      PreparedStatement preparedStatement = null;
      ResultSet resultSet = null;
      try
      {
         mySqldbconnection = DaoUtil.getMySqlDBConnection();
         String getTestRiskIssuesQuery =
            "SELECT * FROM Downtime_Log WHERE NOT archive ='yes'";
         preparedStatement =
            mySqldbconnection.prepareStatement(getTestRiskIssuesQuery);
         preparedStatement.execute();
         resultSet = preparedStatement.getResultSet();
         while (resultSet.next())
         {
            SimpleDateFormat sdf1 = new SimpleDateFormat(DB_DATE_FORMAT);
            SimpleDateFormat sdf2 = new SimpleDateFormat(JSP_DATE_FORMAT);

            // CHECKSTYLE:OFF
            Date logDate = sdf1.parse(resultSet.getString("Log_Date"));
            String logDat = sdf2.format(logDate);

            DowntimeLog dtl = new DowntimeLog();
            dtl.setSno(resultSet.getInt("SNo"));
            dtl.setLogDate((resultSet.getString("Log_Date")));
            dtl.setEnvironment(resultSet.getString("Environment"));
            dtl.setProjectName(resultSet.getString("Project_Name"));
            dtl.setProjectRelease(resultSet.getString("Project_Release"));
            dtl.setComponent(resultSet.getString("Component"));
            dtl.setDowntime(Double.parseDouble(resultSet
               .getString("down_time")));
            dtl.setReasonsIfKnown(resultSet.getString("Reasons_If_Known"));
            dtl.setProjectImpact(resultSet.getString("Project_Impact"));
            dtl.setMitigation(resultSet.getString("Mitigation"));
            dtl.setCostImpact(resultSet.getString("Cost_Impact"));
            dtl.setEffortLost(Double.parseDouble(resultSet
               .getString("Effort_Lost")));
            dtl.setUsername(resultSet.getString("username"));
            downTimeLogs.add(dtl);
         }
         modelAndView.addObject("lists", downTimeLogs);
         // CHECKSTYLE:ON
      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
      finally
      {
         resultSet.close();
         preparedStatement.close();
         mySqldbconnection.close();

      }
      return modelAndView;
   }

   /**
    * edit delete downtime log.
    *
    * @param request HttpRequest
    * @param response HttpResponse
    * @return modelAndView
    * @throws Exception Exception
    */
   @SuppressWarnings("resource")
   @RequestMapping("/editdowntimelog")
   public ModelAndView editDeleteDownTimeLog(HttpServletRequest request,
      HttpServletResponse response) throws Exception
   {
      // CHECKSTYLE:OFF
      if (request.getSession().getAttribute(
         TestManagementSessionKeys.USER_NAME) == null
         && request.getSession().getAttribute(
            TestManagementSessionKeys.PASS_WORD) == null)
      {
         return new ModelAndView("loginForm");
      }
      // CHECKSTYLE:ON
      ModelAndView modelAndView = null;
      String sNo = request.getParameter("SNo");
      String edit = request.getParameter(EDIT_BUTTON);
      String delete = request.getParameter(DELETE_BUTTON);
      String query = StringUtils.EMPTY;
      String userName =
         (String) request.getSession().getAttribute(
            TestManagementSessionKeys.USER_NAME);

      Connection mySqldbconnection = null;
      PreparedStatement preparedStatement = null;
      ResultSet resultSet = null;

      try
      {
         mySqldbconnection = DaoUtil.getMySqlDBConnection();
         if (edit != null && StringUtils.equalsIgnoreCase(edit, EDIT))
         {
            modelAndView = new ModelAndView("editDownTimeLog");
            query = "SELECT * FROM Downtime_Log WHERE SNo = " + sNo;
         }
         if (delete != null
            && StringUtils.equalsIgnoreCase(delete, ARCHIVE))
         {
            modelAndView = null;
            String selectQuery =
               "SELECT * FROM Downtime_Log WHERE SNo =  " + sNo;
            String deleteQuery =
               "DELETE FROM Downtime_Log WHERE SNo =  " + sNo;
            /*
             * introducing archiving, as requirement states to archive all
             * the records instead of deleting
             */
            String archiveQuery =
               "UPDATE Downtime_Log SET archive = 'yes' WHERE SNo =  "
                  + sNo;
            preparedStatement =
               mySqldbconnection.prepareStatement(selectQuery);
            preparedStatement.execute();
            resultSet = preparedStatement.getResultSet();
            while (resultSet.next())
            {
               // CHECKSTYLE:OFF
               String dbUserName = resultSet.getString("username").trim();
               if (userName != null
                  && dbUserName != null
                  && !StringUtils.equalsIgnoreCase(userName, dbUserName)
                  && !StringUtils.equalsIgnoreCase(userName,
                     getAdminUser()))
               {
                  modelAndView = new ModelAndView("accessDenied");
                  modelAndView.addObject("userName",
                     resultSet.getString("username").trim());
                  modelAndView.addObject("action", "downtimeloglist");
                  return modelAndView;
               }
               else
               {
                  preparedStatement =
                     mySqldbconnection.prepareStatement(archiveQuery);
                  preparedStatement.execute();
                  return getDownTimeLog(request, response);
               }
               // CHECKSTYLE:ON
            }
         }

         ArrayList<DowntimeLog> downTimeLogs =
            new ArrayList<DowntimeLog>();
         preparedStatement = mySqldbconnection.prepareStatement(query);
         preparedStatement.execute();
         resultSet = preparedStatement.getResultSet();
         while (resultSet.next())
         {
            Date logDate = new Date();
            SimpleDateFormat sdf2 = new SimpleDateFormat(JSP_DATE_FORMAT);
            String logDat = sdf2.format(logDate);

            DowntimeLog dtl = new DowntimeLog();
            dtl.setSno(resultSet.getInt("SNo"));
            dtl.setLogDate(logDat);
            dtl.setEnvironment(resultSet.getString("Environment"));
            dtl.setProjectName(resultSet.getString("Project_Name"));
            dtl.setProjectRelease(resultSet.getString("Project_Release"));
            dtl.setComponent(resultSet.getString("Component"));
            dtl.setDowntime(Double.parseDouble(resultSet
               .getString("down_time")));
            dtl.setReasonsIfKnown(resultSet.getString("Reasons_If_Known"));
            dtl.setProjectImpact(resultSet.getString("Project_Impact"));
            dtl.setMitigation(resultSet.getString("Mitigation"));
            dtl.setCostImpact(resultSet.getString("Cost_Impact"));
            dtl.setEffortLost(Double.parseDouble(resultSet
               .getString("Effort_Lost")));
            dtl.setUsername(resultSet.getString("username"));
            downTimeLogs.add(dtl);
            // CHECKSTYLE:OFF
            if (userName != null
               && dtl.getUsername() != null
               && !StringUtils.equalsIgnoreCase(userName, dtl
                  .getUsername().trim())
               && !StringUtils.equalsIgnoreCase(userName, getAdminUser()))
            {
               modelAndView = new ModelAndView("accessDenied");
               modelAndView
                  .addObject("userName", dtl.getUsername().trim());
               modelAndView.addObject("action", "downtimeloglist");
               return modelAndView;
            }
            // CHECKSTYLE:ON
         }
         resultSet.close();
         modelAndView.addObject("lists", downTimeLogs);

      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
      finally
      {

         preparedStatement.close();
         mySqldbconnection.close();
      }
      return modelAndView;
   }

   /**
    * update down time log.
    *
    * @param request HttpRequest
    * @param response HttpResponse
    * @return modelAndView
    * @throws Exception Exception
    */
   @RequestMapping("/updatedowntimelog")
   public ModelAndView updateDownTimeLog(HttpServletRequest request,
      HttpServletResponse response) throws Exception
   {

      String sNo = request.getParameter("SNo");
      String username =
         (String) request.getSession().getAttribute(
            TestManagementSessionKeys.USER_NAME);

      Connection mySqldbconnection = null;
      PreparedStatement preparedStatement = null;
      try
      {
         mySqldbconnection = DaoUtil.getMySqlDBConnection();
         String updateDownTimeLog =
            "UPDATE Downtime_Log SET Log_Date = ?,Environment = ?,"
               + "Project_Name = ?,Project_Release = ?,Component = ?,"
               + "down_time = ?,Reasons_If_Known = ?,Project_Impact = ?,"
               + "Mitigation = ?,Cost_Impact = ?,Effort_Lost = ?,username= ? WHERE SNo ="
               + sNo;
         preparedStatement =
            mySqldbconnection.prepareStatement(updateDownTimeLog);
         Date logDat = new Date();
         preparedStatement.setDate(Numbers.ONE,
            new java.sql.Date(logDat.getTime()));
         preparedStatement.setString(Numbers.TWO,
            request.getParameter("Environment"));
         preparedStatement.setString(Numbers.THREE,
            request.getParameter("Project"));
         preparedStatement.setString(Numbers.FOUR,
            request.getParameter("Release"));
         preparedStatement.setString(Numbers.FIVE,
            request.getParameter("Component"));
         preparedStatement.setDouble(Numbers.SIX,
            Double.parseDouble(request.getParameter("Downtime")));
         preparedStatement.setString(Numbers.SEVEN,
            request.getParameter("Reason"));
         preparedStatement.setString(Numbers.EIGHT,
            request.getParameter("Projectimpact"));
         preparedStatement.setString(Numbers.NINE,
            request.getParameter("Mitigation"));
         preparedStatement.setString(Numbers.TEN,
            request.getParameter("Costimpact"));
         preparedStatement.setString(Numbers.ELEVEN,
            request.getParameter("EffortLost"));
         preparedStatement.setString(Numbers.TWELVE, username);
         preparedStatement.execute();

      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
      finally
      {

         preparedStatement.close();
         mySqldbconnection.close();
      }
      return getDownTimeLog(request, response);
   }

   /**
    * gets the detailed test status.
    *
    * @param request HttpRequest
    * @param response HttpResponse
    * @return modelAndView
    * @throws Exception Exception
    */
   @RequestMapping("/detailedteststatuslist")
   public ModelAndView getDetailedTestStatus(HttpServletRequest request,
      HttpServletResponse response) throws Exception
   {

      ModelAndView modelAndView =
         new ModelAndView("viewdetailedteststatus");
      ArrayList<DetailedTestStatus> testStatusList =
         new ArrayList<DetailedTestStatus>();

      Connection mySqldbconnection = null;
      PreparedStatement preparedStatement = null;
      ResultSet resultset = null;

      try
      {
         mySqldbconnection = DaoUtil.getMySqlDBConnection();
         /*
          * change the following call, with a join as per the requirement
          * later... to get active projects only
          *
          * removeNonActiveTestStatus()
          */
         String getDetailedTestStatus =
            "SELECT * FROM Detailed_Test_Status WHERE NOT archive='yes'";
         preparedStatement =
            mySqldbconnection.prepareStatement(getDetailedTestStatus);
         preparedStatement.execute();
         resultset = preparedStatement.getResultSet();

         while (resultset.next())
         {

            DetailedTestStatus dts = new DetailedTestStatus();
            dts.setDate(resultset.getString(("last_updated")));
            dts.setRelease(resultset.getString("project_release"));
            dts.setProjectName(resultset.getString("project_name"));
            dts.setBpc(resultset.getString("bpc"));
            dts.setChannel(resultset.getString("channel"));
            dts.setCycle(resultset.getString("cycl"));
            dts.setPlanned(Double.parseDouble(resultset
               .getString("testcases_planned")));
            dts.setRun(Double.parseDouble(resultset
               .getString("testcases_run")));
            dts.setPass(Double.parseDouble(resultset
               .getString("testcases_pass")));
            dts.setFail(Double.parseDouble(resultset
               .getString("testcases_fail")));
            dts.setNotRun(Double.parseDouble(resultset
               .getString("testcases_not_run")));
            dts.setDeScoped(Double.parseDouble(resultset
               .getString("testcases_de_scoped")));
            dts.setBlocked(Double.parseDouble(resultset
               .getString("testcases_blocked")));
            dts.setPercentRun(Double.parseDouble(resultset
               .getString("run_percentage")));
            dts.setPercentNotRun(Double.parseDouble(resultset
               .getString("not_run_percentage")));
            dts.setPercentBlocked(Double.parseDouble(resultset
               .getString("blocked_percentage")));
            dts.setPercentPass(Double.parseDouble(resultset
               .getString("pass_percentage")));
            dts.setPercentFail(Double.parseDouble(resultset
               .getString("fail_percentage")));
            dts.setRag(resultset.getString("rag"));
            dts.setUsername(resultset.getString("username"));
            dts.setSno(resultset.getInt("SNo"));
            dts.setPreExe(resultset.getString("pre_exe"));

            SimpleDateFormat sdf1 = new SimpleDateFormat(DB_DATE_FORMAT);
            Date startDate =
               sdf1.parse(resultset.getString("start_date").toString());
            Date endDate = sdf1.parse(resultset.getString("end_date"));
            SimpleDateFormat sdf2 = new SimpleDateFormat(JSP_DATE_FORMAT);
            String startDat = sdf2.format(startDate);
            String endDat = sdf2.format(endDate);

            dts.setStartDate(startDat);
            dts.setEndDate(endDat);
            testStatusList.add(dts);
         }

         /**
          * remove the following method and change the procedure above
          * with. a join for the requirement
          */
         removeNonActiveTestStatus(testStatusList);
         modelAndView.addObject("lists", testStatusList);

      }
      catch (SQLException e)
      {

         e.printStackTrace();
      }
      finally
      {

         resultset.close();
         preparedStatement.close();
         mySqldbconnection.close();
      }

      return modelAndView;
   }

   /**
    * edit delete update test status.
    *
    * @param request HttpRequest
    * @param response HttpResponse
    * @return modelAndView
    * @throws Exception Exception
    */
   @SuppressWarnings("resource")
   @RequestMapping("/editteststatus")
   public ModelAndView editDeleteTestStatus(HttpServletRequest request,
      HttpServletResponse response) throws Exception
   {

      String userName =
         (String) request.getSession().getAttribute(
            TestManagementSessionKeys.USER_NAME);

      if (userName == null
         && request.getSession().getAttribute(
            TestManagementSessionKeys.PASS_WORD) == null)
      {
         return new ModelAndView("loginForm");
      }

      String sNo = request.getParameter("SNo");
      String edit = request.getParameter(EDIT_BUTTON);
      String delete = request.getParameter(DELETE_BUTTON);
      ModelAndView modelAndView = null;
      String query = StringUtils.EMPTY;

      Connection mySqldbconnection = null;
      ResultSet resultset = null;
      PreparedStatement preparedStatement = null;
      ArrayList<DetailedTestStatus> testStatusList =
         new ArrayList<DetailedTestStatus>();

      try
      {

         mySqldbconnection = DaoUtil.getMySqlDBConnection();
         if (edit != null && StringUtils.equalsIgnoreCase(edit, EDIT))
         {
            modelAndView = new ModelAndView("editteststatus");
            query =
               "SELECT * FROM Detailed_Test_Status WHERE SNo = " + sNo;
         }
         if (delete != null
            && StringUtils.equalsIgnoreCase(delete, ARCHIVE))
         {

            String selectQuery =
               "SELECT * FROM Detailed_Test_Status WHERE SNo = " + sNo;
            String deleteQuery =
               "DELETE FROM Detailed_Test_Status WHERE SNo =  " + sNo;
            String archiveQuery =
               "UPDATE Detailed_Test_Status SET archive='yes' WHERE SNo =  "
                  + sNo;
            modelAndView = null;
            preparedStatement =
               mySqldbconnection.prepareStatement(selectQuery);
            preparedStatement.execute();
            resultset = preparedStatement.getResultSet();
            while (resultset.next())
            {
               // CHECKSTYLE:OFF
               String dbUserName = resultset.getString("username").trim();
               if (userName != null
                  && dbUserName != null
                  && !StringUtils.equalsIgnoreCase(userName, dbUserName)
                  && !StringUtils.equalsIgnoreCase(userName,
                     getAdminUser()))
               {
                  modelAndView = new ModelAndView("accessDenied");
                  modelAndView.addObject("userName",
                     resultset.getString("username").trim());
                  modelAndView.addObject("action",
                     "detailedteststatuslist");
                  return modelAndView;
               }
               else
               {
                  preparedStatement =
                     mySqldbconnection.prepareStatement(archiveQuery);
                  preparedStatement.execute();
                  return getDetailedTestStatus(request, response);
               }
               // CHECKSTYLE:ON
            }
         }
         preparedStatement = mySqldbconnection.prepareStatement(query);
         preparedStatement.execute();
         resultset = preparedStatement.getResultSet();
         while (resultset.next())
         {

            DetailedTestStatus dts = new DetailedTestStatus();
            dts.setDate(resultset.getString("last_updated"));
            dts.setRelease(resultset.getString("project_release"));
            dts.setProjectName(resultset.getString("project_name"));
            dts.setBpc(resultset.getString("bpc"));
            dts.setChannel(resultset.getString("channel"));
            dts.setCycle(resultset.getString("cycl"));
            dts.setPlanned(Double.parseDouble(resultset
               .getString("testcases_planned")));
            dts.setRun(Double.parseDouble(resultset
               .getString("testcases_run")));
            dts.setPass(Double.parseDouble(resultset
               .getString("testcases_pass")));
            dts.setFail(Double.parseDouble(resultset.getString(
               "testcases_fail").replace(".0", " ")));
            dts.setNotRun(Double.parseDouble(resultset
               .getString("testcases_not_run")));
            dts.setDeScoped(Double.parseDouble(resultset
               .getString("testcases_de_scoped")));
            dts.setBlocked(Double.parseDouble(resultset
               .getString("testcases_blocked")));
            dts.setPercentRun(Double.parseDouble(resultset
               .getString("run_percentage")));
            dts.setPercentNotRun(Double.parseDouble(resultset
               .getString("not_run_percentage")));
            dts.setPercentBlocked(Double.parseDouble(resultset
               .getString("blocked_percentage")));
            dts.setPercentPass(Double.parseDouble(resultset
               .getString("pass_percentage")));
            dts.setPercentFail(Double.parseDouble(resultset
               .getString("fail_percentage")));
            dts.setRag(resultset.getString("rag"));
            dts.setUsername(resultset.getString("username"));
            dts.setSno(resultset.getInt("SNo"));
            dts.setPreExe(resultset.getString("pre_exe"));

            SimpleDateFormat sdf1 = new SimpleDateFormat(DB_DATE_FORMAT);
            Date startDat = sdf1.parse(resultset.getString("start_date"));
            Date endDate = sdf1.parse(resultset.getString("end_date"));
            SimpleDateFormat sdf2 = new SimpleDateFormat(JSP_DATE_FORMAT);
            String startdate = sdf2.format(startDat);
            String enddate = sdf2.format(endDate);

            dts.setStartDate(startdate);
            dts.setEndDate(enddate);
            testStatusList.add(dts);
            // CHECKSTYLE:OFF
            if (userName != null
               && dts.getUsername() != null
               && !StringUtils.equalsIgnoreCase(userName, dts
                  .getUsername().trim())
               && !StringUtils.equalsIgnoreCase(userName, getAdminUser()))
            {
               modelAndView = new ModelAndView("accessDenied");
               modelAndView
                  .addObject("userName", dts.getUsername().trim());
               modelAndView.addObject("action", "detailedteststatuslist");
               return modelAndView;
            }
            // CHECKSTYLE:ON
         }
         resultset.close();
         modelAndView.addObject("lists", testStatusList);
      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
      finally
      {
         preparedStatement.close();
         mySqldbconnection.close();
      }

      return modelAndView;
   }

   /**
    * update delete Test Status.
    *
    * @param request HttpRequest
    * @param response HttpResponse
    * @return modelAndView
    * @throws Exception Exception
    */
   @RequestMapping("/updateteststatus")
   public ModelAndView updateTestStatus(HttpServletRequest request,
      HttpServletResponse response) throws Exception
   {

      String sNo = request.getParameter("SNo");
      String username =
         (String) request.getSession().getAttribute(
            TestManagementSessionKeys.USER_NAME);

      Connection mySqldbconnection = null;
      PreparedStatement preparedStatement = null;
      try
      {
         mySqldbconnection = DaoUtil.getMySqlDBConnection();

         String updateTableSQL =
            "UPDATE Detailed_Test_Status SET last_updated = ?"
               + ",project_release = ?,project_name = ?,bpc = ?,channel = ?,cycl = ?,"
               + "testcases_planned = ?,"
               + "testcases_run = ?,testcases_pass = ?,testcases_fail = ?,testcases_not_run = ?"
               + ",testcases_de_scoped = ?,testcases_blocked = ?,run_percentage = ?,"
               + "not_run_percentage = ?,blocked_percentage = ?,pass_percentage ="
               + " ?,fail_percentage = ?,rag = ?,username = ?,pre_exe = ?,"
               + "start_date = ?,end_date = ? WHERE SNo = " + sNo;
         java.sql.Date date = new java.sql.Date(new Date().getTime());
         java.sql.Date sDate = new java.sql.Date(date.getTime());
         preparedStatement =
            mySqldbconnection.prepareStatement(updateTableSQL);
         preparedStatement.setDate(Numbers.ONE, sDate);
         preparedStatement.setString(Numbers.TWO,
            request.getParameter("Release"));
         preparedStatement.setString(Numbers.THREE,
            request.getParameter("ProjectName"));
         preparedStatement.setString(Numbers.FOUR,
            request.getParameter("BPC"));
         preparedStatement.setString(Numbers.FIVE,
            request.getParameter("Channel"));
         preparedStatement.setString(Numbers.SIX,
            request.getParameter("Cycle"));
         preparedStatement.setDouble(Numbers.SEVEN,
            Double.parseDouble(request.getParameter("Planned")));
         preparedStatement.setDouble(Numbers.EIGHT,
            Double.parseDouble(request.getParameter("Run")));
         preparedStatement.setDouble(Numbers.NINE,
            Double.parseDouble(request.getParameter("Pass")));
         preparedStatement.setDouble(Numbers.TEN,
            Double.parseDouble(request.getParameter("fail")));
         preparedStatement.setDouble(Numbers.ELEVEN,
            Double.parseDouble(request.getParameter("Notrun")));
         preparedStatement.setDouble(Numbers.TWELVE,
            Double.parseDouble(request.getParameter("Descoped")));
         preparedStatement.setDouble(Numbers.THIRTEEN,
            Double.parseDouble(request.getParameter("Blocked")));
         preparedStatement.setDouble(Numbers.FOURTEEN,
            Double.parseDouble(request.getParameter("PerRun")));
         preparedStatement.setDouble(Numbers.FIFTEEN,
            Double.parseDouble(request.getParameter("PerNotRun")));
         preparedStatement.setDouble(Numbers.SIXTEEN,
            Double.parseDouble(request.getParameter("PerBlock")));
         preparedStatement.setDouble(Numbers.SEVENTEEN,
            Double.parseDouble(request.getParameter("PerPass")));
         preparedStatement.setDouble(Numbers.EIGHTEEN,
            Double.parseDouble(request.getParameter("PerFail")));
         preparedStatement.setString(Numbers.NINETEEN,
            request.getParameter("RAG"));
         preparedStatement.setString(Numbers.TWENTY, username);
         preparedStatement.setString(Numbers.TWENTYONE,
            request.getParameter("PreExe"));
         preparedStatement.setDate(Numbers.TWENTYTWO, new java.sql.Date(
            new Date(request.getParameter("FromDate")).getTime()));
         preparedStatement.setDate(Numbers.TWENTYTHREE, new java.sql.Date(
            new Date(request.getParameter("ToDate")).getTime()));

         // execute insert SQL stetement
         preparedStatement.executeUpdate();
      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
      finally
      {
         preparedStatement.close();
         mySqldbconnection.close();
      }

      return getDetailedTestStatus(request, response);

   }

   /**
    * gets test risk and issues.
    *
    * @param request HttpRequest
    * @param response HttpResponse
    * @return modelAndView
    * @throws Exception Exception
    */
   @RequestMapping("/testrisksandissueslist")
   public ModelAndView getTestRisksAndIssues(HttpServletRequest request,
      HttpServletResponse response) throws Exception
   {

      ModelAndView modelAndView = new ModelAndView("viewtestriskissues");
      ArrayList<TestRisksAndIssues> testRiskIssuesList =
         new ArrayList<TestRisksAndIssues>();

      Connection mySqldbconnection = null;
      PreparedStatement preparedStatement = null;
      ResultSet resultSet = null;
      try
      {
         mySqldbconnection = DaoUtil.getMySqlDBConnection();
         String getTestRiskIssuesQuery =
            "SELECT * FROM TestRisk_and_Issues WHERE NOT archive='yes'";
         preparedStatement =
            mySqldbconnection.prepareStatement(getTestRiskIssuesQuery);
         preparedStatement.execute();

         resultSet = preparedStatement.getResultSet();
         while (resultSet.next())
         {
            TestRisksAndIssues trI = new TestRisksAndIssues();
            trI.setReleaseName(resultSet.getString("Release_Name"));
            trI.setProjectName(resultSet.getString("Project_Name"));
            trI.setRidiscription(resultSet
               .getString("Risk_Issue_Description"));
            trI.setImpact(resultSet.getString("Impact"));
            trI.setOwner(resultSet.getString("Owner"));
            trI.setRistatus(resultSet.getString("Risk_Issue_Status"));
            trI.setResolutionDate((resultSet
               .getString("Resolution_Target_Date")));
            trI.setResolvedDate((resultSet.getString("Resolved_Date")));
            trI.setSno(resultSet.getInt("SNo"));
            trI.setUsername(resultSet.getString("username"));
            trI.setLogDate((resultSet.getString("Log_Date")));
            trI.setRimstrategy(resultSet
               .getString("Risk_Issue_M_Strategy"));
            trI.setArchive(resultSet.getString("archive"));
            trI.setRiskOrIssue(resultSet.getString("riskorissue"));
            trI.setPriority(resultSet.getString("priority"));
            trI.setSeverity(resultSet.getString("severity"));
            testRiskIssuesList.add(trI);

         }
         modelAndView.addObject("riskIssues", testRiskIssuesList);
      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
      finally
      {
         resultSet.close();
         preparedStatement.close();
         mySqldbconnection.close();
      }
      return modelAndView;
   }

   /**
    * edli delete Rish Issues.
    *
    * @param request HttpRequest
    * @param response HttpResponse
    * @return modelAndView
    * @throws Exception Exception
    */
   @SuppressWarnings("resource")
   @RequestMapping("/edittestrisksandissues")
   public ModelAndView editDeleteRiskIssues(HttpServletRequest request,
      HttpServletResponse response) throws Exception
   {

      if (request.getSession().getAttribute(
         TestManagementSessionKeys.USER_NAME) == null
         && request.getSession().getAttribute(
            TestManagementSessionKeys.PASS_WORD) == null)
      {
         return new ModelAndView("loginForm");
      }

      String sNo = request.getParameter("SNo");
      String edit = request.getParameter(EDIT_BUTTON);
      String delete = request.getParameter(DELETE_BUTTON);
      String query = StringUtils.EMPTY;
      String userName =
         (String) request.getSession().getAttribute(
            TestManagementSessionKeys.USER_NAME);

      ModelAndView modelAndView = null;
      Connection mySqldbconnection = null;
      ResultSet resultSet = null;
      PreparedStatement preparedStatement = null;
      ArrayList<TestRisksAndIssues> testRiskIssuesList =
         new ArrayList<TestRisksAndIssues>();

      try
      {
         mySqldbconnection = DaoUtil.getMySqlDBConnection();
         if (edit != null && StringUtils.equalsIgnoreCase(edit, EDIT))
         {
            modelAndView = new ModelAndView("edittestriskissue");
            query = "SELECT * FROM TestRisk_and_Issues WHERE SNo = " + sNo;
         }
         if (delete != null
            && StringUtils.equalsIgnoreCase(delete, ARCHIVE))
         {
            modelAndView = null;
            String selectQuery =
               "SELECT * FROM TestRisk_and_Issues WHERE SNo =  " + sNo;
            String deleteQuery =
               "DELETE FROM TestRisk_and_Issues WHERE SNo =  " + sNo;
            String archiveQuery =
               "UPDATE TestRisk_and_Issues SET archive='yes' WHERE SNo =  "
                  + sNo;
            preparedStatement =
               mySqldbconnection.prepareStatement(selectQuery);
            preparedStatement.execute();
            resultSet = preparedStatement.getResultSet();
            while (resultSet.next())
            {
               // CHECKSTYLE:OFF
               String dbUserName = resultSet.getString("username").trim();
               if (userName != null
                  && dbUserName != null
                  && !StringUtils.equalsIgnoreCase(userName, dbUserName)
                  && !StringUtils.equalsIgnoreCase(userName,
                     getAdminUser()))
               {
                  modelAndView = new ModelAndView("accessDenied");
                  modelAndView.addObject("userName",
                     resultSet.getString("username").trim());
                  modelAndView.addObject("action",
                     "testrisksandissueslist");
                  return modelAndView;
               }
               else
               {
                  preparedStatement =
                     mySqldbconnection.prepareStatement(archiveQuery);
                  preparedStatement.execute();
                  return getTestRisksAndIssues(request, response);
               }
               // CHECKSTYLE:ON
            }
         }

         preparedStatement = mySqldbconnection.prepareStatement(query);
         preparedStatement.execute();
         resultSet = preparedStatement.getResultSet();
         while (resultSet.next())
         {
            TestRisksAndIssues trI = new TestRisksAndIssues();
            trI.setReleaseName(resultSet.getString("Release_Name"));
            trI.setProjectName(resultSet.getString("Project_Name"));
            trI.setRidiscription(resultSet
               .getString("Risk_Issue_Description"));
            trI.setImpact(resultSet.getString("Impact"));
            trI.setOwner(resultSet.getString("Owner"));
            trI.setRistatus(resultSet.getString("Risk_Issue_Status"));
            SimpleDateFormat sdf1 = new SimpleDateFormat(DB_DATE_FORMAT);
            Date resolutionDate =
               sdf1.parse(resultSet.getString("Resolution_Target_Date"));
            Date resolvedDate =
               sdf1.parse(resultSet.getString("Resolved_Date"));
            SimpleDateFormat sdf2 = new SimpleDateFormat(JSP_DATE_FORMAT);
            String resolutionDat = sdf2.format(resolutionDate);
            String resolvedDat = sdf2.format(resolvedDate);
            trI.setResolutionDate(resolutionDat);
            trI.setResolvedDate(resolvedDat);
            trI.setSno(resultSet.getInt("SNo"));
            trI.setUsername(resultSet.getString("username"));
            trI.setLogDate((resultSet.getString("Log_Date")));
            trI.setRimstrategy(resultSet
               .getString("Risk_Issue_M_Strategy"));
            trI.setArchive(resultSet.getString("archive"));
            trI.setRiskOrIssue(resultSet.getString("riskorissue"));
            trI.setPriority(resultSet.getString("priority"));
            trI.setSeverity(resultSet.getString("severity"));
            testRiskIssuesList.add(trI);
            // CHECKSTYLE:OFF
            if (userName != null
               && trI.getUsername() != null
               && !StringUtils.equalsIgnoreCase(userName, trI
                  .getUsername().trim())
               && !StringUtils.equalsIgnoreCase(userName, getAdminUser()))
            {
               modelAndView = new ModelAndView("accessDenied");
               modelAndView
                  .addObject("userName", trI.getUsername().trim());
               modelAndView.addObject("action", "testrisksandissueslist");
               return modelAndView;
            }
            // CHECKSTYLE:ON
         }
         resultSet.close();
         modelAndView.addObject("lists", testRiskIssuesList);

      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
      finally
      {
         preparedStatement.close();
         mySqldbconnection.close();
      }
      return modelAndView;

   }

   /**
    * update risk issues.
    *
    * @param request HttpRequest
    * @param response HttpResponse
    * @return modelAndView
    * @throws Exception Exception
    */
   @RequestMapping("/updateriskissues")
   public ModelAndView updateRiskIssues(HttpServletRequest request,
      HttpServletResponse response) throws Exception
   {
      String sNo = request.getParameter("SNo");
      String username =
         (String) request.getSession().getAttribute(
            TestManagementSessionKeys.USER_NAME);

      Connection mySqldbconnection = null;
      PreparedStatement preparedStatement = null;

      try
      {
         mySqldbconnection = DaoUtil.getMySqlDBConnection();

         String updateTableSQL =
            "UPDATE TestRisk_and_Issues SET Release_Name = ?,"
               + "Project_Name = ?,Risk_Issue_Description = ?"
               + ",Impact = ?,Owner = ?,Risk_Issue_Status = ?,Resolution_Target_Date = ?,"
               + "Resolved_Date = ?, username=?, Log_Date= ?, Risk_Issue_M_Strategy=? ,"
               + "archive=?, riskorissue=? , priority=?, severity=? "
               + "WHERE SNo= " + sNo;
         preparedStatement =
            mySqldbconnection.prepareStatement(updateTableSQL);
         Date resolutionDate =
            new Date(request.getParameter("ResolutionDate"));
         Date resolvedDate =
            new Date(request.getParameter("ResolvedDate"));
         java.sql.Date logDate = new java.sql.Date(new Date().getTime());
         preparedStatement.setString(Numbers.ONE,
            request.getParameter("ReleaseName"));
         preparedStatement.setString(Numbers.TWO,
            request.getParameter("ProjectName"));
         preparedStatement.setString(Numbers.THREE,
            request.getParameter("RIDiscription"));
         preparedStatement.setString(Numbers.FOUR,
            request.getParameter("Impact"));
         preparedStatement.setString(Numbers.FIVE,
            request.getParameter("Owner"));
         preparedStatement.setString(Numbers.SIX,
            request.getParameter("RIStatus"));
         preparedStatement.setDate(Numbers.SEVEN, new java.sql.Date(
            resolutionDate.getTime()));
         preparedStatement.setDate(Numbers.EIGHT, new java.sql.Date(
            resolvedDate.getTime()));
         preparedStatement.setString(Numbers.NINE, username);
         preparedStatement.setDate(Numbers.TEN,
            new java.sql.Date(logDate.getTime()));
         preparedStatement.setString(Numbers.ELEVEN,
            request.getParameter("RIMStrategy"));
         preparedStatement.setString(Numbers.TWELVE, "no");
         preparedStatement.setString(Numbers.THIRTEEN,
            request.getParameter("RiskOrIssue"));
         preparedStatement.setString(Numbers.FOURTEEN,
            request.getParameter("Priority"));
         preparedStatement.setString(Numbers.FIFTEEN,
            request.getParameter("Severity"));
         preparedStatement.executeUpdate();

      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
      finally
      {
         preparedStatement.close();
         mySqldbconnection.close();
      }
      return getTestRisksAndIssues(request, response);
   }

   /**
    * gets test risk and issues.
    *
    * @param request HttpRequest
    * @param response HttpResponse
    * @return modelAndView
    * @throws Exception Exception
    */
   @RequestMapping("/masterprojectsreleaselist")
   public ModelAndView getMasterProjectsRelease(
      HttpServletRequest request, HttpServletResponse response)
      throws Exception
   {

      ModelAndView modelAndView =
         new ModelAndView("viewmasterprojectsreleases");
      ArrayList<MasterProjectsRelease> projectsReleaseList =
         new ArrayList<MasterProjectsRelease>();

      Connection mySqldbconnection = null;
      PreparedStatement preparedStatement = null;
      ResultSet resultSet = null;
      try
      {
         mySqldbconnection = DaoUtil.getMySqlDBConnection();
         String getMasterProjectsReleaseQuery =
            "SELECT * FROM MasterProjectsRelease WHERE NOT archive ='yes'";
         preparedStatement =
            mySqldbconnection
               .prepareStatement(getMasterProjectsReleaseQuery);
         preparedStatement.execute();

         resultSet = preparedStatement.getResultSet();
         while (resultSet.next())
         {
            MasterProjectsRelease mpr = new MasterProjectsRelease();
            // CHECKSTYLE:OFF
            mpr.setProjectName(resultSet.getString("projectName"));
            mpr.setReleaseName(resultSet.getString("releaseName"));
            // CHECKSTYLE:ON
            /*
             * used an enum to sort through the records and following code
             * to set the value of status as an enum
             */
            String status =
               StringUtils.remove(resultSet.getString("status"), " ");
            mpr.setStatus(MasterProjectReleaseOrderEnum.valueOf(status));
            mpr.setUsername(resultSet.getString("username"));
            mpr.setLastUpdated(resultSet.getString("lastUpdated"));
            mpr.setSno(Integer.parseInt(resultSet.getString("sNo")));
            projectsReleaseList.add(mpr);

         }

         Collections.sort(projectsReleaseList,
            new ProjectReleaseComparator());

         modelAndView.addObject("masterlists", projectsReleaseList);
      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
      finally
      {
         resultSet.close();
         preparedStatement.close();
         mySqldbconnection.close();
      }
      return modelAndView;
   }

   /**
    * edli delete Rish Issues.
    *
    * @param request HttpRequest
    * @param response HttpResponse
    * @return modelAndView
    * @throws Exception Exception
    */
   @SuppressWarnings("resource")
   @RequestMapping("/editmasterprojectsrelease")
   public ModelAndView editDeleteMasterProjectsRelease(
      HttpServletRequest request, HttpServletResponse response)
      throws Exception
   {

      if (request.getSession().getAttribute(
         TestManagementSessionKeys.USER_NAME) == null
         && request.getSession().getAttribute(
            TestManagementSessionKeys.PASS_WORD) == null)
      {
         return new ModelAndView("loginForm");
      }

      String sNo = request.getParameter("SNo");
      String edit = request.getParameter(EDIT_BUTTON);
      String delete = request.getParameter(DELETE_BUTTON);
      String query = StringUtils.EMPTY;
      String userName =
         (String) request.getSession().getAttribute(
            TestManagementSessionKeys.USER_NAME);

      ModelAndView modelAndView = null;
      Connection mySqldbconnection = null;
      ResultSet resultSet = null;
      PreparedStatement preparedStatement = null;
      ArrayList<MasterProjectsRelease> projectsReleaseList =
         new ArrayList<MasterProjectsRelease>();

      try
      {
         mySqldbconnection = DaoUtil.getMySqlDBConnection();
         if (edit != null && StringUtils.equalsIgnoreCase(edit, EDIT))
         {
            modelAndView = new ModelAndView("editmasterprojectsrelease");
            query =
               "SELECT * FROM MasterProjectsRelease WHERE sNo = " + sNo;
         }
         if (delete != null
            && StringUtils.equalsIgnoreCase(delete, ARCHIVE))
         {
            modelAndView = null;
            String selectQuery =
               "SELECT * FROM MasterProjectsRelease WHERE sNo =  " + sNo;
            String deleteQuery =
               "DELETE FROM MasterProjectsRelease WHERE sNo =  " + sNo;
            String archiveQuery =
               "UPDATE MasterProjectsRelease SET archive='yes' WHERE sNo =  "
                  + sNo;
            preparedStatement =
               mySqldbconnection.prepareStatement(selectQuery);
            preparedStatement.execute();
            resultSet = preparedStatement.getResultSet();
            while (resultSet.next())
            {
               // CHECKSTYLE:OFF
               String dbUserName = resultSet.getString("username").trim();
               if (userName != null
                  && dbUserName != null
                  && !StringUtils.equalsIgnoreCase(userName, dbUserName)
                  && !StringUtils.equalsIgnoreCase(userName,
                     getAdminUser()))
               {
                  modelAndView = new ModelAndView("accessDenied");
                  modelAndView.addObject("userName",
                     resultSet.getString("username").trim());
                  modelAndView.addObject("action",
                     "masterprojectsreleaselist");

                  return modelAndView;
               }
               else
               {
                  preparedStatement =
                     mySqldbconnection.prepareStatement(archiveQuery);
                  preparedStatement.execute();
                  return getMasterProjectsRelease(request, response);
               }
               // CHECKSTYLE:ON
            }
         }

         preparedStatement = mySqldbconnection.prepareStatement(query);
         preparedStatement.execute();
         resultSet = preparedStatement.getResultSet();
         while (resultSet.next())
         {
            MasterProjectsRelease mpr = new MasterProjectsRelease();
            mpr.setProjectName(resultSet.getString("projectName"));
            mpr.setReleaseName(resultSet.getString("releaseName"));
            String status =
               StringUtils.remove(resultSet.getString("status"), " ");
            mpr.setStatus(MasterProjectReleaseOrderEnum.valueOf(status));
            mpr.setUsername(resultSet.getString("username"));
            mpr.setLastUpdated(resultSet.getString("lastUpdated"));
            mpr.setSno(Integer.parseInt(resultSet.getString("sNo")));
            projectsReleaseList.add(mpr);
            // CHECKSTYLE:OFF
            if (userName != null
               && mpr.getUsername() != null
               && !StringUtils.equalsIgnoreCase(userName, mpr
                  .getUsername().trim())
               && !StringUtils.equalsIgnoreCase(userName, getAdminUser()))
            {
               modelAndView = new ModelAndView("accessDenied");
               modelAndView
                  .addObject("userName", mpr.getUsername().trim());
               modelAndView.addObject("action",
                  "masterprojectsreleaselist");
               return modelAndView;
            }
            // CHECKSTYLE:ON
         }
         resultSet.close();
         modelAndView.addObject("masterlists", projectsReleaseList);

      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
      finally
      {
         preparedStatement.close();
         mySqldbconnection.close();
      }
      return modelAndView;

   }

   /**
    * update risk issues.
    *
    * @param request HttpRequest
    * @param response HttpResponse
    * @return modelAndView
    * @throws Exception Exception
    */
   @RequestMapping("/updatemasterprojectsrelease")
   public ModelAndView updateMasterProjectsRelease(
      HttpServletRequest request, HttpServletResponse response)
      throws Exception
   {
      String sNo = request.getParameter("SNo");
      String username =
         (String) request.getSession().getAttribute(
            TestManagementSessionKeys.USER_NAME);

      Connection mySqldbconnection = null;
      PreparedStatement preparedStatement = null;

      try
      {
         mySqldbconnection = DaoUtil.getMySqlDBConnection();
         String updateTableSQL =
            "UPDATE MasterProjectsRelease SET projectName = ?,"
               + "releaseName = ?,status = ?"
               + ",username = ?,lastUpdated = ? WHERE sNo=" + sNo;
         preparedStatement =
            mySqldbconnection.prepareStatement(updateTableSQL);
         java.sql.Date logDate = new java.sql.Date(new Date().getTime());
         preparedStatement.setString(Numbers.ONE,
            request.getParameter("projectName"));
         preparedStatement.setString(Numbers.TWO,
            request.getParameter("releaseName"));
         preparedStatement.setString(Numbers.THREE,
            request.getParameter("status"));
         preparedStatement.setString(Numbers.FOUR, username);
         preparedStatement.setDate(Numbers.FIVE,
            new java.sql.Date(logDate.getTime()));
         preparedStatement.executeUpdate();

      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
      finally
      {
         preparedStatement.close();
         mySqldbconnection.close();
      }
      return getMasterProjectsRelease(request, response);
   }

   /**
    * method to remove non-active(Closed) test status.
    *
    * @param testStatusList teststatus list
    * @throws Exception SQL Exception
    */
   private void removeNonActiveTestStatus(
      ArrayList<DetailedTestStatus> testStatusList) throws Exception
   {

      /*
       * status of a detailed test status has to be taken from
       * MasterProjectsRelease status field, performing a db call and
       * comparing a release and project name and if it matched then
       * removing the record from test status
       */
      Connection mySqldbconnection = null;
      PreparedStatement preparedStatement = null;
      ResultSet resultSet = null;
      try
      {
         mySqldbconnection = DaoUtil.getMySqlDBConnection();
         String getMasterProjectsReleaseQuery =
            "SELECT * FROM MasterProjectsRelease WHERE status = 'On hold' "
               + "OR status = 'De scoped' OR status = 'Closed'";
         preparedStatement =
            mySqldbconnection
               .prepareStatement(getMasterProjectsReleaseQuery);
         preparedStatement.execute();
         resultSet = preparedStatement.getResultSet();
         while (resultSet.next())
         {
            java.util.Iterator<DetailedTestStatus> statusList =
               testStatusList.iterator();
            while (statusList.hasNext())
            {
               DetailedTestStatus testStatus =
                  (DetailedTestStatus) statusList.next();
               if (StringUtils.equalsIgnoreCase(
                  testStatus.getProjectName(),
                  resultSet.getString("projectName"))
                  && StringUtils.equalsIgnoreCase(testStatus.getRelease(),
                     resultSet.getString("releaseName")))
               {
                  statusList.remove();
               }
            }
         }
      }
      catch (SQLException e)
      {

         e.printStackTrace();
      }
      finally
      {
         resultSet.close();
         preparedStatement.close();
         mySqldbconnection.close();
      }

   }

   /**
    * gets admin_user key from properties file.
    *
    * @return admin user name
    */
   private String getAdminUser()
   {
      return DaoUtil.getProperties().getProperty(ADMIN_USER);

   }

}
