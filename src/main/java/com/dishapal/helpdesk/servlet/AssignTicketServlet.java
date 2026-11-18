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

@WebServlet("/assign-ticket")
public class AssignTicketServlet extends HttpServlet
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

        User admin =
                (User) session.getAttribute("loggedInUser");

        if(!"ADMIN".equalsIgnoreCase(admin.getRole()))
        {
            response.sendRedirect(
                    request.getContextPath() + "/dashboard");
            return;
        }

        try
        {
            int ticketId = Integer.parseInt(
                    request.getParameter("ticketId"));

            int agentId = Integer.parseInt(
                    request.getParameter("agentId"));

            TicketDAO ticketDAO = new TicketDAO();

            boolean assigned = ticketDAO.assignTicket(
                    ticketId,
                    agentId,
                    admin.getUserId());

            if(assigned)
            {
                response.sendRedirect(
                        request.getContextPath() +
                        "/admin-dashboard?assigned=true");
            }
            else
            {
                response.sendRedirect(
                        request.getContextPath() +
                        "/admin-dashboard?error=assignment");
            }
        }
        catch(NumberFormatException e)
        {
            response.sendRedirect(
                    request.getContextPath() +
                    "/admin-dashboard?error=invalid");
        }
    }
}