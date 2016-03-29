package com.sonata.tm.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Class to redirect new views.
 *
 * @author kirankumar.kadapa
 *
 */
@Controller
public class RedirectController
{

   /**
    * method to redirect home view.
    *
    * @param request Http Request
    * @param response Http Response
    * @return home view.
    */
   @RequestMapping("/home.htm")
   public ModelAndView redirect(HttpServletRequest request,
      HttpServletResponse response)
   {

      return new ModelAndView("home");
   }

   /**
    * method to redirect loginform view.
    *
    * @return loginform view
    */
   @RequestMapping("/login.htm")
   public ModelAndView redirect1()
   {

      return new ModelAndView("loginForm");
   }

   /**
    * Method to handle refresh from browser.
    *
    * @param request Http Request
    * @param response Http Response
    * @return Check if logged in and return accordingly
    */
   @RequestMapping("/refresh.htm")
   public ModelAndView redirect2(HttpServletRequest request,
      HttpServletResponse response)
   {
      if (request.getParameter("username") != null
         && request.getParameter("password") != null)
      {

         return new ModelAndView("refresh");
      }
      else
      {
         return new ModelAndView("loginaccessdenied");
      }
   }

   /**
    * method to handle downtime log.
    *
    * @param request Http Request
    * @param response Http Response
    * @return Check if logged in and return accordingly
    */
   @RequestMapping("/downtimelog.htm")
   public ModelAndView redirect3(HttpServletRequest request,
      HttpServletResponse response)
   {

      return new ModelAndView("downtimelog");

   }

   /**
    * Method to return logout jsp.
    *
    * @return logout view
    */
   @RequestMapping("/logout.htm")
   public ModelAndView redirect4()
   {
      return new ModelAndView("logout");
   }

   /**
    * method to return logout form. This method is used to logout the.
    * current user and in the same session set another user. This is to
    * maintain the same session and create a new user, so he can edit /
    * delete test status accordingly Please see logoutAndLogin jsp for more
    * details
    *
    * @return logoutAndLogin view
    */
   @RequestMapping("/logoutAndLogin.htm")
   public ModelAndView redirect5()
   {
      return new ModelAndView("logoutAndLogin");
   }

}
