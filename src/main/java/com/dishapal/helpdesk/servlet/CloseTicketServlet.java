package com.dishapal.helpdesk.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.dishapal.helpdesk.dao.TicketClosureDAO;
import com.dishapal.helpdesk.model.User;

@WebServlet("/close-ticket")
public class CloseTicketServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null) {
            response.sendRedirect(
                    request.getContextPath() + "/login");
            return;
        }

        User loggedInUser =
                (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            response.sendRedirect(
                    request.getContextPath() + "/login");
            return;
        }

        if (!"EMPLOYEE".equalsIgnoreCase(loggedInUser.getRole())) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String ticketIdValue =
                request.getParameter("ticketId");

        try {
            int ticketId =
                    Integer.parseInt(ticketIdValue);

            TicketClosureDAO closureDAO =
                    new TicketClosureDAO();

            boolean closed =
                    closureDAO.closeTicket(
                            ticketId,
                            loggedInUser.getUserId());

            if (closed) {
                response.sendRedirect(
                        request.getContextPath()
                        + "/ticket-details?id="
                        + ticketId
                        + "&closed=true");
            } else {
                response.sendRedirect(
                        request.getContextPath()
                        + "/ticket-details?id="
                        + ticketId
                        + "&closeError=true");
            }

        } catch (Exception e) {
            response.sendRedirect(
                    request.getContextPath()
                    + "/dashboard");
        }
    }
}