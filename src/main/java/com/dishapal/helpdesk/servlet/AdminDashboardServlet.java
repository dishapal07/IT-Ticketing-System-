package com.dishapal.helpdesk.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.dishapal.helpdesk.dao.TicketDAO;
import com.dishapal.helpdesk.dao.UserDAO;
import com.dishapal.helpdesk.model.Ticket;
import com.dishapal.helpdesk.model.User;

@WebServlet("/admin-dashboard")
public class AdminDashboardServlet extends HttpServlet
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

        User loggedInUser =
                (User) session.getAttribute("loggedInUser");

        if(!"ADMIN".equalsIgnoreCase(
                loggedInUser.getRole()))
        {
            response.sendRedirect(
                    request.getContextPath() + "/dashboard");
            return;
        }

        TicketDAO ticketDAO = new TicketDAO();
        UserDAO userDAO = new UserDAO();

        List<Ticket> tickets =
                ticketDAO.getAllTickets();

        List<User> agents =
                userDAO.getAllAgents();

        request.setAttribute("tickets", tickets);
        request.setAttribute("agents", agents);

        request.getRequestDispatcher("/admin-dashboard.jsp")
               .forward(request, response);
    }
}