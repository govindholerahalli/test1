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
 * $RCSfile: HomePageController.java$
 *
 * $Revision: $
 *
 * $Date: Nov 24, 2015$
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
import com.sonata.tm.helper.ProjectReleaseComparator;
import com.sonata.tm.model.DetailedTestStatus;
import com.sonata.tm.model.DowntimeLog;
import com.sonata.tm.model.MasterProjectReleaseOrderEnum;
import com.sonata.tm.model.MasterProjectsRelease;
import com.sonata.tm.model.TestRisksAndIssues;

/**
 * Class to return home page. This class returns the views for records in
 * the home page. Further this can be modified by using an
 * interface/abstract class which allows the record to be marked editable
 * and deletable and thus few jsps can be removed by using if the record is
 * editable then allow use to edit or if delete is enabled allow user to
 * delete.
 *
 * For further information see the class <>
 *
 * @author kirankumar.kadapa
 *
 */
@Controller
public class HomePageController
{

   /**
    * gets down time logs.
    *
    * @param request HttpRequest
    * @param response HttpResponse
    * @return modelAndView
    * @throws Exception Exception
    */
   @RequestMapping("/viewonlydowntimeloglist")
   public ModelAndView getViewOnlyDownTimeLog(HttpServletRequest request,
      HttpServletResponse response) throws Exception
   {

      ModelAndView modelAndView = new ModelAndView("viewonlydowntimelogs");
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
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-mm-dd");
            SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MMM-yyyy");

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
            // CHECKSTYLE:OFF
            dtl.setUsername(resultSet.getString("username"));
            // CHECKSTYLE:ON
            downTimeLogs.add(dtl);
         }
         modelAndView.addObject("lists", downTimeLogs);
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
    * gets the detailed test status.
    *
    * @param request HttpRequest
    * @param response HttpResponse
    * @return modelAndView
    * @throws Exception Exception
    */
   @RequestMapping("/viewonlydetailedteststatuslist")
   public ModelAndView getViewOnlyDetailedTestStatus(
      HttpServletRequest request, HttpServletResponse response)
      throws Exception
   {

      ModelAndView modelAndView =
         new ModelAndView("viewonlydetailedteststatus");
      ArrayList<DetailedTestStatus> testStatusList =
         new ArrayList<DetailedTestStatus>();

      Connection mySqldbconnection = null;
      PreparedStatement preparedStatement = null;
      ResultSet resultset = null;

      try
      {
         mySqldbconnection = DaoUtil.getMySqlDBConnection();
         String getDetailedTestStatus =
            "SELECT * FROM Detailed_Test_Status WHERE NOT archive ='yes'";
         preparedStatement =
            mySqldbconnection.prepareStatement(getDetailedTestStatus);
         preparedStatement.execute();
         resultset = preparedStatement.getResultSet();

         while (resultset.next())
         {

            DetailedTestStatus dts = new DetailedTestStatus();
            // dts.setDate(StringUtils.EMPTY);
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

            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-mm-dd");
            Date startDate = sdf1.parse(resultset.getString("start_date"));
            Date endDate = sdf1.parse(resultset.getString("end_date"));
            SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MMM-yyyy");
            String startDat = sdf2.format(startDate);
            String endDat = sdf2.format(endDate);

            dts.setStartDate(startDat);
            dts.setEndDate(endDat);
            testStatusList.add(dts);
         }

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
    * gets test risk and issues.
    *
    * @param request HttpRequest
    * @param response HttpResponse
    * @return modelAndView
    * @throws Exception Exception
    */
   @RequestMapping("/viewonlytestrisksandissueslist")
   public ModelAndView getViewOnlyTestRisksAndIssues(
      HttpServletRequest request, HttpServletResponse response)
      throws Exception
   {

      ModelAndView modelAndView =
         new ModelAndView("viewonlytestriskissues");
      ArrayList<TestRisksAndIssues> testRiskIssuesList =
         new ArrayList<TestRisksAndIssues>();

      Connection mySqldbconnection = null;
      PreparedStatement preparedStatement = null;
      ResultSet resultSet = null;
      try
      {
         mySqldbconnection = DaoUtil.getMySqlDBConnection();
         String getTestRiskIssuesQuery =
            "SELECT * FROM TestRisk_and_Issues WHERE NOT archive ='yes'";
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
    * gets test risk and issues.
    *
    * @param request HttpRequest
    * @param response HttpResponse
    * @return modelAndView
    * @throws Exception Exception
    */
   @RequestMapping("/viewonlymasterprojectsreleaselist")
   public ModelAndView getViewOnlyMasterProjectsRelease(
      HttpServletRequest request, HttpServletResponse response)
      throws Exception
   {

      ModelAndView modelAndView =
         new ModelAndView("viewonlymasterprojectsreleases");
      ArrayList<MasterProjectsRelease> projectsReleaseList =
         new ArrayList<MasterProjectsRelease>();

      Connection mySqldbconnection = null;
      PreparedStatement preparedStatement = null;
      ResultSet resultSet = null;
      try
      {
         mySqldbconnection = DaoUtil.getMySqlDBConnection();
         String getMasterProjectsReleaseQuery =
            "SELECT * FROM MasterProjectsRelease  WHERE NOT archive ='yes'";
         preparedStatement =
            mySqldbconnection
               .prepareStatement(getMasterProjectsReleaseQuery);
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
}
