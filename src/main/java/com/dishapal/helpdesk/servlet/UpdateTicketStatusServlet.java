package com.dishapal.helpdesk.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.dishapal.helpdesk.dao.TicketDAO;
import com.dishapal.helpdesk.model.User;

@WebServlet("/update-ticket-status")
public class UpdateTicketStatusServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request,
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

        User agent =
                (User) session.getAttribute("loggedInUser");

        if(!"AGENT".equalsIgnoreCase(agent.getRole()))
        {
            response.sendRedirect(
                    request.getContextPath() + "/dashboard");
            return;
        }

        try
        {
            int ticketId = Integer.parseInt(
                    request.getParameter("ticketId"));

            String newStatus =
                    request.getParameter("newStatus");

            if(newStatus == null)
            {
                response.sendRedirect(
                        request.getContextPath() +
                        "/agent-dashboard?error=invalid");
                return;
            }

            newStatus = newStatus.trim().toUpperCase();

            if(!"IN_PROGRESS".equals(newStatus) &&
               !"RESOLVED".equals(newStatus))
            {
                response.sendRedirect(
                        request.getContextPath() +
                        "/agent-dashboard?error=invalid");
                return;
            }

            TicketDAO ticketDAO = new TicketDAO();

            boolean updated =
                    ticketDAO.updateTicketStatus(
                            ticketId,
                            agent.getUserId(),
                            newStatus);

            if(updated)
            {
                response.sendRedirect(
                        request.getContextPath() +
                        "/agent-dashboard?updated=true");
            }
            else
            {
                response.sendRedirect(
                        request.getContextPath() +
                        "/agent-dashboard?error=update");
            }
        }
        catch(NumberFormatException e)
        {
            response.sendRedirect(
                    request.getContextPath() +
                    "/agent-dashboard?error=invalid");
        }
    }
}