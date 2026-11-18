package com.dishapal.helpdesk.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.dishapal.helpdesk.dao.CommentDAO;
import com.dishapal.helpdesk.dao.HistoryDAO;
import com.dishapal.helpdesk.dao.TicketDetailsDAO;
import com.dishapal.helpdesk.model.Ticket;
import com.dishapal.helpdesk.model.TicketComment;
import com.dishapal.helpdesk.model.TicketHistory;
import com.dishapal.helpdesk.model.User;

@WebServlet("/ticket-details")
public class TicketDetailsServlet extends HttpServlet
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

        int ticketId;

        try
        {
            ticketId = Integer.parseInt(
                    request.getParameter("id"));
        }
        catch(Exception e)
        {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid ticket ID");
            return;
        }

        TicketDetailsDAO detailsDAO =
                new TicketDetailsDAO();

        boolean allowed =
                detailsDAO.canUserAccessTicket(
                        ticketId,
                        user.getUserId(),
                        user.getRole());

        if(!allowed)
        {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "You cannot access this ticket");
            return;
        }

        Ticket ticket =
                detailsDAO.getTicketById(ticketId);

        if(ticket == null)
        {
            response.sendError(
                    HttpServletResponse.SC_NOT_FOUND,
                    "Ticket not found");
            return;
        }

        CommentDAO commentDAO = new CommentDAO();
        HistoryDAO historyDAO = new HistoryDAO();

        List<TicketComment> comments =
                commentDAO.getCommentsByTicket(ticketId);

        List<TicketHistory> history =
                historyDAO.getHistoryByTicket(ticketId);

        request.setAttribute("ticket", ticket);
        request.setAttribute("comments", comments);
        request.setAttribute("history", history);

        request.getRequestDispatcher("/ticket-details.jsp")
               .forward(request, response);
    }
}