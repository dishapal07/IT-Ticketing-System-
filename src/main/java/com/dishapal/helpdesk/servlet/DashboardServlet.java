package com.dishapal.helpdesk.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.dishapal.helpdesk.model.User;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException
    {
        HttpSession session = request.getSession(false);

        if(session == null ||
           session.getAttribute("loggedInUser") == null)
        {
            response.sendRedirect(
                    request.getContextPath() + "/login");
            return;
        }

        User user =
                (User) session.getAttribute("loggedInUser");

        if("ADMIN".equalsIgnoreCase(user.getRole()))
        {
            response.sendRedirect(
                    request.getContextPath() +
                    "/admin-dashboard");
            return;
        }

        if("AGENT".equalsIgnoreCase(user.getRole()))
        {
            response.sendRedirect(
                    request.getContextPath() +
                    "/agent-dashboard");
            return;
        }

        request.getRequestDispatcher("/dashboard.jsp")
               .forward(request, response);
    }
}