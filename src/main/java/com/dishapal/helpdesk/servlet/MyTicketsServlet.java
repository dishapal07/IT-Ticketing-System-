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
import com.dishapal.helpdesk.model.Ticket;
import com.dishapal.helpdesk.model.User;

@WebServlet("/my-tickets")
public class MyTicketsServlet extends HttpServlet
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

        if(!"EMPLOYEE".equalsIgnoreCase(user.getRole()))
        {
            response.sendRedirect(
                    request.getContextPath() + "/dashboard");
            return;
        }

        TicketDAO ticketDAO = new TicketDAO();

        List<Ticket> tickets =
                ticketDAO.getTicketsByUser(
                        user.getUserId());

        request.setAttribute("tickets", tickets);

        request.getRequestDispatcher("/my-tickets.jsp")
               .forward(request, response);
    }
}