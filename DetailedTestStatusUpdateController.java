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
import com.sonata.tm.model.DetailedTestStatus;

/**
 * Class to update test status.
 *
 * @author kirankumar.kadapa
 *
 */
@Controller
public class DetailedTestStatusUpdateController
{

   /**
    * method to return new form.
    *
    * @param request Http Request
    * @param response Http Response
    * @return ModelAndView test status view
    */
   @RequestMapping("/view_detailedteststatus")
   public ModelAndView getNewForm(HttpServletRequest request,
      HttpServletResponse response)
   {
      if (request.getSession().getAttribute(
         TestManagementSessionKeys.USER_NAME) != null
         && request.getSession().getAttribute(
            TestManagementSessionKeys.PASS_WORD) != null)
      {

         return new ModelAndView("detailed_test_status");
      }
      else
      {
         return new ModelAndView("loginForm");
      }
   }

   /**
    * method to save test status.
    *
    * @param request Http Request
    * @param response Http Response
    * @return ModelAndView test status view
    * @throws SQLException SQL Exception
    * @throws IOException IO Exception
    */
   @RequestMapping("/save_detailedteststatus")
   public ModelAndView saveDetailedTestStatus(HttpServletRequest request,
      HttpServletResponse response) throws SQLException, IOException
   {

      DetailedTestStatus detailedTestStatus = new DetailedTestStatus();
      String bpc = request.getParameter("BPC");
      String projectName = request.getParameter("ProjectName");
      String release = request.getParameter("Release");
      String channel = request.getParameter("Channel");
      String cycle = request.getParameter("Cycle");
      Double planned = Double.parseDouble(request.getParameter("Planned"));
      Double run = Double.parseDouble(request.getParameter("Run"));
      Double pass = Double.parseDouble(request.getParameter("Pass"));
      Double fail = Double.parseDouble(request.getParameter("fail"));
      Double notRun = Double.parseDouble(request.getParameter("Notrun"));
      Double deScoped =
         Double.parseDouble(request.getParameter("Descoped"));
      Double blocked = Double.parseDouble(request.getParameter("Blocked"));
      Double percentRun =
         Double.parseDouble(request.getParameter("PerRun"));
      Double percentNotRun =
         Double.parseDouble(request.getParameter("PerNotRun"));
      Double percentPass =
         Double.parseDouble(request.getParameter("PerBlock"));
      Double percentFail =
         Double.parseDouble(request.getParameter("PerPass"));
      Double percentBlocked =
         Double.parseDouble(request.getParameter("PerFail"));
      String rAG = request.getParameter("RAG");
      String preExecution = request.getParameter("PreExe");
      Date systemDate = new Date();
      String date11 = request.getParameter("FromDate");
      String date12 = request.getParameter("ToDate");
      java.sql.Date date3 = new java.sql.Date(systemDate.getTime());
      Date fromDate = new Date(date11);
      Date toDate = new Date(date12);
      java.sql.Date date1 = new java.sql.Date(fromDate.getTime());
      java.sql.Date date2 = new java.sql.Date(toDate.getTime());

      detailedTestStatus.setBlocked(blocked);
      detailedTestStatus.setBpc(bpc);
      detailedTestStatus.setChannel(channel);
      detailedTestStatus.setCycle(cycle);
      // detailedTestStatus.setDate(systemDate);
      detailedTestStatus.setFail(fail);
      detailedTestStatus.setNotRun(notRun);
      detailedTestStatus.setPass(pass);
      detailedTestStatus.setPercentBlocked(percentBlocked);
      detailedTestStatus.setPercentFail(percentFail);
      detailedTestStatus.setPercentNotRun(percentNotRun);
      detailedTestStatus.setPercentPass(percentPass);
      detailedTestStatus.setPercentRun(percentRun);
      detailedTestStatus.setPlanned(planned);
      detailedTestStatus.setProjectName(projectName);
      detailedTestStatus.setRag(rAG);
      detailedTestStatus.setRelease(release);
      detailedTestStatus.setRun(run);
      detailedTestStatus.setDeScoped(deScoped);

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
            "INSERT INTO Detailed_Test_Status"
               + "(last_updated,project_release, project_name, bpc,channel,cycl,testcases_planned,"
               + " testcases_run, testcases_pass, testcases_fail,testcases_not_run, "
               + "testcases_de_scoped, testcases_blocked, run_percentage, not_run_percentage,"
               + " blocked_percentage, pass_percentage, fail_percentage, rag,username,pre_exe,"
               + "start_date,end_date,archive) VALUES"
               + "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

         preparedStatement =
            mySqldbconnection.prepareStatement(insertTableSQL);

         preparedStatement.setDate(Numbers.ONE, date3);
         preparedStatement.setString(Numbers.TWO,
            detailedTestStatus.getRelease());
         preparedStatement.setString(Numbers.THREE,
            detailedTestStatus.getProjectName());
         preparedStatement.setString(Numbers.FOUR,
            detailedTestStatus.getBpc());
         preparedStatement.setString(Numbers.FIVE,
            detailedTestStatus.getChannel());
         preparedStatement.setString(Numbers.SIX,
            detailedTestStatus.getCycle());
         preparedStatement.setDouble(Numbers.SEVEN,
            detailedTestStatus.getPlanned());
         preparedStatement.setDouble(Numbers.EIGHT,
            detailedTestStatus.getRun());
         preparedStatement.setDouble(Numbers.NINE,
            detailedTestStatus.getPass());
         preparedStatement.setDouble(Numbers.TEN,
            detailedTestStatus.getFail());
         preparedStatement.setDouble(Numbers.ELEVEN,
            detailedTestStatus.getNotRun());
         preparedStatement.setDouble(Numbers.TWELVE,
            detailedTestStatus.getDeScoped());
         preparedStatement.setDouble(Numbers.THIRTEEN,
            detailedTestStatus.getBlocked());
         preparedStatement.setDouble(Numbers.FOURTEEN,
            detailedTestStatus.getPercentRun());
         preparedStatement.setDouble(Numbers.FIFTEEN,
            detailedTestStatus.getPercentNotRun());
         preparedStatement.setDouble(Numbers.SIXTEEN,
            detailedTestStatus.getPercentBlocked());
         preparedStatement.setDouble(Numbers.SEVENTEEN,
            detailedTestStatus.getPercentPass());
         preparedStatement.setDouble(Numbers.EIGHTEEN,
            detailedTestStatus.getPercentFail());
         preparedStatement.setString(Numbers.NINETEEN,
            detailedTestStatus.getRag());
         preparedStatement.setString(Numbers.TWENTY, username);
         preparedStatement.setString(Numbers.TWENTYONE, preExecution);
         preparedStatement.setDate(Numbers.TWENTYTWO, date1);
         preparedStatement.setDate(Numbers.TWENTYTHREE, date2);
         preparedStatement.setString(Numbers.TWENTYFOUR, "no");

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
