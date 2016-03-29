package com.sonata.tm.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Class to generate reports.
 *
 * @author kirankumar.kadapa
 *
 */
@Controller
public class ReportsGeneratorController
{
   /**
    * method to return downtime log report.
    *
    * @param request Http Request
    * @param response Http Response
    * @return downtime log
    */
   @RequestMapping("/downtime_logreport")
   public ModelAndView generateDowntimeLogReport(
      HttpServletRequest request, HttpServletResponse response)
   {

      return new ModelAndView("downtime_log");

   }

   /**
    * method to return test status report.
    *
    * @param request Http Request
    * @param response Http Response
    * @return test status
    */
   @RequestMapping("/teststatus_report")
   public ModelAndView generateTestStatusReport(
      HttpServletRequest request, HttpServletResponse response)
   {

      return new ModelAndView("test_status");

   }

   /**
    * method to return risk issue report.
    *
    * @param request Http Request
    * @param response Http Response
    * @return risk issues
    */
   @RequestMapping("/testrisksandissuesreport")
   public ModelAndView generateTestRisksAndIssuesReport(
      HttpServletRequest request, HttpServletResponse response)
   {

      return new ModelAndView("test_risks_and_issues_report");

   }

   /**
    * method to return master project release report.
    *
    * @param request Http Request
    * @param response Http Response
    * @return master project release
    */
   @RequestMapping("/masterprojectreleasereport")
   public ModelAndView generateMasterProjectReleaseReport(
      HttpServletRequest request, HttpServletResponse response)
   {

      return new ModelAndView("master_project_release_report");

   }
}
