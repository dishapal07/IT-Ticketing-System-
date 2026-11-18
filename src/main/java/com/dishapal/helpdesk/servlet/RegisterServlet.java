package com.dishapal.helpdesk.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dishapal.helpdesk.dao.UserDAO;
import com.dishapal.helpdesk.model.User;
import com.dishapal.helpdesk.util.PasswordUtil;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException
    {
        request.getRequestDispatcher("/register.jsp")
               .forward(request, response);
    }

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException
    {
        request.setCharacterEncoding("UTF-8");

        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword =
                request.getParameter("confirmPassword");

        if(fullName == null || fullName.trim().isEmpty() ||
           email == null || email.trim().isEmpty() ||
           password == null || password.isEmpty())
        {
            request.setAttribute(
                    "error", "All fields are required");

            request.getRequestDispatcher("/register.jsp")
                   .forward(request, response);
            return;
        }

        if(password.length() < 6)
        {
            request.setAttribute(
                    "error",
                    "Password must contain at least 6 characters");

            request.getRequestDispatcher("/register.jsp")
                   .forward(request, response);
            return;
        }

        if(!password.equals(confirmPassword))
        {
            request.setAttribute(
                    "error", "Passwords do not match");

            request.getRequestDispatcher("/register.jsp")
                   .forward(request, response);
            return;
        }

        fullName = fullName.trim();
        email = email.trim().toLowerCase();

        UserDAO userDAO = new UserDAO();

        if(userDAO.emailExists(email))
        {
            request.setAttribute(
                    "error", "This email is already registered");

            request.getRequestDispatcher("/register.jsp")
                   .forward(request, response);
            return;
        }

        String passwordHash =
                PasswordUtil.hashPassword(password);

        User user = new User(
                fullName,
                email,
                passwordHash,
                "EMPLOYEE");

        boolean registered = userDAO.registerUser(user);

        if(registered)
        {
            request.setAttribute(
                    "success",
                    "Registration successful. You can now log in.");
        }
        else
        {
            request.setAttribute(
                    "error",
                    "Registration failed. Please try again.");
        }

        request.getRequestDispatcher("/register.jsp")
               .forward(request, response);
    }
}