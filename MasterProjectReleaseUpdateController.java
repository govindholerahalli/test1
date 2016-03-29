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
 * $RCSfile: MasterProjectReleaseUpdateController.java$
 *
 * $Revision: $
 *
 * $Date: Nov 18, 2015$
 *
 * Author: kirankumar.kadapa
 *
 *
 * $Log: $
 */
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

/**
 * Class to Insert Master Project Release.
 *
 * @author kirankumar.kadapa
 *
 */
@Controller
public class MasterProjectReleaseUpdateController
{

   /**
    * method to return master project release form.
    *
    * @param request Http Request
    * @param response Http Response
    * @return master prooject release form
    */
   @RequestMapping("/view_masterprojectrelease")
   public ModelAndView getNewForm(HttpServletRequest request,
      HttpServletResponse response)
   {
      if (request.getSession().getAttribute(
         TestManagementSessionKeys.USER_NAME) != null
         && request.getSession().getAttribute(
            TestManagementSessionKeys.PASS_WORD) != null)
      {

         return new ModelAndView("masterprojectsrelease");
      }
      else
      {
         return new ModelAndView("loginForm");
      }
   }

   /**
    * method to insert master project release to db.
    *
    * @param request Http Request
    * @param response Http Reponse
    * @return master project release status
    * @throws IOException IOE xception
    * @throws SQLException SQL Exception
    */
   @RequestMapping("/save_masterprojectrelease")
   public ModelAndView saveDetailedTestStatus(HttpServletRequest request,
      HttpServletResponse response) throws SQLException, IOException
   {

      String username =
         (String) request.getSession().getAttribute(
            TestManagementSessionKeys.USER_NAME);

      Connection mySqldbconnection = null;
      PreparedStatement preparedStatement = null;

      try
      {
         mySqldbconnection = DaoUtil.getMySqlDBConnection();
         String updateTableSQL =
            "INSERT INTO MasterProjectsRelease(projectName, releaseName, status,"
               + " username, lastUpdated, archive) VALUES(?,?,?,?,?,?)";
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
         preparedStatement.setString(Numbers.SIX,
            "no");
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

      return new ModelAndView("success");

   }

}
