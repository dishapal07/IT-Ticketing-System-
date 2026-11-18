package com.dishapal.helpdesk.servlet;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.dishapal.helpdesk.dao.TicketDAO;
import com.dishapal.helpdesk.model.Ticket;
import com.dishapal.helpdesk.model.User;

@WebServlet("/create-ticket")
public class CreateTicketServlet extends HttpServlet
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

        request.getRequestDispatcher("/create-ticket.jsp")
               .forward(request, response);
    }

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

        if(!"EMPLOYEE".equalsIgnoreCase(user.getRole()))
        {
            response.sendRedirect(
                    request.getContextPath() + "/dashboard");
            return;
        }

        String title = request.getParameter("title");

        String description =
                request.getParameter("description");

        String category =
                request.getParameter("category");

        String priority =
                request.getParameter("priority");

        if(title == null || title.trim().isEmpty() ||
           description == null ||
           description.trim().isEmpty() ||
           category == null || category.trim().isEmpty() ||
           priority == null || priority.trim().isEmpty())
        {
            request.setAttribute(
                    "error",
                    "All ticket fields are required");

            request.getRequestDispatcher("/create-ticket.jsp")
                   .forward(request, response);
            return;
        }

        title = title.trim();
        description = description.trim();
        category = category.trim().toUpperCase();
        priority = priority.trim().toUpperCase();

        if(title.length() > 150)
        {
            request.setAttribute(
                    "error",
                    "Title cannot contain more than 150 characters");

            request.getRequestDispatcher("/create-ticket.jsp")
                   .forward(request, response);
            return;
        }

        if(description.length() > 2000)
        {
            request.setAttribute(
                    "error",
                    "Description cannot contain more than 2000 characters");

            request.getRequestDispatcher("/create-ticket.jsp")
                   .forward(request, response);
            return;
        }

        boolean validCategory =
                "HARDWARE".equals(category) ||
                "SOFTWARE".equals(category) ||
                "NETWORK".equals(category) ||
                "ACCOUNT_ACCESS".equals(category) ||
                "OTHER".equals(category);

        if(!validCategory)
        {
            request.setAttribute(
                    "error",
                    "Invalid category selected");

            request.getRequestDispatcher("/create-ticket.jsp")
                   .forward(request, response);
            return;
        }

        int slaHours;

        switch(priority)
        {
            case "CRITICAL":
                slaHours = 4;
                break;

            case "HIGH":
                slaHours = 8;
                break;

            case "MEDIUM":
                slaHours = 24;
                break;

            case "LOW":
                slaHours = 48;
                break;

            default:
                request.setAttribute(
                        "error",
                        "Invalid priority selected");

                request.getRequestDispatcher("/create-ticket.jsp")
                       .forward(request, response);
                return;
        }

        LocalDateTime dueDate =
                LocalDateTime.now().plusHours(slaHours);

        Timestamp dueAt =
                Timestamp.valueOf(dueDate);

        Ticket ticket = new Ticket(
                user.getUserId(),
                title,
                description,
                category,
                priority,
                dueAt);

        TicketDAO ticketDAO = new TicketDAO();

        boolean created =
                ticketDAO.createTicket(ticket);

        if(created)
        {
            response.sendRedirect(
                    request.getContextPath()
                    + "/my-tickets?created=true");
        }
        else
        {
            request.setAttribute(
                    "error",
                    "Ticket creation failed. Please try again.");

            request.getRequestDispatcher("/create-ticket.jsp")
                   .forward(request, response);
        }
    }
}