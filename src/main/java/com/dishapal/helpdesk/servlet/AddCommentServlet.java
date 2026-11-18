package com.dishapal.helpdesk.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.dishapal.helpdesk.dao.CommentDAO;
import com.dishapal.helpdesk.dao.TicketDetailsDAO;
import com.dishapal.helpdesk.model.TicketComment;
import com.dishapal.helpdesk.model.User;

@WebServlet("/add-comment")
public class AddCommentServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException
    {
        request.setCharacterEncoding("UTF-8");

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
                    request.getParameter("ticketId"));
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
                    "You cannot comment on this ticket");
            return;
        }

        String message =
                request.getParameter("message");

        if(message == null ||
           message.trim().isEmpty() ||
           message.trim().length() > 1000)
        {
            response.sendRedirect(
                    request.getContextPath() +
                    "/ticket-details?id=" + ticketId +
                    "&error=comment");
            return;
        }

        TicketComment comment =
                new TicketComment();

        comment.setTicketId(ticketId);
        comment.setUserId(user.getUserId());
        comment.setMessage(message.trim());

        CommentDAO commentDAO = new CommentDAO();

        boolean added =
                commentDAO.addComment(comment);

        if(added)
        {
            response.sendRedirect(
                    request.getContextPath() +
                    "/ticket-details?id=" + ticketId +
                    "&commented=true");
        }
        else
        {
            response.sendRedirect(
                    request.getContextPath() +
                    "/ticket-details?id=" + ticketId +
                    "&error=comment");
        }
    }
}