package com.dishapal.helpdesk.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.dishapal.helpdesk.dao.UserDAO;
import com.dishapal.helpdesk.model.User;
import com.dishapal.helpdesk.util.PasswordUtil;

@WebServlet("/login")
public class LoginServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException
    {
        request.getRequestDispatcher("/login.jsp")
               .forward(request, response);
    }

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException
    {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if(email == null || email.trim().isEmpty() ||
           password == null || password.isEmpty())
        {
            request.setAttribute(
                    "error", "Email and password are required");

            request.getRequestDispatcher("/login.jsp")
                   .forward(request, response);
            return;
        }

        email = email.trim().toLowerCase();

        String passwordHash =
                PasswordUtil.hashPassword(password);

        UserDAO userDAO = new UserDAO();

        User user =
                userDAO.loginUser(email, passwordHash);

        if(user != null)
        {
            HttpSession session = request.getSession();

            session.setAttribute("loggedInUser", user);
            session.setMaxInactiveInterval(30 * 60);

            response.sendRedirect(
                    request.getContextPath() + "/dashboard");
        }
        else
        {
            request.setAttribute(
                    "error", "Invalid email or password");

            request.getRequestDispatcher("/login.jsp")
                   .forward(request, response);
        }
    }
}