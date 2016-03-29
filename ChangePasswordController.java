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
 * $RCSfile: ChangePasswordController.java$
 *
 * $Revision: $
 *
 * $Date: Dec 31, 2015$
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

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sonata.tm.connectionutil.DaoUtil;
import com.sonata.tm.constants.TestManagementSessionKeys;

/**
 * Class to change password.
 *
 * This class is unused for now. Please use call this class using ajax and
 * populate response in jsp
 *
 * @author kirankumar.kadapa
 *
 */
@Controller
public class ChangePasswordController
{

   /**
    * method to change password in db.
    *
    * @param passWord password
    * @return String success/failure
    * @param request Http Request
    * @throws SQLException Sql Exception
    */
   @RequestMapping("/changePassword")
   @ResponseBody
   public String changePassword(@RequestParam("passWord") String passWord,
      HttpServletRequest request) throws SQLException
   {

      String userName =
         (String) request.getSession().getAttribute(
            TestManagementSessionKeys.USER_NAME);

      if (userName != null && passWord != null)
      {

         Connection mySqlDbCon = null;
         PreparedStatement preparedStatement = null;
         String query = null;
         try
         {
            mySqlDbCon = DaoUtil.getMySqlDBConnection();
            query =
               "update authentication set password = '" + passWord
                  + "' where username = '" + userName + "'";

            preparedStatement = mySqlDbCon.prepareStatement(query);
            preparedStatement.execute(query);
            request.getSession().setAttribute(
               TestManagementSessionKeys.PASS_WORD, passWord);
            return "Password is changed for username " + userName;
         }

         catch (IOException e)
         {
            e.printStackTrace();
            return "Facing Technical difficulties, please try again.";
         }
         catch (SQLException e)
         {
            e.printStackTrace();
            return "Facing Technical difficulties, please try again.";
         }
         finally
         {
            preparedStatement.close();
            mySqlDbCon.close();
         }

      }
      else
      {
         return "Unable to change password now, please try again.";
      }

   }
}
