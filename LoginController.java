package com.sonata.tm.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.sonata.tm.constants.TestManagementSessionKeys;
import com.sonata.tm.facade.LoginFacade;

/**
 * Class to Login to the application.
 *
 * @author kirankumar.kadapa
 *
 */
@Controller
public class LoginController
{

   /**
    * Login Service.
    *
    * @param request Http Request
    * @param response Http Response
    * @return returns home page, logged in view.
    * @throws Exception Exception
    */
   @RequestMapping("/loginform.htm")
   public ModelAndView service(HttpServletRequest request,
      HttpServletResponse response) throws Exception
   {

      String uname = request.getParameter("uname");
      String password = request.getParameter("pasword");
      request.getSession().setAttribute(
         TestManagementSessionKeys.USER_NAME, uname);
      request.getSession().setAttribute(
         TestManagementSessionKeys.PASS_WORD, password);
      LoginFacade invoicefacede = new LoginFacade();
      boolean accessstatus =
         invoicefacede.getUserAuthenticationDetails(uname, password);
      if (accessstatus)
      {
         request.setAttribute("username", uname);
         request.setAttribute("password", password);
         return new ModelAndView("home");
      }
      else
      {
         return new ModelAndView("loginaccessdenied");
      }

   }

}
