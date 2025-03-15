package com.example.todo.controller;

import com.example.todo.model.Todo;
import com.example.todo.model.User;
import com.example.todo.service.TodoService;
import com.example.todo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/todos")
public class TodoController {
    
    @Autowired
    private TodoService todoService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    public String listTodos(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        Optional<User> userOpt = userService.getUserById(userId);
        if (userOpt.isPresent()) {
            List<Todo> todos = todoService.getTodosByUser(userOpt.get());
            model.addAttribute("todos", todos);
            return "todos";
        } else {
            session.invalidate();
            return "redirect:/login";
        }
    }
    
    @PostMapping
    public String createTodo(@RequestParam String title,
                             @RequestParam String description,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        Optional<User> userOpt = userService.getUserById(userId);
        if (userOpt.isPresent()) {
            todoService.createTodo(title, description, userOpt.get());
            redirectAttributes.addFlashAttribute("success", "Todo created successfully");
        }
        
        return "redirect:/todos";
    }
    
    @GetMapping("/edit/{id}")
    public String editTodoForm(@PathVariable Long id, Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        Optional<Todo> todoOpt = todoService.getTodoById(id);
        if (todoOpt.isPresent() && todoOpt.get().getUser().getId().equals(userId)) {
            model.addAttribute("todo", todoOpt.get());
            return "edit-todo";
        }
        
        return "redirect:/todos";
    }
    
    @PostMapping("/update/{id}")
    public String updateTodo(@PathVariable Long id,
                             @RequestParam String title,
                             @RequestParam String description,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        Optional<Todo> todoOpt = todoService.getTodoById(id);
        if (todoOpt.isPresent() && todoOpt.get().getUser().getId().equals(userId)) {
            Todo todo = todoOpt.get();
            todo.setTitle(title);
            todo.setDescription(description);
            todoService.updateTodo(todo);
            redirectAttributes.addFlashAttribute("success", "Todo updated successfully");
        }
        
        return "redirect:/todos";
    }
    
    @GetMapping("/delete/{id}")
    public String deleteTodo(@PathVariable Long id,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        Optional<Todo> todoOpt = todoService.getTodoById(id);
        if (todoOpt.isPresent() && todoOpt.get().getUser().getId().equals(userId)) {
            todoService.deleteTodo(id);
            redirectAttributes.addFlashAttribute("success", "Todo deleted successfully");
        }
        
        return "redirect:/todos";
    }
    
    @GetMapping("/toggle/{id}")
    public String toggleTodoStatus(@PathVariable Long id,
                                   HttpSession session) {
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        Optional<Todo> todoOpt = todoService.getTodoById(id);
        if (todoOpt.isPresent() && todoOpt.get().getUser().getId().equals(userId)) {
            todoService.toggleTodoStatus(id);
        }
        
        return "redirect:/todos";
    }
}

